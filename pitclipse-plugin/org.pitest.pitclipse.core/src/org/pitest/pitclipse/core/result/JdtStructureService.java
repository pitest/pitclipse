package org.pitest.pitclipse.core.result;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.pitest.pitclipse.core.launch.MutatedClassNotFoundException;
import org.pitest.pitclipse.core.launch.ProjectNotFoundException;
import org.pitest.pitclipse.pitrunner.model.ProjectStructureService;

public enum JdtStructureService implements ProjectStructureService {

	INSTANCE;

	@Override
	public String packageFrom(String project, String mutatedClass) {
		try {
			IJavaProject javaProject = javaProject(project);
			IType type = javaProject.findType(mutatedClass);
			IPackageFragment pkg = type.getPackageFragment();
			return pkg.getElementName();
		} catch (JavaModelException e) {
			throw new MutatedClassNotFoundException(mutatedClass);
		}
	}

	@Override
	public boolean isClassInProject(String mutatedClass, String projectName) {
		IJavaProject project = javaProject(projectName);
		try {
			return null != project.findType(mutatedClass);
		} catch (Exception e) {
			return false;
		}
	}

	private IJavaProject javaProject(String projectName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject project : root.getProjects()) {
			if (projectName.equals(project.getName())) {
				if (project.isOpen()) {
					IJavaProject javaProject = JavaCore.create(project);
					return javaProject;
				}
			}
		}
		throw new ProjectNotFoundException(projectName);
	}
}
