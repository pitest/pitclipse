package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class NewProjectWizard {

	private final SWTWorkbenchBot bot;

	public NewProjectWizard(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void createJavaProject(String projectName) {
		SWTBotShell shell = bot.shell("New Project");
		shell.activate();
		bot.tree().expandNode("Java").select("Java Project");
		bot.button("Next >").click();
		bot.textWithLabel("Project name:").setText(projectName);
		bot.button("Finish").click();
	}

}
