package org.pitest.pitclipse.core.launch.config;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

public interface ProjectFinder {
	List<String> getProjects(LaunchConfigurationWrapper configurationWrapper) throws CoreException;
}
