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

/**
 * <p>Options used by a running PIT application.</p>
 * 
 * <p>An instance of this class is <strong>immutable</strong> and, once built,
 * is inherently <strong>thread-safe</strong>.</p>
 */
public class PitRuntimeOptions {

    private final int portNumber;
    private final PitOptions options;
    private final ImmutableList<String> projects;

    /**
     * Creates a new object representing the options used by a running PIT application.
     * 
     * @param portNumber
     *          The port used by PIT to send its results.
     * @param options
     *          The options given to PIT to parameterize its analyze.
     * @param projects
     *          The projects analyzed by PIT.
     */
    public PitRuntimeOptions(int portNumber, PitOptions options, List<String> projects) {
        this.portNumber = portNumber;
        this.options = options;
        this.projects = ImmutableList.copyOf(projects);
    }

    /**
     * Returns the port used by PIT to send its results.
     * @return the port used by PIT to send its results
     */
    public int getPortNumber() {
        return portNumber;
    }

    /**
     * <p>Returns the options that have been given to PIT.</p>
     * <p>These options parameterize PIT analyze.</p>
     * 
     * @return the options that have been given to PIT
     */
    public PitOptions getOptions() {
        return options;
    }

    /**
     * Returns the name of the projects analyzed by PIT.
     * @return the name of the projects analyzed by PIT
     */
    public List<String> getMutatedProjects() {
        return projects;
    }
}
