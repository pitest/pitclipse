package org.pitest.pitclipse.pitrunner.model;

import static org.pitest.pitclipse.reloc.guava.collect.Collections2.transform;

import java.util.List;

import org.pitest.pitclipse.reloc.guava.base.Function;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.Ordering;

public class ProjectMutations implements Visitable, Countable {
	private final String projectName;
	private final ImmutableList<PackageMutations> packageMutations;
	private final Status status;

	private ProjectMutations(Status status, String projectName, ImmutableList<PackageMutations> packageMutations) {
		this.status = status;
		this.projectName = projectName;
		this.packageMutations = ImmutableList.copyOf(transform(packageMutations,
				new Function<PackageMutations, PackageMutations>() {
					@Override
					public PackageMutations apply(PackageMutations input) {
						return input.copyOf().withProjectMutations(ProjectMutations.this).build();
					}
				}));
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

	public Builder copyOf() {
		return builder().withPackageMutations(packageMutations).withProjectName(projectName);
	}

	public List<PackageMutations> getPackageMutations() {
		return packageMutations;
	}

	public static class Builder {
		private String projectName;
		private ImmutableList<PackageMutations> packageMutations = ImmutableList.of();
		private Status status;

		private Builder() {
		}

		public Builder withProjectName(String projectName) {
			this.projectName = projectName;
			return this;
		}

		public Builder withPackageMutations(List<PackageMutations> packages) {
			this.packageMutations = Ordering.natural().nullsLast().onResultOf(PackageName.GET)
					.immutableSortedCopy(packages);
			return this;
		}

		public ProjectMutations build() {
			return new ProjectMutations(status, projectName, packageMutations);
		}

		public Builder withStatus(Status status) {
			this.status = status;
			return this;
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

	public Status getStatus() {
		return status;
	}

	private enum PackageName implements Function<PackageMutations, String> {
		GET;

		@Override
		public String apply(PackageMutations input) {
			return input.getPackageName();
		}
	}

}
