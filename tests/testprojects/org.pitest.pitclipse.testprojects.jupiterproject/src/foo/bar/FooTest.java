package foo.bar;

public class FooTest {

    @org.junit.jupiter.api.Test
    public void fooTest3() {
        org.junit.jupiter.api.Assertions
            .assertEquals(2,
                new Foo().doFoo(1));
    }

}
