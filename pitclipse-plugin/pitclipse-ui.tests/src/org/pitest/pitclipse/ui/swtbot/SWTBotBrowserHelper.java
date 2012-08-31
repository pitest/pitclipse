package org.pitest.pitclipse.ui.swtbot;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.ui.IViewReference;

public class SWTBotBrowserHelper {

	private static final class MenuFinder implements WidgetResult<Browser> {
		private final SWTBotView view;

		private MenuFinder(SWTBotView view) {
			this.view = view;
		}

		public Browser run() {
			if (isPitView()) {
			}
			return null;
		}

		private boolean isPitView() {
			IViewReference viewRef = view.getReference();
			return "org.pitest.pitclipse-ui.PITView".equals(viewRef.getId());
		}
	}

	public SWTBotBrowserHelper() {
	}

	public SWTBotMenu findBrowser(final SWTBotView parentMenu) {
		Browser menuItem = UIThreadRunnable
				.syncExec(new MenuFinder(parentMenu));

		if (menuItem == null) {
			throw new WidgetNotFoundException("Browser not found.");
		} else {
			return null;
		}
	}
}
