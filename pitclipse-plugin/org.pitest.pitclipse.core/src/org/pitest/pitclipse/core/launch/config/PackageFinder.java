package org.pitest.pitclipse.core.launch.config;

import static org.eclipse.core.resources.IResource.FOLDER;
import static org.eclipse.core.resources.IResource.NONE;
import static org.eclipse.core.resources.IResource.PROJECT;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT_ROOT;
import static org.eclipse.jdt.core.IPackageFragment.DEFAULT_PACKAGE_NAME;
import static org.pitest.pitclipse.reloc.guava.collect.ImmutableList.copyOf;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableSet;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableSet.Builder;

public class PackageFinder {

	private static final class ProjectLevelProxyVisitor implements IResourceProxyVisitor {
		private final LaunchConfigurationWrapper configurationWrapper;
		private final Builder<String> builder;

		private ProjectLevelProxyVisitor(LaunchConfigurationWrapper configurationWrapper, Builder<String> builder) {
			this.configurationWrapper = configurationWrapper;
			this.builder = builder;
		}

		@Override
		public boolean visit(IResourceProxy proxy) throws CoreException {
			IJavaProject project = configurationWrapper.getProject();
			if (proxy.getType() == FOLDER) {
				IJavaElement element = JavaCore.create(proxy.requestResource());
				if (element.getElementType() == PACKAGE_FRAGMENT)
					builder.add(element.getElementName() + ".*");
				else if (element.getElementType() == PACKAGE_FRAGMENT_ROOT)
					builder.addAll(getPackagesFromRoot(project, element.getHandleIdentifier()));
			} else if (proxy.getType() == PROJECT) {
				IPackageFragmentRoot[] packageRoots = project.getAllPackageFragmentRoots();
				for (IPackageFragmentRoot packageRoot : packageRoots) {
					if (!packageRoot.isArchive())
						builder.addAll(getPackagesFromRoot(project, packageRoot.getHandleIdentifier()));
				}
			}
			return false;
		}

		private Set<String> getPackagesFromRoot(IJavaProject project, String handleId) throws CoreException {
			Builder<String> builder = ImmutableSet.builder();
			IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
			for (IPackageFragmentRoot root : roots) {
				if (handleId.equals(root.getHandleIdentifier()))
					builder.addAll(packagesFrom(root));
			}
			return builder.build();
		}

		private Set<String> packagesFrom(IPackageFragmentRoot root) throws CoreException {
			Builder<String> builder = ImmutableSet.builder();
			IJavaElement[] elements = root.getChildren();
			for (IJavaElement element : elements) {
				if (element instanceof IPackageFragment) {
					IPackageFragment packge = (IPackageFragment) element;
					builder.addAll(packagesFrom(packge));
				}

			}
			return builder.build();
		}

		private Set<String> packagesFrom(IPackageFragment packageFragment) throws CoreException {
			Builder<String> builder = ImmutableSet.builder();
			if (packageFragment.getElementName().equals(DEFAULT_PACKAGE_NAME))
				builder.addAll(classesFromDefaultPackage(packageFragment));
			else {
				if (packageFragment.getCompilationUnits().length > 0)
					builder.add(packageFragment.getElementName() + ".*");
			}
			return builder.build();
		}

		private Set<String> classesFromDefaultPackage(IPackageFragment packageFragment) throws CoreException {
			Builder<String> builder = ImmutableSet.builder();
			for (ICompilationUnit c : packageFragment.getCompilationUnits()) {
				for (IType type : c.getAllTypes()) {
					builder.add(type.getFullyQualifiedName());
				}
			}
			return builder.build();
		}
	}

	public List<String> getPackages(LaunchConfigurationWrapper configurationWrapper) throws CoreException {
		final Builder<String> builder = ImmutableSet.builder();
		IResource[] resources = configurationWrapper.getMappedResources();
		IResourceProxyVisitor visitor = new ProjectLevelProxyVisitor(configurationWrapper, builder);
		for (IResource resource : resources) {
			resource.accept(visitor, NONE);
		}
		return copyOf(builder.build());
	}

}
