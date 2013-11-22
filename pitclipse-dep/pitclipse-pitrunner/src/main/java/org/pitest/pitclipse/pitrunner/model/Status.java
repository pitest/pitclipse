package org.pitest.pitclipse.pitrunner.model;

import static com.google.common.collect.ImmutableList.copyOf;

import java.util.List;

import org.pitest.pitclipse.pitrunner.results.DetectionStatus;

import com.google.common.collect.ImmutableList;

public class Status implements Visitable, Countable {

	private final DetectionStatus detectionStatus;
	private final ImmutableList<ProjectMutations> projectMutations;

	private Status(DetectionStatus detectionStatus, ImmutableList<ProjectMutations> projectMutations) {
		this.detectionStatus = detectionStatus;
		this.projectMutations = projectMutations;
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

		private Builder() {
		}

		public Builder withDetectionStatus(DetectionStatus detectionStatus) {
			this.detectionStatus = detectionStatus;
			return this;
		}

		public Builder withProjectMutations(List<ProjectMutations> projectMutations) {
			this.projectMutations = copyOf(projectMutations);
			return this;
		}

		public Status build() {
			return new Status(detectionStatus, projectMutations);
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

}
