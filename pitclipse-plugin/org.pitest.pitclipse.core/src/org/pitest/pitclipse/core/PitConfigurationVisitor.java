package org.pitest.pitclipse.core;

public interface PitConfigurationVisitor {
	void visitProjectLevelConfiguration();

	void visitWorkspaceLevelConfiguration();
}
