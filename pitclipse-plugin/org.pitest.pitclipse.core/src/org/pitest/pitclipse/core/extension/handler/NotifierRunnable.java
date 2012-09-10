package org.pitest.pitclipse.core.extension.handler;

import org.pitest.pitclipse.core.extension.point.ResultNotifier;

public final class NotifierRunnable<U> implements Runnable {
	private final U results;
	private final ResultNotifier<U> notifier;

	public NotifierRunnable(ResultNotifier<U> notifier, U results) {
		this.results = results;
		this.notifier = notifier;
	}

	public void run() {
		notifier.handleResults(results);
	}
}