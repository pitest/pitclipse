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

import static org.eclipse.core.resources.IResourceChangeEvent.POST_BUILD;

import java.io.Closeable;
import java.io.IOException;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

public class WaitForBuildCondition extends DefaultCondition implements
        Closeable {

    private volatile boolean completed = false;
    private IResourceChangeListener listener = null;

    public WaitForBuildCondition() {
    }

    public void subscribe() {
        listener = new BuiltResourceChangeListener();
        ResourcesPlugin.getWorkspace().addResourceChangeListener(listener,
                POST_BUILD);
    }

    public boolean test() throws Exception {
        return completed;
    }

    public String getFailureMessage() {
        return "Unable to determine if build completed.";
    }

    public void close() throws IOException {
        unsubscribe();
    }

    public void unsubscribe() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
    }

    private class BuiltResourceChangeListener implements
            IResourceChangeListener {
        public void resourceChanged(IResourceChangeEvent event) {
            completed = true;
        }
    }

}
