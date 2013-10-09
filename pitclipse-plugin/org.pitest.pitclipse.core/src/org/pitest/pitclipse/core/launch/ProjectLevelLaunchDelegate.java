package org.pitest.pitclipse.core.launch;

import org.pitest.pitclipse.core.launch.config.ClassFinder;
import org.pitest.pitclipse.core.launch.config.PackageFinder;
import org.pitest.pitclipse.core.launch.config.ProjectFinder;
import org.pitest.pitclipse.core.launch.config.ProjectLevelClassFinder;
import org.pitest.pitclipse.core.launch.config.ProjectLevelProjectFinder;
import org.pitest.pitclipse.core.launch.config.ProjectLevelSourceDirFinder;
import org.pitest.pitclipse.core.launch.config.SourceDirFinder;
import org.pitest.pitclipse.pitrunner.config.PitConfiguration;

public class ProjectLevelLaunchDelegate extends AbstractPitLaunchDelegate {

	public ProjectLevelLaunchDelegate(PitConfiguration pitConfiguration) {
		super(pitConfiguration);
	}

	@Override
	protected SourceDirFinder getSourceDirFinder() {
		return new ProjectLevelSourceDirFinder();
	}

	@Override
	protected ClassFinder getClassFinder() {
		return new ProjectLevelClassFinder();
	}

	@Override
	protected PackageFinder getPackageFinder() {
		return new PackageFinder();
	}

	@Override
	protected ProjectFinder getProjectFinder() {
		return new ProjectLevelProjectFinder();
	}

}
