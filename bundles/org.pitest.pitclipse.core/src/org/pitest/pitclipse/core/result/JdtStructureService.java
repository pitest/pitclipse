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

package org.pitest.pitclipse.core.result;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.pitest.pitclipse.core.launch.MutatedClassNotFoundException;
import org.pitest.pitclipse.core.launch.ProjectNotFoundException;
import org.pitest.pitclipse.runner.model.ProjectStructureService;

public enum JdtStructureService implements ProjectStructureService {

    INSTANCE;

    @Override
    public String packageFrom(String project, String mutatedClass) {
        try {
            IJavaProject javaProject = javaProject(project);
            IType type = javaProject.findType(mutatedClass);
            IPackageFragment pkg = type.getPackageFragment();
            return pkg.getElementName();
        } catch (JavaModelException e) {
            throw new MutatedClassNotFoundException(mutatedClass);
        }
    }

    @Override
    public boolean isClassInProject(String mutatedClass, String projectName) {
        IJavaProject project = javaProject(projectName);
        try {
            return null != project.findType(mutatedClass);
        } catch (Exception e) {
            return false;
        }
    }

    private IJavaProject javaProject(String projectName) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (IProject project : root.getProjects()) {
            if (projectName.equals(project.getName()) && project.isOpen()) {
                return JavaCore.create(project);
            }
        }
        throw new ProjectNotFoundException(projectName);
    }
}
