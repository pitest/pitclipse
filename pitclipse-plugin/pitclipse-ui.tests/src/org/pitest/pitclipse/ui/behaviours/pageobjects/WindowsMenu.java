package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.core.PitExecutionMode;

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
		selectExecutionMode(mode);
	}

	public PitExecutionMode getPitExecutionMode() {
		bot.menu(WINDOWS).menu(PREFERENCES).click();
		return preferenceSelector.getPitExecutionMode();
	}

	private void selectExecutionMode(PitExecutionMode mode) {
		// The workaround for Eclipse bug 344484.didn't seem to work here
		// so for now we'll set the property directly. We have assertions
		// on reading back the property which should suffice
		PitCoreActivator.getDefault().setExecutionMode(mode);
	}

	public boolean isPitRunInParallel() {
		bot.menu(WINDOWS).menu(PREFERENCES).click();
		return preferenceSelector.isPitRunInParallel();
	}

}
