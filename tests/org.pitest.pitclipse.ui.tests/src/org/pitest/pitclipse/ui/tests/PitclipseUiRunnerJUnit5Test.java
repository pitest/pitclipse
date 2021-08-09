package org.pitest.pitclipse.ui.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
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

    private static final String TEST_PROJECT = "org.pitest.pitclipse.testprojects.jupiterproject";
    private static final String FOO_BAR_PACKAGE = "foo.bar";
    private static final String FOO_CLASS = "Foo";
    private static final String FOO_TEST_CLASS = "FooTest";

    @BeforeClass
    public static void setupJavaProject() throws CoreException {
        importTestProject(TEST_PROJECT);
        openEditor(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        openEditor(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
    }

    // @Test This one is really flaky on Maven
    public void runPitJunit5AtPackageLevel() throws CoreException {
        runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
        coverageReportGenerated(2, 100, 100, 2, 2);
    }

    // @Test This one is really flaky on Maven
    public void runPitJunit5AtPackageRootLevel() throws CoreException {
        runPackageRootTest("src", TEST_PROJECT);
        coverageReportGenerated(2, 100, 100, 2, 2);
    }

    @Test
    public void runPitJunit5AtProjectLevel() throws CoreException {
        final long timeOutBefore = SWTBotPreferences.TIMEOUT;
        try {
            SWTBotPreferences.TIMEOUT = 10000;
            runProjectTest(TEST_PROJECT);
            coverageReportGenerated(1, 100, 100, 2, 2);
        } finally {
            SWTBotPreferences.TIMEOUT = timeOutBefore;
        }
    }

}
