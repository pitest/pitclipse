package org.pitest.pitclipse.pitrunner.results;

public interface Dispatcher<T> {
	void dispatch(T result);
}
