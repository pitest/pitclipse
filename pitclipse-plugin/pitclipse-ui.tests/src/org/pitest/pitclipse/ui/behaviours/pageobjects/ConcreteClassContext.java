package org.pitest.pitclipse.ui.behaviours.pageobjects;

public class ConcreteClassContext extends AbstractClassContext {

    private final String method;

    protected ConcreteClassContext(String className, String packageName,
            String projectName, String method, String sourceDir) {
        super(className, packageName, projectName, sourceDir);
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public static class Builder {

        private String className;
        private String packageName;
        private String projectName;
        private String method;
        private String sourceDir;

        public Builder() {
        };

        public Builder withClassName(String className) {
            this.className = className;
            return this;
        }

        public Builder withPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder withProjectName(String projectName) {
            this.projectName = projectName;
            return this;
        }

        public Builder withMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder withSourceDir(String sourceDir) {
            this.sourceDir = sourceDir;
            return this;
        }

        public ConcreteClassContext build() {
            return new ConcreteClassContext(className, packageName,
                    projectName, method, sourceDir);
        }

        public Builder clone(ConcreteClassContext context) {
            className = context.getClassName();
            packageName = context.getPackageName();
            projectName = context.getProjectName();
            method = context.getMethod();
            sourceDir = context.getSourceDir();
            return this;
        }

    }

}