package org.pitest.pitclipse.ui.behaviours.steps;

import org.pitest.pitclipse.pitrunner.results.Mutations.Mutation;

import com.google.common.collect.ImmutableList;

public class ProjectMutations {

	private final ImmutableList<Mutation> mutations;
	private final String project;

	public ProjectMutations(String project, ImmutableList<Mutation> mutations) {
		this.project = project;
		this.mutations = mutations;
	}

	public static class Builder {
		private String project;
		private final ImmutableList.Builder<Mutation> mutationsBuilder = ImmutableList
				.builder();

		private Builder() {
		}

		public Builder withProjectName(String project) {
			this.project = project;
			return this;
		}

		public void addMutation(Mutation mutation) {
			mutationsBuilder.add(mutation);
		}

		public ProjectMutations build() {
			return new ProjectMutations(project, mutationsBuilder.build());
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public String getProject() {
		return project;
	}

	public ImmutableList<Mutation> getMutations() {
		return mutations;
	}
}
