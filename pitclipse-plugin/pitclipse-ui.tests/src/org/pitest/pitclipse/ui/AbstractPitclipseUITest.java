package org.pitest.pitclipse.ui;

import static org.pitest.pitclipse.ui.util.StepUtil.safeSleep;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.ui.behaviours.steps.SetupSteps;

@RunWith(SWTBotJunit4ClassRunner.class)
public abstract class AbstractPitclipseUITest {
	@BeforeClass
	public static void beforeClass() throws Exception {
		SWTBotPreferences.PLAYBACK_DELAY = 40L;
		SetupSteps setupSteps = new SetupSteps();
		setupSteps.acknowledgeWelcome();
		setupSteps.openJavaPerspective();
	}

	@Before
	public void hackyWait() {
		// Sometimes SWTBot spawned eclipse gets it's knickers in a twist
		// Hopefully a timing issue...
		safeSleep(1000);
	}
}
