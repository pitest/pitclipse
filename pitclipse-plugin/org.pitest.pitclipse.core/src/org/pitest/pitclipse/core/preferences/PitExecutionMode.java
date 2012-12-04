package org.pitest.pitclipse.core.preferences;

public enum PitExecutionMode {
	PROJECT_ISOLATION("containingProject", "&Project containing test only"), WORKSPACE(
			"allProjects", "&All projects in workspace");

	private final String label;
	private final String id;

	private PitExecutionMode(String id, String label) {
		this.id = id;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String getId() {
		return id;
	}
}
