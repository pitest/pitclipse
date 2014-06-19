package org.pitest.pitclipse.ui.behaviours.pageobjects;

import static org.pitest.pitclipse.reloc.guava.collect.ImmutableList.builder;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.SwtBotTreeHelper.selectAndExpand;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitRunConfiguration.Builder;

public class RunConfigurationSelector {

	private final SWTWorkbenchBot bot;

	public RunConfigurationSelector(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public PitRunConfiguration getConfiguration(String configName) {
		activateShell();
		Builder builder = new PitRunConfiguration.Builder();
		return builder.build();
	}

	public List<PitRunConfiguration> getConfigurations() {
		activateShell();
		try {
			ImmutableList.Builder<PitRunConfiguration> builder = builder();
			SWTBotTreeItem[] configurations = activateShell().getItems();
			for (SWTBotTreeItem treeItem : configurations) {
				treeItem.select();
				builder.add(getPitConfiguration(treeItem));
			}
			return builder.build();
		} finally {
			bot.button("Close").click();
		}
	}

	private PitRunConfiguration getPitConfiguration(SWTBotTreeItem treeItem) {
		String name = treeItem.getText();
		String project = bot.textWithLabel("Project to mutate:").getText();
		boolean runInParallel = bot.checkBox("Mutation tests run in parallel").isChecked();
		boolean incrementalAnalysis = bot.checkBox("Use incremental analysis").isChecked();
		String excludedClasses = bot.textWithLabel("Excluded classes (e.g.*IntTest)").getText();
		String excludedMethods = bot.textWithLabel("Excluded methods (e.g.*toString*)").getText();
		String avoidCallsTo = bot.textWithLabel("Avoid calls to").getText();
		return new Builder().withName(name).withProjects(project).withRunInParallel(runInParallel)
				.withIncrementalAnalysis(incrementalAnalysis).withExcludedClasses(excludedClasses)
				.withExcludedMethods(excludedMethods).withAvoidCallsTo(avoidCallsTo).build();
	}

	private SWTBotTreeItem activateShell() {
		SWTBotShell shell = bot.shell("Run Configurations");
		shell.activate();
		for (SWTBotTreeItem treeItem : bot.tree().getAllItems()) {
			if ("PIT Mutation Test".equals(treeItem.getText())) {
				return selectAndExpand(treeItem);
			}
		}
		return null;
	}
}
