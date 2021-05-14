package org.pitest.pitclipse.ui.tests;

import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.ui.util.ProjectImportUtil;

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
        PAGES.getBuildProgress().listenForBuild();
        ProjectImportUtil.importProject(TEST_PROJECT);
        PAGES.getBuildProgress().waitForBuild();
        verifyProjectExists(TEST_PROJECT);
        assertNoErrorsInWorkspace();
        openEditor(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        openEditor(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
    }

    @Test
    public void runPitAtPackageAndPackageRootAndProjectLevel() throws CoreException {
        runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
        consoleContains(2, 2, 100, 2, 1);
        runPackageRootTest("src", TEST_PROJECT);
        consoleContains(2, 2, 100, 2, 1);
        runProjectTest(TEST_PROJECT);
        consoleContains(2, 2, 100, 2, 1);
    }

}
