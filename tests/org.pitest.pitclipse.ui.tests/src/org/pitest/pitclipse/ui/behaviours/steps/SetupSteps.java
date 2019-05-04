package org.pitest.pitclipse.ui.behaviours.steps;

import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import org.jbehave.core.annotations.Given;

public class SetupSteps {

    @Given("eclipse opens and the welcome screen is acknowledged")
    public void acknowledgeWelcome() {
        PAGES.getWelcomeScreen().acknowledge();
    }

    @Given("the java perspective is opened")
    public void openJavaPerspective() {
        PAGES.getWindowsMenu().openJavaPerspective();
    }
}
