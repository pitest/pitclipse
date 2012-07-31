package org.pitest.pitclipse.ui.behaviours.pageobjects;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class PackageExplorer {

	private final SWTWorkbenchBot bot;

	public PackageExplorer(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public List<String> getProjectsInWorkspace() {
		Builder<String> builder = ImmutableList.builder();
		SWTBotView view = bot.viewByTitle("Package Explorer");
		SWTBotTreeItem[] treeItems = view.bot().tree().getAllItems();
		for (SWTBotTreeItem swtBotTreeItem : treeItems) {
			builder.add(swtBotTreeItem.getText());
		}
		return builder.build();
	}

}
