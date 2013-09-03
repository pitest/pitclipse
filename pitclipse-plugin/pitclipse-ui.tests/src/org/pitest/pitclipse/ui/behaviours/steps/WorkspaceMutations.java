package org.pitest.pitclipse.ui.behaviours.steps;

import static com.google.common.collect.ImmutableList.copyOf;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class WorkspaceMutations {

	public static class Builder {
		private ImmutableList<ProjectMutations> projectMutations = ImmutableList
				.of();

		private Builder() {
		}

		public Builder withProjectMutations(
				List<ProjectMutations> projectMutations) {
			this.projectMutations = copyOf(projectMutations);
			return this;
		}

		public WorkspaceMutations build() {
			return new WorkspaceMutations(projectMutations);
		}

	}

	private final ImmutableList<ProjectMutations> projectMutations;

	private WorkspaceMutations(ImmutableList<ProjectMutations> projectMutations) {
		this.projectMutations = projectMutations;
	}

	public static Builder builder() {
		return new Builder();
	}

	public ImmutableList<ProjectMutations> getProjectMutations() {
		return projectMutations;
	}

}
