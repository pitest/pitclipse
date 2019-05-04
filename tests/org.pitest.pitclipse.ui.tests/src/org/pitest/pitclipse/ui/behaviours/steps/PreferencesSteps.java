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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.pitest.pitclipse.core.PitMutators.ALL;
import static org.pitest.pitclipse.core.PitMutators.STRONGER;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_AVOID_CALLS_TO_LIST;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_MUTATORS;
import static org.pitest.pitclipse.runner.config.PitExecutionMode.PROJECT_ISOLATION;
import static org.pitest.pitclipse.runner.config.PitExecutionMode.WORKSPACE;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import java.math.BigDecimal;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.pitest.pitclipse.core.PitMutators;
import org.pitest.pitclipse.runner.config.PitExecutionMode;

public class PreferencesSteps {

    @When("the isolate tests at project scope preference is selected")
    public void testProjectsInIsolation() {
        PAGES.getWindowsMenu().setPitExecutionMode(PROJECT_ISOLATION);
    }

    @Then("the project level scope preference is selected")
    public void projectScopePreferenceIsChosen() {
        assertEquals(PROJECT_ISOLATION, PAGES.getWindowsMenu().getPitExecutionMode());
    }

    @When("the workspace level scope preference is selected")
    public void testProjectsInWorkspace() {
        PAGES.getWindowsMenu().setPitExecutionMode(WORKSPACE);
    }

    @Then("the level scope preference is set to workspace")
    public void workspacePreferenceIsChosen() {
        PitExecutionMode pitExecutionMode = PAGES.getWindowsMenu().getPitExecutionMode();
        assertEquals(WORKSPACE, pitExecutionMode);
    }

    @Then("the mutation tests run in parallel preference is selected")
    public void runInParallelPreferenceIsChosen() {
        assertTrue(PAGES.getWindowsMenu().isPitRunInParallel());
    }

    @When("the run mutation tests in parallel preference is selected")
    public void setPreferenceToRunInParallel() {
        PAGES.getWindowsMenu().setPitRunInParallel(true);
    }

    @When("the mutation tests run in parallel preference is deselected")
    public void setPreferenceToNotRunInParallel() {
        PAGES.getWindowsMenu().setPitRunInParallel(false);
    }

    @When("the mutation tests use incremental analysis preference is selected")
    public void setPreferenceToRunIncrementalAnalysis() {
        PAGES.getWindowsMenu().setIncrementalAnalysisEnabled(true);
    }

    @When("the mutation tests use incremental analysis preference is deselected")
    public void setPreferenceToNotRunIncrementalAnalysis() {
        PAGES.getWindowsMenu().setIncrementalAnalysisEnabled(false);
    }

    @Then("the use incremental analysis preference is not selected")
    public void useIncrementalAnalysis() {
        assertFalse(PAGES.getWindowsMenu().isIncrementalAnalysisEnabled());
    }

    @When("the excluded classes preference is unset")
    public void setNoClassesAreExcluded() {
        PAGES.getWindowsMenu().setExcludedClasses("");
    }

    @When("the excluded classes preference is set to {string}")
    public void setExcludedClasses(String excludedClasses) {
        PAGES.getWindowsMenu().setExcludedClasses(excludedClasses);
    }

    @Then("the excluded classes preference is not set")
    public void noClassesAreExcluded() {
        String excludedClasses = PAGES.getWindowsMenu().getExcludedClasses();
        assertNotNull(excludedClasses);
        assertTrue("The 'Excluded Classes' preference should be an empty string", excludedClasses.isEmpty());
    }

    @Then("the excluded classes preference is {string}")
    public void excludedClassesAre(String expectedExcludedClasses) {
        String excludedClasses = PAGES.getWindowsMenu().getExcludedClasses();
        assertNotNull(excludedClasses);
        assertEquals("The 'Excluded Classes' preference has not the expected value", 
                     expectedExcludedClasses, excludedClasses);
    }

