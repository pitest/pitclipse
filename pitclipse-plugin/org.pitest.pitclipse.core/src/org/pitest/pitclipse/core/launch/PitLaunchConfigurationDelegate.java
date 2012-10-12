package org.pitest.pitclipse.core.launch;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableSet.builder;
import static org.eclipse.core.resources.IResource.FOLDER;
import static org.eclipse.core.resources.IResource.NONE;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.pitest.pitclipse.core.PitCoreActivator.getPitClasspath;
import static org.pitest.pitclipse.core.PitCoreActivator.log;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.pitest.mutationtest.MutationCoverageReport;
import org.pitest.pitclipse.core.extension.handler.ExtensionPointHandler;
import org.pitest.pitclipse.core.extension.point.PitCoreResults;
import org.pitest.pitclipse.pitrunner.PITOptions;
import org.pitest.pitclipse.pitrunner.PITOptions.PITOptionsBuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class PitLaunchConfigurationDelegate extends JavaLaunchDelegate {

	private static final String EXTENSION_POINT_ID = "org.pitest.pitclipse.core.results";

	private static final String PIT_REPORT_GENERATOR = MutationCoverageReport.class
			.getCanonicalName();

	private static final int PROJECT_IS_CLOSED = -1;

	private static final int BAD_PROJECT = -2;

	private static final class UpdateExtensions implements Runnable {
		private final File reportDirectory;

		public UpdateExtensions(File reportDirectory) {
			this.reportDirectory = new File(reportDirectory.toURI());
		}

		public void run() {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			PitCoreResults results = new PitCoreResults(reportDirectory.toURI());
			new ExtensionPointHandler<PitCoreResults>(EXTENSION_POINT_ID)
					.execute(registry, results);
		}
	}

	private final ExecutorService executorService = Executors
			.newSingleThreadExecutor();

	private PITOptions options = null;

	@Override
	public void launch(ILaunchConfiguration launchConfig, String mode,
			ILaunch launch, IProgressMonitor progress) throws CoreException {
		IJavaProject project = getProject(launchConfig);
		List<String> classPath = getClassesFromProject(project);
		List<File> sourceDirs = getSourceDirsForProject(project);
		if (isTestLaunch(launchConfig)) {
			IType testClass = getTestClass(launchConfig);
			options = new PITOptionsBuilder()
					.withClassUnderTest(testClass.getFullyQualifiedName())
					.withClassesToMutate(classPath)
					.withSourceDirectories(sourceDirs).build();
		} else {
			List<String> packages = getPackagesToTest(launchConfig);
			options = new PITOptionsBuilder().withPackagesToTest(packages)
					.withClassesToMutate(classPath)
					.withSourceDirectories(sourceDirs).build();
		}

		super.launch(launchConfig, mode, launch, progress);
		ProcessPoller updater = new ProcessPoller(ImmutableList.copyOf(launch
				.getProcesses()), new UpdateExtensions(
				options.getReportDirectory()));
		executorService.execute(updater);
	}

	private List<String> getPackagesToTest(ILaunchConfiguration launchConfig)
			throws CoreException {
		final Builder<String> builder = builder();
		IResource[] resources = launchConfig.getMappedResources();
		IResourceProxyVisitor visitor = new IResourceProxyVisitor() {
			public boolean visit(IResourceProxy proxy) throws CoreException {
				if (proxy.getType() == FOLDER) {
					IJavaElement element = JavaCore.create(proxy
							.requestResource());
					if (element.getElementType() == PACKAGE_FRAGMENT) {
						builder.add(element.getElementName() + ".*");
					}
				}
				return false;
			}
		};
		for (IResource resource : resources) {
			resource.accept(visitor, NONE);
		}
		return copyOf(builder.build());
	}

	private boolean isTestLaunch(ILaunchConfiguration launchConfig)
			throws CoreException {
		return !launchConfig.getAttribute(ATTR_MAIN_TYPE_NAME, "").trim()
				.isEmpty();
	}

	private IJavaProject getProject(ILaunchConfiguration launchConfig)
			throws CoreException {
		return getProject(launchConfig.getAttribute(ATTR_PROJECT_NAME, ""));
	}

	private IJavaProject getProject(String projectName) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject project : root.getProjects()) {
			if (projectName.equals(project.getName())) {
				if (project.isOpen()) {
					return JavaCore.create(project);
				} else {
					abort("Project: " + projectName + "is closed.", null,
							PROJECT_IS_CLOSED);
				}
			}
		}
		abort("Project: " + projectName + "could not be found.", null,
				BAD_PROJECT);
		return null; // Never reached
	}

	private IType getTestClass(ILaunchConfiguration launchConfig)
			throws CoreException {
		String testClass = super.getMainTypeName(launchConfig);
		if (testClass.length() > 0) {
			IJavaProject javaProject = getProject(launchConfig);
			IType type = javaProject.findType(testClass);
			if (type != null && type.exists()) {
				return type;
			}
		}
		abort("Test: " + testClass + " could not be found.", null, BAD_PROJECT);
		return null; // Never reached
	}

	@Override
	public String getMainTypeName(ILaunchConfiguration launchConfig)
			throws CoreException {
		return PIT_REPORT_GENERATOR;
	}

	@Override
	public String[] getClasspath(ILaunchConfiguration launchConfig)
			throws CoreException {
		List<String> newClasspath = ImmutableList.<String> builder()
				.addAll(ImmutableList.copyOf(super.getClasspath(launchConfig)))
				.addAll(getPitClasspath()).build();
		log("Classpath: " + newClasspath);
		return newClasspath.toArray(new String[newClasspath.size()]);
	}

	private List<String> getClassesFromProject(IJavaProject javaProject)
			throws JavaModelException {
		Builder<String> classPathBuilder = ImmutableSet.builder();
		IPackageFragmentRoot[] packageRoots = javaProject
				.getPackageFragmentRoots();
		for (IPackageFragmentRoot packageRoot : packageRoots) {
			if (!packageRoot.isArchive()) {
				for (IJavaElement element : packageRoot.getChildren()) {
					if (element instanceof IPackageFragment) {
						IPackageFragment packge = (IPackageFragment) element;
						if (packge.getCompilationUnits().length > 0) {
							classPathBuilder
									.add(packge.getElementName() + ".*");
							// classPathBuilder.addAll(getClassesFromPackage(packge));
						}
					}
				}
			}
		}
		return copyOf(classPathBuilder.build());
	}

	/*
	 * private Set<String> getClassesFromPackage( IPackageFragment packge)
	 * throws JavaModelException { Builder<String> classPathBuilder =
	 * ImmutableSet.builder(); for (ICompilationUnit javaFile :
	 * packge.getCompilationUnits()) {
	 * classPathBuilder.addAll(getClassesFromSourceFile(javaFile)) ; } return
	 * classPathBuilder.build(); }
	 * 
	 * private Set<String> getClassesFromSourceFile( ICompilationUnit javaFile)
	 * throws JavaModelException { Builder<String> classPathBuilder =
	 * ImmutableSet.builder(); for (IType type : javaFile.getAllTypes()) {
	 * classPathBuilder.add(type.getFullyQualifiedName()); } return
	 * classPathBuilder.build(); }
	 */
	private List<File> getSourceDirsForProject(IJavaProject javaProject)
			throws CoreException {
		Builder<File> sourceDirBuilder = ImmutableSet.builder();
		URI location = getProjectLocation(javaProject.getProject());
		IPackageFragmentRoot[] packageRoots = javaProject
				.getPackageFragmentRoots();

		File projectRoot = new File(location);
		for (IPackageFragmentRoot packageRoot : packageRoots) {
			if (!packageRoot.isArchive()) {
				File packagePath = removeProjectFromPackagePath(javaProject,
						packageRoot.getPath());
				sourceDirBuilder.add(new File(projectRoot, packagePath
						.toString()));
			}
		}
		return ImmutableList.copyOf(sourceDirBuilder.build());
	}

	private File removeProjectFromPackagePath(IJavaProject javaProject,
			IPath packagePath) {
		IPath newPath = packagePath.removeFirstSegments(1);
		return newPath.toFile();
	}

	private URI getProjectLocation(IProject project) throws CoreException {
		URI locationURI = project.getDescription().getLocationURI();
		if (null != locationURI) {
			return locationURI;
		}
		// We're using the default location under workspace
		File projLocation = new File(project.getLocation().toOSString());
		return projLocation.toURI();
	}

	@Override
	public String getProgramArguments(ILaunchConfiguration launchConfig)
			throws CoreException {
		String pitArgs = null == options ? "" : options.toCLIArgsAsString();
		pitArgs += " --verbose";
		log("PIT Arguments: " + pitArgs);
		return new StringBuilder(super.getProgramArguments(launchConfig))
				.append(pitArgs).toString();
	}

}
