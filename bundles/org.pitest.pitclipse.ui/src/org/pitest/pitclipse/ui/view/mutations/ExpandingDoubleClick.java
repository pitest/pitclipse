/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

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
