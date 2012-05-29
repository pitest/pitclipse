package org.pitest.pitclipse.ui.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;

public class PITLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		final ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new PITArgumentsTab(),
				new JavaArgumentsTab(),
				new JavaJRETab(),
				new JavaClasspathTab(), 
				new CommonTab()
		};
		setTabs(tabs);
	}
}
