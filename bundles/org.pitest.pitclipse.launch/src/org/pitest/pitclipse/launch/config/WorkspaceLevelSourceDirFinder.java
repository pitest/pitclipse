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

import static com.google.common.collect.ImmutableList.copyOf;
import static org.pitest.pitclipse.launch.config.ProjectUtils.getOpenJavaProjects;
import static org.pitest.pitclipse.launch.config.ProjectUtils.onClassPathOf;
import static org.pitest.pitclipse.launch.config.ProjectUtils.sameProject;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class WorkspaceLevelSourceDirFinder implements SourceDirFinder {

    @Override
    public List<File> getSourceDirs(LaunchConfigurationWrapper configurationWrapper) throws CoreException {
        Builder<File> sourceDirBuilder = ImmutableSet.builder();
        IJavaProject testProject = configurationWrapper.getProject();
        List<IJavaProject> projects = getOpenJavaProjects();
        for (IJavaProject project : projects) {
            if (sameProject(testProject, project) || onClassPathOf(testProject, project)) {
                sourceDirBuilder.addAll(getSourceDirsFromProject(project));
            }
        }
        return copyOf(sourceDirBuilder.build());
    }

    private Set<File> getSourceDirsFromProject(IJavaProject project) throws CoreException {
        Builder<File> sourceDirBuilder = ImmutableSet.builder();
        IPackageFragmentRoot[] packageRoots = project.getPackageFragmentRoots();

        for (IPackageFragmentRoot packageRoot : packageRoots) {
            if (!packageRoot.isArchive()) {
                IResource resource = packageRoot.getResource();
                // the resource could be null for ExternalPackageFragmentRoot
                // and it's meant to contain only .class files
                // so it's useless to add it anyway
                if (resource != null) {
                    String locationString = resource.getLocation().toOSString();
                    File sourceDirectory = new File(locationString);
                    // a project can link to a non-existent directory
                    if (sourceDirectory.exists())
                        sourceDirBuilder.add(sourceDirectory);
                }
            }
        }
        return sourceDirBuilder.build();
    }

}
