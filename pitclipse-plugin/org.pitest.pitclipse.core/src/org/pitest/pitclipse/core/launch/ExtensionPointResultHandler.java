package org.pitest.pitclipse.core.launch;

import org.eclipse.swt.widgets.Display;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.client.PitResultHandler;

public class ExtensionPointResultHandler implements PitResultHandler {

	public void handle(PitResults results) {
		Display.getDefault().asyncExec(new UpdateExtensions(results));
	}

}
