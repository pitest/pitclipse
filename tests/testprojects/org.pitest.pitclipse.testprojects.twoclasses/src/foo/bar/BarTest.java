package foo.bar;

public class BarTest {

    @org.junit.Test public void badTest() {
        Bar x = new Bar();
        x.f(1);
    }

}
