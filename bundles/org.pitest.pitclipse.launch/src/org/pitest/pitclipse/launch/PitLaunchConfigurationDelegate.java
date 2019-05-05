/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.pitest.pitclipse.runner.config.PitConfiguration;
import org.pitest.pitclipse.runner.config.PitExecutionMode;

import static org.pitest.pitclipse.core.PitCoreActivator.getDefault;

/**
 * Launches PIT, delegating the actual behavior to a {@link PitLaunchVisitor}. 
 */
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
