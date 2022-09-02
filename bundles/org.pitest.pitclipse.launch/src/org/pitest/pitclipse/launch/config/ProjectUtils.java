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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class ProjectUtils {

    private ProjectUtils() {
    }

    public static List<IJavaProject> getOpenJavaProjects() throws CoreException {
        List<IJavaProject> resultBuilder = new ArrayList<>();
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (IProject project : root.getProjects()) {
            if (project.isOpen() && project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
                resultBuilder.add(JavaCore.create(project));
            }
        }
        return resultBuilder;
    }

    public static boolean onClassPathOf(IJavaProject testProject, IJavaProject project) {
        return testProject.isOnClasspath(project);
    }

    public static boolean onClassPathOf(IJavaProject project, String fullyQualifiedName) throws CoreException {
        return project.findType(fullyQualifiedName) != null;
    }

    public static boolean sameProject(IJavaProject testProject, IJavaProject project) {
        return testProject.getElementName().equals(project.getElementName());
    }

}
