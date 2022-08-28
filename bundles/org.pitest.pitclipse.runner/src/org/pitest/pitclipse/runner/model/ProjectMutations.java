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

public class ProjectMutations implements Visitable, Countable {
    private final String projectName;
    private final List<PackageMutations> packageMutations;
    private final Status status;

    private ProjectMutations(Status status, String projectName, List<PackageMutations> packageMutations) {
        this.status = status;
        this.projectName = projectName;
        this.packageMutations = packageMutations.stream()
                .map(input -> input.copyOf().withProjectMutations(ProjectMutations.this).build())
                .collect(Collectors.toList());
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
        return builder()
            .withPackageMutations(packageMutations)
            .withProjectName(projectName);
    }

    public List<PackageMutations> getPackageMutations() {
        return packageMutations;
    }

    public static class Builder {
        private String projectName;
        private List<PackageMutations> packageMutations = new ArrayList<>();
        private Status status;

        private Builder() {
        }

        public Builder withProjectName(String projectName) {
            this.projectName = projectName;
            return this;
        }

        public Builder withPackageMutations(List<PackageMutations> packages) {
            this.packageMutations = packages.stream()
                    .sorted(
                            comparing(PackageMutations::getPackageName,
                                nullsLast(
                                    naturalOrder())))
                        .collect(Collectors.toList());
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectMutations that = (ProjectMutations) o;
        return Objects.equals(projectName, that.projectName) &&
            Objects.equals(packageMutations, that.packageMutations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectName, packageMutations);
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

}
