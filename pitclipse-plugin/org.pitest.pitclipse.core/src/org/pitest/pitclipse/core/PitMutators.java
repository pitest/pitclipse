package org.pitest.pitclipse.core;

public enum PitMutators {
	DEFAULTS("defaultMutators", "&Default Mutators"), STRONGER("strongerMutators", "&Stronger Mutators"), ALL(
			"allMutators", "&All Mutators");

	private final String label;
	private final String id;

	private PitMutators(String id, String label) {
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
