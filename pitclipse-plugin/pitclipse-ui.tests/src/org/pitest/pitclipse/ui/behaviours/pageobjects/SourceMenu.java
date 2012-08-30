package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;

public class SourceMenu {

	private static final String SOURCE = "Source";
	private static final String ORGANIZE_IMPORTS = "Organize Imports";

	private final SWTWorkbenchBot bot;

	public SourceMenu(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void organizeImports() {
		SWTBotMenu sourceMenu = bot.menu(SOURCE);
		SWTBotMenu orgImports = sourceMenu.menu(ORGANIZE_IMPORTS);
		orgImports.click();
	}

}
