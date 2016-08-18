package org.pitest.pitclipse.ui.behaviours.pageobjects;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.pitest.pitclipse.pitrunner.PitOptions;
import org.pitest.pitclipse.ui.swtbot.PitOptionsNotifier;
import org.pitest.pitclipse.ui.swtbot.SWTBotMenuHelper;

public class RunMenu {

    private static final String RUN = "Run";
    private static final String RUN_AS = "Run As";
    private static final String PIT_MUTATION_TEST = "PIT Mutation Test";
    private static final String JUNIT_TEST = "JUnit Test";
    private static final String RUN_CONFIGURATIONS = "Run Configurations";
    private final SWTWorkbenchBot bot;
    private final RunConfigurationSelector runConfigurationSelector;

    public RunMenu(SWTWorkbenchBot bot) {
        this.bot = bot;
        runConfigurationSelector = new RunConfigurationSelector(bot);
    }

    public void runJUnit() {
        SWTBotMenuHelper menuHelper = new SWTBotMenuHelper();
        menuHelper.findMenu(bot.menu(RUN).menu(RUN_AS), JUNIT_TEST).click();
    }

    public void runPit() {
        SWTBotMenuHelper menuHelper = new SWTBotMenuHelper();
        menuHelper.findMenu(bot.menu(RUN).menu(RUN_AS), PIT_MUTATION_TEST)
                .click();
    }

    public List<PitRunConfiguration> runConfigurations() {
        SWTBotMenuHelper menuHelper = new SWTBotMenuHelper();
        menuHelper.findMenu(bot.menu(RUN), RUN_CONFIGURATIONS).click();
        return runConfigurationSelector.getConfigurations();
    }

    public PitOptions getLastUsedPitOptions() {
        return PitOptionsNotifier.INSTANCE.getLastUsedOptions();
    }

}
