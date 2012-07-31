package org.pitest.pitclipse.ui;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.ui.behaviours.steps.SetupSteps;

@RunWith(SWTBotJunit4ClassRunner.class)
public abstract class AbstractPitclipseUITest {

	@BeforeClass
	public static void beforeClass() throws Exception {
		SWTBotPreferences.PLAYBACK_DELAY = 10;
		SetupSteps setupSteps = new SetupSteps();
		setupSteps.acknowledgeWelcome();
		setupSteps.openJavaPerspective();
	}

}
