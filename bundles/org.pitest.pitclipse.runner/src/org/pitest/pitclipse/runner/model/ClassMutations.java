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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClassMutations implements Visitable, Countable {
    private final String className;
    private final List<Mutation> mutations;
    private final PackageMutations packageMutations;

    private ClassMutations(PackageMutations packageMutations, String className, List<Mutation> mutations) {
        this.packageMutations = packageMutations;
        this.className = className;
        this.mutations = mutations.stream()
            .map(input -> input.copyOf().withClassMutation(ClassMutations.this).build())
            .collect(Collectors.toList());
    }

    @Override
    public <T> T accept(MutationsModelVisitor<T> visitor) {
        return visitor.visitClass(this);
    }

    public String getClassName() {
        return className;
    }

    public List<Mutation> getMutations() {
        return mutations;
    }

    public PackageMutations getPackageMutations() {
        return packageMutations;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copyOf() {
        return new Builder().withClassName(className).withMutations(mutations);
    }

    public static class Builder {
        private String className;
        private List<Mutation> mutations = new ArrayList<>();
        private PackageMutations packageMutations;

        private Builder() {
        }

        public Builder withClassName(String className) {
            this.className = className;
            return this;
        }

        public Builder withMutations(Collection<Mutation> mutations) {
            this.mutations = mutations.stream()
                    .sorted(MutationComparator.INSTANCE)
                    .collect(Collectors.toList());
            return this;
        }

        public ClassMutations build() {
            return new ClassMutations(packageMutations, className, mutations);
        }

        private enum MutationComparator implements Comparator<Mutation> {
            INSTANCE;

            @Override
            public int compare(Mutation lhs, Mutation rhs) {
                if (lhs.getLineNumber() != rhs.getLineNumber()) {
                    return Integer.compare(lhs.getLineNumber(), rhs.getLineNumber());
                }
                return lhs.getDescription().compareTo(rhs.getDescription());
            }
        }

        public Builder withPackageMutations(PackageMutations packageMutations) {
            this.packageMutations = packageMutations;
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
        ClassMutations that = (ClassMutations) o;
        return Objects.equals(className, that.className) &&
            Objects.equals(mutations, that.mutations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, mutations);
    }



    @Override
    public String toString() {
        return "ClassMutations [className=" + className + ", mutations=" + mutations + "]";
    }

    @Override
    public long count() {
        return mutations.size();
    }

}
