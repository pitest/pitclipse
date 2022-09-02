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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;

public class ProjectLevelSourceDirFinder implements SourceDirFinder {

    @Override
    public List<File> getSourceDirs(LaunchConfigurationWrapper configurationWrapper) throws CoreException {
        Set<File> sourceDirBuilder = new HashSet<>();
        IJavaProject javaProject = configurationWrapper.getProject();
        URI location = getProjectLocation(javaProject.getProject());
        IPackageFragmentRoot[] packageRoots = javaProject.getPackageFragmentRoots();

        File projectRoot = new File(location);
        for (IPackageFragmentRoot packageRoot : packageRoots) {
            if (isValid(packageRoot)) {
                File packagePath = removeProjectFromPackagePath(packageRoot.getPath());
                File potentialSourceRoot = new File(projectRoot, packagePath.toString());
                if (isValidSourceDir(potentialSourceRoot)) {
                    sourceDirBuilder.add(potentialSourceRoot);
                }
            }
        }
        return new ArrayList<>(sourceDirBuilder);
    }

    private boolean isValid(IPackageFragmentRoot packageRoot) {
        return !packageRoot.isArchive() && !packageRoot.isExternal();
    }

    private boolean isValidSourceDir(File sourceDir) {
        return sourceDir.exists() && sourceDir.isDirectory();
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

    private File removeProjectFromPackagePath(IPath packagePath) {
        IPath newPath = packagePath.removeFirstSegments(1);
        return newPath.toFile();
    }

}
