package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class PitPreferenceSelector {

	private final SWTWorkbenchBot bot;

	public PitPreferenceSelector(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void setPitExecutionMode(PitExecutionMode mode) {
		SWTBotShell shell = bot.shell("Preferences");
		shell.activate();
		bot.tree().getTreeItem("Pitest").select().expand();
		bot.radio(mode.getLabel()).click();
		bot.button("OK").click();
	}
}
