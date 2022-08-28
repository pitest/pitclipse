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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PackageMutations implements Visitable, Countable {
    private final String packageName;
    private final List<ClassMutations> classMutations;
    private final ProjectMutations projectMutations;

    private PackageMutations(ProjectMutations projectMutations, String packageName,
            List<ClassMutations> mutations) {
        this.projectMutations = projectMutations;
        this.packageName = packageName;
        this.classMutations = mutations.stream()
                .map(input -> input.copyOf().withPackageMutations(PackageMutations.this).build())
                .collect(Collectors.toList());
    }

    public String getPackageName() {
        return packageName;
    }

    public List<ClassMutations> getClassMutations() {
        return classMutations;
    }

    public ProjectMutations getProjectMutations() {
        return projectMutations;
    }

    @Override
    public <T> T accept(MutationsModelVisitor<T> visitor) {
        return visitor.visitPackage(this);
    }

    public static class Builder {
        private String packageName;
        private List<ClassMutations> mutations;
        private ProjectMutations projectMutations;

        private Builder() {
        }

        public Builder withPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder withClassMutations(Collection<ClassMutations> mutations) {
            this.mutations = mutations.stream()
                    .sorted(
                            comparing(ClassMutations::getClassName,
                                nullsLast(
                                    naturalOrder())))
                        .collect(Collectors.toList());
            return this;
        }

        public PackageMutations build() {
            return new PackageMutations(projectMutations, packageName, mutations);
        }

        public Builder withProjectMutations(ProjectMutations projectMutations) {
            this.projectMutations = projectMutations;
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copyOf() {
        return builder().withClassMutations(classMutations).withPackageName(packageName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PackageMutations that = (PackageMutations) o;
        return Objects.equals(packageName, that.packageName) &&
            Objects.equals(classMutations, that.classMutations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, classMutations);
    }


    @Override
    public String toString() {
        return "PackageMutations [packageName=" + packageName + ", classMutations=" + classMutations + "]";
    }

    @Override
    public long count() {
        long sum = 0L;
        for (ClassMutations classMutation : classMutations) {
            sum += classMutation.count();
        }
        return sum;
    }
}
