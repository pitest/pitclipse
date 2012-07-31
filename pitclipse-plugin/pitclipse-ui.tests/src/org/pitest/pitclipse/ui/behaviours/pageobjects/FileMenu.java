package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

public class FileMenu {

	private static final String PROJECT = "Project...";
	private static final String NEW = "New";
	private static final String FILE = "File";
	private final SWTWorkbenchBot bot;
	private final NewProjectWizard newProjectWizard;

	public FileMenu(SWTWorkbenchBot bot) {
		this.bot = bot;
		newProjectWizard = new NewProjectWizard(bot);
	}

	public void newJavaProject(String projectName) {
		bot.menu(FILE).menu(NEW).menu(PROJECT).click();
		newProjectWizard.createJavaProject(projectName);
	}

}
