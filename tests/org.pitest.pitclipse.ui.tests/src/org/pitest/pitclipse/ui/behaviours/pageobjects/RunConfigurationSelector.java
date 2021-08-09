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

import static com.google.common.collect.ImmutableList.builder;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRadio;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.pitest.pitclipse.core.Mutators;
import org.pitest.pitclipse.core.preferences.PitPreferences;
import org.pitest.pitclipse.launch.ui.PitArgumentsTab;
import org.pitest.pitclipse.launch.ui.PitMutatorsTab;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitRunConfiguration.Builder;
import org.pitest.pitclipse.ui.swtbot.PitResultNotifier.PitSummary;
import org.pitest.pitclipse.ui.swtbot.SWTBotMenuHelper;

import com.google.common.collect.ImmutableList;

public class RunConfigurationSelector {

    private static final String RUN = "Run";
    private static final String RUN_CONFIGURATIONS = "Run Configurations";
    private final SWTWorkbenchBot bot;
    private final static String DELETE_SHELL_TITLE = "Confirm Launch Configuration Deletion";

    public RunConfigurationSelector(SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    public PitRunConfiguration getConfiguration(String configName) {
        activateConfiguration(configName);
        Builder builder = new PitRunConfiguration.Builder();
        return builder.build();
    }

    public List<PitRunConfiguration> getConfigurations() {
        try {
            ImmutableList.Builder<PitRunConfiguration> builder = builder();
            SWTBotTreeItem[] configurations = getPitConfigurationItem().getItems();
            for (SWTBotTreeItem treeItem : configurations) {
                treeItem.select();
                builder.add(getPitConfiguration(treeItem));
            }
            return builder.build();
        } finally {
            bot.button("Close").click();
        }
    }

    private PitRunConfiguration getPitConfiguration(SWTBotTreeItem treeItem) {
        String name = treeItem.getText();
        String project = bot.textWithLabel(PitArgumentsTab.PROJECT_TEXT).getText();
        boolean isTestClass = bot.radio(PitArgumentsTab.TEST_CLASS_RADIO_TEXT).isSelected();
        String testObject;
        if (isTestClass) {
            testObject = bot.textWithLabel(PitArgumentsTab.TEST_CLASS_TEXT).getText();
        } else {
            testObject = bot.textWithLabel(PitArgumentsTab.TEST_DIR_TEXT).getText();
        }
        boolean runInParallel = bot.checkBox(PitPreferences.RUN_IN_PARALLEL_LABEL).isChecked();
        boolean incrementalAnalysis = bot.checkBox(PitPreferences.INCREMENTAL_ANALYSIS_LABEL).isChecked();
        String excludedClasses = bot.textWithLabel(PitPreferences.EXCLUDED_CLASSES_LABEL).getText();
        String excludedMethods = bot.textWithLabel(PitPreferences.EXCLUDED_METHODS_LABEL).getText();
        String avoidCallsTo = bot.textWithLabel(PitPreferences.AVOID_CALLS_TO_LABEL).getText();
        return new Builder().withName(name).withProjects(project).withRunInParallel(runInParallel)
                .withIncrementalAnalysis(incrementalAnalysis).withExcludedClasses(excludedClasses)
                .withExcludedMethods(excludedMethods).withAvoidCallsTo(avoidCallsTo).withTestObject(testObject)
                .withTestClassOrDir(isTestClass).build();
    }

    private SWTBotShell activateShell() {
        // look if shell is already open
        for (SWTBotShell shell : bot.shells()) {
            if (shell.getText().equals(RUN_CONFIGURATIONS)) {
                shell.activate();
                return shell;
            }
        }
        // make sure we don't have pending shells
        bot.closeAllShells();
        // shell was not open, open and activate it
        SWTBotMenuHelper menuHelper = new SWTBotMenuHelper();
        menuHelper.findMenu(bot.menu(RUN), RUN_CONFIGURATIONS + "...").click();
        SWTBotShell shell = bot.shell(RUN_CONFIGURATIONS);
        shell.activate();
        // make sure the dialog is active
        bot.waitUntil(shellIsActive(RUN_CONFIGURATIONS));
        return shell;
    }

    private SWTBotTreeItem getPitConfigurationItem() {
        activateShell();
        final String itemName = "PIT Mutation Test";
        for (SWTBotTreeItem treeItem : bot.tree().getAllItems()) {
            if (itemName.equals(treeItem.getText())) {
                return treeItem.select().expand();
            }
        }
        fail("Could not find '" + itemName + "' in the configurations tab.");
        return null; // never reached
    }

    private void activateConfiguration(String configurationName) {
        for (SWTBotTreeItem i : getPitConfigurationItem().getItems()) {
            if (i.getText().equals(configurationName)) {
                i.click();
                return;
            }
        }
        fail("Could not find '" + configurationName + "' in the configurations of PIT.");
    }

    public void activateMutatorsTab(String configurationName) {
        activateConfigurationTab(configurationName, PitMutatorsTab.NAME);
    }

    public void activatePitTab(String configurationName) {
        activateConfigurationTab(configurationName, PitArgumentsTab.NAME);
    }

    private void activateConfigurationTab(String configurationName, String name) {
        activateShell();
        activateConfiguration(configurationName);
        bot.cTabItem(PitMutatorsTab.NAME).activate();
    }

    public void createRunConfiguration(String configurationName, String projectName, String className) {
        getPitConfigurationItem().contextMenu("New Configuration").click();
        bot.textWithLabel("Name:").setText(configurationName);
        activateShell().bot().button("Apply").click();
        PitRunConfiguration config = new Builder().withName(configurationName).withProjects(projectName)
                .withTestClass(className).build();
        setConfiguration(config);
    }

    public void setProjectForConfiguration(String configurationName, String project) {
        setConfiguration(new Builder(getConfiguration(configurationName)).withProjects(project).build());
    }

    public void setTestClassForConfiguration(String configurationName, String testClass) {
        setConfiguration(new Builder(getConfiguration(configurationName)).withTestClass(testClass).build());
    }

    public void setTestDirForConfiguration(String configurationName, String testDir) {
        setConfiguration(new Builder(getConfiguration(configurationName)).withTestDir(testDir).build());
    }

    private void setConfiguration(PitRunConfiguration config) {
        activateConfiguration(config.getName());
        bot.textWithLabel(PitArgumentsTab.PROJECT_TEXT).setText(getProjectsAsString(config));
        if (config.isTestClass()) {
            bot.radio(PitArgumentsTab.TEST_CLASS_RADIO_TEXT).click();
            bot.textWithLabel(PitArgumentsTab.TEST_CLASS_TEXT).setText(config.getTestObject());
        } else {
            bot.radio(PitArgumentsTab.TEST_DIR_RADIO_TEXT).click();
            bot.textWithLabel(PitArgumentsTab.TEST_DIR_TEXT).setText(config.getTestObject());

        }
        if (config.isRunInParallel()) {
            bot.checkBox(PitPreferences.RUN_IN_PARALLEL_LABEL).select();
        } else {
            bot.checkBox(PitPreferences.RUN_IN_PARALLEL_LABEL).deselect();
        }
        if (config.isIncrementalAnalysis()) {
            bot.checkBox(PitPreferences.INCREMENTAL_ANALYSIS_LABEL).select();
        } else {
            bot.checkBox(PitPreferences.INCREMENTAL_ANALYSIS_LABEL).deselect();
        }
        bot.textWithLabel(PitPreferences.EXCLUDED_CLASSES_LABEL).setText(config.getExcludedClasses());
        bot.textWithLabel(PitPreferences.EXCLUDED_METHODS_LABEL).setText(config.getExcludedMethods());
        bot.textWithLabel(PitPreferences.AVOID_CALLS_TO_LABEL).setText(config.getAvoidCallsTo());
        // close shell and save
        closeConfigurationShell();
    }

    /**
     * @param config where the projects are listed
     * @return String which lists all projects of the configuration separated with
     *         commas
     */
    private String getProjectsAsString(PitRunConfiguration config) {
        return config.getProjects().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    public void setMutatorGroup(String configurationName, Mutators mutatorGroup) {
        activateMutatorsTab(configurationName);
        bot.radio(mutatorGroup.getDescriptor()).click();
        closeConfigurationShell();
    }

    public void toggleCustomMutator(String configurationName, Mutators mutator) {
        activateMutatorsTab(configurationName);
        SWTBotRadio radioButton = bot.radio(PitMutatorsTab.CUSTOM_MUTATOR_RADIO_TEXT);
        radioButton.click();
        SWTBotTable table = bot.table();
        toggleMutator(table, mutator);
        closeConfigurationShell();
    }

    public void setOneCustomMutator(String configurationName, Mutators mutator) {
        activateMutatorsTab(configurationName);
        bot.radio(PitMutatorsTab.CUSTOM_MUTATOR_RADIO_TEXT).click();
        uncheckAllMutators();
        SWTBotTable table = bot.table();
        toggleMutator(table, mutator);
        closeConfigurationShell();
    }

    public void checkAllMutators(String configurationName) {
        activateMutatorsTab(configurationName);
        bot.radio(PitMutatorsTab.CUSTOM_MUTATOR_RADIO_TEXT).click();
        SWTBotTable table = bot.table();
        Display.getDefault().syncExec(() -> {
            final int itemCount = table.widget.getItems().length;
            for (int i = 0; i < itemCount; i++) {
                table.getTableItem(i).check();
            }
        });
        closeConfigurationShell();
    }

    /**
     * Assumes the Mutator tab is active and custom mutator mode is selected.
     */
    private void uncheckAllMutators() {
        SWTBotTable table = bot.table();
        Display.getDefault().syncExec(() -> {
            for (TableItem item : table.widget.getItems()) {
                item.setChecked(false);
            }
        });
        // don't close, because you cannot apply empty mutators
    }

    /**
     * Assumes the Mutator tab is active and custom mutator mode is selected.
     * @param table   where to check the mutator
     * @param mutator which is set to active
     */
    private void toggleMutator(SWTBotTable table, Mutators mutator) {
        table.getTableItem(mutator.getDescriptor()).toggleCheck();
    }

    /**
     * Closes the configuration shell and applies, if possible.
     */
    private void closeConfigurationShell() {
        SWTBotShell shell = bot.shell(RUN_CONFIGURATIONS);
        SWTBotButton apply = shell.bot().button("Apply");
        if (apply.isEnabled()) {
            apply.click();
        }
        shell.bot().button("Close").click();
    }

    /**
     * Runs the configuration specified by the given name and waits for it to be
     * finished.
     * @param configurationName
     */
    public void runWithConfigurationAndWaitForIt(String configurationName) {
        // reset Summary result
        PitSummary.INSTANCE.resetSummary();
        activateConfiguration(configurationName);
        SWTBotShell shell = bot.shell(RUN_CONFIGURATIONS);
        shell.bot().button(RUN).click();
        // wait for pit
        PitSummary.INSTANCE.waitForPitToFinish();
    }

    /**
     * Deletes the pit run configuration, which matches the given name
     * @param configurationName which should be deleted
     */
    public void removeConfig(String configurationName) {
        for (SWTBotTreeItem i : getPitConfigurationItem().getItems()) {
            if (i.getText().equals(configurationName)) {
                i.contextMenu("Delete").click();
                bot.waitUntil(shellIsActive(DELETE_SHELL_TITLE));
                bot.shell(DELETE_SHELL_TITLE).bot().button("Delete").click();
                return;
            }
        }
        fail("Could not find '" + configurationName + "' in the configurations of PIT.");
    }
}
