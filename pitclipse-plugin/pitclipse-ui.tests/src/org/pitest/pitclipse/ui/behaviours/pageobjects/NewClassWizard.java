package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class NewClassWizard {

	private final SWTWorkbenchBot bot;

	public NewClassWizard(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void createClass(String packageName, String className) {
		SWTBotShell shell = bot.shell("New Java Class");
		shell.activate();
		bot.textWithLabel("Package:").setText(packageName);
		bot.textWithLabel("Name:").setText(className);
		bot.button("Finish").click();
	}

}
