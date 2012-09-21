package org.pitest.pitclipse.example;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExampleTest {

	private final Foo foo = new Foo();

	private final Bar bar = new Bar();

	@Test
	public void test() {
		assertEquals(foo.doFoo(0), bar.doBar(2));
	}

}
