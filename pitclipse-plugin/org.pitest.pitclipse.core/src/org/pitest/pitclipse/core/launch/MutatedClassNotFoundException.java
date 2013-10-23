package org.pitest.pitclipse.core.launch;

public final class MutatedClassNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 8701338270511815060L;

	public MutatedClassNotFoundException(String name) {
		super(name);
	}

}