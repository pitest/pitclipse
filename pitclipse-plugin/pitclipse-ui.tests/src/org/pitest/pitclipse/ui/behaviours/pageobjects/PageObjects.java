package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

public enum PageObjects {
	INSTANCE;

	private final FileMenu fileMenu;
	private final WelcomeScreen welcomeScreen;
	private final PackageExplorer packageExplorer;
	private final WindowsMenu windowsMenu;

	private PageObjects() {
		SWTWorkbenchBot bot = new SWTWorkbenchBot();
		fileMenu = new FileMenu(bot);
		welcomeScreen = new WelcomeScreen(bot);
		packageExplorer = new PackageExplorer(bot);
		windowsMenu = new WindowsMenu(bot);
	}

	public FileMenu getfileMenu() {
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
}
