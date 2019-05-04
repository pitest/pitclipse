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

package org.pitest.pitclipse.core.extension.point;

import com.google.common.collect.ImmutableList;

import org.pitest.pitclipse.runner.PitOptions;

import java.util.List;

public class PitRuntimeOptions {

    private final int portNumber;
    private final PitOptions options;
    private final ImmutableList<String> projects;

    public PitRuntimeOptions(int portNumber, PitOptions options, List<String> projects) {
        this.portNumber = portNumber;
        this.options = options;
        this.projects = ImmutableList.copyOf(projects);
    }

    public int getPortNumber() {
        return portNumber;
    }

    public PitOptions getOptions() {
        return options;
    }

    public List<String> getMutatedProjects() {
        return projects;
    }
}
