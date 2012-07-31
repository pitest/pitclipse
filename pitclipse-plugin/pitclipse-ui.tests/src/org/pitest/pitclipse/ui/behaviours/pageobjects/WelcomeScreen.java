package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;

public class WelcomeScreen {

	private final SWTWorkbenchBot bot;

	public WelcomeScreen(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void acknowledge() {
		try {
			bot.viewByTitle("Welcome").close();
		} catch (WidgetNotFoundException e) {
			// Swallowed - may not have a welcome screen
		}
	}

}
