package org.pitest.pitclipse.ui.view;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.part.ViewPart;

public class PITView extends ViewPart {

	private Browser browser = null;
	private File currentReportDirectory = null;

	@Override
	public synchronized void createPartControl(Composite parent) {
		try {
			browser = new Browser(parent, SWT.NONE);
		} catch (SWTError e) {
			MessageBox messageBox = new MessageBox(parent.getShell(),
					SWT.ICON_ERROR | SWT.OK);
			messageBox.setMessage("Browser cannot be initialized.");
			messageBox.setText("Exit");
			messageBox.open();
		}
	}

	@Override
	public void setFocus() {
	}

	public synchronized void update(File result) {
		clearDown(currentReportDirectory);
		currentReportDirectory = new File(result.toURI());
		File reportFile = findResultFile(currentReportDirectory);
		if (reportFile == null) {
			browser.setText("<html/>");
		} else {
			browser.setUrl(reportFile.toURI().toString());
		}
	}

	private void clearDown(File directory) {
		if (null != directory) {
			for (File file : directory.listFiles()) {
				if (file.isDirectory()) {
					clearDown(file);
				}
				file.delete();
			}
		}
	}

	private File findResultFile(File reportDir) {
		// Does this directory contain the file?
		for (File file : reportDir.listFiles()) {
			if (!file.isDirectory() && "index.html".equals(file.getName())) {
				return file;
			}
		}
		for (File file : reportDir.listFiles()) {
			if (file.isDirectory()) {
				File result = findResultFile(file);
				if (null != result) {
					return result;
				}
			}
		}
		return null;
	}

}
