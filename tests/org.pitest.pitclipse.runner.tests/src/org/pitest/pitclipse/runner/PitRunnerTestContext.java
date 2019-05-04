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

import org.pitest.pitclipse.runner.client.PitClient;
import org.pitest.pitclipse.runner.server.PitServer;

import java.io.ByteArrayOutputStream;

public final class PitRunnerTestContext {

    private int portNumber;
    private PitRequest request;
    private PitServer pitServer;
    private PitRequest transmittedRequest;
    private PitClient client;
    private PitResults results;
    private PitResults transmittedResults;
    private ByteArrayOutputStream outputStream;

    public PitRequest getRequest() {
        return request;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPitServer(PitServer pitServer) {
        this.pitServer = pitServer;
    }

    public void setPortNumber(int port) {
        portNumber = port;
    }

    public void setRequest(PitRequest request) {
        this.request = request;
    }

    public PitServer getPitServer() {
        return pitServer;
    }

    public void setTransmittedRequest(PitRequest pitRequest) {
        this.transmittedRequest = pitRequest;
    }

    public PitRequest getTransmittedRequest() {
        return transmittedRequest;
    }

    public void setPitClient(PitClient client) {
        this.client = client;
    }

    public PitClient getPitClient() {
        return client;
    }

    public PitResults getResults() {
        return results;
    }

    public void setResults(PitResults results) {
        this.results = results;
    }

    public PitResults getTransmittedResults() {
        return transmittedResults;
    }

    public void setTransmittedResults(PitResults transmittedResults) {
        this.transmittedResults = transmittedResults;
    }

    public void setOutputStream(ByteArrayOutputStream byteOutputStream) {
        this.outputStream = byteOutputStream;

    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

}
