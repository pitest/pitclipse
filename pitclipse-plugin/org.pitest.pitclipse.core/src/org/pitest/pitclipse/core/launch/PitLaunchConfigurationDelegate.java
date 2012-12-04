package org.pitest.pitclipse.core.launch;

import static org.pitest.pitclipse.core.PitCoreActivator.getDefault;
import static org.pitest.pitclipse.core.PitCoreActivator.log;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.pitest.mutationtest.MutationCoverageReport;
import org.pitest.pitclipse.core.extension.handler.ExtensionPointHandler;
import org.pitest.pitclipse.core.extension.point.PitCoreResults;
import org.pitest.pitclipse.pitrunner.PITOptions;
import org.pitest.pitclipse.pitrunner.PITOptions.PITOptionsBuilder;

import com.google.common.collect.ImmutableList;

public class PitLaunchConfigurationDelegate extends JavaLaunchDelegate {

	private static final String EXTENSION_POINT_ID = "org.pitest.pitclipse.core.results";

	private static final String PIT_REPORT_GENERATOR = MutationCoverageReport.class
			.getCanonicalName();

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
		LaunchConfigurationWrapper configWrapper = new LaunchConfigurationWrapper(
				launchConfig);
		IJavaProject project = configWrapper.getProject();
		List<String> classPath = configWrapper.getClassesFromProject();
		List<File> sourceDirs = configWrapper.getSourceDirsForProject(project);
		File reportDir = getDefault().emptyResultDir();
		if (configWrapper.isTestLaunch(launchConfig)) {
			IType testClass = configWrapper.getTestClass();
			options = new PITOptionsBuilder()
					.withClassUnderTest(testClass.getFullyQualifiedName())
					.withClassesToMutate(classPath)
					.withSourceDirectories(sourceDirs)
					.withReportDirectory(reportDir).build();
		} else {
			List<String> packages = configWrapper.getPackagesToTest();
			options = new PITOptionsBuilder().withPackagesToTest(packages)
					.withClassesToMutate(classPath)
					.withSourceDirectories(sourceDirs)
					.withReportDirectory(reportDir).build();
		}

		super.launch(launchConfig, mode, launch, progress);
		ProcessPoller updater = new ProcessPoller(ImmutableList.copyOf(launch
				.getProcesses()), new UpdateExtensions(
				options.getReportDirectory()));
		executorService.execute(updater);
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
				.addAll(getDefault().getPitClasspath()).build();
		log("Classpath: " + newClasspath);
		return newClasspath.toArray(new String[newClasspath.size()]);
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
