/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

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
