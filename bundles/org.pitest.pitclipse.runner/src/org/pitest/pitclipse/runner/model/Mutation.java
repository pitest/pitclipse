package org.pitest.pitclipse.runner.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.pitest.pitclipse.runner.results.DetectionStatus;

public class Mutation implements Visitable {

    private final String killingTest;
    private final int lineNumber;
    private final String mutatedMethod;
    private final String mutator;
    private final DetectionStatus status;
    private final String description;
    private final ClassMutations classMutations;

    private Mutation(ClassMutations classMutations, String killingTest, int lineNumber, String mutatedMethod,
            String mutator, DetectionStatus status, String description) {
        this.classMutations = classMutations;
        this.killingTest = killingTest;
        this.lineNumber = lineNumber;
        this.mutatedMethod = mutatedMethod;
        this.mutator = mutator;
        this.status = status;
        this.description = description;
    }

    @Override
    public <T> T accept(MutationsModelVisitor<T> visitor) {
        return visitor.visitMutation(this);
    }

    public String getKillingTest() {
        return killingTest;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getMutatedMethod() {
        return mutatedMethod;
    }

    public String getMutator() {
        return mutator;
    }

    public DetectionStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public ClassMutations getClassMutations() {
        return classMutations;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copyOf() {
        return new Builder().withDescription(description).withKillingTest(killingTest).withLineNumber(lineNumber)
                .withMutatedMethod(mutatedMethod).withMutator(mutator).withStatus(status);
    }

    public static class Builder {
        private String killingTest;
        private int lineNumber;
        private String mutatedMethod;
        private String mutator;
        private DetectionStatus status;
        private String description;
        private ClassMutations classMutations;

        private Builder() {
        }

        public Mutation build() {
            return new Mutation(classMutations, killingTest, lineNumber, mutatedMethod, mutator, status, description);
        }

        public Builder withKillingTest(String killingTest) {
            this.killingTest = killingTest;
            return this;
        }

        public Builder withLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public Builder withMutatedMethod(String mutatedMethod) {
            this.mutatedMethod = mutatedMethod;
            return this;
        }

        public Builder withMutator(String mutator) {
            this.mutator = mutator;
            return this;
        }

        public Builder withStatus(DetectionStatus status) {
            this.status = status;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withClassMutation(ClassMutations classMutations) {
            this.classMutations = classMutations;
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
        Mutation mutation = (Mutation) o;
        return lineNumber == mutation.lineNumber &&
            Objects.equal(killingTest, mutation.killingTest) &&
            Objects.equal(mutatedMethod, mutation.mutatedMethod) &&
            Objects.equal(mutator, mutation.mutator) &&
            status == mutation.status &&
            Objects.equal(description, mutation.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(killingTest, lineNumber, mutatedMethod, mutator, status, description);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("killingTest", killingTest)
            .add("lineNumber", lineNumber)
            .add("mutatedMethod", mutatedMethod)
            .add("mutator", mutator)
            .add("status", status)
            .add("description", description)
            .toString();
    }
}