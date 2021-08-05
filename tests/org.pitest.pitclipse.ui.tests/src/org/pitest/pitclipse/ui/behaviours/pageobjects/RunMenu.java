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

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.pitest.pitclipse.core.Mutators;
import org.pitest.pitclipse.runner.PitOptions;
import org.pitest.pitclipse.ui.swtbot.PitOptionsNotifier;
import org.pitest.pitclipse.ui.swtbot.SWTBotMenuHelper;

public class RunMenu {

    private static final String RUN = "Run";
    private static final String RUN_AS = "Run As";
    private static final String PIT_MUTATION_TEST = "PIT Mutation Test";
    private static final String JUNIT_TEST = "JUnit Test";
    private final SWTWorkbenchBot bot;
    private final RunConfigurationSelector runConfigurationSelector;

    public RunMenu(SWTWorkbenchBot bot) {
        this.bot = bot;
        runConfigurationSelector = new RunConfigurationSelector(bot);
    }

    public void runJUnit() {
        SWTBotMenuHelper menuHelper = new SWTBotMenuHelper();
        menuHelper.findMenu(bot.menu(RUN).menu(RUN_AS), JUNIT_TEST).click();
    }

    public void runPit() {
        // focus package explorer to ensure the menu is found
        bot.viewByTitle("Package Explorer").setFocus();
        SWTBotMenuHelper menuHelper = new SWTBotMenuHelper();
        SWTBotMenu runAsMenu = bot.menu(RUN)
                                  .menu(RUN_AS);
        menuHelper.findMenu(runAsMenu, PIT_MUTATION_TEST)
                  .click();
        
        ensureSelectTestConfigurationDialogIsClosed();
    }

    public void runPitWithConfiguration(String configurationName) {
        runConfigurationSelector.runWithConfigurationAndWaitForIt(configurationName);
    }

    /**
     * The 'Select a Test Configuration' dialog only appears when Pit has been
     * launched at least once. If it is not found then PIT has been launched
     * directly during the click on 'Run As > PIT Mutation Test' so everything's
     * alright.
     * 
     * This is a fast way for closing the dialog by iterating over the shells, instead
     * of searching for such a shell swallowing {@link WidgetNotFoundException}.
     */
    private void ensureSelectTestConfigurationDialogIsClosed() {
        SWTBotShell[] shells = bot.shells();
        for (SWTBotShell shell : shells) {
            if ("Select a Test Configuration".equals(shell.getText())) {
                shell.bot()
                    .button("OK")
                    .click();
                return;
            }
        }
    }

    public List<PitRunConfiguration> runConfigurations() {
        return runConfigurationSelector.getConfigurations();
    }

    public void createRunConfiguration(String configurationName, String projectName, String className) {
        runConfigurationSelector.createRunConfiguration(configurationName, projectName, className);
    }

    public void setProjectForConfiguration(String configurationName, String project) {
        runConfigurationSelector.setProjectForConfiguration(configurationName, project);
    }

    public void setTestClassForConfiguration(String configurationName, String testClass) {
        runConfigurationSelector.setTestClassForConfiguration(configurationName, testClass);
    }

    public PitOptions getLastUsedPitOptions() {
        return PitOptionsNotifier.INSTANCE.getLastUsedOptions();
    }

    /**
     * Selects the given group in the mutator launch configuration tab.
     * @param configurationName where to select the group
     * @param mutatorGroup      which group to select
     */
    public void setMutatorGroup(String configurationName, Mutators mutatorGroup) {
        runConfigurationSelector.setMutatorGroup(configurationName, mutatorGroup);

    }

    /**
     * Checks all custom mutators for the given configuration
     * @param configurationName of the configuration, where to select all mutators
     */
    public void checkAllMutators(String configurationName) {
        runConfigurationSelector.checkAllMutators(configurationName);
    }

    /**
     * Toggle the given mutator in the table of mutators
     * @param configurationName which the mutator should be toggled
     * @param mutator           which should be toggled
     */
    public void toggleCustomMutator(String configurationName, Mutators mutator) {
        runConfigurationSelector.toggleCustomMutator(configurationName, mutator);
    }

    /**
     * Unchecks all custom mutators and selects the given mutator for the
     * configuration specified by the given name
     * @param configurationName where to select the one mutator
     * @param mutator           which to select in the configuration
     */
    public void setOneCustomMutator(String configurationName, Mutators mutator) {
        runConfigurationSelector.setOneCustomMutator(configurationName, mutator);
    }

    /**
     * Removes the pit run configuration, which matches the given name.
     * @param configurationName which should be removed
     */
    public void removeConfig(String configurationName) {
        runConfigurationSelector.removeConfig(configurationName);
    }

}
