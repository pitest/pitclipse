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
import java.util.ArrayList;
import java.util.List;

import org.pitest.pitclipse.runner.PitOptions.PitLaunchException;
import org.pitest.pitclipse.runner.results.Mutations;
import org.pitest.pitclipse.runner.results.ObjectFactory;

/**
 * Results produced by PIT during an analyze.
 */
public final class PitResults implements Serializable {

    private static final long serialVersionUID = -3422757345245132100L;

    private final File htmlResultFile;

    private final Mutations mutations;

    private final List<String> projects;

    private PitResults(File htmlResultFile, Mutations mutations,
            List<String> projects) {
        this.htmlResultFile = htmlResultFile;
        this.mutations = mutations;
        this.projects = projects;
    }

    public File getHtmlResultFile() {
        return htmlResultFile;
    }

    public static final class Builder {

        private File htmlResultFile = null;
        private List<String> projects = new ArrayList<>();
        private Mutations mutations = new ObjectFactory().createMutations();

        private Builder() {
        }

        public PitResults build() {
            return new PitResults(htmlResultFile, mutations, projects);
        }

        /**
         * In recent versions of PIT the HTML file is not created
         * if no mutants are generated, so we must check whether
         * the passed file itself is null or not.
         * 
         * @param htmlResultFile
         * @return
         */
        public Builder withHtmlResults(File htmlResultFile) {
            if (htmlResultFile != null) {
                checkFileExists(htmlResultFile);
                this.htmlResultFile = new File(htmlResultFile.getPath());
            }
            return this;
        }

        private void checkFileExists(File file) {
            if (!file.exists()) {
                throw new PitLaunchException("File does not exist: " + file);
            }
        }

        public Builder withProjects(List<String> projects) {
            this.projects = new ArrayList<>(projects);
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

    public List<String> getProjects() {
        return projects;
    }

}
