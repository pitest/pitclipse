package org.pitest.pitclipse.runner.config;

public interface PitConfigurationVisitor {
    void visitProjectLevelConfiguration(PitConfiguration pitConfiguration);

    void visitWorkspaceLevelConfiguration(PitConfiguration pitConfiguration);
}
