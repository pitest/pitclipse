package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

public enum PageObjects {
	INSTANCE;

	private final FileMenu fileMenu;
	private final WelcomeScreen welcomeScreen;
	private final PackageExplorer packageExplorer;
	private final WindowsMenu windowsMenu;
	private final RunMenu runMenu;
	private final PitView pitView;
	private final BuildProgress buildProgress;
	private final AbstractSyntaxTree abstractSyntaxTree;
	private final SourceMenu sourceMenu;
	private final RefactorMenu refactorMenu;

	private PageObjects() {
		SWTWorkbenchBot bot = new SWTWorkbenchBot();
		fileMenu = new FileMenu(bot);
		welcomeScreen = new WelcomeScreen(bot);
		packageExplorer = new PackageExplorer(bot);
		windowsMenu = new WindowsMenu(bot);
		runMenu = new RunMenu(bot);
		pitView = new PitView();
		buildProgress = new BuildProgress(bot);
		abstractSyntaxTree = new AbstractSyntaxTree();
		sourceMenu = new SourceMenu(bot);
		refactorMenu = new RefactorMenu(bot);
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

	public PitView getPitView() {
		return pitView;
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
}
