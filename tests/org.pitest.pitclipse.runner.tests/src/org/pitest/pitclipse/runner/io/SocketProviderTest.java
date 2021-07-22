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

package org.pitest.pitclipse.runner.io;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.pitest.pitclipse.runner.io.SocketProviderTestFixture.ECHO;
import static org.pitest.pitclipse.runner.io.SocketProviderTestFixture.aFreePort;
import static org.pitest.pitclipse.runner.io.SocketProviderTestFixture.connectTo;
import static org.pitest.pitclipse.runner.io.SocketProviderTestFixture.listenOn;
import static org.pitest.pitclipse.runner.io.SocketProviderTestFixture.readMessageFrom;
import static org.pitest.pitclipse.runner.io.SocketProviderTestFixture.ReturnStatus.SUCCESS;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import org.junit.Test;
import org.pitest.pitclipse.runner.io.SocketProvider.ServerSocketFactory;
import org.pitest.pitclipse.runner.io.SocketProviderTestFixture.ReturnStatus;

public class SocketProviderTest {

    static class FakeServerSocketFactory extends ServerSocketFactory {
        @Override
        public ServerSocket create(int port) throws IOException {
            throw new IOException();
        }
    }

    @Test
    public void socketProviderCanListenOnAFreePort() {
        assertThat(aFreePort(), is(greaterThanOrEqualTo(0)));
    }

    @Test
    public void cannotConnectToAPortThatIsNotBeingListenedOn() {
        assertThat(connectTo(aFreePort()), is(equalTo(Optional.<ObjectStreamSocket>empty())));
    }

    @Test
    public void cannotConnectToAPortThatIsNotBeingListenedOnInterrupted() throws Exception {
        List<Optional<ObjectStreamSocket>> result = new ArrayList<>();
        Thread t = new Thread(
            () -> result.add(new SocketProvider().connectTo(aFreePort())));
        t.start();
        t.interrupt();
        t.join();
        assertThat(result.get(0),
                is(equalTo(Optional.<ObjectStreamSocket>empty())));
    }

    @Test
    public void canConnectToAPortThatIsBeingListenedOn() throws Exception {
        int port = aFreePort();
        Future<ReturnStatus> result = listenOn(port);
        Future<String> msg = readMessageFrom(port);
        assertThat(result.get(5, SECONDS), is(SUCCESS));
        assertThat(msg.get(5, SECONDS), is(ECHO));
    }

    @Test
    public void errorOnListen() {
        try {
            new SocketProvider(new FakeServerSocketFactory())
                .listen(1000);
            fail("should not get here");
        } catch (Exception e) {
            assertTrue(e.getClass().getCanonicalName(),
                    e instanceof RuntimeException);
        }
    }

    @Test
    public void errorOnFreePort() {
        try {
            new SocketProvider(new FakeServerSocketFactory())
                .getFreePort();
            fail("should not get here");
        } catch (Exception e) {
            assertTrue(e.getClass().getCanonicalName(),
                    e instanceof RuntimeException);
        }
    }
}
