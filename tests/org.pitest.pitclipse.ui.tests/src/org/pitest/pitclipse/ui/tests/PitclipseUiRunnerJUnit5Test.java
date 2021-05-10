package org.pitest.pitclipse.ui.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Similar to {@link PitclipseUiRunnerTest} but using JUnit 5 and only executing
 * a smaller number of tests.
 * 
 * @author Lorenzo Bettini
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class PitclipseUiRunnerJUnit5Test extends AbstractPitclipseSWTBotTest {

    private static final String TEST_PROJECT = "project1";

    @BeforeClass
    public static void setupJavaProject() {
        createJavaProjectWithJUnit5(TEST_PROJECT);
        verifyProjectExists(TEST_PROJECT);
    }

    @Before
    public void cleanProject() throws CoreException {
        deleteSrcContents(TEST_PROJECT);
    }

    @After
    public void cleanConsole() {
        clearConsole();
    }

    @Test
    public void classWithMethodAndNoCoverageTestMethod() throws CoreException {
        createClassWithMethod("Foo", "foo.bar", TEST_PROJECT,
                "public int doFoo(int i) {return i + 1;}");
        createClassWithMethod("FooTest", "foo.bar", TEST_PROJECT,
                "@org.junit.jupiter.api.Test public void fooTest1() {Foo foo = new Foo();}");
        runTest("FooTest", "foo.bar", TEST_PROJECT);
        consoleContains(2, 0, 0, 0, 0);
        mutationsAre(
        "NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |    6 | Replaced integer addition with subtraction       \n" +
        "NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |    6 | replaced int return with 0 for foo/bar/Foo::doFoo ");
    }

    @Test
    public void classWithMethodAndBadTestMethod() throws CoreException {
        createClassWithMethod("Foo", "foo.bar", TEST_PROJECT,
                "public int doFoo(int i) {return i + 1;}");
        createClassWithMethod("FooTest", "foo.bar", TEST_PROJECT,
                "@org.junit.jupiter.api.Test public void fooTest2() {new Foo().doFoo(1);}");
        runTest("FooTest", "foo.bar", TEST_PROJECT);
        consoleContains(2, 0, 0, 2, 1);
        mutationsAre(
        "SURVIVED | project1 | foo.bar | foo.bar.Foo |    6 | Replaced integer addition with subtraction       \n" +
        "SURVIVED | project1 | foo.bar | foo.bar.Foo |    6 | replaced int return with 0 for foo/bar/Foo::doFoo ");
    }

    @Test
    public void classWithMethodAndBetterTestMethod() throws CoreException {
        createClassWithMethod("Foo", "foo.bar", TEST_PROJECT,
                "public int doFoo(int i) {return i + 1;}");
        createClassWithMethod("FooTest", "foo.bar", TEST_PROJECT,
                "@org.junit.jupiter.api.Test public void fooTest3() {org.junit.jupiter.api.Assertions.assertEquals(2, new Foo().doFoo(1));}");
        runTest("FooTest", "foo.bar", TEST_PROJECT);
        consoleContains(2, 2, 100, 2, 1);
        mutationsAre(
        "KILLED | project1 | foo.bar | foo.bar.Foo |    6 | Replaced integer addition with subtraction       \n" +
        "KILLED | project1 | foo.bar | foo.bar.Foo |    6 | replaced int return with 0 for foo/bar/Foo::doFoo ");
    }

    @Test
    public void runPitAtPackageAndPackageRootAndProjectLevel() throws CoreException {
        createClassWithMethod("Foo", "foo.bar", TEST_PROJECT,
                "public int doFoo(int i) {return i + 1;}");
        createClassWithMethod("FooTest", "foo.bar", TEST_PROJECT,
                "@org.junit.jupiter.api.Test public void fooTest3() {org.junit.jupiter.api.Assertions.assertEquals(2, new Foo().doFoo(1));}");
        runPackageTest("foo.bar", TEST_PROJECT);
        consoleContains(2, 2, 100, 2, 1);
        clearConsole();
        runPackageRootTest("src", TEST_PROJECT);
        consoleContains(2, 2, 100, 2, 1);
        clearConsole();
        runProjectTest(TEST_PROJECT);
        consoleContains(2, 2, 100, 2, 1);
    }

}
