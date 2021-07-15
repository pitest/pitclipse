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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.pitest.pitclipse.core.extension.handler.ExtensionPointHandler;
import org.pitest.pitclipse.ui.extension.point.PitUiUpdate;

/**
 * A progress listener that notifies contributions to the
 * {@code org.pitest.pitclipse.ui.results} extension point when the <i>PIT
 * Summary</i> view is fully loaded.
 */
public class PitUiUpdatePublisher implements ProgressListener {
    static final boolean WINDOWS = System.getProperty("os.name").startsWith("Windows");
    private static final String EXTENSION_POINT_ID = "org.pitest.pitclipse.ui.results";
    private final Browser browser;
    private final ExtensionPointHandler<PitUiUpdate> handler;

    public PitUiUpdatePublisher(Browser browser) {
        this.browser = browser;
        this.handler = new ExtensionPointHandler<>(EXTENSION_POINT_ID);
    }

    public void changed(ProgressEvent event) {
        // Do nothing
    }

    public void completed(ProgressEvent event) {
        PitUiUpdate update = new PitUiUpdate.Builder().withHtml(getHtml()).build();
        handler.execute(Platform.getExtensionRegistry(), update);
    }

    /**
     * Get the index.html of the results, even if the browser shows a different file
     * of the results
     * @return html text of the index.html as a String
     */
    private String getHtml() {
        // assume url is path to index.html and top folder should represent date and
        // time
        // remove os differences in path
        final Pattern pattern;
        if (WINDOWS) {
            pattern = Pattern.compile("(file:\\\\)(.*\\[0-9]+\\)(.+)");
        } else {
            pattern = Pattern.compile("(file://)(.*/[0-9]+/)(.+)");
        }
        Matcher matcher = pattern.matcher(browser.getUrl());
        if (matcher.find() && matcher.groupCount() == 3) {
            try {
                return new String(Files.readAllBytes(Paths.get(File.separatorChar + matcher.group(2) + "index.html")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "No HTML found!";
    }
}
