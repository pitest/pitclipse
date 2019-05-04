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

package org.pitest.pitclipse.ui.swtbot;

import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.ui.PitclipseTestActivator;
import org.pitest.pitclipse.ui.extension.point.PitUiUpdate;
import org.pitest.pitclipse.ui.swtbot.ResultsParser.Summary;

public class PitResultNotifier implements ResultNotifier<PitUiUpdate> {
    @Override
    public void handleResults(PitUiUpdate updateEvent) {
        if (testsAreInProgress())
            notifiyTestsOfHtmlResults(updateEvent);
    }

    private void notifiyTestsOfHtmlResults(PitUiUpdate updateEvent) {
        PitResultsView view = buildResultsView(updateEvent);
        tryNotifyResults(view);
    }

    private PitResultsView buildResultsView(PitUiUpdate results) {
        ResultsParser parser = new ResultsParser(results.getHtml());
        Summary summary = parser.getSummary();
        PitResultsView view = PitResultsView.builder().withClassesTested(summary.getClasses())
                .withTotalCoverage(summary.getCodeCoverage()).withMutationCoverage(summary.getMutationCoverage())
                .build();
        return view;
    }

    private void tryNotifyResults(PitResultsView view) {
        try {
            PitNotifier.INSTANCE.notifyResults(view);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean testsAreInProgress() {
        return PitclipseTestActivator.getDefault().areTestsInProgress();
    }
}
