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
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class NewProjectWizard {

    private final SWTWorkbenchBot bot;

    public NewProjectWizard(SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    public void createJavaProject(String projectName) {
        SWTBotShell shell = bot.shell("New Project");
        shell.activate();
        bot.tree().expandNode("Java").select("Java Project");
        bot.button("Next >").click();
        bot.textWithLabel("Project name:").setText(projectName);
        // make sure to set Java 8 otherwise we get another dialog for module-info.java
        bot.radio("Use an execution environment JRE:").click();
        bot.comboBox().setSelection("JavaSE-1.8");
        bot.button("Finish").click();

        // Ensure the project is fully created before moving on
        bot.waitUntil(Conditions.shellCloses(shell));
    }

}
