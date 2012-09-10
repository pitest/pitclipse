package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

public class SourceMenu {

	private static final String SOURCE = "Source";
	private static final String ORGANIZE_IMPORTS = "Organize Imports";
	private static final String FORMAT = "Format";

	private final SWTWorkbenchBot bot;

	public SourceMenu(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void organizeImports() {
		bot.menu(SOURCE).menu(ORGANIZE_IMPORTS).click();
	}

	public void format() {
		bot.menu(SOURCE).menu(FORMAT).click();
	}

}
