package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

public class FileMenu {

	private static final String PROJECT = "Project...";
	private static final String NEW = "New";
	private static final String FILE = "File";
	private static final String CLASS = "Class";
	private static final String SAVE_ALL = "Save All";

	private final SWTWorkbenchBot bot;
	private final NewProjectWizard newProjectWizard;
	private final NewClassWizard newClassWizard;

	public FileMenu(SWTWorkbenchBot bot) {
		this.bot = bot;
		newProjectWizard = new NewProjectWizard(bot);
		newClassWizard = new NewClassWizard(bot);
	}

	public void newJavaProject(String projectName) {
		bot.menu(FILE).menu(NEW).menu(PROJECT).click();
		newProjectWizard.createJavaProject(projectName);
	}

	public void createClass(String packageName, String className) {
		bot.menu(FILE).menu(NEW).menu(CLASS).click();
		newClassWizard.createClass(packageName, className);
	}

	public void saveAll() {
		bot.menu(FILE).menu(SAVE_ALL).click();
	}
}
