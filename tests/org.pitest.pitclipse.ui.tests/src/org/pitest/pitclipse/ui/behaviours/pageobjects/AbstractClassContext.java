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

public class AbstractClassContext implements ClassContext {

    protected final String className;
    protected final String packageName;
    protected final String projectName;
    private final String sourceDir;

    protected AbstractClassContext(String className, String packageName,
            String projectName, String sourceDir) {
        this.className = className;
        this.packageName = packageName;
        this.projectName = projectName;
        this.sourceDir = sourceDir;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getFullyQualifiedTestClassName() {
        boolean isWithinDefaultPackage = packageName.isEmpty();
        if (isWithinDefaultPackage) {
            return className;
        }
        return packageName + "." + className;
    }

    public String getSourceDir() {
        return sourceDir;
    }

}
