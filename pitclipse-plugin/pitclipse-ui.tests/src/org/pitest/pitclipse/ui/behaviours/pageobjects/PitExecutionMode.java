package org.pitest.pitclipse.ui.behaviours.pageobjects;

public enum PitExecutionMode {
	PROJECT_ISOLATION("Project containing test only"), WORKSPACE(
			"All projects in workspace");

	private final String label;

	private PitExecutionMode(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
