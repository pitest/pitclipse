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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.pitest.pitclipse.launch.AbstractPitLaunchDelegate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ProjectUtils {

    private ProjectUtils() {
    }

    public static List<IJavaProject> getOpenJavaProjects() throws CoreException {
        ImmutableList.Builder<IJavaProject> resultBuilder = ImmutableList.builder();
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (IProject project : root.getProjects()) {
            if (project.isOpen() && project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
                resultBuilder.add(JavaCore.create(project));
            }
        }
        return resultBuilder.build();
    }

    public static boolean onClassPathOf(IJavaProject testProject, IJavaProject project) {
        return testProject.isOnClasspath(project);
    }

    public static boolean onClassPathOf(IJavaProject project, String fullyQualifiedName) throws CoreException {
        String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(project);
        List<URL> urlList = new ArrayList<>();
        for (int i = 0; i < classPathEntries.length; i++) {
            String entry = classPathEntries[i];
            IPath path = new Path(entry);
            URL url;
            try {
                url = path.toFile().toURI().toURL();
                urlList.add(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        URL[] urls = urlList.toArray(new URL[urlList.size()]);
        try (URLClassLoader classLoader = new URLClassLoader(urls, AbstractPitLaunchDelegate.class.getClassLoader())) {
            try {
                classLoader.loadClass(fullyQualifiedName);
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        } catch (IOException e1) {
            throw new CoreException(Status.error("Closing the classloader", e1));
        }
    }

    public static boolean sameProject(IJavaProject testProject, IJavaProject project) {
        return testProject.getElementName().equals(project.getElementName());
    }

}
