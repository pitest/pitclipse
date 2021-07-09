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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.pitest.pitclipse.runner.model.MutationsModel;

public class PitMutationsView extends ViewPart implements MutationsView {

    private static final int TREE_STYLE = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
    private TreeViewer viewer;

    @Override
    public void createPartControl(Composite parent) {
        createTreeViewer(parent);
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
            viewer.setInput(mutations);
            viewer.expandAll();
        }
    }
}
