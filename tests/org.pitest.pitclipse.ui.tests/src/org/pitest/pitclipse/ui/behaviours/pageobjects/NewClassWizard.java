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
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class NewClassWizard {

    private final SWTWorkbenchBot bot;

    public NewClassWizard(SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    public void createClass(String packageName, String className) {
        SWTBotShell shell = bot.shell("New Java Class");
        shell.activate();
        bot.textWithLabel("Package:").setText(packageName);
        bot.textWithLabel("Name:").setText(className);
        bot.button("Finish").click();
        
        // Ensure the class is fully created before moving on
        bot.waitUntil(Conditions.shellCloses(shell));
    }

}
