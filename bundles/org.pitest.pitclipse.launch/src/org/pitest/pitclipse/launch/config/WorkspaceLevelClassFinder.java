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

package org.pitest.pitclipse.launch.config;

import static org.pitest.pitclipse.launch.config.ProjectLevelClassFinder.getClassesFromProject;
import static org.pitest.pitclipse.launch.config.ProjectUtils.getOpenJavaProjects;
import static org.pitest.pitclipse.launch.config.ProjectUtils.onClassPathOf;
import static org.pitest.pitclipse.launch.config.ProjectUtils.sameProject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

public class WorkspaceLevelClassFinder implements ClassFinder {

    @Override
    public List<String> getClasses(LaunchConfigurationWrapper configurationWrapper) throws CoreException {
        Set<String> classes = new HashSet<>();
        IJavaProject testProject = configurationWrapper.getProject();
        List<IJavaProject> projects = getOpenJavaProjects();
        for (IJavaProject project : projects) {
            if (sameProject(testProject, project) || onClassPathOf(testProject, project)) {
                classes.addAll(getClassesFromProject(project));
            }
        }
        return new ArrayList<>(classes);
    }

}
