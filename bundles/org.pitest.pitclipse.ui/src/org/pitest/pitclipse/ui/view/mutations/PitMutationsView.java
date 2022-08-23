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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.pitest.pitclipse.runner.model.MutationsModel;
import org.pitest.pitclipse.ui.utils.PitclipseUiUtils;

public class PitMutationsView extends ViewPart implements MutationsView {

    private static final int TREE_STYLE = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
    private TreeViewer viewer;

    public static final String EXPAND_ALL_BUTTON_TEXT = "Expand All";
    public static final String COLLAPSE_ALL_BUTTON_TEXT = "Collapse All";

    private static final ImageDescriptor EXPAND_ALL = PitclipseUiUtils.getBundleImage("expandall.png");
    private static final ImageDescriptor COLLAPSE_ALL = PitclipseUiUtils.getBundleImage("collapseall.png");

    @Override
    public void createPartControl(Composite parent) {
        createTreeViewer(parent);

        IActionBars actionBars = getViewSite().getActionBars();
        IToolBarManager toolBar = actionBars.getToolBarManager();

        final Action expandAllAction = new Action(EXPAND_ALL_BUTTON_TEXT) {
            @Override
            public void run() {
                viewer.expandAll();
            }
        };
        expandAllAction.setImageDescriptor(EXPAND_ALL);
        toolBar.add(expandAllAction);

        final Action collapseAllAction = new Action(COLLAPSE_ALL_BUTTON_TEXT) {
            @Override
            public void run() {
                viewer.collapseAll();
            }
        };
        collapseAllAction.setImageDescriptor(COLLAPSE_ALL);
        toolBar.add(collapseAllAction);
    }

    private void createTreeViewer(Composite parent) {
        viewer = new TreeViewer(parent, TREE_STYLE);
        viewer.setContentProvider(new ViewContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());
        viewer.addDoubleClickListener(ExpandingDoubleClick.LISTENER);
        viewer.addDoubleClickListener(OpenMutationDoubleClick.LISTENER);
        viewer.setInput(MutationsModel.EMPTY_MODEL);
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    @Override
    public void updateWith(MutationsModel mutations) {
        Display.getDefault().asyncExec(new ViewUpdater(mutations));
    }

    private class ViewUpdater implements Runnable {
        private final MutationsModel mutations;

        private ViewUpdater(MutationsModel mutations) {
            this.mutations = mutations;
        }

        @Override
        public void run() {
            // better not to expand the view to avoid UI freeze
            // see https://github.com/pitest/pitclipse/issues/147
            viewer.setInput(mutations);
        }
    }

}
