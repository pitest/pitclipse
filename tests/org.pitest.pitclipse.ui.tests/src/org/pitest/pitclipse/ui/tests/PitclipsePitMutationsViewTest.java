package org.pitest.pitclipse.ui.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.ui.behaviours.steps.PitMutation;
import org.pitest.pitclipse.ui.behaviours.steps.PitclipseSteps;

/**
 * @author Lorenzo Bettini
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class PitclipsePitMutationsViewTest extends AbstractPitclipseSWTBotTest {

    private static final String TEST_PROJECT = "org.pitest.pitclipse.testprojects.twoclasses";;
    private static final String FOO_BAR_PACKAGE = "foo.bar";
    private static final String FOO_CLASS = "Foo";
    private static final String FOO_TEST_CLASS = "FooTest";
    private static final String BAR_CLASS = "Bar";
    private static final String BAR_TEST_CLASS = "BarTest";

    @BeforeClass
    public static void setupJavaProject() throws CoreException {
        importTestProject(TEST_PROJECT);
        openEditor(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        openEditor(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        openEditor(BAR_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        openEditor(BAR_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
    }

    @Test
    public void selectMutationOpensTheClassAtTheRightLineNumber() throws CoreException {
        final long timeOutBefore = SWTBotPreferences.TIMEOUT;
        try {
            SWTBotPreferences.TIMEOUT = 10000;
            runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
            coverageReportGenerated(2, 80, 0, 6, 0);
            PitclipseSteps pitclipseSteps = new PitclipseSteps();
            PitMutation mutation = fromMutationLine(
                    "SURVIVED    | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    7 | negated conditional");
            pitclipseSteps.doubleClickMutationInMutationsView(mutation);
            pitclipseSteps.mutationIsOpened(FOO_CLASS + ".java", 7);
            mutation = fromMutationLine(
                    "SURVIVED    | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    7 | negated conditional");
            pitclipseSteps.doubleClickMutationInMutationsView(mutation);
            pitclipseSteps.mutationIsOpened(BAR_CLASS + ".java", 7);
        } finally {
            SWTBotPreferences.TIMEOUT = timeOutBefore;
        }
    }
}
