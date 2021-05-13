package org.pitest.pitclipse.ui.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.pitest.pitclipse.core.PitMutators.STRONGER;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_AVOID_CALLS_TO_LIST;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_MUTATORS;
import static org.pitest.pitclipse.runner.config.PitExecutionMode.PROJECT_ISOLATION;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import java.math.BigDecimal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.core.PitMutators;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitPreferenceSelector;

/**
 * @author Lorenzo Bettini
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class PitclipseOptionsTest extends AbstractPitclipseSWTBotTest {

    private static final String TEST_PROJECT = "project1";
    private static final String FOO_BAR_PACKAGE = "foo.bar";
    private static final String FOO_CLASS = "Foo";
    private static final String FOO_TEST_CLASS = "FooTest";
    private static final String BAR_CLASS = "Bar";
    private static final String BAR_TEST_CLASS = "BarTest";

    @BeforeClass
    public static void setupJavaProject() {
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
    }

    @Test
    public void defaultOptions() {
        PitPreferenceSelector selector = PAGES.getWindowsMenu().openPreferences().andThen();
        assertEquals(PROJECT_ISOLATION, selector.getPitExecutionMode());
        assertTrue(selector.isPitRunInParallel());
        assertFalse(selector.isIncrementalAnalysisEnabled());
        assertEquals("The 'Excluded Classes' preference has not the expected value", 
                "*Test", selector.getExcludedClasses());
        assertTrue(selector.getExcludedMethods().isEmpty());
        assertThat(selector.getAvoidCallsTo(), equalTo(DEFAULT_AVOID_CALLS_TO_LIST));
        assertThat(selector.getTimeout(), equalTo(3000));
        assertEquals(selector.getPitTimeoutFactor(), new BigDecimal(1.25));
        assertThat(selector.getMutators().toString(), equalTo(DEFAULT_MUTATORS));
        selector.close();
    }

    @Test
    public void useDefaultMutators() throws CoreException {
        runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
        coverageReportGenerated(2, 80, 0);
        mutationsAre(
        "SURVIVED    | project1 | foo.bar | foo.bar.Bar |    7 | negated conditional\n" +
        "SURVIVED    | project1 | foo.bar | foo.bar.Foo |    7 | negated conditional\n" +
        "NO_COVERAGE | project1 | foo.bar | foo.bar.Bar |    8 | Replaced integer addition with subtraction\n" +
        "NO_COVERAGE | project1 | foo.bar | foo.bar.Bar |    8 | replaced int return with 0 for foo/bar/Bar::f\n" +
        "NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |    8 | Replaced integer addition with subtraction\n" +
        "NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |    8 | replaced int return with 0 for foo/bar/Foo::f");
    }

    @Test
    public void useStrongerMutators() throws CoreException {
        // now set STRONGER mutators
        PAGES.getWindowsMenu().setMutators(STRONGER);
        try {
            runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
            coverageReportGenerated(2, 80, 0);
            mutationsAre(
            "SURVIVED    | project1 | foo.bar | foo.bar.Bar |    7 | negated conditional\n" +
            "SURVIVED    | project1 | foo.bar | foo.bar.Bar |    7 | removed conditional - replaced equality check with false\n" +
            "SURVIVED    | project1 | foo.bar | foo.bar.Foo |    7 | negated conditional\n" +
            "SURVIVED    | project1 | foo.bar | foo.bar.Foo |    7 | removed conditional - replaced equality check with false\n" +
            "NO_COVERAGE | project1 | foo.bar | foo.bar.Bar |    8 | Replaced integer addition with subtraction\n" +
            "NO_COVERAGE | project1 | foo.bar | foo.bar.Bar |    8 | replaced int return with 0 for foo/bar/Bar::f\n" +
            "NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |    8 | Replaced integer addition with subtraction\n" +
            "NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |    8 | replaced int return with 0 for foo/bar/Foo::f");
        } finally {
            // it's crucial to reset it to the default or we break other tests
            PAGES.getWindowsMenu().setMutators(PitMutators.DEFAULTS);
        }
    }
}
