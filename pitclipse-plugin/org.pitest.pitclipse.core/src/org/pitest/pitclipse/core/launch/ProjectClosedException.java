package org.pitest.pitclipse.core.launch;

public final class ProjectClosedException extends RuntimeException {

	private static final long serialVersionUID = -5291861390444875055L;

	public ProjectClosedException(String projectName) {
		super(projectName);
	}

}