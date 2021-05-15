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

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import static com.google.common.collect.Collections2.transform;

import java.util.Objects;

public class PackageMutations implements Visitable, Countable {
    private final String packageName;
    private final ImmutableList<ClassMutations> classMutations;
    private final ProjectMutations projectMutations;

    private PackageMutations(ProjectMutations projectMutations, String packageName,
            ImmutableList<ClassMutations> mutations) {
        this.projectMutations = projectMutations;
        this.packageName = packageName;
        this.classMutations = ImmutableList.copyOf(transform(mutations,
                input -> input.copyOf().withPackageMutations(PackageMutations.this).build()));
    }

    public String getPackageName() {
        return packageName;
    }

    public ImmutableList<ClassMutations> getClassMutations() {
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
        private ImmutableList<ClassMutations> mutations;
        private ProjectMutations projectMutations;

        private Builder() {
        }

        public Builder withPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder withClassMutations(Iterable<ClassMutations> mutations) {
            this.mutations = Ordering.natural().nullsLast().onResultOf(ClassName.GET).immutableSortedCopy(mutations);
            return this;
        }

        public PackageMutations build() {
            return new PackageMutations(projectMutations, packageName, mutations);
        }

        public Builder withProjectMutations(ProjectMutations projectMutations) {
            this.projectMutations = projectMutations;
            return this;
        }

        private enum ClassName implements Function<ClassMutations, String> {
            GET;

            @Override
            public String apply(ClassMutations input) {
                return input.getClassName();
            }
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
        return MoreObjects.toStringHelper(this)
            .add("packageName", packageName)
            .add("classMutations", classMutations)
            .toString();
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
