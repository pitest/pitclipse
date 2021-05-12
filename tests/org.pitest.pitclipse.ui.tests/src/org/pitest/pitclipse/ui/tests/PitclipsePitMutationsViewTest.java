package org.pitest.pitclipse.ui.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
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

    private static final String TEST_PROJECT = "project1";
    private static final String FOO_BAR_PACKAGE = "foo.bar";
    private static final String FOO_CLASS = "Foo";
    private static final String FOO_TEST_CLASS = "FooTest";
    private static final String BAR_CLASS = "Bar";
    private static final String BAR_TEST_CLASS = "BarTest";

    @Test
    public void selectMutationOpensTheClassAtTheRightLineNumber() throws CoreException {
        createJavaProjectWithJUnit4(TEST_PROJECT);
        verifyProjectExists(TEST_PROJECT);
        createClassWithMethod(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
             "public int f(int i) {\n"
           + "    java.util.ArrayList<Object> pointless = new java.util.ArrayList<>();\n"
           + "    if (pointless.size() == 1)\n"
           + "        return i + 1;\n"
           + "    else\n"
           + "        return 0;\n"
           + "}");
        createClassWithMethod(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
            "@org.junit.Test public void badTest() {\n"
          + "    " + FOO_CLASS + " x = new " + FOO_CLASS + "();\n"
          + "    x.f(1);\n"
          + "}");
        createClassWithMethod(BAR_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
            "public int f(int i) {\n"
          + "    java.util.ArrayList<Object> pointless = new java.util.ArrayList<>();\n"
          + "    if (pointless.size() == 1)\n"
          + "        return i + 1;\n"
          + "    else\n"
          + "        return 0;\n"
          + "}");
        createClassWithMethod(BAR_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
           "@org.junit.Test public void badTest() {\n"
          + "    " + BAR_CLASS + " x = new " + BAR_CLASS + "();\n"
          + "    x.f(1);\n"
          + "}");
        runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
        coverageReportGenerated(2, 80, 0);
        PitclipseSteps pitclipseSteps = new PitclipseSteps();
        PitMutation mutation = fromMutationLine(
        "SURVIVED    | project1 | foo.bar | foo.bar.Foo |    7 | negated conditional");
        pitclipseSteps.mutationIsSelected(mutation);
        pitclipseSteps.mutationIsOpened(FOO_CLASS + ".java", 7);
        mutation = fromMutationLine(
        "SURVIVED    | project1 | foo.bar | foo.bar.Bar |    7 | negated conditional");
        pitclipseSteps.mutationIsSelected(mutation);
        pitclipseSteps.mutationIsOpened(BAR_CLASS + ".java", 7);
    }
}
