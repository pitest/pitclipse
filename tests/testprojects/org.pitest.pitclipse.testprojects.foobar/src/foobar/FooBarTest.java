package foobar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FooBarTest {

    @Test public void fooBarTest() {
        FooBar x = new FooBar();
        assertEquals(4, x.f(1));
    }

}
