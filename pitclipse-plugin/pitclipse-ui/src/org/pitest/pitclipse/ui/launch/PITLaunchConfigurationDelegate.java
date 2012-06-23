package org.pitest.pitclipse.ui.launch;

import static org.pitest.pitclipse.ui.PITActivator.log;
import static org.pitest.pitclipse.ui.launch.PITClipseConstants.PIT_PROJECT;
import static org.pitest.pitclipse.ui.launch.PITClipseConstants.PIT_TEST_CLASS;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.pitest.mutationtest.MutationCoverageReport;
import org.pitest.pitclipse.pitrunner.PITOptions;
import org.pitest.pitclipse.pitrunner.PITOptions.PITOptionsBuilder;
import org.pitest.pitclipse.ui.PITActivator;
import org.pitest.pitclipse.ui.view.PITView;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
public class PITLaunchConfigurationDelegate extends JavaLaunchDelegate {

	private static final String PIT_REPORT_GENERATOR = MutationCoverageReport.class
			.getCanonicalName();

	private static final class UpdateView implements Runnable {
		private final File reportDirectory;
		private final PITViewFinder viewFinder = new PITViewFinder();

		public UpdateView(File reportDirectory) {
			this.reportDirectory = new File(reportDirectory.toURI());
		}

		public void run() {
			PITView view = viewFinder.getView() ;
			view.update(reportDirectory);
		}
	}

	private PITOptions options = null;

	@Override
	public void launch(ILaunchConfiguration launchConfig, String mode,
			ILaunch launch, IProgressMonitor progress) throws CoreException {
		List<String> classPath = ImmutableList
				.copyOf(getClassesForProject(launchConfig.getAttribute(
						PIT_PROJECT, "")));
		List<File> sourceDirs = ImmutableList
				.copyOf(getSourceDirsForProject(launchConfig.getAttribute(
						PIT_PROJECT, "")));
		options = new PITOptionsBuilder()
				.withClassUnderTest(
						launchConfig.getAttribute(PIT_TEST_CLASS, ""))
				.withClassesToMutate(classPath)
				.withSourceDirectories(sourceDirs).build();
		super.launch(launchConfig, mode, launch, progress);
		UIUpdate updater = new UIUpdate(ImmutableList.copyOf(launch
				.getProcesses()), new UpdateView(options.getReportDirectory()));
		new Thread(updater).start();
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
				.addAll(PITActivator.getPITClasspath()).build();
		log("Classpath: " + newClasspath);
		return newClasspath.toArray(new String[newClasspath.size()]);
	}

	private Set<String> getClassesForProject(String projectName)
			throws CoreException {
		Builder<String> classPathBuilder = ImmutableSet.builder();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject project : root.getProjects()) {
			if (projectName.equals(project.getName()) && project.isOpen()) {
				IJavaProject javaProject = JavaCore.create(project);
				classPathBuilder.addAll(getClassesFromProject(javaProject));
			}
		}
		return classPathBuilder.build();
	}

	private Set<String> getClassesFromProject(IJavaProject javaProject)
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
		return classPathBuilder.build();
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
	private Set<File> getSourceDirsForProject(String projectName)
			throws CoreException {
		Builder<File> sourceDirBuilder = ImmutableSet.builder();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject project : root.getProjects()) {
			if (projectName.equals(project.getName())) {
				URI location = getProjectLocation(project);
				IJavaProject javaProject = JavaCore.create(project);
				IPackageFragmentRoot[] packageRoots = javaProject
						.getPackageFragmentRoots();

				File workspaceRoot = new File(location).getParentFile();
				for (IPackageFragmentRoot packageRoot : packageRoots) {
					if (!packageRoot.isArchive()) {
						sourceDirBuilder.add(new File(workspaceRoot,
								packageRoot.getPath().toString()));
					}
				}
			}
		}
		return sourceDirBuilder.build();
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
