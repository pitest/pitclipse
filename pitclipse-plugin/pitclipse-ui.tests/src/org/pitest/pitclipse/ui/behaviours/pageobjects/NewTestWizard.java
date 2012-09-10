package org.pitest.pitclipse.ui.behaviours.pageobjects;

import static org.pitest.pitclipse.ui.util.StepUtil.safeSleep;

import java.util.Set;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

import com.google.common.collect.Sets;

public class NewTestWizard {

	private final SWTWorkbenchBot bot;
	private final Set<String> projectsWithJunit = Sets.newHashSet();

	public NewTestWizard(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	private void acknowlegeJUnitPathDialog() {
		// May get asked to add JUnit to the path
		try {
			SWTBotShell shell = bot.shell("New JUnit Test Case");
			safeSleep(750);
			if (shell.isOpen()) {
				shell.activate();
				bot.button("OK").click();
			}
		} catch (WidgetNotFoundException e) {
			System.out
					.println("Did not find exepected element.  It might be ok...");
			e.printStackTrace();
		}
	}

	public void createClass(TestClassContext context) {
		SWTBotShell wizardShell = bot.shell("New JUnit Test Case");
		wizardShell.activate();
		bot.textWithLabel("Package:").setText(context.getPackageName());
		bot.textWithLabel("Name:").setText(context.getClassName());
		bot.button("Finish").click();
		String projectName = context.getProjectName();
		if (!projectsWithJunit.contains(projectName)) {
			acknowlegeJUnitPathDialog();
			projectsWithJunit.add(projectName);
		}
	}

	public void resetProject(String projectName) {
		projectsWithJunit.remove(projectName);
	}

}
