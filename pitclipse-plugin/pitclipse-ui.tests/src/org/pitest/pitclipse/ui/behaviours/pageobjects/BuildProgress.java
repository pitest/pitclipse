package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.pitest.pitclipse.ui.swtbot.WaitForBuildCondition;

public class BuildProgress {

	private static final long BUILD_TIMEOUT = 20000L;
	private final SWTWorkbenchBot bot;
	private WaitForBuildCondition buildCompleteCondition;

	public BuildProgress(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void waitForBuild() {
		try {
			bot.waitUntil(buildCompleteCondition, BUILD_TIMEOUT);
		} finally {
			buildCompleteCondition.unsubscribe();
			buildCompleteCondition = null;
		}
	}

	public void listenForBuild() {
		buildCompleteCondition = new WaitForBuildCondition();
		buildCompleteCondition.subscribe();
	}

}
