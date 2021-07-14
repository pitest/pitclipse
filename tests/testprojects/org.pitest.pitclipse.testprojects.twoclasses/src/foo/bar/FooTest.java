package foo.bar;

public class FooTest {

    @org.junit.Test public void badTest() {
        Foo x = new Foo();
        x.f(1);
    }

}
