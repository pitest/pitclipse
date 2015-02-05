package org.pitest.pitclipse.core.launch.config;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.pitest.pitclipse.core.PitCoreActivator.getDefault;

import java.io.File;
import java.math.BigDecimal;
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
import org.pitest.pitclipse.core.launch.ProjectClosedException;
import org.pitest.pitclipse.core.launch.ProjectNotFoundException;
import org.pitest.pitclipse.core.launch.TestClassNotFoundException;
import org.pitest.pitclipse.pitrunner.PitOptions;
import org.pitest.pitclipse.pitrunner.PitOptions.PitOptionsBuilder;
import org.pitest.pitclipse.pitrunner.config.PitConfiguration;
import org.pitest.pitclipse.reloc.guava.base.Splitter;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

public class LaunchConfigurationWrapper {

	private final ILaunchConfiguration launchConfig;
	private final PackageFinder packageFinder;
	private final ClassFinder classFinder;
	private final SourceDirFinder sourceDirFinder;
	private final PitConfiguration pitConfiguration;
	public static final String ATTR_TEST_INCREMENTALLY = "org.pitest.pitclipse.core.test.incrementalAnalysis";
	public static final String ATTR_TEST_IN_PARALLEL = "org.pitest.pitclipse.core.test.parallel";
	public static final String ATTR_EXCLUDE_CLASSES = "org.pitest.pitclipse.core.test.excludeClasses";
	public static final String ATTR_EXCLUDE_METHODS = "org.pitest.pitclipse.core.test.excludeMethods";
	public static final String ATTR_AVOID_CALLS_TO = "org.pitest.pitclipse.core.test.avoidCallsTo";
	private final ProjectFinder projectFinder;

	private LaunchConfigurationWrapper(ILaunchConfiguration launchConfig, ProjectFinder projectFinder,
			SourceDirFinder sourceDirFinder, PackageFinder packageFinder, ClassFinder classFinder,
			PitConfiguration pitConfiguration) {
		this.launchConfig = launchConfig;
		this.projectFinder = projectFinder;
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

	private String getMainTypeName(ILaunchConfiguration configuration) throws CoreException {
		return configuration.getAttribute(ATTR_MAIN_TYPE_NAME, "");
	}

	public boolean isTestLaunch() throws CoreException {
		return !getLaunchConfig().getAttribute(ATTR_MAIN_TYPE_NAME, "").trim().isEmpty();
	}

	public PitOptions getPitOptions() throws CoreException {
		List<String> classPath = getClassesFromProject();
		List<File> sourceDirs = getSourceDirsForProject();
		int threadCount = getThreadCount();
		File reportDir = getDefault().emptyResultDir();
		List<String> excludedClasses = getExcludedClasses();
		List<String> excludedMethods = getExcludedMethods();
		List<String> avoidCallsTo = getAvoidCallsTo();
		List<String> mutators = getMutators();
		int timeout = pitConfiguration.getTimeout();
		BigDecimal timeoutFactor = pitConfiguration.getTimeoutFactor();

		PitOptionsBuilder builder = PitOptions.builder().withClassesToMutate(classPath)
				.withSourceDirectories(sourceDirs).withReportDirectory(reportDir).withThreads(threadCount)
				.withExcludedClasses(excludedClasses).withExcludedMethods(excludedMethods)
				.withAvoidCallsTo(avoidCallsTo).withMutators(mutators).withTimeout(timeout)
				.withTimeoutFactor(timeoutFactor);
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

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private ILaunchConfiguration launchConfig;
		private PackageFinder packageFinder;
		private ClassFinder classFinder;
		private SourceDirFinder sourceDirFinder;
		private PitConfiguration pitConfiguration;
		private ProjectFinder projectFinder;

		private Builder() {
		}

		public LaunchConfigurationWrapper build() {
			return new LaunchConfigurationWrapper(launchConfig, projectFinder, sourceDirFinder, packageFinder,
					classFinder, pitConfiguration);
		}

		public Builder withLaunchConfiguration(ILaunchConfiguration configuration) {
			this.launchConfig = configuration;
			return this;
		}

		public Builder withProjectFinder(ProjectFinder projectFinder) {
			this.projectFinder = projectFinder;
			return this;
		}

		public Builder withPackageFinder(PackageFinder packageFinder) {
			this.packageFinder = packageFinder;
			return this;
		}

		public Builder withClassFinder(ClassFinder classFinder) {
			this.classFinder = classFinder;
			return this;
		}

		public Builder withSourceDirFinder(SourceDirFinder sourceDirFinder) {
			this.sourceDirFinder = sourceDirFinder;
			return this;
		}

		public Builder withPitConfiguration(PitConfiguration pitConfiguration) {
			this.pitConfiguration = pitConfiguration;
			return this;
		}
	}

	private List<String> getExcludedMethods() throws CoreException {
		ImmutableList.Builder<String> results = ImmutableList.builder();
		String excludedMethods;
		if (launchConfig.hasAttribute(ATTR_EXCLUDE_METHODS)) {
			excludedMethods = launchConfig.getAttribute(ATTR_EXCLUDE_METHODS, "");
		} else {
			excludedMethods = pitConfiguration.getExcludedMethods();
		}
		results.addAll(Splitter.on(',').trimResults().omitEmptyStrings().split(excludedMethods));
		return results.build();
	}

	private List<String> getAvoidCallsTo() throws CoreException {
		ImmutableList.Builder<String> results = ImmutableList.builder();
		String avoidCallsTo;
		if (launchConfig.hasAttribute(ATTR_AVOID_CALLS_TO)) {
			avoidCallsTo = launchConfig.getAttribute(ATTR_AVOID_CALLS_TO, "");
		} else {
			avoidCallsTo = pitConfiguration.getAvoidCallsTo();
		}
		results.addAll(Splitter.on(',').trimResults().omitEmptyStrings().split(avoidCallsTo));
		return results.build();
	}

	private List<String> getExcludedClasses() throws CoreException {
		ImmutableList.Builder<String> results = ImmutableList.builder();
		String excludedClasses;
		if (launchConfig.hasAttribute(ATTR_EXCLUDE_CLASSES)) {
			excludedClasses = launchConfig.getAttribute(ATTR_EXCLUDE_CLASSES, "");
		} else {
			excludedClasses = pitConfiguration.getExcludedClasses();
		}
		results.addAll(Splitter.on(',').trimResults().omitEmptyStrings().split(excludedClasses));
		return results.build();
	}

	private boolean isIncrementalAnalysis() throws CoreException {
		return launchConfig.getAttribute(ATTR_TEST_INCREMENTALLY, false) || pitConfiguration.isIncrementalAnalysis();
	}

	private int getThreadCount() throws CoreException {
		return launchConfig.getAttribute(ATTR_TEST_IN_PARALLEL, false) || pitConfiguration.isParallelExecution() ? Runtime
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

	public List<String> getMutatedProjects() throws CoreException {
		return projectFinder.getProjects(this);
	}

	private List<String> getMutators() {
		return ImmutableList.of(pitConfiguration.getMutators());
	}
}