package org.pitest.pitclipse.ui.view;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public final class PitViewFinder {

	private static final String PIT_VIEW = "org.pitest.pitclipse.ui.view.PitView";

	private static final class MissingViewException extends RuntimeException {
		private static final long serialVersionUID = 6672829886156086528L;

		public MissingViewException(Exception e) {
			super(e);
		}
	}

	private static final class ViewSearch implements Runnable {
		private final AtomicReference<PitView> viewRef;

		private ViewSearch() {
			viewRef = new AtomicReference<PitView>();
		}

		public void run() {
			try {
				IWorkbenchPage activePage = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				activePage.showView(PIT_VIEW);
				PitView view = (PitView) activePage.findView(PIT_VIEW);
				activePage.activate(view);
				viewRef.set(view);
			} catch (PartInitException e) {
				throw new MissingViewException(e);
			}
		}

		public PitView getView() {
			return viewRef.get();
		}
	}

	public PitView getView() {
		ViewSearch viewSearch = new ViewSearch();
		Display.getDefault().syncExec(viewSearch);
		return viewSearch.getView();
	}
}