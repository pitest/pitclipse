package org.pitest.pitclipse.ui.behaviours.pageobjects;

public class AbstractClassContext implements ClassContext {

	protected final String className;
	protected final String packageName;
	protected final String projectName;
	private final String sourceDir;

	protected AbstractClassContext(String className, String packageName,
			String projectName, String sourceDir) {
		this.className = className;
		this.packageName = packageName;
		this.projectName = projectName;
		this.sourceDir = sourceDir;
	}

	public String getClassName() {
		return className;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getFullyQualifiedTestClassName() {
		return packageName + "." + className;
	}

	public String getSourceDir() {
		return sourceDir;
	}

}