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

package org.pitest.pitclipse.runner;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * <p>Parameters of a PIT analysis.</p>
 * 
 * <p>Supposed to be sent to a {@link PitRunner} through network.</p> 
 */
public class PitRequest implements Serializable {
    private static final long serialVersionUID = 2058881520214195050L;
    private final PitOptions options;
    private final ImmutableList<String> projects;

    private PitRequest(PitOptions options, ImmutableList<String> projects) {
        this.options = options;
        this.projects = projects;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private PitOptions options;
        private ImmutableList<String> projects = ImmutableList.of();

        private Builder() {
        }

        public PitRequest build() {
            return new PitRequest(options, projects);
        }

        public Builder withPitOptions(PitOptions options) {
            this.options = options;
            return this;
        }

        public Builder withProjects(List<String> projects) {
            this.projects = ImmutableList.copyOf(projects);
            return this;
        }
    }

    public File getReportDirectory() {
        return options.getReportDirectory();
    }

    public PitOptions getOptions() {
        return options;
    }

    public List<String> getProjects() {
        return projects;
    }

    @Override
    public String toString() {
        return "PitRequest [options=" + options + ", projects=" + projects + "]";
    }
}
