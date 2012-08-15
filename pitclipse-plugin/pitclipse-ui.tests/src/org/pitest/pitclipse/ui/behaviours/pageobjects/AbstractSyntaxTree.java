package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.pitest.pitclipse.ui.behaviours.StepException;

public class AbstractSyntaxTree {

	public void removeAllMethods(TestClassContext context) {
		IJavaProject javaProject = getJavaProject(context);
		try {
			IProgressMonitor progressMonitor = new NullProgressMonitor();
			IType type = javaProject.findType(context
					.getFullyQUalifiedTestClassName());
			for (IMethod method : type.getMethods()) {
				method.delete(true, progressMonitor);
			}
		} catch (JavaModelException e) {
			throw new StepException(e);
		}
	}

	private IJavaProject getJavaProject(TestClassContext context) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(context.getProjectName());
		return JavaCore.create(project);
	}

}
