package org.pitest.pitclipse.core.launch.config;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

import com.google.common.collect.ImmutableSet;

public class ProjectLevelProjectFinder implements ProjectFinder {

	@Override
	public Set<String> getProjects(LaunchConfigurationWrapper configurationWrapper) throws CoreException {
		IJavaProject project = configurationWrapper.getProject();
		return ImmutableSet.of(project.getProject().getName());
	}

}
