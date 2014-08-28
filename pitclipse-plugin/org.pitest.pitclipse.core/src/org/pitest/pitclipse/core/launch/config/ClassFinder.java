package org.pitest.pitclipse.core.launch.config;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

public interface ClassFinder {
	List<String> getClasses(LaunchConfigurationWrapper configurationWrapper) throws CoreException;
}