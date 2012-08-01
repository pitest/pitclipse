package org.pitest.pitclipse.ui.behaviours.pageobjects;

import static junit.framework.Assert.fail;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class PackageExplorer {

	private static final String PACKAGE_EXPLORER = "Package Explorer";
	private final SWTWorkbenchBot bot;

	public PackageExplorer(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public List<String> getProjectsInWorkspace() {
		Builder<String> builder = ImmutableList.builder();
		SWTBotTreeItem[] treeItems = bot.viewByTitle(PACKAGE_EXPLORER).bot()
				.tree().getAllItems();
		for (SWTBotTreeItem swtBotTreeItem : treeItems) {
			builder.add(swtBotTreeItem.getText());
		}
		return builder.build();
	}

	public void selectProject(String projectName) {
		openProject(getProject(projectName));
	}

	private void openProject(SWTBotTreeItem project) {
		project.click().expand();
	}

	private SWTBotTreeItem getProject(String projectName) {
		SWTBotTreeItem[] treeItems = bot.viewByTitle(PACKAGE_EXPLORER).bot()
				.tree().getAllItems();
		for (SWTBotTreeItem treeItem : treeItems) {
			if (projectName.equals(treeItem.getText())) {
				return treeItem;
			}
		}
		fail("Project: " + projectName + " couldn't be found");
		return null; // Never reached
	}

	public boolean doesPackageExistInProject(String packageName,
			String projectName) {
		SWTBotTreeItem project = getProject(projectName);
		openProject(project);
		return null != getPackageFromProject(project, packageName);
	}

	private SWTBotTreeItem getPackageFromProject(SWTBotTreeItem project,
			String packageName) {
		for (SWTBotTreeItem srcDir : project.getItems()) {
			srcDir.expand();
			for (SWTBotTreeItem pkg : srcDir.getItems()) {
				if (packageName.equals(pkg.getText())) {
					return pkg;
				}
			}
		}
		return null;
	}

	public boolean doesClassExistInProject(String className,
			String packageName, String projectName) {
		SWTBotTreeItem project = getProject(projectName);
		openProject(project);
		SWTBotTreeItem pkg = getPackageFromProject(project, packageName);
		pkg.select();
		pkg.expand();
		if (null != pkg) {
			String fileName = className + ".java";
			for (SWTBotTreeItem clazz : pkg.getItems()) {
				if (fileName.equals(clazz.getText())) {
					return true;
				}
			}
		}
		return false;
	}

}
