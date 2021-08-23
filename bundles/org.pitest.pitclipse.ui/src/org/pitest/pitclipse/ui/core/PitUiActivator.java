/*******************************************************************************
 * Copyright 2021 Jonas Kutscha and contributors
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

package org.pitest.pitclipse.ui.core;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.pitest.pitclipse.ui.highlighting.PitclipseMutantHighlighter;

/**
 * The ui activator class which initializes the icons of the plug in
 */
public class PitUiActivator extends AbstractUIPlugin {
    /**
     * Image registry for this plug in
     */
    private ImageRegistry imageRegistry;
    /**
     * Key under which the pit icon is put in the registry
     */
    private static final String PIT_ICON = "org.pitest.pitclipse.pitIcon";
    /**
     * Qualifier of this plug in
     */
    public static final String PLUGIN_ID = "org.pitest.pitclipse.ui";
    /**
     * The shared instance
     */
    private static PitUiActivator plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        plugin = this; // NOSONAR typical in Eclipse
        initIcons(context);
        super.start(context);
    }

    /**
     * Init of the icons of the plug in
     */
    private void initIcons(BundleContext context) {
        Display.getDefault().syncExec(() -> {
            imageRegistry = new ImageRegistry();
            Bundle bundle = FrameworkUtil.getBundle(getClass());
            final URL url = FileLocator.find(bundle, new Path("icons/pit.gif"), null);
            imageRegistry.put(PIT_ICON, ImageDescriptor.createFromURL(url).createImage());
            // annotation icons
            final URL killedUrl = FileLocator.find(bundle, new Path("icons/killed.gif"), null);
            imageRegistry.put(PitclipseMutantHighlighter.KILLED_MUTANT_MARKER,
                    ImageDescriptor.createFromURL(killedUrl).createImage());
            final URL survivingUrl = FileLocator.find(bundle, new Path("icons/survived.gif"), null);
            imageRegistry.put(PitclipseMutantHighlighter.SURVIVING_MUTANT_MARKER,
                    ImageDescriptor.createFromURL(survivingUrl).createImage());
            final URL noCoverageUrl = FileLocator.find(bundle, new Path("icons/noCoverage.gif"), null);
            imageRegistry.put(PitclipseMutantHighlighter.NO_COVERAGE_MUTANT_MARKER,
                    ImageDescriptor.createFromURL(noCoverageUrl).createImage());
            final URL timeoutUrl = FileLocator.find(bundle, new Path("icons/timeout.gif"), null);
            imageRegistry.put(PitclipseMutantHighlighter.TIMEOUT_MUTANT_MARKER,
                    ImageDescriptor.createFromURL(timeoutUrl).createImage());
            // use same icon for these 3, because they are not from huge interest
            final URL nonViableUrl = FileLocator.find(bundle, new Path("icons/nonViable.gif"), null);
            imageRegistry.put(PitclipseMutantHighlighter.NON_VIABLE_MUTANT_MARKER,
                    ImageDescriptor.createFromURL(nonViableUrl).createImage());
            imageRegistry.put(PitclipseMutantHighlighter.MEMORY_ERROR_MARKER,
                    ImageDescriptor.createFromURL(nonViableUrl).createImage());
            imageRegistry.put(PitclipseMutantHighlighter.RUN_ERROR_MARKER,
                    ImageDescriptor.createFromURL(nonViableUrl).createImage());
        });
    }

    /**
     * @return Returns the pit icon.
     */
    public Image getPitIcon() {
        return imageRegistry.get(PIT_ICON);
    }

    /**
     * @param imageDescritporId
     * @return the requested image descriptor or null, if not found
     */
    public ImageDescriptor getImageDescriptor(String imageDescritporId) {
        return imageRegistry.getDescriptor(imageDescritporId);
    }

    /**
     * @return the shared instance
     */
    public static PitUiActivator getDefault() {
        return plugin;
    }

    public Image getImage(String imageDescriptorId) {
        return imageRegistry.get(imageDescriptorId);
    }
}
