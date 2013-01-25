package org.pitest.pitclipse.core.launch;

public final class TestClassNotFoundException extends
		RuntimeException {

	private static final long serialVersionUID = 1708246133941190992L;

	public TestClassNotFoundException(String name) {
		super(name);
	}

}