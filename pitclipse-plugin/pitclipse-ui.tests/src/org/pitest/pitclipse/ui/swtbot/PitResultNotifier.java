package org.pitest.pitclipse.ui.swtbot;

import static org.pitest.pitclipse.ui.util.VerifyUtil.isNull;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.pitest.pitclipse.ui.extension.PitResultComponent;
import org.pitest.pitclipse.ui.extension.ResultNotifier;

public class PitResultNotifier implements ResultNotifier<PitResultComponent>,
		ProgressListener {
	private Browser browser = null;

	public synchronized void handleResults(PitResultComponent results) {
		Browser browser = results.getBrowser();
		if (isNull(this.browser)) {
			this.browser = browser;
			this.browser.addProgressListener(this);
		}
	}

	public void changed(ProgressEvent event) {
		// Do nothing
	}

	public void completed(ProgressEvent event) {
		String text = browser.getText();
		ResultsParser parser = new ResultsParser(text);
		PitResultsView view = PitResultsView.builder()
				.withTotalCoverage(parser.getSummary().getCodeCoverage())
				.build();
		try {
			PitNotifier.INSTANCE.notifyResults(view);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
