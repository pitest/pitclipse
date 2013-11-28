package org.pitest.pitclipse.ui.view;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.part.ViewPart;

public class PitView extends ViewPart implements SummaryView {
	private Browser browser = null;
	private PitUiUpdatePublisher publisher = null;

	@Override
	public synchronized void createPartControl(Composite parent) {
		try {
			browser = new Browser(parent, SWT.NONE);
			publisher = new PitUiUpdatePublisher(browser);
			browser.addProgressListener(publisher);
		} catch (SWTError e) {
			MessageBox messageBox = new MessageBox(parent.getShell(), SWT.ICON_ERROR | SWT.OK);
			messageBox.setMessage("Browser cannot be initialized.");
			messageBox.setText("Exit");
			messageBox.open();
		}
	}

	@Override
	public void setFocus() {
	}

	@Override
	public synchronized void update(File result) {
		if (result == null) {
			browser.setText("<html/>");
		} else {
			browser.setUrl(result.toURI().toString());
		}
	}
}
