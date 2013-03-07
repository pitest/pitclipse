package org.pitest.pitclipse.pitrunner.config;

public interface PitConfigurationVisitor {
	void visitProjectLevelConfiguration(PitConfiguration pitConfiguration);

	void visitWorkspaceLevelConfiguration(PitConfiguration pitConfiguration);
}
