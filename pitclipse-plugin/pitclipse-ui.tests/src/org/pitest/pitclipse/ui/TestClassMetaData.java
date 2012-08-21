package org.pitest.pitclipse.ui;


public final class TestClassMetaData {

	private final String projectName;
	private final String packageName;
	private final String className;

	private TestClassMetaData(String projectName, String packageName,
			String className) {
		this.projectName = projectName;
		this.packageName = packageName;
		this.className = className;
	}

	public String getProjectName() {
		return projectName;
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

		public TestClassMetaData build() {
			return new TestClassMetaData(projectName, packageName, className);
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
	}
}