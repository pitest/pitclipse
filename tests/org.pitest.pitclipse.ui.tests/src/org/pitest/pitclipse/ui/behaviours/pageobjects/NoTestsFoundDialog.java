/*******************************************************************************
 * Copyright 2022 Lorenzo Bettini and contributors
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
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class NoTestsFoundDialog {

    private final SWTWorkbenchBot bot;

    public NoTestsFoundDialog(SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    public void assertAppears() {
        SWTBotShell shell = bot.shell("Pitclipse");
        shell.activate();
        bot.label("No tests found");
        bot.button("OK").click();

        bot.waitUntil(Conditions.shellCloses(shell));
    }

}
