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

package org.pitest.pitclipse.ui;

import static org.eclipse.core.runtime.FileLocator.toFileURL;
import static com.google.common.collect.ImmutableList.builder;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Sets.newTreeSet;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * The activator class controls the plug-in life cycle
 */
public class PitclipseTestActivator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.pitest.pitclipse-ui.tests"; //$NON-NLS-1$

    // The shared instance
    private static final PitclipseTestActivator plugin = new PitclipseTestActivator();

    private ImmutableList<String> stories = ImmutableList.of();

    private final AtomicBoolean testsInProgress = new AtomicBoolean(false);

    public PitclipseTestActivator() {
    }

    public List<String> getStories() {
        return copyOf(stories);
    }

    private void setStories(List<String> stories) {
        this.stories = copyOf(newTreeSet(stories));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext context) throws Exception { // NOPMD - Base
                                                                // class defines
                                                                // signature
        super.start(context);
        setActivator(this);
        Enumeration<URL> stories = context.getBundle().findEntries("/", "j06*.story", true);
        Builder<String> builder = builder();
        while (stories.hasMoreElements()) {
            URL storyUrl = stories.nextElement();
            URI filePath = locateAndEscapeUrl(storyUrl).toURI();
            if (!filePath.getPath().contains("src")) {
                builder.add(filePath.toString());
            }
        }
        setStories(builder.build());
    }

    private URL locateAndEscapeUrl(URL url) throws IOException {
        // Nasty hack thanks to the following 6 year old bug in Eclipse
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=145096
        // Astonishingly, the reason given for not fixing is that many plugins
        // expect invalid Urls so would be broken!!!
        URL unescapedUrl = toFileURL(url);
        String escaped = unescapedUrl.getPath().replace(" ", "%20");
        return new URL("file", "", escaped);
    }

    private static void setActivator(PitclipseTestActivator pitActivator) {
//        plugin = pitActivator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(BundleContext context) throws Exception { // NOPMD - Base
                                                                // class defines
                                                                // signature
        setActivator(null);
        List<String> emptyPath = ImmutableList.of();
        setStories(emptyPath);
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static PitclipseTestActivator getDefault() {
        return plugin;
    }

    public boolean areTestsInProgress() {
        return testsInProgress.get();
    }

    public void startTests() {
        testsInProgress.set(true);
    }

}
