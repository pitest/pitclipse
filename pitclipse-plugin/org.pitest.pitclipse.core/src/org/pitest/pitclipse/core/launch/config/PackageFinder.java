package org.pitest.pitclipse.core.launch.config;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableSet.builder;
import static org.eclipse.core.resources.IResource.FOLDER;
import static org.eclipse.core.resources.IResource.NONE;
import static org.eclipse.core.resources.IResource.PROJECT;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT_ROOT;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

import com.google.common.collect.ImmutableSet.Builder;

public class PackageFinder {

	private static final class ProjectLevelProxyVisitor implements
			IResourceProxyVisitor {
		private final LaunchConfigurationWrapper configurationWrapper;
		private final Builder<String> builder;

		private ProjectLevelProxyVisitor(
				LaunchConfigurationWrapper configurationWrapper,
				Builder<String> builder) {
			this.configurationWrapper = configurationWrapper;
			this.builder = builder;
		}

		public boolean visit(IResourceProxy proxy) throws CoreException {
			IJavaProject project = configurationWrapper.getProject();
			if (proxy.getType() == FOLDER) {
				IJavaElement element = JavaCore.create(proxy.requestResource());
				if (element.getElementType() == PACKAGE_FRAGMENT) {
					builder.add(element.getElementName() + ".*");
				} else if (element.getElementType() == PACKAGE_FRAGMENT_ROOT) {
					builder.addAll(getPackagesFromRoot(project,
							element.getHandleIdentifier()));
				}
			} else if (proxy.getType() == PROJECT) {
				IPackageFragmentRoot[] packageRoots = project
						.getAllPackageFragmentRoots();
				for (IPackageFragmentRoot packageRoot : packageRoots) {
					if (!packageRoot.isArchive()) {
						builder.addAll(getPackagesFromRoot(project,
								packageRoot.getHandleIdentifier()));
					}
				}
			}
			return false;
		}

		private Set<String> getPackagesFromRoot(IJavaProject project,
				String handleId) throws CoreException {
			Builder<String> builder = builder();
			IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
			for (IPackageFragmentRoot root : roots) {
				if (handleId.equals(root.getHandleIdentifier())) {
					IJavaElement[] elements = root.getChildren();
					for (IJavaElement element : elements) {
						if (element.getElementType() == PACKAGE_FRAGMENT) {
							builder.add(element.getElementName() + ".*");
						}
					}
				}
			}
			return builder.build();
		}
	}

	public List<String> getPackages(
			final LaunchConfigurationWrapper configurationWrapper)
			throws CoreException {

		final Builder<String> builder = builder();
		IResource[] resources = configurationWrapper.getMappedResources();
		IResourceProxyVisitor visitor = new ProjectLevelProxyVisitor(
				configurationWrapper, builder);
		for (IResource resource : resources) {
			resource.accept(visitor, NONE);
		}
		return copyOf(builder.build());
	}

}
