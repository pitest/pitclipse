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
 * 
 * @param <T> 
 */
public class ExtensionPointHandler<T> {
    private final String extensionPointId;

    public ExtensionPointHandler(String extensionPointId) {
        this.extensionPointId = extensionPointId;
    }

    public <U> void execute(IExtensionRegistry registry, U results) {
        evaluate(registry, results);
    }

    private <U> void evaluate(IExtensionRegistry registry, final U results) {
        IConfigurationElement[] config = registry.getConfigurationElementsFor(extensionPointId);
        try {
            for (IConfigurationElement e : config) {
                Object obj = e.createExecutableExtension("class");
                if (obj instanceof ResultNotifier) {
                    @SuppressWarnings("unchecked")
                    final ResultNotifier<U> notifier = (ResultNotifier<U>) obj;
                    executeExtension(new NotifierRunnable<U>(notifier, results));
                }
            }
        } catch (CoreException ex) {
            warn("Error thrown notifying results", ex);
        }
    }

    private <U> void executeExtension(final Runnable extension) {
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
