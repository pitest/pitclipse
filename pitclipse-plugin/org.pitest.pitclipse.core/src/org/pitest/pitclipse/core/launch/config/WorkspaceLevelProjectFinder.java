package org.pitest.pitclipse.core.launch.config;

import static org.pitest.pitclipse.core.launch.config.ProjectUtils.getOpenJavaProjects;
import static org.pitest.pitclipse.core.launch.config.ProjectUtils.onClassPathOf;
import static org.pitest.pitclipse.core.launch.config.ProjectUtils.sameProject;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList.Builder;

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
