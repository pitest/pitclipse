package org.pitest.pitclipse.ui;

import static org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences.PLAYBACK_DELAY;
import static org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences.TIMEOUT;
import static org.pitest.pitclipse.ui.util.StepUtil.safeSleep;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.ui.behaviours.steps.ProjectSteps;
import org.pitest.pitclipse.ui.behaviours.steps.SetupSteps;

@RunWith(SWTBotJunit4ClassRunner.class)
public abstract class AbstractPitclipseUITest {

	private final ProjectSteps projectSteps = new ProjectSteps();

	@BeforeClass
	public static void beforeClass() throws Exception {
		PLAYBACK_DELAY = 40L;
		TIMEOUT = 5000L;
		SetupSteps setupSteps = new SetupSteps();
		setupSteps.acknowledgeWelcome();
		setupSteps.openJavaPerspective();
	}

	@Before
	public void cleanUp() {
		projectSteps.deleteAllProjects();
		safeSleep(2000);
	}
}
