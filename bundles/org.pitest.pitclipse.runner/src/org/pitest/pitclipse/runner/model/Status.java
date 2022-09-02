/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.runner.model;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.pitest.pitclipse.runner.results.DetectionStatus;

public class Status implements Visitable, Countable {

    private final DetectionStatus detectionStatus;
    private final List<ProjectMutations> projectMutations;
    private final MutationsModel mutationsModel;

    private Status(MutationsModel mutationsModel, DetectionStatus detectionStatus,
            List<ProjectMutations> projectMutations) {
        this.mutationsModel = mutationsModel;
        this.detectionStatus = detectionStatus;
        this.projectMutations = projectMutations.stream()
                .map(input -> input.copyOf().withStatus(Status.this).build())
                .collect(Collectors.toList());
    }

    public DetectionStatus getDetectionStatus() {
        return detectionStatus;
    }

    public List<ProjectMutations> getProjectMutations() {
        return projectMutations;
    }

    public static class Builder {
        private DetectionStatus detectionStatus;
        private List<ProjectMutations> projectMutations = new ArrayList<>();
        private MutationsModel mutationsModel;

        private Builder() {
        }

        public Builder withDetectionStatus(DetectionStatus detectionStatus) {
            this.detectionStatus = detectionStatus;
            return this;
        }

        public Builder withProjectMutations(List<ProjectMutations> projectMutations) {
            this.projectMutations = projectMutations.stream()
                    .sorted(
                        comparing(ProjectMutations::getProjectName,
                            nullsLast(
                                naturalOrder())))
                    .collect(Collectors.toList());
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
            Objects.equals(projectMutations, status.projectMutations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(detectionStatus, projectMutations);
    }

    @Override
    public long count() {
        long sum = 0L;
        for (ProjectMutations projectMutation : projectMutations) {
            sum += projectMutation.count();
        }
        return sum;
    }

    public Builder copyOf() {
        return builder()
            .withDetectionStatus(detectionStatus)
            .withProjectMutations(projectMutations);
    }

    public MutationsModel getMutationsModel() {
        return mutationsModel;
    }
}
