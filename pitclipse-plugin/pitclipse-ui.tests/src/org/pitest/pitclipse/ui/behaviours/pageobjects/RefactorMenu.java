package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.pitest.pitclipse.ui.swtbot.SWTBotMenuHelper;

public class RefactorMenu {

	private static final String REFACTOR = "Refactor";
	private static final String RENAME = "Rename";

	private final SWTWorkbenchBot bot;
	private final RefactorWizard refactorWizard;

	public RefactorMenu(SWTWorkbenchBot bot) {
		this.bot = bot;
		refactorWizard = new RefactorWizard(bot);
	}

	public void renameClass(String newClassName) {
		clickRename();
		refactorWizard.renameClass(newClassName);
	}

	private void clickRename() {
		SWTBotMenuHelper menuHelper = new SWTBotMenuHelper();
		menuHelper.findMenu(bot.menu(REFACTOR), RENAME).click();
	}

	public void renamePackage(String packageName) {
		clickRename();
		refactorWizard.renamePackage(packageName);
	}
}
