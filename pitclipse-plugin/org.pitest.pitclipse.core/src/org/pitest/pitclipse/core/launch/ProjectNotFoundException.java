package org.pitest.pitclipse.core.launch;

public final class ProjectNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -6545988416609531935L;

	public ProjectNotFoundException(String projectName) {
		super(projectName);
	}

}