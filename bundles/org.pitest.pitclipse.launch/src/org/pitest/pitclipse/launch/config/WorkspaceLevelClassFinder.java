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
