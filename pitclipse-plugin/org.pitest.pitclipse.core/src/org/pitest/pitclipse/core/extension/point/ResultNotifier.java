package org.pitest.pitclipse.core.extension.point;

public interface ResultNotifier<T> {
	void handleResults(T results);
}
