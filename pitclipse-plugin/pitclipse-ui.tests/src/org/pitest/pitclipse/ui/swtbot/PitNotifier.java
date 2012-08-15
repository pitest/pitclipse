package org.pitest.pitclipse.ui.swtbot;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public enum PitNotifier {
	INSTANCE;

	private final BlockingQueue<PitResultsView> resultQueue = new ArrayBlockingQueue<PitResultsView>(
			1);

	public PitResultsView getResults() throws InterruptedException {
		return resultQueue.take();
	}

	public void notifyResults(PitResultsView resultsView)
			throws InterruptedException {
		resultQueue.put(resultsView);
	}

	public void reset() {
		resultQueue.clear();
	}
}
