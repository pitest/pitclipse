package foo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FooTest {

    @Test public void fooTest() {
        Foo x = new Foo();
        assertEquals(2, x.f(1));
    }

}
