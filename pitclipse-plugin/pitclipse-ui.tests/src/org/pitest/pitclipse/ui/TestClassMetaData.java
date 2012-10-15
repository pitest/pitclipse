package org.pitest.pitclipse.ui;

public final class TestClassMetaData {

	private final String projectName;
	private final String packageName;
	private final String className;
	private final String sourceDir;

	private TestClassMetaData(String projectName, String sourceDir,
			String packageName, String className) {
		this.projectName = projectName;
		this.sourceDir = sourceDir;
		this.packageName = packageName;
		this.className = className;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getClassName() {
		return className;
	}

	public String getTestClassName() {
		return className + "Test";
	}

	public static TestClassMetaData.Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String projectName;
		private String packageName;
		private String className;
		private String sourceDir;

		public TestClassMetaData build() {
			return new TestClassMetaData(projectName, sourceDir, packageName,
					className);
		}

		public Builder withClass(String className) {
			this.className = className;
			return this;
		}

		public Builder withProject(String projectName) {
			this.projectName = projectName;
			return this;
		}

		public Builder withPackage(String packageName) {
			this.packageName = packageName;
			return this;
		}

		public Builder withSrcDir(String sourceDir) {
			this.sourceDir = sourceDir;
			return this;
		}
	}
}