package org.pitest.pitclipse.pitrunner.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class ProjectMutations implements Visitable, Countable {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((packageMutations == null) ? 0 : packageMutations.hashCode());
		result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectMutations other = (ProjectMutations) obj;
		if (packageMutations == null) {
			if (other.packageMutations != null)
				return false;
		} else if (!packageMutations.equals(other.packageMutations))
			return false;
		if (projectName == null) {
			if (other.projectName != null)
				return false;
		} else if (!projectName.equals(other.projectName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProjectMutations [projectName=" + projectName + ", packageMutations=" + packageMutations + "]";
	}

	@Override
	public long count() {
		long sum = 0L;
		for (PackageMutations packageMutation : packageMutations) {
			sum += packageMutation.count();
		}
		return sum;
	}
}
