package org.pitest.pitclipse.core;

public interface PitConfigurationVisitor {
	void visitProjectLevelConfiguration(PitConfiguration pitConfiguration);

	void visitWorkspaceLevelConfiguration(PitConfiguration pitConfiguration);
}
