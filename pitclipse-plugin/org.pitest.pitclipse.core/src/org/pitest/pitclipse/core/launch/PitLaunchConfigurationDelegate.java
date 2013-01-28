package org.pitest.pitclipse.core.launch;

import static org.pitest.pitclipse.core.PitCoreActivator.getDefault;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;

public class PitLaunchConfigurationDelegate extends
		AbstractJavaLaunchConfigurationDelegate {

	public void launch(ILaunchConfiguration launchConfig, String mode,
			ILaunch launch, IProgressMonitor progress) throws CoreException {
		getDefault().getConfiguration().accept(
				new PitLaunchVisitor(launchConfig, launch, progress));
	}

}
