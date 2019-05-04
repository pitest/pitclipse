package org.pitest.pitclipse.runner.results;

public interface Dispatcher<T> {
    void dispatch(T result);
}
