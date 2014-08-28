package org.pitest.pitclipse.core.launch.config;

import static org.pitest.pitclipse.reloc.guava.collect.ImmutableList.copyOf;
import static org.pitest.pitclipse.reloc.guava.collect.ImmutableSet.builder;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableSet;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableSet.Builder;

public class ProjectLevelClassFinder implements ClassFinder {

	@Override
	public List<String> getClasses(LaunchConfigurationWrapper configurationWrapper) throws CoreException {
		IJavaProject project = configurationWrapper.getProject();
		return copyOf(getClassesFromProject(project));
	}

	public static Set<String> getClassesFromProject(IJavaProject project) throws JavaModelException {
		Builder<String> classPathBuilder = builder();
		IPackageFragmentRoot[] packageRoots = project.getPackageFragmentRoots();
		for (IPackageFragmentRoot packageRoot : packageRoots) {
			classPathBuilder.addAll(getNonTestClassesFromPackageRoot(packageRoot));
		}
		return classPathBuilder.build();
	}

	private static Set<String> getNonTestClassesFromPackageRoot(IPackageFragmentRoot packageRoot)
			throws JavaModelException {
		Builder<String> classPathBuilder = ImmutableSet.builder();
		if (!isMavenTestDir(packageRoot)) {
			classPathBuilder.addAll(getAllClassesFromPackageRoot(packageRoot));
		}
		return classPathBuilder.build();
	}

	public static Set<String> getAllClassesFromPackageRoot(IPackageFragmentRoot packageRoot) throws JavaModelException {
		Builder<String> classPathBuilder = ImmutableSet.builder();
		if (!packageRoot.isArchive()) {
			for (IJavaElement element : packageRoot.getChildren()) {
				if (element instanceof IPackageFragment) {
					IPackageFragment packge = (IPackageFragment) element;
					if (packge.getCompilationUnits().length > 0) {
						classPathBuilder.addAll(getClassesFromPackage(packge));
					}
				}
			}
		}
		return classPathBuilder.build();
	}

	private static boolean isMavenTestDir(IPackageFragmentRoot packageRoot) {
		return packageRoot.getPath().toPortableString().contains("src/test/java");
	}

	public static Set<String> getClassesFromPackage(IPackageFragment packge) throws JavaModelException {
		Builder<String> classPathBuilder = ImmutableSet.builder();
		for (ICompilationUnit javaFile : packge.getCompilationUnits()) {
			classPathBuilder.addAll(getClassesFromSourceFile(javaFile));
		}
		return classPathBuilder.build();
	}

	private static Set<String> getClassesFromSourceFile(ICompilationUnit javaFile) throws JavaModelException {
		Builder<String> classPathBuilder = ImmutableSet.builder();
		for (IType type : javaFile.getAllTypes()) {
			classPathBuilder.add(type.getFullyQualifiedName());
		}
		return classPathBuilder.build();
	}
}
