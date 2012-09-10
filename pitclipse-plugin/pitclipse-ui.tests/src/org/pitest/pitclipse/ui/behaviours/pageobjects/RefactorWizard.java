package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class RefactorWizard {

	private final SWTWorkbenchBot bot;

	public RefactorWizard(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void renameClass(String newClassName) {
		rename("Rename Compilation Unit", "Finish", newClassName);
	}

	public void renamePackage(String packageName) {
		rename("Rename Package", "OK", packageName);
	}

	private void rename(String dialogTitle, String completeButtonText,
			String newName) {
		SWTBotShell wizardShell = bot.shell(dialogTitle);
		wizardShell.activate();
		bot.textWithLabel("New name:").setText(newName);
		bot.button(completeButtonText).click();
	}

}
