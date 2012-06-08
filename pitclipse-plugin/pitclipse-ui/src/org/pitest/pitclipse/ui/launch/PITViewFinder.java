package org.pitest.pitclipse.ui.launch;

import static org.pitest.pitclipse.ui.launch.PITClipseConstants.PIT_VIEW;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.pitest.pitclipse.ui.view.PITView;

public final class PITViewFinder {

	public PITView getView() {
		final AtomicReference<PITView> viewRef = new AtomicReference<PITView>();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					IWorkbenchPage activePage = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					activePage.showView(PIT_VIEW);
					PITView view = (PITView) activePage.findView(PIT_VIEW);
					activePage.activate(view);
					viewRef.set(view);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});
		return viewRef.get();
	}
}