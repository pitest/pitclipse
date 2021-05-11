package org.pitest.pitclipse.ui.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
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
    private static final String FOO_BAR_PACKAGE = "foo.bar";
    private static final String FOO_CLASS = "Foo";
    private static final String FOO_TEST_CLASS = "FooTest";

    @BeforeClass
    public static void setupJavaProject() {
        createJavaProjectWithJUnit5(TEST_PROJECT);
        verifyProjectExists(TEST_PROJECT);
        createClass(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        createClass(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
    }

    @Before
    public void cleanClasses() throws CoreException {
        removeMethods(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        removeMethods(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
    }

    @Test
    public void classWithMethodAndNoCoverageTestMethod() throws CoreException {
        createMethod(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "public int doFoo(int i) {return i + 1;}");
        createMethod(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "@org.junit.jupiter.api.Test public void fooTest1() {Foo foo = new Foo();}");
        runTest(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        consoleContains(2, 0, 0, 0, 0);
        mutationsAre(
        "NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |    6 | Replaced integer addition with subtraction       \n" +
        "NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |    6 | replaced int return with 0 for foo/bar/Foo::doFoo ");
    }

    @Test
    public void classWithMethodAndBadTestMethod() throws CoreException {
        createMethod(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "public int doFoo(int i) {return i + 1;}");
        createMethod(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "@org.junit.jupiter.api.Test public void fooTest2() {new Foo().doFoo(1);}");
        runTest(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        consoleContains(2, 0, 0, 2, 1);
        mutationsAre(
        "SURVIVED | project1 | foo.bar | foo.bar.Foo |    6 | Replaced integer addition with subtraction       \n" +
        "SURVIVED | project1 | foo.bar | foo.bar.Foo |    6 | replaced int return with 0 for foo/bar/Foo::doFoo ");
    }

    @Test
    public void classWithMethodAndBetterTestMethod() throws CoreException {
        createMethod(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "public int doFoo(int i) {return i + 1;}");
        createMethod(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "@org.junit.jupiter.api.Test public void fooTest3() {org.junit.jupiter.api.Assertions.assertEquals(2, new Foo().doFoo(1));}");
        runTest(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        consoleContains(2, 2, 100, 2, 1);
        mutationsAre(
        "KILLED | project1 | foo.bar | foo.bar.Foo |    6 | Replaced integer addition with subtraction       \n" +
        "KILLED | project1 | foo.bar | foo.bar.Foo |    6 | replaced int return with 0 for foo/bar/Foo::doFoo ");
    }

    @Test
    public void runPitAtPackageAndPackageRootAndProjectLevel() throws CoreException {
        createMethod(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "public int doFoo(int i) {return i + 1;}");
        createMethod(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "@org.junit.jupiter.api.Test public void fooTest3() {org.junit.jupiter.api.Assertions.assertEquals(2, new Foo().doFoo(1));}");
        runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
        consoleContains(2, 2, 100, 2, 1);
        runPackageRootTest("src", TEST_PROJECT);
        consoleContains(2, 2, 100, 2, 1);
        runProjectTest(TEST_PROJECT);
        consoleContains(2, 2, 100, 2, 1);
    }

}
