package foo;

import external.External;

public class Foo {

    public int f(int i) {
        return i + new External().increment();
    }

}
