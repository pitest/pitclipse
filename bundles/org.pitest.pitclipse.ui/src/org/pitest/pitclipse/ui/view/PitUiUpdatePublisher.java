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

package org.pitest.pitclipse.ui.view;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.pitest.pitclipse.core.extension.handler.ExtensionPointHandler;
import org.pitest.pitclipse.ui.extension.point.PitUiUpdate;

public class PitUiUpdatePublisher implements ProgressListener {

    private static final String EXTENSION_POINT_ID = "org.pitest.pitclipse.ui.results";
    private final Browser browser;
    private final ExtensionPointHandler<PitUiUpdate> handler;

    public PitUiUpdatePublisher(Browser browser) {
        this.browser = browser;
        handler = new ExtensionPointHandler<PitUiUpdate>(EXTENSION_POINT_ID);
    }

    public void changed(ProgressEvent event) {
        // Do nothing
    }

    public void completed(ProgressEvent event) {
        PitUiUpdate update = new PitUiUpdate.Builder().withHtml(
                browser.getText()).build();
        handler.execute(Platform.getExtensionRegistry(), update);
    }

}
