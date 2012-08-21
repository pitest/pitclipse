package org.pitest.pitclipse.ui.behaviours.pageobjects;

import static org.pitest.pitclipse.ui.util.StepUtil.safeSleep;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class NewTestWizard {

	private final SWTWorkbenchBot bot;

	public NewTestWizard(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void createClass(String packageName, String testClassName) {
		SWTBotShell wizardShell = bot.shell("New JUnit Test Case");
		wizardShell.activate();
		bot.textWithLabel("Package:").setText(packageName);
		bot.textWithLabel("Name:").setText(testClassName);
		bot.button("Finish").click();
		acknowlegeJUnitPathDialog();
	}

	private void acknowlegeJUnitPathDialog() {
		// May get asked to add JUnit to the path
		SWTBotShell shell = bot.shell("New JUnit Test Case");
		safeSleep(500);
		if (shell.isOpen()) {
			shell.activate();
			bot.button("OK").click();
		}
	}

}
