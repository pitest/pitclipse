package org.pitest.pitclipse.ui.behaviours.steps;

import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.INSTANCE;

import org.jbehave.core.annotations.Given;

public class SetupSteps {

	@Given("eclipse opens and the welcome screen is acknowledged")
	public void acknowledgeWelcome() {
		INSTANCE.getWelcomeScreen().acknowledge();
	}

	@Given("the java perspective is opened")
	public void openJavaPerspective() {
		INSTANCE.getWindowsMenu().openJavaPerspective();
	}
}
