package org.pitest.pitclipse.core.launch;

import static com.google.common.collect.ImmutableList.copyOf;
import static org.pitest.pitclipse.core.PitCoreActivator.getDefault;
import static org.pitest.pitclipse.core.PitCoreActivator.log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.pitest.mutationtest.MutationCoverageReport;
import org.pitest.pitclipse.core.PitConfiguration;
import org.pitest.pitclipse.core.launch.config.LaunchConfigurationWrapper;
import org.pitest.pitclipse.core.launch.config.PackageFinder;
import org.pitest.pitclipse.core.launch.config.ProjectLevelClassFinder;
import org.pitest.pitclipse.core.launch.config.ProjectLevelSourceDirFinder;
import org.pitest.pitclipse.pitrunner.PitOptions;

import com.google.common.collect.ImmutableList;

public class ProjectLevelLaunchDelegate extends JavaLaunchDelegate {

	private static final String PIT_REPORT_GENERATOR = MutationCoverageReport.class
			.getCanonicalName();

	private static final ExecutorService executorService = Executors
			.newSingleThreadExecutor();

	private PitOptions options = null;

	private final PitConfiguration pitConfiguration;

	public ProjectLevelLaunchDelegate(PitConfiguration pitConfiguration) {
		this.pitConfiguration = pitConfiguration;
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		LaunchConfigurationWrapper configWrapper = new LaunchConfigurationWrapper(
				configuration, new PackageFinder(),
				new ProjectLevelClassFinder(),
				new ProjectLevelSourceDirFinder(), pitConfiguration);
		options = configWrapper.getPitOptions();
		System.out.println(options);

		super.launch(configuration, mode, launch, monitor);
		ProcessPoller updater = new ProcessPoller(
				copyOf(launch.getProcesses()), new UpdateExtensions(
						options.getReportDirectory()));
		executorService.execute(updater);
	}

	@Override
	public String getProgramArguments(ILaunchConfiguration launchConfig)
			throws CoreException {
		String pitArgs = null == options ? "" : options.toCLIArgsAsString();
		log("PIT Arguments: " + pitArgs);
		return new StringBuilder(super.getProgramArguments(launchConfig))
				.append(pitArgs).toString();
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

}
