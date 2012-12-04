package org.pitest.pitclipse.core.launch;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.pitest.pitclipse.core.PitConfigurationVisitor;

public class PitLaunchVisitor implements PitConfigurationVisitor {

	private final LaunchConfigurationWrapper launchConfig;
	private final ILaunch launch;
	private final IProgressMonitor progress;

	public PitLaunchVisitor(ILaunchConfiguration launchConfig, ILaunch launch,
			IProgressMonitor progress) {
		this.launchConfig = new LaunchConfigurationWrapper(launchConfig);
		this.launch = launch;
		this.progress = progress;
	}

	public void visitProjectLevelConfiguration() {

	}

	public void visitWorkspaceLevelConfiguration() {

	}

}