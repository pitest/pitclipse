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

package org.pitest.pitclipse.ui.behaviours.pageobjects;

import static org.pitest.pitclipse.ui.view.PitView.BACK_BUTTON_TEXT;
import static org.pitest.pitclipse.ui.view.PitView.FORWARD_BUTTON_TEXT;
import static org.pitest.pitclipse.ui.view.PitView.HOME_BUTTON_TEXT;

import java.util.List;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotBrowser;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.pitest.pitclipse.ui.behaviours.StepException;
import org.pitest.pitclipse.ui.swtbot.PitNotifier;
import org.pitest.pitclipse.ui.swtbot.PitResultsView;
import org.pitest.pitclipse.ui.util.ViewUtil;
import org.pitest.pitclipse.ui.view.PitView;

public class PitSummaryView {
    private static String SUMMARY_VIEW_TITLE = "PIT Summary";
    private PitResultsView lastResults = null;
    private SWTWorkbenchBot bot;
    private SWTBotView summaryView = null;

    public PitSummaryView(SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    public void waitForUpdate() {
        try {
            lastResults = PitNotifier.INSTANCE.getResults();
        } catch (InterruptedException e) {
            throw new StepException(e);
        }
    }

    public int getClassesTested() {
        return lastResults.getClassesTested();
    }

    public double getOverallCoverage() {
        return lastResults.getTotalCoverage();
    }

    public double getMutationCoverage() {
        return lastResults.getMutationCoverage();
    }

    public void showViewIfNotOpen() {
        if (summaryView == null) {
            summaryView = bot.viewByTitle(SUMMARY_VIEW_TITLE);
        }
        summaryView.show();
    }

    public void clickButtonWithText(String text) {
        showViewIfNotOpen();
        toolbarButton(text).click();
    }

    /**
     * Helper method, because other method works with tooltip. Gets the toolbar
     * button matching the given toolbar button.
     *
     * @param text The text to use to find the button to return.
     * @return The toolbar button.
     * @throws WidgetNotFoundException Thrown if the widget was not found matching
     *                                 the given text.
     */
    public SWTBotToolbarButton toolbarButton(String text) throws WidgetNotFoundException {
        List<SWTBotToolbarButton> l = summaryView.getToolbarButtons();

        for (int i = 0; i < l.size(); i++) {
            SWTBotToolbarButton item = l.get(i);
            if (item.getText().equals(text)) {
                return item;
            }
        }
        throw new WidgetNotFoundException("Unable to find toolitem with the given tooltip '" + text + "'");
    }

    public String getCurrentBrowserUrl() {
        showViewIfNotOpen();
        return summaryView.bot().browser().getUrl();
    }

    public String clickBack() {
        clickButtonWithText(BACK_BUTTON_TEXT);
        summaryView.bot().browser().waitForPageLoaded();
        return getCurrentBrowserUrl();
    }

    public String clickHome() {
        clickButtonWithText(HOME_BUTTON_TEXT);
        summaryView.bot().browser().waitForPageLoaded();
        return getCurrentBrowserUrl();
    }

    public String clickForward() {
        clickButtonWithText(FORWARD_BUTTON_TEXT);
        summaryView.bot().browser().waitForPageLoaded();
        return getCurrentBrowserUrl();
    }

    public void resetView() {
        if (summaryView!=null) {            
            summaryView.close();
        }
        summaryView = ViewUtil.openViewById(bot, SUMMARY_VIEW_TITLE, "PIT", PitView.VIEW_ID);
    }
}
