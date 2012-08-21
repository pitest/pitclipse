package org.pitest.pitclipse.ui.extension;

public interface ResultNotifier<T> {
	void handleResults(T results);
}
