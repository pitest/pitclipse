package org.pitest.pitclipse.pitrunner.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class ProjectMutations implements Visitable {
	private final String projectName;
	private final ImmutableList<PackageMutations> packageMutations;

	private ProjectMutations(String projectName, ImmutableList<PackageMutations> packageMutations) {
		this.projectName = projectName;
		this.packageMutations = packageMutations;
	}

	@Override
	public <T> T accept(MutationsModelVisitor<T> visitor) {
		return visitor.visitProject(this);
	}

	public String getProjectName() {
		return projectName;
	}

	public static Builder builder() {
		return new Builder();
	}

	public List<PackageMutations> getPackageMutations() {
		return packageMutations;
	}

	public static class Builder {
		private String projectName;
		private ImmutableList<PackageMutations> packageMutations = ImmutableList.of();

		private Builder() {
		}

		public Builder withProjectName(String projectName) {
			this.projectName = projectName;
			return this;
		}

		public Builder withPackageMutations(List<PackageMutations> packages) {
			this.packageMutations = ImmutableList.copyOf(packages);
			return this;
		}

		public ProjectMutations build() {
			return new ProjectMutations(projectName, packageMutations);
		}
	}
}
