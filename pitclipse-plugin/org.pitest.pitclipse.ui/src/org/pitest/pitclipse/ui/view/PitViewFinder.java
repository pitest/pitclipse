package org.pitest.pitclipse.ui.view;

import static com.google.common.collect.Sets.newHashSet;

import java.io.File;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.pitest.pitclipse.pitrunner.model.MutationsModel;
import org.pitest.pitclipse.ui.view.mutations.MutationsView;

public enum PitViewFinder {
	INSTANCE;

	private static final String PIT_SUMMARY_VIEW = "org.pitest.pitclipse.ui.view.PitView";
	private static final String PIT_MUTATIONS_VIEW = "org.pitest.pitclipse.ui.view.mutations.PitMutationsView";

	private static final class MissingViewException extends RuntimeException {
		private static final long serialVersionUID = 6672829886156086528L;

		public MissingViewException(Exception e) {
			super(e);
		}
	}

	private static final class ViewSearch implements Runnable {
		private static Set<String> initialisedViews = newHashSet();
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
			activateViewOnceAndOnceOnly(viewId);
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IViewPart view = activePage.findView(viewId);
			return view;
		}

		private static synchronized void activateViewOnceAndOnceOnly(String viewId) throws PartInitException {
			if (!initialisedViews.contains(viewId)) {
				IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				activePage.showView(viewId);
				initialisedViews.add(viewId);
			}
		}

		public <T extends IViewPart> T getView() {
			return asT(viewRef.get());
		}

		@SuppressWarnings("unchecked")
		private <T extends IViewPart> T asT(IViewPart viewPart) {
			return (T) viewPart;
		}
	}

	public SummaryView getSummaryView() {
		if (null == getView(PIT_SUMMARY_VIEW))
			return NoOpSummaryView.INSTANCE;
		else
			return getView(PIT_SUMMARY_VIEW);
	}

	public MutationsView getMutationsView() {
		if (null == getView(PIT_MUTATIONS_VIEW))
			return NoOpMutationsView.INSTANCE;
		else
			return getView(PIT_MUTATIONS_VIEW);
	}

	private <T extends IViewPart> T getView(String viewId) {
		ViewSearch viewSearch = new ViewSearch(viewId);
		Display.getDefault().syncExec(viewSearch);
		return viewSearch.getView();
	}

	private enum NoOpSummaryView implements SummaryView {
		INSTANCE;
		@Override
		public void update(File result) {
			// Do nothing
		}
	}

	private enum NoOpMutationsView implements MutationsView {
		INSTANCE;
		@Override
		public void updateWith(MutationsModel mutations) {
			// Do nothing
		}
	}
}