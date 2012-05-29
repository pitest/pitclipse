package org.pitest.pitclipse.ui;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.junit.Ignore;
import org.junit.Test;

public class PITPluginTest {

	@Ignore
	@Test
	public void test() {
		IWorkbench workbench = PlatformUI.getWorkbench();

		IWorkbenchWindow active = workbench.getActiveWorkbenchWindow();
	}

}
