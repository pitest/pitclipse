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

package org.pitest.pitclipse.ui.behaviours.pageobjects;

public class ConcreteClassContext extends AbstractClassContext {

    private final String method;

    protected ConcreteClassContext(String className, String packageName,
            String projectName, String method, String sourceDir) {
        super(className, packageName, projectName, sourceDir);
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public static class Builder {

        private String className;
        private String packageName;
        private String projectName;
        private String method;
        private String sourceDir;

        public Builder() {
        };

        public Builder withClassName(String className) {
            this.className = className;
            return this;
        }

        public Builder withPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder withProjectName(String projectName) {
            this.projectName = projectName;
            return this;
        }

        public Builder withMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder withSourceDir(String sourceDir) {
            this.sourceDir = sourceDir;
            return this;
        }

        public ConcreteClassContext build() {
            return new ConcreteClassContext(className, packageName,
                    projectName, method, sourceDir);
        }

        public Builder clone(ConcreteClassContext context) {
            className = context.getClassName();
            packageName = context.getPackageName();
            projectName = context.getProjectName();
            method = context.getMethod();
            sourceDir = context.getSourceDir();
            return this;
        }

    }

}
