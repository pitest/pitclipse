package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

public enum PageObjects {
	PAGES;

	private final FileMenu fileMenu;
	private final WelcomeScreen welcomeScreen;
	private final PackageExplorer packageExplorer;
	private final WindowsMenu windowsMenu;
	private final RunMenu runMenu;
	private final PitSummaryView pitSummaryView;
	private final PitMutationsView pitMutationsView;
	private final BuildProgress buildProgress;
	private final AbstractSyntaxTree abstractSyntaxTree;
	private final SourceMenu sourceMenu;
	private final RefactorMenu refactorMenu;
	private final Views views;

	private PageObjects() {
		SWTWorkbenchBot bot = new SWTWorkbenchBot();
		fileMenu = new FileMenu(bot);
		welcomeScreen = new WelcomeScreen(bot);
		packageExplorer = new PackageExplorer(bot);
		windowsMenu = new WindowsMenu(bot);
		runMenu = new RunMenu(bot);
		pitSummaryView = new PitSummaryView();
		pitMutationsView = new PitMutationsView(bot);
		buildProgress = new BuildProgress(bot);
		abstractSyntaxTree = new AbstractSyntaxTree();
		sourceMenu = new SourceMenu(bot);
		refactorMenu = new RefactorMenu(bot);
		views = new Views(bot);
	}

	public FileMenu getFileMenu() {
		return fileMenu;
	}

	public WelcomeScreen getWelcomeScreen() {
		return welcomeScreen;
	}

	public PackageExplorer getPackageExplorer() {
		return packageExplorer;
	}

	public WindowsMenu getWindowsMenu() {
		return windowsMenu;
	}

	public RunMenu getRunMenu() {
		return runMenu;
	}

	public PitSummaryView getPitSummaryView() {
		return pitSummaryView;
	}

	public BuildProgress getBuildProgress() {
		return buildProgress;
	}

	public AbstractSyntaxTree getAbstractSyntaxTree() {
		return abstractSyntaxTree;
	}

	public SourceMenu getSourceMenu() {
		return sourceMenu;
	}

	public RefactorMenu getRefactorMenu() {
		return refactorMenu;
	}

	public PitMutationsView getPitMutationsView() {
		return pitMutationsView;
	}

	public Views views() {
		return views;
	}
}
