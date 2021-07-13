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

    @BeforeClass
    public static void setupJavaProject() {
        createJavaProjectWithJUnit5(TEST_PROJECT);
        verifyProjectExists(TEST_PROJECT);
    }

    @Before
    public void cleanProject() throws CoreException {
        deleteSrcContents(TEST_PROJECT);
    }

    @Test
    public void runPitAtPackageAndPackageRootAndProjectLevel() throws CoreException {
        createClassWithMethod("Foo", "foo.bar", TEST_PROJECT,
                "public int doFoo(int i) {\n"
              + "    return i + 1;\n"
              + "}");
        createClassWithMethod("FooTest", "foo.bar", TEST_PROJECT,
                "@org.junit.jupiter.api.Test\n"
              + "public void fooTest3() {\n"
              + "    org.junit.jupiter.api.Assertions\n"
              + "        .assertEquals(2,\n"
              + "            new Foo().doFoo(1));\n"
              + "}");
        runPackageTest("foo.bar", TEST_PROJECT);
        consoleContains(2, 2, 100, 2, 1);
        runPackageRootTest("src", TEST_PROJECT);
        consoleContains(2, 2, 100, 2, 1);
        runProjectTest(TEST_PROJECT);
        consoleContains(2, 2, 100, 2, 1);
    }

}
