package org.pitest.pitclipse.ui.behaviours.pageobjects;

import static org.pitest.pitclipse.ui.util.StepUtil.safeSleep;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.pitest.pitclipse.pitrunner.results.DetectionStatus;
import org.pitest.pitclipse.reloc.guava.base.Splitter;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.ui.behaviours.steps.PitMutation;

public class PitMutationsView {

	private final SWTWorkbenchBot bot;

	public PitMutationsView(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public List<PitMutation> getMutations() {
		safeSleep(1000);
		PageObjects.INSTANCE.views().closeConsole();
		SWTBotView mutationsView = bot.viewByTitle("PIT Mutations");
		mutationsView.show();
		SWTBotTree mutationTree = mutationsView.bot().tree();
		return mutationsFrom(mutationTree);
	}

	private ImmutableList<PitMutation> mutationsFrom(SWTBotTree mutationTree) {
		ImmutableList.Builder<PitMutation> resultBuilder = ImmutableList.builder();
		for (SWTBotTreeItem statusTreeItem : mutationTree.getAllItems()) {
			String status = normaliseLabel(statusTreeItem);
			statusTreeItem.expand();
			PitMutation.Builder mutationBuilder = PitMutation.builder().withStatus(DetectionStatus.valueOf(status));
			resultBuilder.addAll(statusMutations(mutationBuilder, statusTreeItem));
		}
		return resultBuilder.build();
	}

	private ImmutableList<PitMutation> statusMutations(PitMutation.Builder mutationBuilder,
			SWTBotTreeItem statusTreeItem) {
		ImmutableList.Builder<PitMutation> mutationResults = ImmutableList.builder();
		for (SWTBotTreeItem projectTreeItem : statusTreeItem.getItems()) {
			projectTreeItem.expand();
			mutationBuilder.withProject(normaliseLabel(projectTreeItem));
			mutationResults.addAll(projectMutations(mutationBuilder, projectTreeItem));
		}
		return mutationResults.build();
	}

	private ImmutableList<PitMutation> projectMutations(PitMutation.Builder mutationBuilder,
			SWTBotTreeItem projectTreeItem) {
		ImmutableList.Builder<PitMutation> mutationResults = ImmutableList.builder();
		for (SWTBotTreeItem pkgTreeItem : projectTreeItem.getItems()) {
			pkgTreeItem.expand();
			mutationBuilder.withPackage(normaliseLabel(pkgTreeItem));
			mutationResults.addAll(packageMutations(mutationBuilder, pkgTreeItem));
		}
		return mutationResults.build();
	}

	private ImmutableList<PitMutation> packageMutations(PitMutation.Builder mutationBuilder, SWTBotTreeItem pkgTreeItem) {
		ImmutableList.Builder<PitMutation> mutationResults = ImmutableList.builder();
		for (SWTBotTreeItem classTreeItem : pkgTreeItem.getItems()) {
			classTreeItem.expand();
			mutationBuilder.withClassName(normaliseLabel(classTreeItem));
			mutationResults.addAll(classMutations(mutationBuilder, classTreeItem));
		}
		return mutationResults.build();
	}

	private ImmutableList<PitMutation> classMutations(PitMutation.Builder mutationBuilder, SWTBotTreeItem classTreeItem) {
		ImmutableList.Builder<PitMutation> mutationResults = ImmutableList.builder();
		for (SWTBotTreeItem mutationTreeItem : classTreeItem.getItems()) {
			mutationTreeItem.expand();
			String mutationLabel = mutationTreeItem.getText();
			mutationBuilder.withLineNumber(lineNumberFrom(mutationLabel));
			mutationBuilder.withMutation(mutationFrom(mutationLabel));
			PitMutation mutation = mutationBuilder.build();
			mutationResults.add(mutation);
		}
		return mutationResults.build();
	}

	private int lineNumberFrom(String mutationLabel) {
		List<String> labelSplitByPattern = ImmutableList.copyOf(Splitter.on(':').split(mutationLabel));
		return labelSplitByPattern.isEmpty() ? 0 : Integer.valueOf(labelSplitByPattern.get(0));
	}

	private String mutationFrom(String mutationLabel) {
		List<String> labelSplitByPattern = ImmutableList.copyOf(Splitter.on(": ").split(mutationLabel));
		return labelSplitByPattern.size() > 1 ? labelSplitByPattern.get(1) : "";
	}

	private String normaliseLabel(SWTBotTreeItem treeItem) {
		String originalLabel = treeItem.getText();
		List<String> labelSplitByPattern = ImmutableList.copyOf(Splitter.on(Pattern.compile(" \\([0-9]*\\)")).split(
				originalLabel));
		return labelSplitByPattern.isEmpty() ? originalLabel : labelSplitByPattern.get(0);
	}
}
