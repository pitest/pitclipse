package org.pitest.pitclipse.core.launch.config;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.pitest.pitclipse.core.PitCoreActivator.getDefault;
import static org.pitest.pitclipse.core.launch.PitclipseConstants.ATTR_EXCLUDE_CLASSES;
import static org.pitest.pitclipse.core.launch.PitclipseConstants.ATTR_TEST_INCREMENTALLY;
import static org.pitest.pitclipse.core.launch.PitclipseConstants.ATTR_TEST_IN_PARALLEL;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.pitest.pitclipse.core.PitConfiguration;
import org.pitest.pitclipse.core.launch.ProjectClosedException;
import org.pitest.pitclipse.core.launch.ProjectNotFoundException;
import org.pitest.pitclipse.core.launch.TestClassNotFoundException;
import org.pitest.pitclipse.pitrunner.PitOptions;
import org.pitest.pitclipse.pitrunner.PitOptions.PitOptionsBuilder;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class LaunchConfigurationWrapper {

	private final ILaunchConfiguration launchConfig;
	private final PackageFinder packageFinder;
	private final ClassFinder classFinder;
	private final SourceDirFinder sourceDirFinder;
	private final PitConfiguration pitConfiguration;

	public LaunchConfigurationWrapper(ILaunchConfiguration launchConfig,
			PackageFinder packageFinder, ClassFinder classFinder,
			SourceDirFinder sourceDirFinder, PitConfiguration pitConfiguration) {
		this.launchConfig = launchConfig;
		this.packageFinder = packageFinder;
		this.classFinder = classFinder;
		this.sourceDirFinder = sourceDirFinder;
		this.pitConfiguration = pitConfiguration;
	}

	protected ILaunchConfiguration getLaunchConfig() {
		return launchConfig;
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

	public IType getTestClass() throws CoreException {
		String testClass = getMainTypeName(getLaunchConfig());
		if (testClass.length() > 0) {
			IJavaProject javaProject = getProject();
			IType type = javaProject.findType(testClass);
			if (type != null && type.exists()) {
				return type;
			}
		}
		throw new TestClassNotFoundException(getLaunchConfig().getName());
	}

	private String getMainTypeName(ILaunchConfiguration configuration)
			throws CoreException {
		return configuration.getAttribute(ATTR_MAIN_TYPE_NAME, "");
	}

	public boolean isTestLaunch() throws CoreException {
		return !getLaunchConfig().getAttribute(ATTR_MAIN_TYPE_NAME, "").trim()
				.isEmpty();
	}

	public PitOptions getPitOptions() throws CoreException {
		List<String> classPath = getClassesFromProject();
		List<File> sourceDirs = getSourceDirsForProject();
		int threadCount = getThreadCount();
		File reportDir = getDefault().emptyResultDir();
		List<String> excludedClasses = getExcludedClasses();

		PitOptionsBuilder builder = new PitOptionsBuilder()
				.withClassesToMutate(classPath)
				.withSourceDirectories(sourceDirs)
				.withReportDirectory(reportDir).withThreads(threadCount)
				.withExcludedClasses(excludedClasses);
		if (isIncrementalAnalysis()) {
			builder.withHistoryLocation(getDefault().getHistoryFile());
		}
		if (isTestLaunch()) {
			IType testClass = getTestClass();
			builder.withClassUnderTest(testClass.getFullyQualifiedName());
		} else {
			List<String> packages = getPackagesToTest();
			builder.withPackagesToTest(packages);
		}
		return builder.build();
	}

	private List<String> getExcludedClasses() throws CoreException {
		Builder<String> results = ImmutableList.builder();
		String excludedClasses;
		if (launchConfig.hasAttribute(ATTR_EXCLUDE_CLASSES)) {
			excludedClasses = launchConfig.getAttribute(ATTR_EXCLUDE_CLASSES,
					"");
		} else {
			excludedClasses = pitConfiguration.getExcludedClasses();
		}
		results.addAll(Splitter.on(',').trimResults().split(excludedClasses));
		return results.build();
	}

	private boolean isIncrementalAnalysis() throws CoreException {
		return launchConfig.getAttribute(ATTR_TEST_INCREMENTALLY, false)
				|| pitConfiguration.isIncrementalAnalysis();
	}

	private int getThreadCount() throws CoreException {
		return launchConfig.getAttribute(ATTR_TEST_IN_PARALLEL, false)
				|| pitConfiguration.isParallelExecution() ? Runtime
				.getRuntime().availableProcessors() : 1;
	}

	private List<File> getSourceDirsForProject() throws CoreException {
		return sourceDirFinder.getSourceDirs(this);
	}

	private List<String> getPackagesToTest() throws CoreException {
		return packageFinder.getPackages(this);
	}

	private List<String> getClassesFromProject() throws CoreException {
		return classFinder.getClasses(this);
	}

	public IResource[] getMappedResources() throws CoreException {
		return launchConfig.getMappedResources();
	}

}