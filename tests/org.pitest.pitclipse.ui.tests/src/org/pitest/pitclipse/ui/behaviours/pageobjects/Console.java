/*******************************************************************************
 * Copyright 2021 Jonas Kutscha and contributors
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

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;

/**
 * Class to get the console text.
 * @author Jonas Kutscha
 */
public class Console {
    private SWTWorkbenchBot bot;

    public Console(SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    /**
     * Shows the view console and returns its text.
     * @return the console text
     */
    public String getText() {
        final SWTBotView console = bot.viewByPartName("Console");
        // need to show, otherwise bot can't get text
        console.show();
        return console.bot().styledText().getText();
    }

    /**
     * If the Console view can be found, clear it
     */
    public void clearConsole() {
        List<SWTBotView> allViews = bot.views();
        for (SWTBotView view : allViews) {
            if ("Console".equals(view.getTitle())) {
                view.show();
                if (!view.bot().styledText().getText().isEmpty()) {
                    // use button to clear, because setting the text to "" works not synchronously
                    view.toolbarButton("Clear Console").click();
                }
                return;
            }
        }
    }
}
