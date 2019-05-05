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

import org.pitest.pitclipse.launch.config.ClassFinder;
import org.pitest.pitclipse.launch.config.PackageFinder;
import org.pitest.pitclipse.launch.config.ProjectFinder;
import org.pitest.pitclipse.launch.config.SourceDirFinder;
import org.pitest.pitclipse.launch.config.WorkspaceLevelClassFinder;
import org.pitest.pitclipse.launch.config.WorkspaceLevelProjectFinder;
import org.pitest.pitclipse.launch.config.WorkspaceLevelSourceDirFinder;
import org.pitest.pitclipse.runner.config.PitConfiguration;

/**
 * Launches a PIT analyze on all workspace's projects. 
 */
public class WorkspaceLevelLaunchDelegate extends AbstractPitLaunchDelegate {

    public WorkspaceLevelLaunchDelegate(PitConfiguration pitConfiguration) {
        super(pitConfiguration);
    }

    @Override
    protected SourceDirFinder getSourceDirFinder() {
        return new WorkspaceLevelSourceDirFinder();
    }

    @Override
    protected ClassFinder getClassFinder() {
        return new WorkspaceLevelClassFinder();
    }

    @Override
    protected PackageFinder getPackageFinder() {
        return new PackageFinder();
    }

    @Override
    protected ProjectFinder getProjectFinder() {
        return new WorkspaceLevelProjectFinder();
    }

}
