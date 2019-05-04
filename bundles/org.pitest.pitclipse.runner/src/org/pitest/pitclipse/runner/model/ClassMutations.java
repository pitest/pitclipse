package org.pitest.pitclipse.pitrunner.model;

import org.pitest.pitclipse.reloc.guava.base.Function;
import org.pitest.pitclipse.reloc.guava.base.MoreObjects;
import org.pitest.pitclipse.reloc.guava.base.Objects;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.Ordering;

import java.util.Comparator;
import java.util.List;

import static org.pitest.pitclipse.reloc.guava.collect.Collections2.transform;

public class ClassMutations implements Visitable, Countable {
    private final String className;
    private final ImmutableList<Mutation> mutations;
    private final PackageMutations packageMutations;

    private ClassMutations(PackageMutations packageMutations, String className, ImmutableList<Mutation> mutations) {
        this.packageMutations = packageMutations;
        this.className = className;
        this.mutations = ImmutableList.copyOf(transform(mutations, new Function<Mutation, Mutation>() {
            @Override
            public Mutation apply(Mutation input) {
                return input.copyOf().withClassMutation(ClassMutations.this).build();
            }
        }));
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
        private ImmutableList<Mutation> mutations = ImmutableList.of();
        private PackageMutations packageMutations;

        private Builder() {
        }

        public Builder withClassName(String className) {
            this.className = className;
            return this;
        }

        public Builder withMutations(Iterable<Mutation> mutations) {
            this.mutations = Ordering.from(MutationComparator.INSTANCE).immutableSortedCopy(mutations);
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
                    return Ordering.natural().compare(lhs.getLineNumber(), rhs.getLineNumber());
                }
                return Ordering.natural().compare(lhs.getDescription(), rhs.getDescription());
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
        return Objects.equal(className, that.className) &&
            Objects.equal(mutations, that.mutations);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(className, mutations);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("className", className)
            .add("mutations", mutations)
            .toString();
    }

    @Override
    public long count() {
        return mutations.size();
    }

}
