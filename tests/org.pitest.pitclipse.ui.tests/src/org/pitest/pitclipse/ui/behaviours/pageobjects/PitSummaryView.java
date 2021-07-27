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

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotBrowser;
import org.pitest.pitclipse.ui.view.PitView;

public class PitSummaryView {
    private PitView summaryView;
    private SWTWorkbenchBot bot;
    private SWTBotBrowser browser;

    public PitSummaryView(SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    public void getViewIfNotSet() {
        if (summaryView == null) {
            summaryView = (PitView) Views.getViewById(PitView.VIEW_ID);
            // get browser to wait for page loads
            browser = bot.viewById(PitView.VIEW_ID).bot().browser();
        }
    }

    public String getCurrentBrowserUrl() {
        getViewIfNotSet();
        // needs to run in UIThread
        AtomicReference<String> url = new AtomicReference<>();
        Display.getDefault().syncExec(() -> {
            url.set(summaryView.getUrl());
        });
        return url.get();
    }

    public String clickBack(String expectedUrl) {
        getViewIfNotSet();
        // needs to run in UIThread
        Display.getDefault().syncExec(() -> {
            summaryView.back();
        });
        bot.waitUntil(new BrowserLoadCondition(expectedUrl));
        return getCurrentBrowserUrl();
    }

    public String clickHome(String expectedUrl) {
        getViewIfNotSet();
        // needs to run in UIThread
        Display.getDefault().syncExec(() -> {
            summaryView.home();
        });
        bot.waitUntil(new BrowserLoadCondition(expectedUrl));
        return getCurrentBrowserUrl();
    }

    public String clickForward(String expectedUrl) {
        getViewIfNotSet();
        // needs to run in UIThread
        Display.getDefault().syncExec(() -> {
            summaryView.forward();
        });
        bot.waitUntil(new BrowserLoadCondition(expectedUrl));
        return getCurrentBrowserUrl();
    }

    public String setUrl(String url) {
        getViewIfNotSet();
        // needs to run in UIThread
        Display.getDefault().syncExec(() -> {
            summaryView.setUrl(url);
        });
        bot.waitUntil(new BrowserLoadCondition(url.replace(".html", "")));
        return getCurrentBrowserUrl();
    }

    private class BrowserLoadCondition extends DefaultCondition {
        private final String titleOfPage;
        private String html;
        public BrowserLoadCondition(String titleOfPage) {
            // give the browser to change.
            // To avoid false positives, if the page should not change
            browser.waitForPageLoaded();
            final int lastSegment = titleOfPage.lastIndexOf('/') + 1;
            if (lastSegment > 0) {
                this.titleOfPage = titleOfPage.substring(lastSegment);
            } else {
                this.titleOfPage = titleOfPage;
            }
        }
        @Override
        public boolean test() throws Exception {
            html = browser.getText();
            if (titleOfPage.equals(PitView.BLANK_PAGE)
                    && (html.equals("<html><head></head><body></body></html>") || html.equals(""))) {
                // if page should be blank and is, return true
                return true;
            }
            return html.contains("<h1>" + titleOfPage + "</h1>");
        }

        @Override
        public String getFailureMessage() {
            return "The title of the page didn't match: '" + titleOfPage + "'\nHTML was:\n" + html;
        }

    }
}
