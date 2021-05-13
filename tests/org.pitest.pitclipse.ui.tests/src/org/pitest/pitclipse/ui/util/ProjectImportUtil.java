/*******************************************************************************
 * Copyright 2021 Lorenzo Bettini and contributors
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
package org.pitest.pitclipse.ui.util;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.pitest.pitclipse.ui.PitclipseTestActivator;

/**
 * Programmatically import existing projects into the workspace
 * for SWTBot tests.
 * 
 * @author Lorenzo Bettini
 *
 */
public class ProjectImportUtil {

    public static IProject importProject(String projectName) throws CoreException {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String parentProject = PitclipseTestActivator.PLUGIN_ID;
        int pos = path.lastIndexOf(parentProject);
        String baseDirectory = path.substring(0, pos - 1);
        String projectDirectory =
            baseDirectory + "/testprojects/" + projectName;
        IProject project = importProject(new File(projectDirectory), projectName);
        project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        return project;
    }

    private static IProject importProject(final File baseDirectory, final String projectName) throws CoreException {
        IProjectDescription description = ResourcesPlugin.getWorkspace()
                .loadProjectDescription(new Path(baseDirectory.getAbsolutePath() + "/.project"));
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
        project.create(description, null);
        project.open(null);
        return project;
    }
}
