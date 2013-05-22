package org.pitest.pitclipse.pitrunner.config;

public enum PitExecutionMode {
	PROJECT_ISOLATION("containingProject", "&Project containing test only") {
		@Override
		public <T> T accept(PitExecutionModeVisitor<T> visitor) {
			return visitor.visitProjectLevelConfiguration();
		}
	},
	WORKSPACE("allProjects", "&All projects in workspace") {
		@Override
		public <T> T accept(PitExecutionModeVisitor<T> visitor) {
			return visitor.visitWorkspaceLevelConfiguration();
		}
	};

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

	public abstract <T> T accept(PitExecutionModeVisitor<T> visitor);
}
