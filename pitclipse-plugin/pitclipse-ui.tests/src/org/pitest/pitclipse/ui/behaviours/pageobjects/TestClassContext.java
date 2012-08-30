package org.pitest.pitclipse.ui.behaviours.pageobjects;

public class TestClassContext extends AbstractClassContext {

	private final String classUnderTest;
	private final String testCase;

	private TestClassContext(String className, String packageName,
			String projectName, String classUnderTest, String testCase) {
		super(className, packageName, projectName);
		this.classUnderTest = classUnderTest;
		this.testCase = testCase;
	}

	public String getClassUnderTest() {
		return classUnderTest;
	}

	public String getTestCase() {
		return testCase;
	}

	public static class Builder {
		private String classUnderTest;
		private String className;
		private String packageName;
		private String projectName;
		private String testCase;

		public Builder withClassUnderTest(String classUnderTest) {
			this.classUnderTest = classUnderTest;
			return this;
		}

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

		public TestClassContext build() {
			return new TestClassContext(className, packageName, projectName,
					classUnderTest, testCase);
		}

		public Builder withTestCase(String testCase) {
			this.testCase = testCase;
			return this;
		}

		public Builder clone(TestClassContext context) {
			className = context.getClassName();
			classUnderTest = context.getClassUnderTest();
			packageName = context.getPackageName();
			projectName = context.getProjectName();
			testCase = context.getTestCase();
			return this;
		}
	}

}
