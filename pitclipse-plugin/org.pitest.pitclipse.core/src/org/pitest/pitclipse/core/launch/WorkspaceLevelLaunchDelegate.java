package org.pitest.pitclipse.core.launch;

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
import org.pitest.pitclipse.core.PitConfiguration;
import org.pitest.pitclipse.core.launch.config.LaunchConfigurationWrapper;
import org.pitest.pitclipse.core.launch.config.PackageFinder;
import org.pitest.pitclipse.core.launch.config.WorkspaceLevelClassFinder;
import org.pitest.pitclipse.pitrunner.PitOptions;
import org.pitest.pitclipse.pitrunner.PitRunner;
import org.pitest.pitclipse.pitrunner.io.SocketProvider;

import com.google.common.collect.ImmutableList;

public class WorkspaceLevelLaunchDelegate extends JavaLaunchDelegate {

	private static final String PIT_RUNNER = PitRunner.class.getCanonicalName();

	private static final ExecutorService executorService = Executors
			.newSingleThreadExecutor();

	private int portNumber;

	private final PitConfiguration pitConfiguration;

	public WorkspaceLevelLaunchDelegate(PitConfiguration pitConfiguration) {
		this.pitConfiguration = pitConfiguration;
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		generatePortNumber();
		LaunchConfigurationWrapper configWrapper = new LaunchConfigurationWrapper(
				configuration, new PackageFinder(),
				new WorkspaceLevelClassFinder(),
				new WorkspaceLevelSourceDirFinder(), pitConfiguration);

		PitOptions options = configWrapper.getPitOptions();

		super.launch(configuration, mode, launch, monitor);

		executorService.execute(new PitCommunicator(portNumber, options));
	}

	private void generatePortNumber() {
		portNumber = new SocketProvider().getFreePort();
	}

	@Override
	public String getMainTypeName(ILaunchConfiguration launchConfig)
			throws CoreException {
		return PIT_RUNNER;
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

	@Override
	public String getProgramArguments(ILaunchConfiguration launchConfig)
			throws CoreException {
		return new StringBuilder(super.getProgramArguments(launchConfig))
				.append(' ').append(portNumber).toString();
	}

}
