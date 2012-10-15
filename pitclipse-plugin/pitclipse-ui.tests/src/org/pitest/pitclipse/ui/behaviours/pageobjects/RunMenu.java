package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.pitest.pitclipse.ui.swtbot.SWTBotMenuHelper;

public class RunMenu {

	private static final String RUN = "Run";
	private static final String RUN_AS = "Run As";
	private static final String PIT_MUTATION_TEST = "PIT Mutation Test";
	private static final String JUNIT_TEST = "JUnit Test";
	private final SWTWorkbenchBot bot;

	public RunMenu(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void runJUnit() {
		SWTBotMenuHelper menuHelper = new SWTBotMenuHelper();
		menuHelper.findMenu(bot.menu(RUN).menu(RUN_AS), JUNIT_TEST).click();
	}

	public void runPit() {
		SWTBotMenuHelper menuHelper = new SWTBotMenuHelper();

		menuHelper.findMenu(bot.menu(RUN).menu(RUN_AS), PIT_MUTATION_TEST)
				.click();
	}

}
