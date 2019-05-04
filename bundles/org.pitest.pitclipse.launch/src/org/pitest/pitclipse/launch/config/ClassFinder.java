package org.pitest.pitclipse.launch.config;

import org.eclipse.core.runtime.CoreException;

import java.util.List;

public interface ClassFinder {
    List<String> getClasses(LaunchConfigurationWrapper configurationWrapper) throws CoreException;
}