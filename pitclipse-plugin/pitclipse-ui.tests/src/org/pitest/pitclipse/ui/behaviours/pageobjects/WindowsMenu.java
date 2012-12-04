package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

public class WindowsMenu {

	private static final String WINDOWS = "Window";
	private static final String OPEN_PERSPECTIVES = "Open Perspective";
	private static final String OTHER = "Other...";
	private static final String PREFERENCES = "Preferences";
	private final SWTWorkbenchBot bot;
	private final PerspectiveSelector perspectiveSelector;
	private final PitPreferenceSelector preferenceSelector;

	public WindowsMenu(SWTWorkbenchBot bot) {
		this.bot = bot;
		perspectiveSelector = new PerspectiveSelector(bot);
		preferenceSelector = new PitPreferenceSelector(bot);
	}

	public void openJavaPerspective() {
		bot.menu(WINDOWS).menu(OPEN_PERSPECTIVES).menu(OTHER).click();
		perspectiveSelector.selectPerspective("Java");
	}

	public void setPitExecutionMode(PitExecutionMode mode) {
		bot.menu(WINDOWS).menu(PREFERENCES).click();
		preferenceSelector.setPitExecutionMode(mode);
	}

}
