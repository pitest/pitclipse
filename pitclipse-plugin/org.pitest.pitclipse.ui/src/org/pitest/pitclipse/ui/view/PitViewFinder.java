package org.pitest.pitclipse.ui.view;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.pitest.pitclipse.ui.view.mutations.PitMutationsView;

public final class PitViewFinder {

	private static final String PIT_SUMMARY_VIEW = "org.pitest.pitclipse.ui.view.PitView";
	private static final String PIT_MUTATIONS_VIEW = "org.pitest.pitclipse.ui.view.mutations.PitMutationsView";

	private static final class MissingViewException extends RuntimeException {
		private static final long serialVersionUID = 6672829886156086528L;

		public MissingViewException(Exception e) {
			super(e);
		}
	}

	private static final class ViewSearch implements Runnable {
		private final AtomicReference<IViewPart> viewRef = new AtomicReference<IViewPart>();
		private final String viewId;

		public ViewSearch(String viewId) {
			this.viewId = viewId;
		}

		@Override
		public void run() {
			IViewPart summaryView = findView(viewId);
			viewRef.set(summaryView);
		}

		private IViewPart findView(String viewId) {
			try {
				return tryFindView(viewId);
			} catch (PartInitException e) {
				throw new MissingViewException(e);
			}
		}

		private IViewPart tryFindView(String viewId) throws PartInitException {
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			activePage.showView(viewId);
			IViewPart view = activePage.findView(viewId);
			activePage.activate(view);
			return view;
		}

		public <T extends IViewPart> T getView() {
			return asT(viewRef.get());
		}

		@SuppressWarnings("unchecked")
		private <T extends IViewPart> T asT(IViewPart viewPart) {
			return (T) viewPart;
		}
	}

	public PitView getSummaryView() {
		return getView(PIT_SUMMARY_VIEW);
	}

	public PitMutationsView getMutationsView() {
		return getView(PIT_MUTATIONS_VIEW);
	}

	private <T extends IViewPart> T getView(String viewId) {
		ViewSearch viewSearch = new ViewSearch(viewId);
		Display.getDefault().syncExec(viewSearch);
		return viewSearch.getView();
	}
}