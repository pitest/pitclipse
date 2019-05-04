package org.pitest.pitclipse.runner.io;

import com.google.common.base.Optional;

import org.junit.Test;
import org.pitest.pitclipse.runner.io.SocketProviderIntegrationTestFixture.ReturnStatus;

import java.util.concurrent.Future;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.pitest.pitclipse.runner.io.SocketProviderIntegrationTestFixture.ECHO;
import static org.pitest.pitclipse.runner.io.SocketProviderIntegrationTestFixture.ReturnStatus.SUCCESS;
import static org.pitest.pitclipse.runner.io.SocketProviderIntegrationTestFixture.aFreePort;
import static org.pitest.pitclipse.runner.io.SocketProviderIntegrationTestFixture.connectTo;
import static org.pitest.pitclipse.runner.io.SocketProviderIntegrationTestFixture.listenOn;
import static org.pitest.pitclipse.runner.io.SocketProviderIntegrationTestFixture.readMessageFrom;

public class SocketProviderIntegrationTest {
    @Test
    public void socketProviderCanListenOnAFreePort() {
        assertThat(aFreePort(), is(greaterThanOrEqualTo(0)));
    }

    @Test
    public void cannotConnectToAPortThatIsNotBeingListenedOn() {
        assertThat(connectTo(aFreePort()), is(equalTo(Optional.<ObjectStreamSocket>absent()))) ;
    }

    @Test
    public void canConnectToAPortThatIsBeingListenedOn() throws Exception {
        int port = aFreePort();
        Future<ReturnStatus> result = listenOn(port);
        Future<String> msg = readMessageFrom(port);
        assertThat(result.get(5, SECONDS), is(SUCCESS));
        assertThat(msg.get(5, SECONDS), is(ECHO));
    }
}
