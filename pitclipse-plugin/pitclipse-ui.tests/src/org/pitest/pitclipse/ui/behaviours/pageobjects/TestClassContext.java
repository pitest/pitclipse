package org.pitest.pitclipse.ui.behaviours.pageobjects;

public class TestClassContext {

	private final String testClassName;
	private final String packageName;
	private final String projectName;

	public TestClassContext(String testClassName, String packageName,
			String projectName) {
		this.testClassName = testClassName;
		this.packageName = packageName;
		this.projectName = projectName;
	}

	public String getTestClassName() {
		return testClassName;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getFullyQUalifiedTestClassName() {
		return packageName + "." + testClassName;
	}
}
