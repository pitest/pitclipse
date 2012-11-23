package org.pitest.pitclipse.ui;

import static org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences.PLAYBACK_DELAY;
import static org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences.TIMEOUT;
import static org.pitest.pitclipse.ui.PitclipseTestActivator.getStories;

import java.util.List;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.jbehave.core.annotations.BeforeStories;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.io.LoadFromURL;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.ui.behaviours.steps.ClassSteps;
import org.pitest.pitclipse.ui.behaviours.steps.PitclipseSteps;
import org.pitest.pitclipse.ui.behaviours.steps.ProjectSteps;
import org.pitest.pitclipse.ui.behaviours.steps.SetupSteps;

@RunWith(SWTBotJunit4ClassRunner.class)
public class PitclipseStories extends JUnitStories {

	private static final long STORY_TIMEOUT = 10l * 60 * 1000;

	/*
	 * @Override protected List<String> storyPaths() { // String location =
	 * codeLocationFromClass(this.getClass()).getFile(); Builder<String>
	 * storyBuilder = builder();
	 * storyBuilder.add("stories/simple_java_project.story");
	 * 
	 * return storyBuilder.build(); }
	 */

	@Override
	public Configuration configuration() {
		PLAYBACK_DELAY = 50L;
		TIMEOUT = 5000L;
		return new MostUsefulConfiguration().useStoryLoader(new LoadFromURL())
				.useStoryReporterBuilder(
						new StoryReporterBuilder().withDefaultFormats()
								.withFormats(Format.HTML, Format.CONSOLE)
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
		return getStories();
	}

	@Override
	public InjectableStepsFactory stepsFactory() {
		return new InstanceStepsFactory(configuration(), new SetupSteps(),
				new ProjectSteps(), new ClassSteps(), new PitclipseSteps());
	}

	@BeforeStories
	public static void beforeClass() throws Exception {
		PLAYBACK_DELAY = 100L;
		TIMEOUT = 5000L;
	}
}