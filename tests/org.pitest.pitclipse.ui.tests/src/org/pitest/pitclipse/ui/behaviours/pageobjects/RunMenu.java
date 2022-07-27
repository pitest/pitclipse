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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.pitest.pitclipse.core.Mutators;
import org.pitest.pitclipse.runner.PitOptions;
import org.pitest.pitclipse.ui.swtbot.PitOptionsNotifier;
import org.pitest.pitclipse.ui.swtbot.SWTBotMenuHelper;

public class RunMenu {

    private static final String RUN = "Run";
    private static final String RUN_AS = "Run As";
    private static final String PIT_MUTATION_TEST = "PIT Mutation Test";
    private static final String JUNIT_TEST = "JUnit Test";
    private static final String RUN_CONFIGURATIONS = "Run Configurations";
    private final SWTWorkbenchBot bot;
    private final RunConfigurationSelector runConfigurationSelector;

    public RunMenu(SWTWorkbenchBot bot) {
        this.bot = bot;
        runConfigurationSelector = new RunConfigurationSelector(bot);
    }

    public void runJUnit() {
        SWTBotMenuHelper menuHelper = new SWTBotMenuHelper();
        menuHelper.findMenu(menuHelper.findWorkbenchMenu(bot, RUN).menu(RUN_AS), JUNIT_TEST).click();
    }

    public void runPit() {
        // focus package explorer to ensure the menu is found
        bot.viewByTitle("Package Explorer").setFocus();
        SWTBotMenuHelper menuHelper = new SWTBotMenuHelper();
        SWTBotMenu runAsMenu = menuHelper.findWorkbenchMenu(bot, RUN).menu(RUN_AS);
        menuHelper.findMenu(runAsMenu, PIT_MUTATION_TEST)
                  .click();
    }

    public void runPitWithConfiguration(String configurationName) {
        try (RunConfigurationSelector selector = openRunMenu().andThen()) {
            selector.runWithConfigurationAndWaitForIt(configurationName);
        }
    }

    public List<PitRunConfiguration> runConfigurations() {
        SWTBotMenuHelper menuHelper = new SWTBotMenuHelper();
        menuHelper.findMenu(menuHelper.findWorkbenchMenu(bot, RUN), RUN_CONFIGURATIONS).click();
        return openRunMenu().andThen().getConfigurations();
    }

    public void createRunConfiguration(String configurationName, String projectName, String className) {
        try (RunConfigurationSelector selector = openRunMenu().andThen()) {
            selector.createRunConfiguration(configurationName, projectName, className);
        }
    }

    public void setProjectForConfiguration(String configurationName, String project) {
        try (RunConfigurationSelector selector = openRunMenu().andThen()) {
            selector.setProjectForConfiguration(configurationName, project);
        }
    }

    public void setTestClassForConfiguration(String configurationName, String testClass) {
        try (RunConfigurationSelector selector = openRunMenu().andThen()) {
            selector.setTestClassForConfiguration(configurationName, testClass);
        }
    }

    public void setTestDirForConfiguration(String configurationName, String testDir) {
        try (RunConfigurationSelector selector = openRunMenu().andThen()) {
            selector.setTestDirForConfiguration(configurationName, testDir);
        }
    }

    public void setTargetClassForConfiguration(String configurationName, String targetClass) {
        try (RunConfigurationSelector selector = openRunMenu().andThen()) {
            runConfigurationSelector.setTargetClassForConfiguration(configurationName, targetClass);
        }
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
        try (RunConfigurationSelector selector = openRunMenu().andThen()) {
            selector.setMutatorGroup(configurationName, mutatorGroup);
        }
    }

    /**
     * Checks all custom mutators for the given configuration
     * @param configurationName of the configuration, where to select all mutators
     */
    public void checkAllMutators(String configurationName) {
        try (RunConfigurationSelector selector = openRunMenu().andThen()) {
            selector.checkAllMutators(configurationName);
        }
    }

    /**
     * Toggle the given mutator in the table of mutators
     * @param configurationName which the mutator should be toggled
     * @param mutator           which should be toggled
     */
    public void toggleCustomMutator(String configurationName, Mutators mutator) {

        try (RunConfigurationSelector selector = openRunMenu().andThen()) {
            selector.toggleCustomMutator(configurationName, mutator);
        }
    }

    /**
     * Unchecks all custom mutators and selects the given mutator for the
     * configuration specified by the given name
     * @param configurationName where to select the one mutator
     * @param mutator           which to select in the configuration
     */
    public void setOneCustomMutator(String configurationName, Mutators mutator) {
        try (RunConfigurationSelector selector = openRunMenu().andThen()) {
            selector.setOneCustomMutator(configurationName, mutator);
        }
    }

    /**
     * Removes the pit run configuration, which matches the given name.
     * @param configurationName which should be removed
     */
    public void removeConfig(String configurationName) {
        try (RunConfigurationSelector selector = openRunMenu().andThen()) {
            selector.removeConfig(configurationName);
        }
    }

    /**
     * Opens the run menu and activates it
     */
    public RunConficurationDsl openRunMenu() {
        runConfigurationSelector.openRunConfigurationShell();
        return new RunConficurationDsl();
    }

    public class RunConficurationDsl {
        public RunConfigurationSelector andThen() {
            return runConfigurationSelector;
        }
    }
}
