package org.pitest.pitclipse.pitrunner.model;

import org.pitest.pitclipse.reloc.guava.base.Function;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.Ordering;

import static org.pitest.pitclipse.reloc.guava.collect.Collections2.transform;

public class PackageMutations implements Visitable, Countable {
    private final String packageName;
    private final ImmutableList<ClassMutations> classMutations;
    private final ProjectMutations projectMutations;

    private PackageMutations(ProjectMutations projectMutations, String packageName,
            ImmutableList<ClassMutations> mutations) {
        this.projectMutations = projectMutations;
        this.packageName = packageName;
        this.classMutations = ImmutableList.copyOf(transform(mutations, new Function<ClassMutations, ClassMutations>() {
            @Override
            public ClassMutations apply(ClassMutations input) {
                return input.copyOf().withPackageMutations(PackageMutations.this).build();
            }
        }));
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

        if (packageName != null ? !packageName.equals(that.packageName) : that.packageName != null) {
            return false;
        }
        return classMutations != null ? classMutations.equals(that.classMutations) : that.classMutations == null;

    }

    @Override
    public int hashCode() {
        int result = packageName != null ? packageName.hashCode() : 0;
        result = 31 * result + (classMutations != null ? classMutations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PackageMutations{" +
                "packageName='" + packageName + '\'' +
                ", classMutations=" + classMutations +
                '}';
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