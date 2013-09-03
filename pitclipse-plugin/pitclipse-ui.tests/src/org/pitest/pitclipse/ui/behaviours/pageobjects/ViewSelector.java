package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class ViewSelector {

	private static final String SHOW_VIEW = "Show View";

	private final SWTWorkbenchBot bot;

	public ViewSelector(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void selectView(String category, String view) {
		SWTBotShell shell = bot.shell(SHOW_VIEW);
		shell.activate();
		bot.tree().select(category).expandNode(category).select(view).click();
		bot.button("OK").click();
	}

}
