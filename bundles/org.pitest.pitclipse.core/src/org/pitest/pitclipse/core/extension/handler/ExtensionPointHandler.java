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

package org.pitest.pitclipse.core.extension.handler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;

import static org.pitest.pitclipse.core.PitCoreActivator.warn;

/**
 * <p>Manages contributions to a given extension point.</p>
 * 
 * <p>This extension point must provide a "class" attribute
 * that correspond to a fully qualified name of a Java class
 * implementing the {@link ResultNotifier} interface.</p>
 * 
 * @param <U>
 *          The type of results expected by the extensions
 */
public class ExtensionPointHandler<U> {
    private final String extensionPointId;

    /**
     * Creates a new handler to manage contributions to the given extension point.
     * 
     * @param extensionPointId
     *          The id of the extension points which contributions must be handled.
     */
    public ExtensionPointHandler(String extensionPointId) {
        this.extensionPointId = extensionPointId;
    }

    /**
     * <p>Makes contributions to the extension points handling given results.</p>
     * 
     * <p>More specifically, this method:
     * <ol>
     *  <li>Browse all contributions to the extension point
     *  <li>Create a new instance corresponding the "class" attribute of each of these contributions
     *  <li>Cast these instances as {@link ResultNotifier ResultNotifier&lt;U&gt;}
     *  <li>Call {@link ResultNotifier#handleResults(Object) notifier.handleResults} with given results as parameter
     * </ol>
     * 
     * @param registry
     *          The registry providing available extensions.
     * @param results
     *          The results to be handled by the extensions.
     */
    public void execute(IExtensionRegistry registry, U results) {
        evaluate(registry, results);
    }

    private void evaluate(IExtensionRegistry registry, final U results) {
        IConfigurationElement[] config = registry.getConfigurationElementsFor(extensionPointId);
        try {
            for (IConfigurationElement e : config) {
                Object obj = e.createExecutableExtension("class");
                if (obj instanceof ResultNotifier) {
                    @SuppressWarnings("unchecked")
                    final ResultNotifier<U> notifier = (ResultNotifier<U>) obj;
                    executeExtension(() -> notifier.handleResults(results));
                }
            }
        } catch (CoreException ex) {
            warn("Error thrown notifying results", ex);
        }
    }

    private void executeExtension(final Runnable extension) {
        ISafeRunnable runnable = new ISafeRunnable() {
            @Override
            public void handleException(Throwable e) {
                warn("Exception in client", e);
            }

            @Override
            public void run() throws Exception {
                extension.run();
            }
        };
        SafeRunner.run(runnable);
    }

}
