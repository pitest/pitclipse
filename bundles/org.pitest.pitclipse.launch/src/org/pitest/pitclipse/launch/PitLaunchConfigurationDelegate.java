package org.pitest.pitclipse.core.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.pitest.pitclipse.pitrunner.config.PitConfiguration;
import org.pitest.pitclipse.pitrunner.config.PitExecutionMode;

import static org.pitest.pitclipse.core.PitCoreActivator.getDefault;

public class PitLaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate {

    @Override
    public void launch(ILaunchConfiguration launchConfig, String mode, ILaunch launch, IProgressMonitor progress)
            throws CoreException {

        pluginExecutionMode().accept(new PitLaunchVisitor(pluginConfiguration(), launchConfig, launch, progress));
    }

    private PitExecutionMode pluginExecutionMode() {
        return pluginConfiguration().getExecutionMode();
    }

    private PitConfiguration pluginConfiguration() {
        return getDefault().getConfiguration();
    }
}
