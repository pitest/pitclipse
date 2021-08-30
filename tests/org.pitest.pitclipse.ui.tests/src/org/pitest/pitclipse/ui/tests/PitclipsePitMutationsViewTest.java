package org.pitest.pitclipse.ui.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.ui.behaviours.steps.PitMutation;
import org.pitest.pitclipse.ui.behaviours.steps.PitclipseSteps;
import org.pitest.pitclipse.ui.mutation.marker.PitclipseMutantMarkerFactory;

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
    }

    @Test
    public void checkForExistingMutationMarkers() throws CoreException {
        runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
        coverageReportGenerated(2, 80, 0, 6, 0);
        bot.waitUntil(new DefaultCondition() {
            @Override
            public boolean test() throws Exception {
                return getPitclipseMarker().length > 0;
            }
            @Override
            public String getFailureMessage() {
                return "No pitclipse marker were found!";
            }
        });
        IMarker[] marker = getPitclipseMarker();
        assertEquals(6, marker.length);
        int numberOfKilled = 0, numberOfSurvived = 0, numberOfNoCoverage = 0, numberOfOthers = 0;
        for (IMarker iMarker : marker) {
            switch (iMarker.getType()) {
            case PitclipseMutantMarkerFactory.KILLED_MUTANT_MARKER:
                numberOfKilled++;
                break;
            case PitclipseMutantMarkerFactory.SURVIVING_MUTANT_MARKER:
                numberOfSurvived++;
                break;
            case PitclipseMutantMarkerFactory.NO_COVERAGE_MUTANT_MARKER:
                numberOfNoCoverage++;
                break;
            default:
                numberOfOthers++;
                break;
            }
        }
        assertEquals(0, numberOfKilled);
        assertEquals(2, numberOfSurvived);
        assertEquals(4, numberOfNoCoverage);
        assertEquals(0, numberOfOthers);
    }

    private IMarker[] getPitclipseMarker() throws CoreException {
        return ResourcesPlugin.getWorkspace().getRoot().findMarkers(
                PitclipseMutantMarkerFactory.PITCLIPSE_MUTANT_MARKER, true,
                IResource.DEPTH_INFINITE);
    }
}
