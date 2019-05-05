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

import org.pitest.pitclipse.core.extension.point.PitRuntimeOptions;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.runner.PitRequest;
import org.pitest.pitclipse.runner.client.PitCommunicator;
import org.pitest.pitclipse.runner.client.PitResultHandler;
import org.pitest.pitclipse.runner.server.PitServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>Launches a server that will update all contributions to the {@code results} 
 * extension point with the results produced by the currently running PIT application.</p>
 * 
 * <p>This class is registered through the {@code executePit} extension point
 * and is hence called each time a new PIT application is launched.</p>
 */
public class PitExecutionNotifier implements ResultNotifier<PitRuntimeOptions> {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void handleResults(PitRuntimeOptions runtimeOptions) {
        PitResultHandler resultHandler = new ExtensionPointResultHandler();
        PitServer server = new PitServer(runtimeOptions.getPortNumber());
        PitRequest request = PitRequest.builder().withPitOptions(runtimeOptions.getOptions())
                .withProjects(runtimeOptions.getMutatedProjects()).build();
        // TODO Check whether it can be replaced by Jobs API
        executorService.execute(new PitCommunicator(server, request, resultHandler));
    }
}
