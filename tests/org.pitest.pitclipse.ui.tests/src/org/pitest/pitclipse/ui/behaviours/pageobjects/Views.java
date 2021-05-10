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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

import java.util.List;

public class Views {

    private final SWTWorkbenchBot bot;

    public Views(SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    public void closeConsole() {
        List<SWTBotView> allViews = bot.views();
        for (SWTBotView view : allViews) {
            if ("Console".equals(view.getTitle())) {
                view.close();
            }
        }
    }

    public void waitForTestsAreRunOnConsole() {
        SWTBotView consoleView = bot.viewByPartName("Console");
        consoleView.show();
        bot.waitUntil(new ICondition() {
            @Override
            public boolean test() {
                return consoleView.bot()
                        .styledText().getText()
                        .trim()
                        .endsWith("tests per mutation)");
            }

            @Override
            public void init(SWTBot bot) {
            }

            @Override
            public String getFailureMessage() {
                return "Console View is empty";
            }
        });
    }

}
