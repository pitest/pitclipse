package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.pitest.pitclipse.ui.behaviours.StepException;

public class AbstractSyntaxTree {

	public void removeAllMethods(ClassContext context) {
		IJavaProject javaProject = getJavaProject(context);
		try {
			IProgressMonitor progressMonitor = new NullProgressMonitor();
			IType type = javaProject.findType(context
					.getFullyQualifiedTestClassName());
			for (IMethod method : type.getMethods()) {
				method.delete(true, progressMonitor);
			}
		} catch (JavaModelException e) {
			throw new StepException(e);
		}
	}

	private IJavaProject getJavaProject(ClassContext context) {
		return getJavaProject(context.getProjectName());
	}

	private IJavaProject getJavaProject(String projectName) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);
		return JavaCore.create(project);
	}

	public void addTestMethod(TestClassContext context) {
		IJavaProject javaProject = getJavaProject(context);
		try {
			IType type = javaProject.findType(context
					.getFullyQualifiedTestClassName());
			NullProgressMonitor progressMonitor = new NullProgressMonitor();
			type.createMethod(context.getTestCase(), null, false,
					progressMonitor);
		} catch (JavaModelException e) {
			throw new StepException(e);
		}
	}

	public void addMethod(ConcreteClassContext context) {
		IJavaProject javaProject = getJavaProject(context);
		try {
			IType type = javaProject.findType(context
					.getFullyQualifiedTestClassName());
			NullProgressMonitor progressMonitor = new NullProgressMonitor();
			type.createMethod(context.getMethod(), null, false, progressMonitor);
		} catch (JavaModelException e) {
			throw new StepException(e);
		}
	}

	public void deleteProject(String projectName) {
		NullProgressMonitor progressMonitor = new NullProgressMonitor();
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);
		try {
			project.delete(true, progressMonitor);
		} catch (CoreException e) {
			throw new StepException(e);
		}
	}

	public void addJUnitToClassPath(String projectName) {
		IJavaProject project = getJavaProject(projectName);
		try {
			IClasspathEntry[] entries = project.getRawClasspath();
			IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];

			System.arraycopy(entries, 0, newEntries, 0, entries.length);

			// add a new entry using the path to the container
			Path junitPath = new Path("org.eclipse.jdt.junit.JUNIT_CONTAINER/4");
			IClasspathEntry junitEntry = JavaCore.newContainerEntry(junitPath);
			newEntries[entries.length] = JavaCore.newContainerEntry(junitEntry
					.getPath());
			project.setRawClasspath(newEntries, null);
		} catch (JavaModelException e) {
			throw new StepException(e);
		}
	}

	/*
	 * ASTParser parser = ASTParser.newParser(AST.JLS3);
	 * parser.setKind(ASTParser.K_COMPILATION_UNIT);
	 * parser.setSource(type.getCompilationUnit());
	 * parser.setResolveBindings(true); ASTNode ast =
	 * parser.createAST(progressMonitor); ASTVisitor visitor = new ASTVisitor()
	 * {
	 * 
	 * }; ast.accept(visitor);
	 */

}
