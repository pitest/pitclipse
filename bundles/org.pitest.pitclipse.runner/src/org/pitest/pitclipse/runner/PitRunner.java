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

package org.pitest.pitclipse.runner;

import java.io.File;

import org.pitest.mutationtest.commandline.MutationCoverageReport;
import org.pitest.pitclipse.runner.results.Mutations;
import org.pitest.pitclipse.runner.results.mutations.RecordingMutationsDispatcher;
import org.pitest.pitclipse.runner.util.PitFileUtils;

import com.google.common.base.Function;

/**
 * Executes PIT.
 */
public class PitRunner {

    private PitRunner() {
        // Only static methods
    }

    public static Function<PitRequest, PitResults> executePit() {
        return request -> {
            String[] cliArgs = PitCliArguments.from(request.getOptions());
            MutationCoverageReport.main(cliArgs);
            File reportDir = request.getReportDirectory();
            File htmlResultFile = PitFileUtils.findFile(reportDir, "index.html");
            Mutations mutations = RecordingMutationsDispatcher.INSTANCE.getDispatchedMutations();
            return PitResults.builder()
                   .withHtmlResults(htmlResultFile)
                   .withProjects(request.getProjects())
                   .withMutations(mutations)
                   .build();
        };
    }

}
