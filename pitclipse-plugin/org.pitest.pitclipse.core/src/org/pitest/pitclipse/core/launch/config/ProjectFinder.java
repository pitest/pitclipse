package org.pitest.pitclipse.core.launch.config;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;

public interface ProjectFinder {
	Set<String> getProjects(LaunchConfigurationWrapper configurationWrapper) throws CoreException;
}
