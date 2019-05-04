package org.pitest.pitclipse.launch.config;

import org.eclipse.core.runtime.CoreException;

import java.util.List;

public interface ProjectFinder {
    List<String> getProjects(LaunchConfigurationWrapper configurationWrapper) throws CoreException;
}
