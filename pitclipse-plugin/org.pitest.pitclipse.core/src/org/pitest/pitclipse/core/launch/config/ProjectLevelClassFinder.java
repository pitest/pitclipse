package org.pitest.pitclipse.core.launch.config;

import static org.pitest.pitclipse.reloc.guava.collect.ImmutableList.copyOf;
import static org.pitest.pitclipse.reloc.guava.collect.ImmutableSet.builder;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableSet.Builder;

public class ProjectLevelClassFinder implements ClassFinder {

	@Override
	public List<String> getClasses(LaunchConfigurationWrapper configurationWrapper) throws CoreException {
		Builder<String> classPathBuilder = builder();
		IPackageFragmentRoot[] packageRoots = configurationWrapper.getProject().getPackageFragmentRoots();
		for (IPackageFragmentRoot packageRoot : packageRoots) {
			if (!packageRoot.isArchive()) {
				for (IJavaElement element : packageRoot.getChildren()) {
					if (element instanceof IPackageFragment) {
						IPackageFragment packge = (IPackageFragment) element;
						if (packge.getCompilationUnits().length > 0)
							classPathBuilder.add(packge.getElementName() + ".*");
					}
				}
			}
		}
		return copyOf(classPathBuilder.build());
	}

}
