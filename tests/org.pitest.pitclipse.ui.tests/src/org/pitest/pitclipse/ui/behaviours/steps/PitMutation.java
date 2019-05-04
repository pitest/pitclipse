package org.pitest.pitclipse.ui.behaviours.steps;

import static org.pitest.pitclipse.ui.behaviours.steps.TestFactory.TEST_FACTORY;

import org.pitest.pitclipse.pitrunner.results.DetectionStatus;

public class PitMutation {

    private final DetectionStatus status;
    private final String project;
    private final String pkg;
    private final String className;
    private final int lineNumber;
    private final String mutation;

    private PitMutation(DetectionStatus status, String project, String pkg, String className, int lineNumber,
            String mutation) {
        this.status = status;
        this.project = project;
        this.pkg = pkg;
        this.className = className;
        this.lineNumber = lineNumber;
        this.mutation = mutation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private DetectionStatus status = TEST_FACTORY.aDetectionStatus();
        private String project = TEST_FACTORY.aStringOfLength(8);
        private String pkg = TEST_FACTORY.aPackage();
        private String className = TEST_FACTORY.aClass();
        private int lineNumber = TEST_FACTORY.aRandomInt();
        private String mutation = TEST_FACTORY.aStringOfLength(50);

        private Builder() {
        }

        public Builder withStatus(DetectionStatus status) {
            this.status = status;
            return this;
        }

        public Builder withProject(String project) {
            this.project = project;
            return this;
        }

        public Builder withPackage(String pkg) {
            this.pkg = pkg;
            return this;
        }

        public Builder withClassName(String className) {
            this.className = className;
            return this;
        }

        public Builder withLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public Builder withMutation(String mutation) {
            this.mutation = mutation;
            return this;
        }

        public PitMutation build() {
            return new PitMutation(status, project, pkg, className, lineNumber, mutation);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + lineNumber;
        result = prime * result + ((mutation == null) ? 0 : mutation.hashCode());
        result = prime * result + ((pkg == null) ? 0 : pkg.hashCode());
        result = prime * result + ((project == null) ? 0 : project.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PitMutation other = (PitMutation) obj;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        if (lineNumber != other.lineNumber)
            return false;
        if (mutation == null) {
            if (other.mutation != null)
                return false;
        } else if (!mutation.equals(other.mutation))
            return false;
        if (pkg == null) {
            if (other.pkg != null)
                return false;
        } else if (!pkg.equals(other.pkg))
            return false;
        if (project == null) {
            if (other.project != null)
                return false;
        } else if (!project.equals(other.project))
            return false;
        if (status != other.status)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PitMutation [status=" + status + ", project=" + project + ", pkg=" + pkg + ", className=" + className
                + ", lineNumber=" + lineNumber + ", mutation=" + mutation + "]";
    }

    public DetectionStatus getStatus() {
        return status;
    }

    public String getProject() {
        return project;
    }

    public String getPkg() {
        return pkg;
    }

    public String getClassName() {
        return className;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getMutation() {
        return mutation;
    }
}
