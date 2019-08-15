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

public class FileMenu {

    private static final String PROJECT = "Project...";
    private static final String NEW = "New";
    private static final String FILE = "File";
    private static final String CLASS = "Class";
    private static final String SAVE_ALL = "Save All";

    private final SWTWorkbenchBot bot;
    private final NewProjectWizard newProjectWizard;
    private final NewClassWizard newClassWizard;

    public FileMenu(SWTWorkbenchBot bot) {
        this.bot = bot;
        newProjectWizard = new NewProjectWizard(bot);
        newClassWizard = new NewClassWizard(bot);
    }

    public void newJavaProject(String projectName) {
        bot.menu(FILE).menu(NEW).menu(PROJECT).click();
        newProjectWizard.createJavaProject(projectName);
    }

    public void createClass(String packageName, String className) {
        bot.tree()
           .contextMenu()
           .menu(NEW)
           .menu(CLASS)
           .click();
        newClassWizard.createClass(packageName, className);
    }

    public void saveAll() {
        bot.menu(FILE).menu(SAVE_ALL).click();
    }
}
