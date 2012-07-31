package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class PerspectiveSelector {

	private final SWTWorkbenchBot bot;

	public PerspectiveSelector(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void selectPerspective(String perspectiveName) {
		SWTBotShell shell = bot.shell("Open Perspective");
		shell.activate();
		bot.table().select(perspectiveName);
		bot.button("OK").click();
	}

}
