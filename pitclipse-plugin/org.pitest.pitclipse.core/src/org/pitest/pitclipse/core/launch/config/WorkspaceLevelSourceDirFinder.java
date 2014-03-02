package org.pitest.pitclipse.core.launch.config;

import static org.pitest.pitclipse.reloc.guava.collect.ImmutableList.copyOf;
import static org.pitest.pitclipse.core.launch.config.ProjectUtils.getOpenJavaProjects;
import static org.pitest.pitclipse.core.launch.config.ProjectUtils.onClassPathOf;
import static org.pitest.pitclipse.core.launch.config.ProjectUtils.sameProject;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import org.pitest.pitclipse.reloc.guava.collect.ImmutableSet;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableSet.Builder;

public class WorkspaceLevelSourceDirFinder implements SourceDirFinder {

	@Override
	public List<File> getSourceDirs(LaunchConfigurationWrapper configurationWrapper) throws CoreException {
		Builder<File> sourceDirBuilder = ImmutableSet.builder();
		IJavaProject testProject = configurationWrapper.getProject();
		List<IJavaProject> projects = getOpenJavaProjects();
		for (IJavaProject project : projects) {
			if (sameProject(testProject, project) || onClassPathOf(testProject, project)) {
				sourceDirBuilder.addAll(getSourceDirsFromProject(project));
			}
		}
		return copyOf(sourceDirBuilder.build());
	}

	private Set<File> getSourceDirsFromProject(IJavaProject project) throws CoreException {
		Builder<File> sourceDirBuilder = ImmutableSet.builder();
		URI location = getProjectLocation(project.getProject());
		IPackageFragmentRoot[] packageRoots = project.getPackageFragmentRoots();

		File projectRoot = new File(location);
		for (IPackageFragmentRoot packageRoot : packageRoots) {
			if (!packageRoot.isArchive()) {
				File packagePath = removeProjectFromPackagePath(project, packageRoot.getPath());
				sourceDirBuilder.add(new File(projectRoot, packagePath.toString()));
			}
		}
		return sourceDirBuilder.build();
	}

	private URI getProjectLocation(IProject project) throws CoreException {
		URI locationURI = project.getDescription().getLocationURI();
		if (null != locationURI) {
			return locationURI;
		}
		// We're using the default location under workspace
		File projLocation = new File(project.getLocation().toOSString());
		return projLocation.toURI();
	}

	private File removeProjectFromPackagePath(IJavaProject javaProject, IPath packagePath) {
		IPath newPath = packagePath.removeFirstSegments(1);
		return newPath.toFile();
	}
}
