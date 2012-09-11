package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class NewTestWizard {

	private final SWTWorkbenchBot bot;

	public NewTestWizard(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void createClass(TestClassContext context) {
		SWTBotShell wizardShell = bot.shell("New JUnit Test Case");
		wizardShell.activate();
		bot.textWithLabel("Package:").setText(context.getPackageName());
		bot.textWithLabel("Name:").setText(context.getClassName());
		bot.button("Finish").click();
	}

}
