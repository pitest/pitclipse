package org.pitest.pitclipse.runner.config;

public interface PitExecutionModeVisitor<T> {
    T visitProjectLevelConfiguration();

    T visitWorkspaceLevelConfiguration();
}
