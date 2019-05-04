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
