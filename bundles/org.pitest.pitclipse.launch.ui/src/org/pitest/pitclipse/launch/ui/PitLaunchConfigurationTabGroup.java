package org.pitest.pitclipse.launch.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;

public class PitLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        final ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
                new PitArgumentsTab(),
                new JavaArgumentsTab(),
                new JavaJRETab(),
                new JavaClasspathTab(), 
                new CommonTab()
        };
        setTabs(tabs);
    }
}
