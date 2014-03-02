package org.pitest.pitclipse.core.launch.config;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

public class ProjectUtils {

	private ProjectUtils() {
	}

	public static List<IJavaProject> getOpenJavaProjects() throws CoreException {
		ImmutableList.Builder<IJavaProject> resultBuilder = ImmutableList.builder();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject project : root.getProjects()) {
			if (project.isOpen() && project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				resultBuilder.add(JavaCore.create(project));
			}
		}
		return resultBuilder.build();
	}

	public static boolean onClassPathOf(IJavaProject testProject, IJavaProject project) {
		return testProject.isOnClasspath(project);
	}

	public static boolean sameProject(IJavaProject testProject, IJavaProject project) {
		return testProject.getElementName().equals(project.getElementName());
	}

}
