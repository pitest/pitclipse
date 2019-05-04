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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableList.copyOf;
import static org.pitest.pitclipse.launch.config.ProjectUtils.getOpenJavaProjects;
import static org.pitest.pitclipse.launch.config.ProjectUtils.onClassPathOf;
import static org.pitest.pitclipse.launch.config.ProjectUtils.sameProject;

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
        URI location = getProjectLocation(project.getProject());
        IPackageFragmentRoot[] packageRoots = project.getPackageFragmentRoots();

        File projectRoot = new File(location);
        for (IPackageFragmentRoot packageRoot : packageRoots) {
            if (!packageRoot.isArchive()) {
                IPath packagePath = packageRoot.getPath();

                boolean pathIsRelativeToWorkspace = ! (packagePath.isAbsolute() && packageRoot.isExternal());
                if (pathIsRelativeToWorkspace) {
                    packagePath = removeProjectFromPackagePath(project, packageRoot.getPath());
                    sourceDirBuilder.add(new File(projectRoot, packagePath.toString()));
                }
                else {
                    // FIXME Commenting out this line leads UI tests to fail during Maven build
                    // because projects seem to have an additional source directory (which they
                    // do not have when tests are run by Eclipse IDE).
                    // It seems however that ignoring this case may cause issues in the future.
//                    sourceDirBuilder.add(packagePath.toFile());
                }
            }
        }
        return sourceDirBuilder.build();
    }

    private URI getProjectLocation(IProject project) throws CoreException {
        URI locationUri = project.getDescription().getLocationURI();
        if (null != locationUri) {
            return locationUri;
        }
        // We're using the default location under workspace
        File projLocation = new File(project.getLocation().toOSString());
        return projLocation.toURI();
    }

    private IPath removeProjectFromPackagePath(IJavaProject javaProject, IPath packagePath) {
        return packagePath.removeFirstSegments(1);
    }
}
