package org.pitest.pitclipse.core.launch.config;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

public interface SourceDirFinder {
	List<File> getSourceDirs(
			LaunchConfigurationWrapper configurationWrapper)
			throws CoreException;
}
