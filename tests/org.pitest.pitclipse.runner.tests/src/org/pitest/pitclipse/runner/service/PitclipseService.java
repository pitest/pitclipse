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

package org.pitest.pitclipse.runner.service;

import org.pitest.pitclipse.runner.PitRequest;
import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.runner.model.ModelBuilder;
import org.pitest.pitclipse.runner.model.MutationsModel;
import org.pitest.pitclipse.runner.server.PitServer;
import org.pitest.pitclipse.runner.server.PitServerProvider;

public class PitclipseService {
    private final PitServerProvider serverProvider;
    private final ModelBuilder modelBuilder;

    public PitclipseService(PitServerProvider serverProvider, ModelBuilder modelBuilder) {
        this.serverProvider = serverProvider;
        this.modelBuilder = modelBuilder;
    }

    public MutationsModel analyse(int port, PitRequest request) {
        PitServer server = serverProvider.newServerFor(port);
        server.sendRequest(request);
        PitResults results = server.receiveResults();
        return modelBuilder.buildFrom(results);
    }
}
