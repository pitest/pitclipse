package foobar;

import bar.Bar;
import foo.Foo;

public class FooBar {

    public int f(int i) {
        return new Foo().f(i) + new Bar().f(i);
    }

}
