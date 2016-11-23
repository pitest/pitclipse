package org.pitest.pitclipse.pitrunner.model;

import org.pitest.pitclipse.pitrunner.results.DetectionStatus;

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

        if (lineNumber != mutation.lineNumber) {
            return false;
        }
        if (killingTest != null ? !killingTest.equals(mutation.killingTest) : mutation.killingTest != null) {
            return false;
        }
        if (mutatedMethod != null ? !mutatedMethod.equals(mutation.mutatedMethod) : mutation.mutatedMethod != null) {
            return false;
        }
        if (mutator != null ? !mutator.equals(mutation.mutator) : mutation.mutator != null) {
            return false;
        }
        if (status != mutation.status) {
            return false;
        }
        return description != null ? description.equals(mutation.description) : mutation.description == null;

    }

    @Override
    public int hashCode() {
        int result = killingTest != null ? killingTest.hashCode() : 0;
        result = 31 * result + lineNumber;
        result = 31 * result + (mutatedMethod != null ? mutatedMethod.hashCode() : 0);
        result = 31 * result + (mutator != null ? mutator.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Mutation{" +
                "killingTest='" + killingTest + '\'' +
                ", lineNumber=" + lineNumber +
                ", mutatedMethod='" + mutatedMethod + '\'' +
                ", mutator='" + mutator + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}