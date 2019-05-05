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

import org.pitest.pitclipse.core.extension.point.PitRuntimeOptions;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.ui.view.mutations.MutationsView;

import static org.pitest.pitclipse.runner.model.MutationsModel.EMPTY_MODEL;

/**
 * <p>Empty Pitclipse's {@link MutationsView}.</p>
 * 
 *  <p>Called by Pitclipse's core each time PIT is launched.</p>
 */
public class PitExecutionStarted implements ResultNotifier<PitRuntimeOptions> {
    @Override
    public void handleResults(PitRuntimeOptions options) {
        MutationsView mutationsView = PitViewFinder.INSTANCE.getMutationsView();
        mutationsView.updateWith(EMPTY_MODEL);
    }
}
