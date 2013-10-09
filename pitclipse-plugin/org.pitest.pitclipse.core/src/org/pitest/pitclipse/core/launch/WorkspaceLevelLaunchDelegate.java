package org.pitest.pitclipse.core.launch;

import org.pitest.pitclipse.core.launch.config.ClassFinder;
import org.pitest.pitclipse.core.launch.config.PackageFinder;
import org.pitest.pitclipse.core.launch.config.ProjectFinder;
import org.pitest.pitclipse.core.launch.config.SourceDirFinder;
import org.pitest.pitclipse.core.launch.config.WorkspaceLevelClassFinder;
import org.pitest.pitclipse.core.launch.config.WorkspaceLevelProjectFinder;
import org.pitest.pitclipse.core.launch.config.WorkspaceLevelSourceDirFinder;
import org.pitest.pitclipse.pitrunner.config.PitConfiguration;

public class WorkspaceLevelLaunchDelegate extends AbstractPitLaunchDelegate {

	public WorkspaceLevelLaunchDelegate(PitConfiguration pitConfiguration) {
		super(pitConfiguration);
	}

	@Override
	protected SourceDirFinder getSourceDirFinder() {
		return new WorkspaceLevelSourceDirFinder();
	}

	@Override
	protected ClassFinder getClassFinder() {
		return new WorkspaceLevelClassFinder();
	}

	@Override
	protected PackageFinder getPackageFinder() {
		return new PackageFinder();
	}

	@Override
	protected ProjectFinder getProjectFinder() {
		return new WorkspaceLevelProjectFinder();
	}

}
