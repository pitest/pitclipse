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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class WorkspaceLevelClassFinder implements ClassFinder {

    @Override
    public List<String> getClasses(LaunchConfigurationWrapper configurationWrapper) throws CoreException {
        Set<String> classPathBuilder = new HashSet<>();
        List<IJavaProject> projects = getOpenJavaProjects();
        IJavaProject testProject = configurationWrapper.getProject();
        for (IJavaProject project : projects) {
            if (sameProject(testProject, project) || onClassPathOf(testProject, project)) {
                classPathBuilder.addAll(getClassesFromProject(project));
            }
        }
        return new ArrayList<>(classPathBuilder);
    }

    private boolean onClassPathOf(IJavaProject testProject, IJavaProject project) {
        return testProject.isOnClasspath(project);
    }

    private boolean sameProject(IJavaProject testProject, IJavaProject project) {
        return testProject.getElementName().equals(project.getElementName());
    }

    private List<IJavaProject> getOpenJavaProjects() throws CoreException {
        List<IJavaProject> resultBuilder = new ArrayList<>();
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (IProject project : root.getProjects()) {
            if (project.isOpen() && project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
                resultBuilder.add(JavaCore.create(project));
            }
        }
        return resultBuilder;
    }

}
