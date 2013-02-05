package org.pitest.pitclipse.core.launch;

import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.pitest.pitclipse.core.PitConfiguration;
import org.pitest.pitclipse.core.PitConfigurationVisitor;

public class PitLaunchVisitor implements PitConfigurationVisitor {

	public class LaunchFailedException extends RuntimeException {
		private static final long serialVersionUID = -1678956151196198597L;

		public LaunchFailedException(String name) {
			super(name);
		}
	}

	private final ILaunchConfiguration configuration;
	private final ILaunch launch;
	private final IProgressMonitor monitor;

	public PitLaunchVisitor(ILaunchConfiguration launchConfig, ILaunch launch,
			IProgressMonitor monitor) {
		configuration = launchConfig;
		this.launch = launch;
		this.monitor = monitor;
	}

	public void visitProjectLevelConfiguration(PitConfiguration pitConfiguration) {
		try {
			new ProjectLevelLaunchDelegate(pitConfiguration).launch(
					configuration, RUN_MODE, launch, monitor);
		} catch (CoreException e) {
			throw new LaunchFailedException(configuration.getName());
		}
	}

	public void visitWorkspaceLevelConfiguration(
			PitConfiguration pitConfiguration) {
		try {
			new WorkspaceLevelLaunchDelegate(pitConfiguration).launch(
					configuration, RUN_MODE, launch, monitor);
		} catch (CoreException e) {
			throw new LaunchFailedException(configuration.getName());
		}
	}

}