    @Then("the excluded methods preference is not set")
    public void noMethodsAreExcluded() {
        String excludedMethods = PAGES.getWindowsMenu().getExcludedMethods();
        assertNotNull(excludedMethods);
        assertTrue(excludedMethods.isEmpty());
    }

    @Then("the avoid calls to preference is set to the PIT defaults")
    public void defaultAvoidCallsToList() {
        String avoidCallsTo = PAGES.getWindowsMenu().getAvoidCallsTo();
        assertThat(avoidCallsTo, is(equalTo(DEFAULT_AVOID_CALLS_TO_LIST)));
    }

    @When("the excluded methods preference is unset")
    public void setNoMethodsAreExcluded() {
        PAGES.getWindowsMenu().setExcludedMethods("");
    }

    @When("the excluded methods preference is set to {string}")
    public void setExcludedMethods(String excludedMethods) {
        PAGES.getWindowsMenu().setExcludedMethods(excludedMethods);
    }

    @When("the avoid calls to preference is set to {string}")
    public void setAvoidCalls(String avoidCallsTo) {
        PAGES.getWindowsMenu().setAvoidCallsTo(avoidCallsTo);
    }

    @Then("the default mutators preference is selected")
    public void defaultMutators() {
        PitMutators selectedMutators = PAGES.getWindowsMenu().getMutators();
        assertThat(selectedMutators.toString(), is(equalTo(DEFAULT_MUTATORS)));
    }

    @Given("the stronger mutator preference is selected")
    public void useStrongerMutators() {
        PAGES.getWindowsMenu().setMutators(STRONGER);
    }

    @Given("the all mutators preference is selected")
    public void useAllMutators() {
        PAGES.getWindowsMenu().setMutators(ALL);
    }

    @Given("the timeout constant is {int}")
    public void updateTimeoutConstant(int timeout) {
        PAGES.getWindowsMenu().setTimeoutConstant(timeout);
    }

    @Given("the timeout factor is {int}")
    public void updateTimeoutFactor(int factor) {
        PAGES.getWindowsMenu().setTimeoutFactor(factor);
    }

    @Then("the default timeout is {int}")
    public void defaultTimeout(int defaultTimeout) {
        int actualTimeout = PAGES.getWindowsMenu().getTimeout();
        assertThat(actualTimeout, is(equalTo(defaultTimeout)));
    }
    
    @Then("the default timeout factor is {float}")
    public void defaultTimeout(double defaultTimeoutFactor) {
        BigDecimal timeoutFactor = PAGES.getWindowsMenu().getTimeoutFactor();
        assertThat(timeoutFactor, is(closeEnoughTo(new BigDecimal(defaultTimeoutFactor))));
    }

    @Then("the default timeout factor is {string}")
    public void defaultTimeout(String defaultTimeoutFactor) {
        BigDecimal timeoutFactor = PAGES.getWindowsMenu().getTimeoutFactor();
        assertThat(timeoutFactor, is(closeEnoughTo(new BigDecimal(defaultTimeoutFactor))));
    }

    private TypeSafeMatcher<BigDecimal> closeEnoughTo(final BigDecimal expectedValue) {
        return new BigDecimalMatcher(expectedValue);
    }

    private static final class BigDecimalMatcher extends TypeSafeMatcher<BigDecimal> {
        private static final BigDecimal TOLERANCE = BigDecimal.valueOf(0.01);
        private final BigDecimal expectedValue;

        private BigDecimalMatcher(BigDecimal expectedValue) {
            this.expectedValue = expectedValue;
        }

        @Override
        public void describeTo(Description d) {
            d.appendText("close match to").appendValue(expectedValue);
        }

        @Override
        protected boolean matchesSafely(BigDecimal actualValue) {
            return expectedValue.subtract(actualValue).abs().compareTo(TOLERANCE) < 0;
        }
    }
}
