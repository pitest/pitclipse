package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.pitest.pitclipse.ui.behaviours.steps.WorkspaceMutations;

public class PitMutationsView {

	private final SWTWorkbenchBot bot;

	public PitMutationsView(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public WorkspaceMutations getMutations() {
		SWTBotView mutationsView = bot.viewByTitle("PIT Mutations");
		mutationsView.show();
		SWTBotTree mutationTree = mutationsView.bot().tree();
		WorkspaceMutations.Builder workspaceBuilder = WorkspaceMutations
				.builder();

		return workspaceBuilder.build();
	}

}
