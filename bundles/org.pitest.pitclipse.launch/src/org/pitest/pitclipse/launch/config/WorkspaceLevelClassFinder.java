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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet.Builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableSet.builder;
import static org.pitest.pitclipse.launch.config.ProjectLevelClassFinder.getClassesFromProject;

public class WorkspaceLevelClassFinder implements ClassFinder {

    @Override
    public List<String> getClasses(LaunchConfigurationWrapper configurationWrapper) throws CoreException {
        Builder<String> classPathBuilder = builder();
        List<IJavaProject> projects = getOpenJavaProjects();
        IJavaProject testProject = configurationWrapper.getProject();
        for (IJavaProject project : projects) {
            if (sameProject(testProject, project) || onClassPathOf(testProject, project)) {
                classPathBuilder.addAll(getClassesFromProject(project));
            }
        }
        return copyOf(classPathBuilder.build());
    }

    private boolean onClassPathOf(IJavaProject testProject, IJavaProject project) {
        return testProject.isOnClasspath(project);
    }

    private boolean sameProject(IJavaProject testProject, IJavaProject project) {
        return testProject.getElementName().equals(project.getElementName());
    }

    private List<IJavaProject> getOpenJavaProjects() throws CoreException {
        ImmutableList.Builder<IJavaProject> resultBuilder = ImmutableList.builder();
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (IProject project : root.getProjects()) {
            if (project.isOpen() && project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
                resultBuilder.add(JavaCore.create(project));
            }
        }
        return resultBuilder.build();
    }

}
