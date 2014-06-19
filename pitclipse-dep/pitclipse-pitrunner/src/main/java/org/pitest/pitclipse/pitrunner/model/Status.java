package org.pitest.pitclipse.pitrunner.model;

import static org.pitest.pitclipse.reloc.guava.collect.Collections2.transform;

import java.util.List;

import org.pitest.pitclipse.pitrunner.results.DetectionStatus;
import org.pitest.pitclipse.reloc.guava.base.Function;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.Ordering;

public class Status implements Visitable, Countable {

	private final DetectionStatus detectionStatus;
	private final ImmutableList<ProjectMutations> projectMutations;
	private final MutationsModel mutationsModel;

	private Status(MutationsModel mutationsModel, DetectionStatus detectionStatus,
			ImmutableList<ProjectMutations> projectMutations) {
		this.mutationsModel = mutationsModel;
		this.detectionStatus = detectionStatus;
		this.projectMutations = ImmutableList.copyOf(transform(projectMutations,
				new Function<ProjectMutations, ProjectMutations>() {
					@Override
					public ProjectMutations apply(ProjectMutations input) {
						return input.copyOf().withStatus(Status.this).build();
					}
				}));
	}

	public DetectionStatus getDetectionStatus() {
		return detectionStatus;
	}

	public ImmutableList<ProjectMutations> getProjectMutations() {
		return projectMutations;
	}

	public static class Builder {
		private DetectionStatus detectionStatus;
		private ImmutableList<ProjectMutations> projectMutations = ImmutableList.of();
		private MutationsModel mutationsModel;

		private Builder() {
		}

		public Builder withDetectionStatus(DetectionStatus detectionStatus) {
			this.detectionStatus = detectionStatus;
			return this;
		}

		public Builder withProjectMutations(List<ProjectMutations> projectMutations) {
			this.projectMutations = Ordering.natural().nullsLast().onResultOf(ProjectName.GET)
					.immutableSortedCopy(projectMutations);
			return this;
		}

		public Status build() {
			return new Status(mutationsModel, detectionStatus, projectMutations);
		}

		public Builder withModel(MutationsModel mutationsModel) {
			this.mutationsModel = mutationsModel;
			return this;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public <T> T accept(MutationsModelVisitor<T> visitor) {
		return visitor.visitStatus(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((detectionStatus == null) ? 0 : detectionStatus.hashCode());
		result = prime * result + ((projectMutations == null) ? 0 : projectMutations.hashCode());
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
		Status other = (Status) obj;
		if (detectionStatus != other.detectionStatus)
			return false;
		if (projectMutations == null) {
			if (other.projectMutations != null)
				return false;
		} else if (!projectMutations.equals(other.projectMutations))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Status [detectionStatus=" + detectionStatus + ", projectMutations=" + projectMutations + "]";
	}

	@Override
	public long count() {
		long sum = 0L;
		for (ProjectMutations projectMutation : projectMutations) {
			sum += projectMutation.count();
		}
		return sum;
	}

	private enum ProjectName implements Function<ProjectMutations, String> {
		GET;

		@Override
		public String apply(ProjectMutations input) {
			return input.getProjectName();
		}

	}

	public Builder copyOf() {
		return builder().withDetectionStatus(detectionStatus).withProjectMutations(projectMutations);
	}

	public MutationsModel getMutationsModel() {
		return mutationsModel;
	}
}
