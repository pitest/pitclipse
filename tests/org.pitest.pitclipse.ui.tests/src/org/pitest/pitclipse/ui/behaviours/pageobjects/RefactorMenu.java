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
import org.pitest.pitclipse.ui.swtbot.SWTBotMenuHelper;

public class RefactorMenu {

    private static final String REFACTOR = "Refactor";
    private static final String RENAME = "Rename";

    private final SWTWorkbenchBot bot;
    private final RefactorWizard refactorWizard;

    public RefactorMenu(SWTWorkbenchBot bot) {
        this.bot = bot;
        refactorWizard = new RefactorWizard(bot);
    }

    public void renameClass(String newClassName) {
        clickRename();
        refactorWizard.renameClass(newClassName);
    }

    private void clickRename() {
        SWTBotMenuHelper menuHelper = new SWTBotMenuHelper();
        menuHelper.findMenu(menuHelper.findWorkbenchMenu(bot, REFACTOR), RENAME).click();
    }

    public void renamePackage(String packageName) {
        clickRename();
        refactorWizard.renamePackage(packageName);
    }
}
