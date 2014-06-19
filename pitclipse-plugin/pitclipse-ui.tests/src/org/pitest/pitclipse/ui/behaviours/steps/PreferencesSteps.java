package org.pitest.pitclipse.ui.behaviours.steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.pitest.pitclipse.core.PitMutators.ALL;
import static org.pitest.pitclipse.core.PitMutators.STRONGER;
import static org.pitest.pitclipse.pitrunner.config.PitConfiguration.DEFAULT_AVOID_CALLS_TO_LIST;
import static org.pitest.pitclipse.pitrunner.config.PitConfiguration.DEFAULT_MUTATORS;
import static org.pitest.pitclipse.pitrunner.config.PitExecutionMode.PROJECT_ISOLATION;
import static org.pitest.pitclipse.pitrunner.config.PitExecutionMode.WORKSPACE;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.INSTANCE;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.pitest.pitclipse.core.PitMutators;
import org.pitest.pitclipse.pitrunner.config.PitExecutionMode;

public class PreferencesSteps {

	@When("the isolate tests at project scope preference is selected")
	public void testProjectsInIsolation() {
		INSTANCE.getWindowsMenu().setPitExecutionMode(PROJECT_ISOLATION);
	}

	@Then("the project level scope preference is selected")
	public void projectScopePreferenceIsChosen() {
		assertEquals(PROJECT_ISOLATION, INSTANCE.getWindowsMenu().getPitExecutionMode());
	}

	@When("the workspace level scope preference is selected")
	public void testProjectsInWorkspace() {
		INSTANCE.getWindowsMenu().setPitExecutionMode(WORKSPACE);
	}

	@Then("the workspace level scope preference is selected")
	public void workspacePreferenceIsChosen() {
		PitExecutionMode pitExecutionMode = INSTANCE.getWindowsMenu().getPitExecutionMode();
		assertEquals(WORKSPACE, pitExecutionMode);
	}

	@Then("the mutation tests run in parallel preference is selected")
	public void runInParallelPreferenceIsChosen() {
		assertTrue(INSTANCE.getWindowsMenu().isPitRunInParallel());
	}

	@When("the mutation tests run in parallel preference is selected")
	public void setPreferenceToRunInParallel() {
		INSTANCE.getWindowsMenu().setPitRunInParallel(true);
	}

	@When("the mutation tests run in parallel preference is deselected")
	public void setPreferenceToNotRunInParallel() {
		INSTANCE.getWindowsMenu().setPitRunInParallel(false);
	}

	@When("the mutation tests use incremental analysis preference is selected")
	public void setPreferenceToRunIncrementalAnalysis() {
		INSTANCE.getWindowsMenu().setIncrementalAnalysisEnabled(true);
	}

	@When("the mutation tests use incremental analysis preference is deselected")
	public void setPreferenceToNotRunIncrementalAnalysis() {
		INSTANCE.getWindowsMenu().setIncrementalAnalysisEnabled(false);
	}

	@Then("the use incremental analysis preference is not selected")
	public void useIncrementalAnalysis() {
		assertFalse(INSTANCE.getWindowsMenu().isIncrementalAnalysisEnabled());
	}

	@When("the excluded classes preference is not set")
	public void setNoClassesAreExcluded() {
		INSTANCE.getWindowsMenu().setExcludedClasses("");
	}

	@When("the excluded classes preference is set to \"$excludedClasses\"")
	public void setExcludedClasses(String excludedClasses) {
		INSTANCE.getWindowsMenu().setExcludedClasses(excludedClasses);
	}

	@Then("the excluded classes preference is not set")
	public void noClassesAreExcluded() {
		String excludedClasses = INSTANCE.getWindowsMenu().getExcludedClasses();
		assertNotNull(excludedClasses);
		assertTrue(excludedClasses.isEmpty());
	}

	@Then("the excluded methods preference is not set")
	public void noMethodsAreExcluded() {
		String excludedMethods = INSTANCE.getWindowsMenu().getExcludedMethods();
		assertNotNull(excludedMethods);
		assertTrue(excludedMethods.isEmpty());
	}

	@Then("the avoid calls to preference is set to the PIT defaults")
	public void defaultAvoidCallsToList() {
		String avoidCallsTo = INSTANCE.getWindowsMenu().getAvoidCallsTo();
		assertThat(avoidCallsTo, is(equalTo(DEFAULT_AVOID_CALLS_TO_LIST)));
	}

	@When("the excluded methods preference is not set")
	public void setNoMethodsAreExcluded() {
		INSTANCE.getWindowsMenu().setExcludedMethods("");
	}

	@When("the excluded methods preference is set to \"$excludedMethods\"")
	public void setExcludedMethods(String excludedMethods) {
		INSTANCE.getWindowsMenu().setExcludedMethods(excludedMethods);
	}

	@When("the avoid calls to preference is set to \"$avoidCallsTo\"")
	public void setAvoidCalls(String avoidCallsTo) {
		INSTANCE.getWindowsMenu().setAvoidCallsTo(avoidCallsTo);
	}

	@Then("the default mutators preference is selected")
	public void defaultMutators() {
		PitMutators selectedMutators = INSTANCE.getWindowsMenu().getMutators();
		assertThat(selectedMutators.toString(), is(equalTo(DEFAULT_MUTATORS)));
	}

	@Given("the stronger mutator preference is selected")
	public void useStrongerMutators() {
		INSTANCE.getWindowsMenu().setMutators(STRONGER);
	}

	@Given("the all mutators preference is selected")
	public void useAllMutators() {
		INSTANCE.getWindowsMenu().setMutators(ALL);
	}
}
