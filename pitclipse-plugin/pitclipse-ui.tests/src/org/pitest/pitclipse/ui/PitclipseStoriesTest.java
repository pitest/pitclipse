package org.pitest.pitclipse.ui;

import static org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences.PLAYBACK_DELAY;
import static org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences.TIMEOUT;
import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.HTML;
import static org.pitest.pitclipse.ui.PitclipseTestActivator.getDefault;

import java.util.List;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.io.LoadFromURL;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.ui.behaviours.steps.ClassSteps;
import org.pitest.pitclipse.ui.behaviours.steps.LaunchConfigurationSteps;
import org.pitest.pitclipse.ui.behaviours.steps.PitclipseSteps;
import org.pitest.pitclipse.ui.behaviours.steps.PreferencesSteps;
import org.pitest.pitclipse.ui.behaviours.steps.ProjectSteps;
import org.pitest.pitclipse.ui.behaviours.steps.SetupSteps;

@RunWith(SWTBotJunit4ClassRunner.class)
public class PitclipseStoriesTest extends JUnitStories {

	private static final long STORY_TIMEOUT = 10l * 60 * 1000;

	@Override
	public Configuration configuration() {
		PLAYBACK_DELAY = 50L;
		TIMEOUT = 5000L;
		PitclipseTestActivator.getDefault().startTests();
		return new MostUsefulConfiguration().useStoryLoader(new LoadFromURL()).useStoryReporterBuilder(
				new StoryReporterBuilder().withDefaultFormats().withFormats(HTML, CONSOLE)
						.withRelativeDirectory("jbehave-report"));
	}

	@Override
	public Embedder configuredEmbedder() {
		Embedder embedder = super.configuredEmbedder();
		embedder.embedderControls().useStoryTimeoutInSecs(STORY_TIMEOUT);
		return embedder;
	}

	@Override
	protected List<String> storyPaths() {
		return getDefault().getStories();
	}

	@Override
	public InjectableStepsFactory stepsFactory() {
		return new InstanceStepsFactory(configuration(), new SetupSteps(), new ProjectSteps(), new ClassSteps(),
				new PitclipseSteps(), new PreferencesSteps(), new LaunchConfigurationSteps());
	}
}