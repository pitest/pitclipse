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

import static org.junit.Assert.fail;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.pitest.pitclipse.ui.PitclipseTestActivator;

/**
 * Programmatically import existing projects into the workspace
 * for SWTBot tests.
 * 
 * @author Lorenzo Bettini
 *
 */
public class ProjectImportUtil {

    private static final String TESTPROJECTS = "testprojects";

    /**
     * Imports an existing project into the running workspace for
     * SWTBot tests.
     * 
     * IMPORTANT: projects to be imported are expected to be located in the
     * "testprojects" folder, which is expected to be found in the parent folder
     * of this project.
     * 
     * @param projectName
     * @return
     * @throws CoreException
     */
    public static IProject importProject(String projectName) throws CoreException {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String thisProject = PitclipseTestActivator.PLUGIN_ID;
        int pos = path.lastIndexOf(thisProject);
        String baseDirectory = path.substring(0, pos - 1);
        String projectToImportPath =
            baseDirectory + "/" + TESTPROJECTS + "/" + projectName;
        IProject project = importProject(new File(projectToImportPath), projectName);
        project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        return project;
    }

    private static IProject importProject(final File projectPath, final String projectName) throws CoreException {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        System.out.println("*** IMPORTING PROJECT: " + projectName);
        ImportOperation importOperation = new ImportOperation(
                project.getFullPath(), // relative to the workspace
                projectPath, // absolute path
                FileSystemStructureProvider.INSTANCE,
                s -> IOverwriteQuery.ALL);
        // this means: copy the imported project into workspace
        importOperation.setCreateContainerStructure(false);
        try {
            importOperation.run(new NullProgressMonitor());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return project;
    }
}
