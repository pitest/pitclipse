package org.pitest.pitclipse.core.launch.config;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableSet.builder;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.google.common.collect.ImmutableSet.Builder;

public class ProjectLevelClassFinder implements ClassFinder {

	public List<String> getClasses(
			LaunchConfigurationWrapper configurationWrapper) throws CoreException {
		Builder<String> classPathBuilder = builder();
		IPackageFragmentRoot[] packageRoots = configurationWrapper.getProject()
				.getPackageFragmentRoots();
		for (IPackageFragmentRoot packageRoot : packageRoots) {
			if (!packageRoot.isArchive()) {
				for (IJavaElement element : packageRoot.getChildren()) {
					if (element instanceof IPackageFragment) {
						IPackageFragment packge = (IPackageFragment) element;
						if (packge.getCompilationUnits().length > 0) {
							classPathBuilder
									.add(packge.getElementName() + ".*");
							// classPathBuilder.addAll(getClassesFromPackage(packge));
						}
					}
				}
			}
		}
		return copyOf(classPathBuilder.build());
	}

}
