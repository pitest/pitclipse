package org.pitest.pitclipse.ui.launch;

import static org.pitest.pitclipse.ui.launch.PITClipseConstants.PIT_VIEW;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.pitest.pitclipse.ui.view.PITView;

public final class PITViewFinder {
	
	private static final class MissingViewException extends RuntimeException {
		private static final long serialVersionUID = 6672829886156086528L;
		
		public MissingViewException(Exception e) {
			super(e);
		}
	}

	private static final class ViewSearch implements Runnable {
		private final AtomicReference<PITView> viewRef;

		private ViewSearch() {
			this.viewRef = new AtomicReference<PITView>();
		}

		public void run() {
			try {
				IWorkbenchPage activePage = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				activePage.showView(PIT_VIEW);
				PITView view = (PITView) activePage.findView(PIT_VIEW);
				activePage.activate(view);
				viewRef.set(view);
			} catch (PartInitException e) {
				throw new MissingViewException(e);
			}
		}

		public PITView getView() {
			return viewRef.get();
		}
	}

	public PITView getView() {
		ViewSearch viewSearch = new ViewSearch();
		Display.getDefault().syncExec(viewSearch);
		return viewSearch.getView();
	}
}