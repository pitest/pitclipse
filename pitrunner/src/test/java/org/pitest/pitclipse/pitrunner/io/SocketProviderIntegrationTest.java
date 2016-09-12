package org.pitest.pitclipse.pitrunner.io;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.pitest.pitclipse.pitrunner.io.SocketProviderIntegrationTestFixture.*;

public class SocketProviderIntegrationTest {
    @Test
    public void socketProviderCanListenOnAFreePort() {
        assertThat(aFreePort(), is(greaterThanOrEqualTo(0)));
    }

    @Test(expected = SocketCreationException.class)
    public void connectingToAPortThatIsNotBeingListenedOnTimesOut() {
        connectTo(aFreePort());
    }
}

class SocketProviderIntegrationTestFixture {
    private SocketProviderIntegrationTestFixture() {}
    static int aFreePort() { return new SocketProvider().getFreePort(); }
    static ObjectStreamSocket connectTo(int port) { return new SocketProvider().connectTo(port); }
}
