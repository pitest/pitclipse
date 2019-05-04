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

package org.pitest.pitclipse.core.result;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.core.launch.UpdateMutations;
import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.runner.model.ModelBuilder;
import org.pitest.pitclipse.runner.model.MutationsModel;

public class MutationsModelNotifier implements ResultNotifier<PitResults> {

    private static final ModelBuilder MODEL_BUILDER = new ModelBuilder(JdtStructureService.INSTANCE);

    @Override
    public void handleResults(PitResults results) {
        MutationsModel mutationModel = MODEL_BUILDER.buildFrom(results);
        Job.create("Reporting detected mutations", monitor -> {
            new UpdateMutations(mutationModel).run();
            return new Status(IStatus.OK, "org.pitest.pitclipse.core", "ok");
        }).schedule();
    }
}
