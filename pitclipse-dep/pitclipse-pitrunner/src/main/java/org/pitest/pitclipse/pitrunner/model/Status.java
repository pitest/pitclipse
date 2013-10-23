package org.pitest.pitclipse.pitrunner.model;

public class Status implements Visitable {
	@Override
	public <T> T accept(MutationsModelVisitor<T> visitor) {
		return visitor.visitStatus(this);
	}
}
