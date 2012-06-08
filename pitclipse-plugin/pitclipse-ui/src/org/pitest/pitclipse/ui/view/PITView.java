package org.pitest.pitclipse.ui.view;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.part.ViewPart;

public class PITView extends ViewPart {

	private Browser browser;
	private File currentReportDirectory;

	@Override
	public void createPartControl(Composite parent) {
		try {
			browser = new Browser(parent, SWT.NONE);
		} catch (SWTError e) {
			MessageBox messageBox = new MessageBox(parent.getShell(),
					SWT.ICON_ERROR | SWT.OK);
			messageBox.setMessage("Browser cannot be initialized.");
			messageBox.setText("Exit");
			messageBox.open();
			System.exit(-1);
		}
	}

	@Override
	public void setFocus() {
	}

	public synchronized void update(File result) {
		currentReportDirectory = new File(result.toURI());
		File reportFile = findResultFile(currentReportDirectory);
		if (reportFile == null) {
			browser.setText("<html/>");
		} else {
			browser.setUrl(reportFile.toURI().toString());
		}
	}

	private File findResultFile(File reportDir) {
		for (File file : reportDir.listFiles()) {
			if (file.isDirectory()) {
				File result = findResultFile(file);
				if (null != result) {
					return result;
				}
			}
			if ("index.html".equals(file.getName())) {
				return file;
			}
		}
		return null;
	}

}
