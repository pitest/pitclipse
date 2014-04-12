package org.pitest.pitclipse.pitrunner.results.mutations;

public interface Dispatcher<T> {
	void dispatch(T result);
}
