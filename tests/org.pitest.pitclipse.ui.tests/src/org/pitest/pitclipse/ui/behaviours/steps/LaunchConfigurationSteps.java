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

package org.pitest.pitclipse.ui.behaviours.steps;

import org.pitest.pitclipse.ui.behaviours.pageobjects.PitRunConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;

public class LaunchConfigurationSteps {

    private PitRunConfiguration launchConfig;

    @Then("no PIT launch configurations exist")
    public void noPitConfigurationsExist() {
        assertTrue(PAGES.getRunMenu().runConfigurations().isEmpty());
    }

    @Then("a launch configuration with name {word} is created")
    public void openPitConfig(String name) {
        for (PitRunConfiguration pitRunConfiguration : PAGES.getRunMenu().runConfigurations()) {
            if (name.equals(pitRunConfiguration.getName())) {
                return;
            }
        }
        fail("Configuration not found: " + name);
    }

    @When("the launch configuration with name {word} is selected")
    public void selectConfig(String name) {
        for (PitRunConfiguration pitRunConfiguration : PAGES.getRunMenu()
                .runConfigurations()) {
            if (name.equals(pitRunConfiguration.getName())) {
                launchConfig = pitRunConfiguration;
                return;
            }
        }
        fail("Configuration not found: " + name);
    }

    @Then("the run in parallel option on the launch configuration is selected")
    public void launchConfigurationRunsInParallel() {
        assertTrue(launchConfig.isRunInParallel());
    }

    @Then("the excluded classes option on the launch configuration is not set")
    public void launchConfigurationDoesNotExcludeClasses() {
        String excludedClasses = launchConfig.getExcludedClasses();
        System.out.println(launchConfig.getName() + " " + excludedClasses);
        assertNotNull(excludedClasses);
        assertTrue(excludedClasses.isEmpty());
    }

    @Then("the excluded classes option on the launch configuration is set to {string}")
    public void launchConfigurationExcludesClasses(String classes) {
        String excludedClasses = launchConfig.getExcludedClasses();
        assertEquals(classes, excludedClasses);
    }

    @Then("the run in parallel option on the launch configuration is not selected")
    public void launchConfigurationDoesNotRunInParallel() {
        assertFalse(launchConfig.isRunInParallel());
    }

    @Then("the use incremental analysis option on the launch configuration is not selected")
    public void launchConfigurationDoesNotAnalyseIncrementally() {
        assertFalse(launchConfig.isIncrementalAnalysis());
    }

    @Then("the use incremental analysis option on the launch configuration is selected")
    public void launchConfigurationAnalysesIncrementally() {
        assertTrue(launchConfig.isIncrementalAnalysis());
    }

    @Then("the launch configuration(s) is/are configured as(:)")
    public void launchConfigurationsMatch(DataTable configTable) {
        List<PitRunConfiguration> launchConfigurations = PAGES.getRunMenu().runConfigurations();
        configurationsMatch(configTable.asMaps(), launchConfigurations);
    }

    private void configurationsMatch(List<Map<String, String>> expectedRows, List<PitRunConfiguration> launchConfigurations) {
        launchConfigurations = new ArrayList<>(launchConfigurations);
        launchConfigurations.removeIf(conf -> conf.getName().contains("("));
        assertEquals("The number of existing configurations is not the expected one", expectedRows.size(), launchConfigurations.size());
        for (int i = 0; i < expectedRows.size(); i++) {
            assertEquivalent(expectedRows.get(i), launchConfigurations.get(i));
        }
    }

    private void assertEquivalent(Map<String, String> tableRow,
            PitRunConfiguration pitRunConfiguration) {
        assertEquals(tableRow.get("name"), pitRunConfiguration.getName());
        assertEquals(Boolean.valueOf(tableRow.get("runInParallel")),
                pitRunConfiguration.isRunInParallel());
        assertEquals(Boolean.valueOf(tableRow.get("useIncrementalAnalysis")),
                pitRunConfiguration.isIncrementalAnalysis());
        assertEquals(tableRow.get("excludedClasses"),
                pitRunConfiguration.getExcludedClasses());
        assertEquals(tableRow.get("excludedMethods"),
                pitRunConfiguration.getExcludedMethods());
        assertEquals(tableRow.get("avoidCallsTo"),
                pitRunConfiguration.getAvoidCallsTo());
    }
}
