package org.pitest.pitclipse.pitrunner.model;

public interface Visitable {
	<T> T accept(MutationsModelVisitor<T> visitor);
}
