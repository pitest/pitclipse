package org.pitest.pitclipse.core.launch.config;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableSet.builder;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class WorkspaceLevelClassFinder implements ClassFinder {

	public List<String> getClasses(
			LaunchConfigurationWrapper configurationWrapper)
			throws CoreException {
		Builder<String> classPathBuilder = builder();
		List<IJavaProject> projects = getOpenJavaProjects();
		for (IJavaProject project : projects) {
			classPathBuilder.addAll(getClassesFromProject(project));
		}
		return copyOf(classPathBuilder.build());
	}

	private Set<String> getClassesFromProject(IJavaProject project)
			throws JavaModelException {
		Builder<String> classPathBuilder = builder();
		IPackageFragmentRoot[] packageRoots = project.getPackageFragmentRoots();
		for (IPackageFragmentRoot packageRoot : packageRoots) {
			if (!packageRoot.isArchive() && !isMavenTestDir(packageRoot)) {
				for (IJavaElement element : packageRoot.getChildren()) {
					if (element instanceof IPackageFragment) {
						IPackageFragment packge = (IPackageFragment) element;
						if (packge.getCompilationUnits().length > 0) {
							classPathBuilder
									.addAll(getClassesFromPackage(packge));
						}
					}
				}
			}
		}
		return classPathBuilder.build();
	}

	private List<IJavaProject> getOpenJavaProjects() throws CoreException {
		ImmutableList.Builder<IJavaProject> resultBuilder = ImmutableList
				.builder();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject project : root.getProjects()) {
			if (project.isOpen()
					&& project
							.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				resultBuilder.add(JavaCore.create(project));
			}
		}
		return resultBuilder.build();
	}

	private boolean isMavenTestDir(IPackageFragmentRoot packageRoot) {
		return packageRoot.getPath().toPortableString()
				.contains("src/test/java");
	}

	private Set<String> getClassesFromPackage(IPackageFragment packge)
			throws JavaModelException {
		Builder<String> classPathBuilder = ImmutableSet.builder();
		for (ICompilationUnit javaFile : packge.getCompilationUnits()) {
			classPathBuilder.addAll(getClassesFromSourceFile(javaFile));
		}
		return classPathBuilder.build();
	}

	private Set<String> getClassesFromSourceFile(ICompilationUnit javaFile)
			throws JavaModelException {
		Builder<String> classPathBuilder = ImmutableSet.builder();
		for (IType type : javaFile.getAllTypes()) {
			classPathBuilder.add(type.getFullyQualifiedName());
		}
		return classPathBuilder.build();
	}

}
