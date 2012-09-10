package org.pitest.pitclipse.ui.view;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.pitest.pitclipse.core.extension.handler.ExtensionPointHandler;
import org.pitest.pitclipse.ui.extension.point.PitUiUpdate;

public class PitUiUpdatePublisher implements ProgressListener {

	private static final String EXTENSION_POINT_ID = "org.pitest.pitclipse.ui.results";
	private final Browser browser;
	private final ExtensionPointHandler<PitUiUpdate> handler;

	public PitUiUpdatePublisher(Browser browser) {
		this.browser = browser;
		handler = new ExtensionPointHandler<PitUiUpdate>(EXTENSION_POINT_ID);
	}

	public void changed(ProgressEvent event) {
		// Do nothing
	}

	public void completed(ProgressEvent event) {
		PitUiUpdate update = new PitUiUpdate.Builder().withHtml(
				browser.getText()).build();
		handler.execute(Platform.getExtensionRegistry(), update);
	}

}
