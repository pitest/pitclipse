package org.pitest.pitclipse.runner.model;

public interface Visitable {
    <T> T accept(MutationsModelVisitor<T> visitor);
}
