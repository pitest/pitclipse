package org.pitest.pitclipse.ui.view.mutations;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

public enum ExpandingDoubleClick implements IDoubleClickListener {
	LISTENER;

	@Override
	public void doubleClick(DoubleClickEvent event) {
		Viewer viewer = Viewer.from(event);
		IStructuredSelection selection = selectionFrom(event);
		viewer.expand(selection);
	}

	private IStructuredSelection selectionFrom(DoubleClickEvent event) {
		return (IStructuredSelection) event.getSelection();
	}

	private static class Viewer {
		private final TreeViewer treeViewer;

		private Viewer(TreeViewer treeViewer) {
			this.treeViewer = treeViewer;
		}

		public void expand(IStructuredSelection selection) {
			Object selectedNode = selection.getFirstElement();
			treeViewer.setExpandedState(selectedNode, !treeViewer.getExpandedState(selectedNode));
		}

		public static Viewer from(DoubleClickEvent event) {
			return new Viewer(treeViewerFor(event));
		}

		private static TreeViewer treeViewerFor(DoubleClickEvent event) {
			return (TreeViewer) event.getViewer();
		}
	}
}
