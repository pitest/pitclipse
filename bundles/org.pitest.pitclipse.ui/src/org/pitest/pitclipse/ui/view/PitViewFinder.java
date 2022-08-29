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

package org.pitest.pitclipse.ui.view;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.pitest.pitclipse.runner.model.MutationsModel;
import org.pitest.pitclipse.ui.utils.PitclipseUiUtils;
import org.pitest.pitclipse.ui.view.mutations.MutationsView;
import org.pitest.pitclipse.ui.view.mutations.PitMutationsView;

/**
 * Singleton making easier to find Pitclipse views. 
 */
public enum PitViewFinder {
    INSTANCE;

    private static final String PIT_SUMMARY_VIEW = PitView.VIEW_ID;
    private static final String PIT_MUTATIONS_VIEW = PitMutationsView.VIEW_ID;

    private static final class ViewSearch implements Runnable {
        private static Set<String> initialisedViews = new HashSet<>();
        private final AtomicReference<IViewPart> viewRef = new AtomicReference<>();
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
            return PitclipseUiUtils.executeViewSafelyOrThrow(
                    () -> tryFindView(viewId));
        }

        private IViewPart tryFindView(String viewId) throws PartInitException {
            activateViewOnceAndOnceOnly(viewId);
            IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            return activePage.findView(viewId);
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
        if (null == getView(PIT_SUMMARY_VIEW)) {
            return NoOpSummaryView.INSTANCE;
        } else {
            return getView(PIT_SUMMARY_VIEW);
        }
    }

    public MutationsView getMutationsView() {
        if (null == getView(PIT_MUTATIONS_VIEW)) {
            return NoOpMutationsView.INSTANCE;
        } else {
            return getView(PIT_MUTATIONS_VIEW);
        }
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
