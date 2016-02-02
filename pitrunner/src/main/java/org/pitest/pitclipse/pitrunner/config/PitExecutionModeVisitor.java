package org.pitest.pitclipse.pitrunner.config;

public interface PitExecutionModeVisitor<T> {
	T visitProjectLevelConfiguration();

	T visitWorkspaceLevelConfiguration();
}
