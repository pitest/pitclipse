package org.pitest.pitclipse.launch;

import org.pitest.pitclipse.launch.config.ClassFinder;
import org.pitest.pitclipse.launch.config.PackageFinder;
import org.pitest.pitclipse.launch.config.ProjectFinder;
import org.pitest.pitclipse.launch.config.SourceDirFinder;
import org.pitest.pitclipse.launch.config.WorkspaceLevelClassFinder;
import org.pitest.pitclipse.launch.config.WorkspaceLevelProjectFinder;
import org.pitest.pitclipse.launch.config.WorkspaceLevelSourceDirFinder;
import org.pitest.pitclipse.runner.config.PitConfiguration;

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
