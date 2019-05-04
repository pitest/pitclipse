package org.pitest.pitclipse.launch;

import org.pitest.pitclipse.launch.config.ClassFinder;
import org.pitest.pitclipse.launch.config.PackageFinder;
import org.pitest.pitclipse.launch.config.ProjectFinder;
import org.pitest.pitclipse.launch.config.ProjectLevelClassFinder;
import org.pitest.pitclipse.launch.config.ProjectLevelProjectFinder;
import org.pitest.pitclipse.launch.config.ProjectLevelSourceDirFinder;
import org.pitest.pitclipse.launch.config.SourceDirFinder;
import org.pitest.pitclipse.runner.config.PitConfiguration;

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
