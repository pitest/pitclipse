package org.pitest.pitclipse.launch.config;

import com.google.common.collect.ImmutableList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

import java.util.List;

public class ProjectLevelProjectFinder implements ProjectFinder {

    @Override
    public List<String> getProjects(LaunchConfigurationWrapper configurationWrapper) throws CoreException {
        IJavaProject project = configurationWrapper.getProject();
        return ImmutableList.of(project.getProject().getName());
    }

}
