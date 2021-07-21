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

import com.google.common.collect.ImmutableList;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.pitest.pitclipse.core.preferences.PitPreferences;
import org.pitest.pitclipse.launch.ui.PitArgumentsTab;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitRunConfiguration.Builder;

import java.util.List;

import static com.google.common.collect.ImmutableList.builder;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.SwtBotTreeHelper.selectAndExpand;

public class RunConfigurationSelector {

    private final SWTWorkbenchBot bot;

    public RunConfigurationSelector(SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    public PitRunConfiguration getConfiguration(String configName) {
        activateShell();
        Builder builder = new PitRunConfiguration.Builder();
        return builder.build();
    }

    public List<PitRunConfiguration> getConfigurations() {
        activateShell();
        try {
            ImmutableList.Builder<PitRunConfiguration> builder = builder();
            SWTBotTreeItem[] configurations = activateShell().getItems();
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
        boolean runInParallel = bot.checkBox(PitPreferences.RUN_IN_PARALLEL_LABEL).isChecked();
        boolean incrementalAnalysis = bot.checkBox(PitPreferences.INCREMENTAL_ANALYSIS_LABEL).isChecked();
        String excludedClasses = bot.textWithLabel(PitPreferences.EXCLUDE_CLASSES_LABEL).getText();
        String excludedMethods = bot.textWithLabel(PitPreferences.EXCLUDE_METHODS_LABEL).getText();
        String avoidCallsTo = bot.textWithLabel(PitPreferences.AVOID_CALLS_LABEL).getText();
        return new Builder().withName(name).withProjects(project).withRunInParallel(runInParallel)
                .withIncrementalAnalysis(incrementalAnalysis).withExcludedClasses(excludedClasses)
                .withExcludedMethods(excludedMethods).withAvoidCallsTo(avoidCallsTo).build();
    }

    private SWTBotTreeItem activateShell() {
        SWTBotShell shell = bot.shell("Run Configurations");
        shell.activate();
        for (SWTBotTreeItem treeItem : bot.tree().getAllItems()) {
            if ("PIT Mutation Test".equals(treeItem.getText())) {
                return selectAndExpand(treeItem);
            }
        }
        return null;
    }
}
