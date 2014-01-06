package org.pitest.pitclipse.ui.behaviours.pageobjects;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;

public class Views {

	private final SWTWorkbenchBot bot;

	public Views(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void closeConsole() {
		List<SWTBotView> allViews = bot.views();
		for (SWTBotView view : allViews) {
			if ("Console".equals(view.getTitle())) {
				view.close();
			}
		}
	}

}
