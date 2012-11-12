package org.pitest.pitclipse.ui.behaviours.pageobjects;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableList.of;

import java.util.List;

public class PitRunConfiguration {

	private final String name;
	private final List<String> projects;

	private PitRunConfiguration(String name, List<String> projects) {
		this.name = name;
		this.projects = projects;
	}

	public String getName() {
		return name;
	}

	public List<String> getProjects() {
		return projects;
	}

	public static class Builder {
		private String name;
		private List<String> projects = of();

		public PitRunConfiguration build() {
			return new PitRunConfiguration(name, projects);
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withProjects(String... projects) {
			this.projects = copyOf(projects);
			return this;
		}
	}
}
