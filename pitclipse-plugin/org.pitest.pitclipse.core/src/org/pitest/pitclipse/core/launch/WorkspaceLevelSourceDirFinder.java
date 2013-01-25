package org.pitest.pitclipse.core.launch;

import static com.google.common.collect.ImmutableList.copyOf;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.pitest.pitclipse.core.launch.config.LaunchConfigurationWrapper;
import org.pitest.pitclipse.core.launch.config.SourceDirFinder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class WorkspaceLevelSourceDirFinder implements SourceDirFinder {

	public List<File> getSourceDirs(
			LaunchConfigurationWrapper configurationWrapper)
			throws CoreException {
		Builder<File> sourceDirBuilder = ImmutableSet.builder();
		List<IJavaProject> projects = getOpenJavaProjects();
		for (IJavaProject project : projects) {
			sourceDirBuilder.addAll(getSourceDirsFromProject(project));
		}
		return copyOf(sourceDirBuilder.build());
	}

	private Set<File> getSourceDirsFromProject(IJavaProject project)
			throws CoreException {
		Builder<File> sourceDirBuilder = ImmutableSet.builder();
		URI location = getProjectLocation(project.getProject());
		IPackageFragmentRoot[] packageRoots = project.getPackageFragmentRoots();

		File projectRoot = new File(location);
		for (IPackageFragmentRoot packageRoot : packageRoots) {
			if (!packageRoot.isArchive()) {
				File packagePath = removeProjectFromPackagePath(project,
						packageRoot.getPath());
				sourceDirBuilder.add(new File(projectRoot, packagePath
						.toString()));
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

	private File removeProjectFromPackagePath(IJavaProject javaProject,
			IPath packagePath) {
		IPath newPath = packagePath.removeFirstSegments(1);
		return newPath.toFile();
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

}
