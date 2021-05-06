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

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.pitest.pitclipse.runner.PitOptions.PitLaunchException;
import org.pitest.pitclipse.runner.results.Mutations;
import org.pitest.pitclipse.runner.results.ObjectFactory;

import com.google.common.collect.ImmutableList;

/**
 * Results produced by PIT during an analyze.
 */
public final class PitResults implements Serializable {
    private static final long serialVersionUID = 5457147591186148047L;

    private final File htmlResultFile;

    private final Mutations mutations;

    private final ImmutableList<String> projects;

    private PitResults(File htmlResultFile, Mutations mutations,
            ImmutableList<String> projects) {
        this.htmlResultFile = htmlResultFile;
        this.mutations = mutations;
        this.projects = projects;
    }

    public File getHtmlResultFile() {
        return htmlResultFile;
    }

    public static final class Builder {

        private File htmlResultFile = null;
        private ImmutableList<String> projects = ImmutableList.of();
        private Mutations mutations = new ObjectFactory().createMutations();

        private Builder() {
        }

        public PitResults build() {
            return new PitResults(htmlResultFile, mutations, projects);
        }

        public Builder withHtmlResults(File htmlResultFile) {
            checkFileExists(htmlResultFile);
            this.htmlResultFile = new File(htmlResultFile.getPath());
            return this;
        }

        private void checkFileExists(File file) {
            if (!file.exists()) {
                throw new PitLaunchException("File does not exist: " + file);
            }
        }

        public Builder withProjects(List<String> projects) {
            this.projects = ImmutableList.copyOf(projects);
            return this;
        }

        public Builder withMutations(Mutations mutations) {
            this.mutations = mutations;
            return this;
        }
    }

    @Override
    public String toString() {
        return "PitResults [htmlResultFile=" + htmlResultFile + ", projects=" + projects + "]";
    }

    public Mutations getMutations() {
        return mutations;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ImmutableList<String> getProjects() {
        return projects;
    }

}
