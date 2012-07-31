package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

public class WindowsMenu {

	private static final String WINDOWS = "Window";
	private static final String OPEN_PERSPECTIVES = "Open Perspective";
	private static final String OTHER = "Other...";
	private final SWTWorkbenchBot bot;
	private final PerspectiveSelector perspectiveSelector;

	public WindowsMenu(SWTWorkbenchBot bot) {
		this.bot = bot;
		perspectiveSelector = new PerspectiveSelector(bot);
	}

	public void openJavaPerspective() {
		bot.menu(WINDOWS).menu(OPEN_PERSPECTIVES).menu(OTHER).click();
		perspectiveSelector.selectPerspective("Java");
	}

}
