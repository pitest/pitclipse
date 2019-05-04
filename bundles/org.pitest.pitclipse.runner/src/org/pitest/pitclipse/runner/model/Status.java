package org.pitest.pitclipse.pitrunner.model;

import org.pitest.pitclipse.pitrunner.results.DetectionStatus;
import org.pitest.pitclipse.reloc.guava.base.Function;
import org.pitest.pitclipse.reloc.guava.base.Objects;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.Ordering;

import java.util.List;

import static org.pitest.pitclipse.reloc.guava.collect.Collections2.transform;

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Status status = (Status) o;
        return detectionStatus == status.detectionStatus &&
            Objects.equal(projectMutations, status.projectMutations);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(detectionStatus, projectMutations);
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
