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
import org.pitest.pitclipse.runner.config.PitConfiguration;
import org.pitest.pitclipse.runner.config.PitExecutionModeVisitor;

import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;

public class PitLaunchVisitor implements PitExecutionModeVisitor<Void> {

    private final ILaunchConfiguration configuration;
    private final ILaunch launch;
    private final IProgressMonitor monitor;
    private final PitConfiguration pitConfiguration;

    public PitLaunchVisitor(PitConfiguration pitConfiguration,
            ILaunchConfiguration launchConfig, ILaunch launch,
            IProgressMonitor monitor) {
        this.pitConfiguration = pitConfiguration;
        configuration = launchConfig;
        this.launch = launch;
        this.monitor = monitor;
    }

    public Void visitProjectLevelConfiguration() {
        try {
            new ProjectLevelLaunchDelegate(pitConfiguration).launch(
                    configuration, RUN_MODE, launch, monitor);
        } catch (CoreException e) {
            throw new LaunchFailedException(configuration.getName());
        }
        return null;
    }

    public Void visitWorkspaceLevelConfiguration() {
        try {
            new WorkspaceLevelLaunchDelegate(pitConfiguration).launch(
                    configuration, RUN_MODE, launch, monitor);
        } catch (CoreException e) {
            throw new LaunchFailedException(configuration.getName());
        }
        return null;
    }

    public static class LaunchFailedException extends RuntimeException {
        private static final long serialVersionUID = -1678956151196198597L;

        public LaunchFailedException(String name) {
            super(name);
        }
    }
}
