package org.pitest.pitclipse.core.launch;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableSet.builder;
import static org.eclipse.core.resources.IResource.FOLDER;
import static org.eclipse.core.resources.IResource.NONE;
import static org.eclipse.core.resources.IResource.PROJECT;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT_ROOT;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.pitest.pitclipse.core.PitCoreActivator.getDefault;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.pitest.pitclipse.pitrunner.PitOptions;
import org.pitest.pitclipse.pitrunner.PitOptions.PitOptionsBuilder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class LaunchConfigurationWrapper {

	public static final class TestClassNotFoundException extends
			RuntimeException {

		private static final long serialVersionUID = 1708246133941190992L;

		public TestClassNotFoundException(String name) {
			super(name);
		}

	}

	public static final class ProjectNotFoundException extends RuntimeException {

		private static final long serialVersionUID = -6545988416609531935L;

		public ProjectNotFoundException(String projectName) {
			super(projectName);
		}

	}

	public static final class ProjectClosedException extends RuntimeException {

		private static final long serialVersionUID = -5291861390444875055L;

		public ProjectClosedException(String projectName) {
			super(projectName);
		}

	}

	private final ILaunchConfiguration launchConfig;

	public LaunchConfigurationWrapper(ILaunchConfiguration launchConfig) {
		this.launchConfig = launchConfig;
	}

	public IJavaProject getProject() throws CoreException {
		return getProject(launchConfig.getAttribute(ATTR_PROJECT_NAME, ""));
	}

	private IJavaProject getProject(String projectName) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject project : root.getProjects()) {
			if (projectName.equals(project.getName())) {
				if (project.isOpen()) {
					return JavaCore.create(project);
				} else {
					throw new ProjectClosedException(projectName);
				}
			}
		}
		throw new ProjectNotFoundException(projectName);
	}

	public List<String> getClassesFromProject() throws CoreException {
		Builder<String> classPathBuilder = ImmutableSet.builder();
		IPackageFragmentRoot[] packageRoots = getProject()
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

	public List<String> getPackagesToTest() throws CoreException {
		final Builder<String> builder = builder();
		IResource[] resources = launchConfig.getMappedResources();
		IResourceProxyVisitor visitor = new IResourceProxyVisitor() {
			public boolean visit(IResourceProxy proxy) throws CoreException {
				if (proxy.getType() == FOLDER) {
					IJavaElement element = JavaCore.create(proxy
							.requestResource());
					if (element.getElementType() == PACKAGE_FRAGMENT) {
						builder.add(element.getElementName() + ".*");
					} else if (element.getElementType() == PACKAGE_FRAGMENT_ROOT) {
						builder.addAll(getPackagesFromRoot(element
								.getHandleIdentifier()));
					}
				} else if (proxy.getType() == PROJECT) {
					IJavaProject project = getProject();
					IPackageFragmentRoot[] packageRoots = project
							.getAllPackageFragmentRoots();
					for (IPackageFragmentRoot packageRoot : packageRoots) {
						if (!packageRoot.isArchive()) {
							builder.addAll(getPackagesFromRoot(packageRoot
									.getHandleIdentifier()));
						}
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

	private Set<String> getPackagesFromRoot(String handleId)
			throws CoreException {
		Builder<String> builder = builder();
		IPackageFragmentRoot[] roots = getProject().getPackageFragmentRoots();
		for (IPackageFragmentRoot root : roots) {
			if (handleId.equals(root.getHandleIdentifier())) {
				IJavaElement[] elements = root.getChildren();
				for (IJavaElement element : elements) {
					if (element.getElementType() == PACKAGE_FRAGMENT) {
						builder.add(element.getElementName() + ".*");
					}
				}
			}
		}
		return builder.build();
	}

	public IType getTestClass() throws CoreException {
		String testClass = getMainTypeName(launchConfig);
		if (testClass.length() > 0) {
			IJavaProject javaProject = getProject();
			IType type = javaProject.findType(testClass);
			if (type != null && type.exists()) {
				return type;
			}
		}
		throw new TestClassNotFoundException(launchConfig.getName());
	}

	private String getMainTypeName(ILaunchConfiguration configuration)
			throws CoreException {
		return configuration.getAttribute(ATTR_MAIN_TYPE_NAME, "");
	}

	public List<File> getSourceDirsForProject(IJavaProject javaProject)
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
		return copyOf(sourceDirBuilder.build());
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

	public boolean isTestLaunch() throws CoreException {
		return !launchConfig.getAttribute(ATTR_MAIN_TYPE_NAME, "").trim()
				.isEmpty();
	}

	public PitOptions getPitOptions() throws CoreException {
		IJavaProject project = getProject();
		List<String> classPath = getClassesFromProject();
		List<File> sourceDirs = getSourceDirsForProject(project);
		File reportDir = getDefault().emptyResultDir();
		if (isTestLaunch()) {
			IType testClass = getTestClass();
			return new PitOptionsBuilder()
					.withClassUnderTest(testClass.getFullyQualifiedName())
					.withClassesToMutate(classPath)
					.withSourceDirectories(sourceDirs)
					.withReportDirectory(reportDir).build();
		} else {
			List<String> packages = getPackagesToTest();
			return new PitOptionsBuilder().withPackagesToTest(packages)
					.withClassesToMutate(classPath)
					.withSourceDirectories(sourceDirs)
					.withReportDirectory(reportDir).build();
		}
	}
}
