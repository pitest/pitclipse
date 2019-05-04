package org.pitest.pitclipse.launch.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

import java.util.List;

import static org.pitest.pitclipse.launch.config.ProjectUtils.getOpenJavaProjects;
import static org.pitest.pitclipse.launch.config.ProjectUtils.onClassPathOf;
import static org.pitest.pitclipse.launch.config.ProjectUtils.sameProject;

public class WorkspaceLevelProjectFinder implements ProjectFinder {
    @Override
    public List<String> getProjects(LaunchConfigurationWrapper configurationWrapper) throws CoreException {
        Builder<String> results = ImmutableList.builder();
        IJavaProject testProject = configurationWrapper.getProject();
        List<IJavaProject> projects = getOpenJavaProjects();
        for (IJavaProject project : projects) {
            if (sameProject(testProject, project) || onClassPathOf(testProject, project)) {
                results.add(project.getProject().getName());
            }
        }
        return results.build();
    }
}
