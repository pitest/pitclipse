package org.pitest.pitclipse.launch.config;

import org.eclipse.core.runtime.CoreException;

import java.io.File;
import java.util.List;

public interface SourceDirFinder {
    List<File> getSourceDirs(
            LaunchConfigurationWrapper configurationWrapper)
            throws CoreException;
}
