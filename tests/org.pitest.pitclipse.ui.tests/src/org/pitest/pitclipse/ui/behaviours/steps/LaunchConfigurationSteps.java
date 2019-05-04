package org.pitest.pitclipse.ui.behaviours.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import java.util.List;
import java.util.Map;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitRunConfiguration;

public class LaunchConfigurationSteps {

    private PitRunConfiguration launchConfig;

    @Then("no PIT launch configurations exist")
    public void noPitConfigurationsExist() {
        assertTrue(PAGES.getRunMenu().runConfigurations().isEmpty());
    }

    @Then("a launch configuration with name $name is created")
    public void openPitConfig(String name) {
        for (PitRunConfiguration pitRunConfiguration : PAGES.getRunMenu()
                .runConfigurations()) {
            if (name.equals(pitRunConfiguration.getName())) {
                return;
            }
        }
        fail("Configuration not found: " + name);
    }

    @When("the launch configuration with name $name is selected")
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

    @Then("the excluded classes option on the launch configuration is set to \"$excludedClasses\"")
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

    @Then("the launch configurations are configured as: $configTable")
    public void launchConfigurationsMatch(ExamplesTable configTable) {
        List<PitRunConfiguration> launchConfigurations = PAGES.getRunMenu()
                .runConfigurations();
        configurationsMatch(configTable.getRows(), launchConfigurations);
    }

    private void configurationsMatch(List<Map<String, String>> expectedRows,
            List<PitRunConfiguration> launchConfigurations) {
        assertEquals(expectedRows.size(), launchConfigurations.size());
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
