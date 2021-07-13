package bar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BarTest {

    @Test public void barTest() {
        Bar x = new Bar();
        assertEquals(2, x.f(1));
    }

}
