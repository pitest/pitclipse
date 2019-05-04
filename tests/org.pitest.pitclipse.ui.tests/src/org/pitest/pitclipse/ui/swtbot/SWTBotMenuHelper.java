package org.pitest.pitclipse.ui.swtbot;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;

public class SWTBotMenuHelper {

    private static final class MenuFinder implements WidgetResult<MenuItem> {
        private final SWTBotMenu parentMenu;
        private final String searchString;

        private MenuFinder(SWTBotMenu parentMenu, String searchString) {
            this.parentMenu = parentMenu;
            this.searchString = searchString;
        }

        public MenuItem run() {
            Menu bar = parentMenu.widget.getMenu();
            if (bar != null) {
                for (MenuItem item : bar.getItems()) {
                    // Remove any hotkey marking
                    String menuText = item.getText().replace("&", "");
                    if (menuText.contains(searchString)) {
                        return item;
                    }
                }
            }
            return null;
        }
    }

    public SWTBotMenuHelper() {
    }

    public SWTBotMenu findMenu(final SWTBotMenu parentMenu,
            final String searchString) {
        MenuItem menuItem = UIThreadRunnable.syncExec(new MenuFinder(
                parentMenu, searchString));

        if (menuItem == null) {
            throw new WidgetNotFoundException("MenuItem \"" + searchString
                    + "\" not found.");
        } else {
            return new SWTBotMenu(menuItem);
        }
    }
}
