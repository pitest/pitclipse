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

package org.pitest.pitclipse.core.launch;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.pitest.pitclipse.core.extension.handler.ExtensionPointHandler;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.runner.model.MutationsModel;

/**
 * <p>Updates all contributions to the {@code mutations.results} extension point.</p>
 * 
 * <p>
 * More specifically, an instance of this class:
 * <ol>
 *  <li>Browse the extension registry to find contributions to the {@code mutations.results} extension point
 *  <li>[for each contribution] Instantiate a new class from the "class" attribute
 *  <li>Cast the new class to {@link ResultNotifier}
 *  <li>Call {@link ResultNotifier#handleResults(Object)}, passing giving results as parameter
 * </ol>
 * </p>
 */
public class UpdateMutations implements Runnable {
    private static final String EXTENSION_POINT_ID = "org.pitest.pitclipse.core.mutations.results";

    private final MutationsModel model;

    /**
     * Creates a new runnable to update contributions to the {@code mutation.results} extension point.
     * 
     * @param results
     *          The results to be handled by the contributions.
     */
    public UpdateMutations(MutationsModel model) {
        this.model = model;
    }

    @Override
    public void run() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        new ExtensionPointHandler<MutationsModel>(EXTENSION_POINT_ID).execute(registry, model);
    }
}
