package org.pitest.pitclipse.ui.tests;

import static org.junit.Assert.fail;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.ui.behaviours.pageobjects.NoTestsFoundDialog;
import org.pitest.pitclipse.ui.behaviours.pageobjects.TestConfigurationSelectorDialog;

/**
 * @author Lorenzo Bettini
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class PitclipseUiRunnerTest extends AbstractPitclipseSWTBotTest {

    private static final String TEST_PROJECT = "org.pitest.pitclipse.testprojects.emptyclasses";
    private static final String NON_JAVA_PROJECT = "org.pitest.pitclipse.testprojects.nonjava";

    private static final String FOO_BAR_PACKAGE = "foo.bar";
    private static final String FOO_CLASS = "Foo";
    private static final String FOO_TEST_CLASS = "FooTest";
    private static final String FOO_TEST_CLASS_MULTIPLE_LAUNCHES = "FooTestWithSavedConfigurations";

    @BeforeClass
    public static void setupJavaProject() throws CoreException {
        // NON_JAVA_PROJECT contains the folder "binarytests"
        // used as an external library from FOO_PROJECT
        importTestProject(NON_JAVA_PROJECT);
        importTestProject(TEST_PROJECT);
        openEditor(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        openEditor(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
    }

    @Test
    public void emptyClassAndEmptyTest() throws CoreException {
        removeMethods(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        removeMethods(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        runTest(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        mutationsAre(Collections.emptyList());
        noCoverageReportGenerated();
    }

    @Test
    public void emptyClassAndEmptyTestMethod() throws CoreException {
        removeMethods(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        createMethod(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "@org.junit.Test\n"
              + "public void fooTest1() {\n"
              + "    Foo foo = new Foo();\n"
              + "}");
        runTest(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        mutationsAre(Collections.emptyList());
        noCoverageReportGenerated();
    }

    @Test
    public void classWithMethodAndNoCoverageTestMethod() throws CoreException {
        createMethod(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "public int doFoo(int i) {\n"
              + "    return i + 1;\n"
              + "}");
        createMethod(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "@org.junit.Test\n"
              + "public void fooTest1() {\n"
              + "    Foo foo = new Foo();\n"
              + "}");
        runTest(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        mutationsAre(
        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    6 | Replaced integer addition with subtraction       \n" +
        "NO_COVERAGE | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    6 | replaced int return with 0 for foo/bar/Foo::doFoo ");
        coverageReportGenerated(1, 50, 0, 2, 0);
    }

    @Test
    public void classWithMethodAndBadTestMethod() throws CoreException {
        createMethod(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "public int doFoo(int i) {\n"
              + "    return i + 1;\n"
              + "}");
        createMethod(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "@org.junit.Test\n"
              + "public void fooTest2() {\n"
              + "    new Foo().doFoo(1);\n"
              + "}");
        runTest(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        mutationsAre(
        "SURVIVED | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    6 | Replaced integer addition with subtraction       \n" +
        "SURVIVED | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    6 | replaced int return with 0 for foo/bar/Foo::doFoo ");
        coverageReportGenerated(1, 100, 0, 2, 0);
    }

    @Test
    public void classWithMethodAndBetterTestMethod() throws CoreException {
        createMethod(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "public int doFoo(int i) {\n"
              + "    return i + 1;\n"
              + "}");
        createMethod(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "@org.junit.Test\n"
              + "public void fooTest3() {\n"
              + "    org.junit.Assert.assertEquals(2,\n"
              + "            new Foo().doFoo(1));\n"
              + "}");
        runTest(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        mutationsAre(
        "KILLED | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    6 | Replaced integer addition with subtraction       \n" +
        "KILLED | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    6 | replaced int return with 0 for foo/bar/Foo::doFoo ");
        coverageReportGenerated(1, 100, 100, 2, 2);
    }

    @Test
    public void runPitAtPackageAndPackageRootAndProjectLevel() throws CoreException {
        createMethod(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "public int doFoo(int i) {\n"
              + "    return i + 1;\n"
              + "}");
        createMethod(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "@org.junit.Test\n"
              + "public void fooTest3() {\n"
              + "    org.junit.Assert.assertEquals(2,\n"
              + "            new Foo().doFoo(1));\n"
              + "}");
        runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
        coverageReportGenerated(1, 100, 100, 2, 2);
        runPackageRootTest("src", TEST_PROJECT);
        coverageReportGenerated(1, 100, 100, 2, 2);
        runProjectTest(TEST_PROJECT);
        coverageReportGenerated(1, 100, 100, 2, 2);
    }

    @Test
    public void runPitOnTwoSelectedElementsShowsDialogNoTestsFound() throws CoreException {
        PAGES.getPackageExplorer().selectFiles(TEST_PROJECT, FOO_BAR_PACKAGE,
                FOO_CLASS + ".java", FOO_TEST_CLASS + ".java");
        PAGES.getRunMenu().runPit();
        new NoTestsFoundDialog(bot).assertAppears();
    }

    @Test
    public void runPitOnNonJavaFileShowsDialogNoTestsFound() throws CoreException {
        PAGES.getPackageExplorer().selectProjectFile(TEST_PROJECT, "README");
        PAGES.getRunMenu().runPit();
        new NoTestsFoundDialog(bot).assertAppears();
    }

    /**
     * Results should be the same as
     * {@link #classWithMethodAndBetterTestMethod()}
     * 
     * @throws CoreException
     */
    @Test
    public void runSingleTestMethod() throws CoreException {
        createMethod(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "public int doFoo(int i) {\n"
              + "    return i + 1;\n"
              + "}");
        createMethod(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "@org.junit.Test\n"
              + "public void fooTest3() {\n"
              + "    org.junit.Assert.assertEquals(2,\n"
              + "            new Foo().doFoo(1));\n"
              + "}");
        runSingleMethodTest("fooTest3() : void", FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        mutationsAre(
        "KILLED | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    6 | Replaced integer addition with subtraction       \n" +
        "KILLED | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    6 | replaced int return with 0 for foo/bar/Foo::doFoo ");
        coverageReportGenerated(1, 100, 100, 2, 2);
    }

    @Test
    public void runSingleFieldShowsDialogNoTestsFound() throws CoreException {
        createMethod(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "int aField;\n");
        PAGES.getPackageExplorer().selectClassMember("aField", FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        PAGES.getRunMenu().runPit();
        new NoTestsFoundDialog(bot).assertAppears();
    }

    @Test
    public void runBinaryTest() throws CoreException {
        removeMethods(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        removeMethods(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        runFreeStyleTest(() ->
            PAGES.getPackageExplorer().selectProjectElement(TEST_PROJECT,
                "Referenced Libraries",
                "binarytests - org.pitest.pitclipse.testprojects.nonjava",
                "(default package)",
                "EmptyBinaryTest.class")
        );
        mutationsAre(Collections.emptyList());
        noCoverageReportGenerated();
    }

    @Test
    public void multipleLaunchConfigurations() throws CoreException {
        createMethod(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "public int doFoo(int i) {\n"
              + "    return i + 1;\n"
              + "}");
        runTest(FOO_TEST_CLASS_MULTIPLE_LAUNCHES, FOO_BAR_PACKAGE, TEST_PROJECT,
            () -> 
            new TestConfigurationSelectorDialog(bot)
                .choose(FOO_TEST_CLASS_MULTIPLE_LAUNCHES));
        coverageReportGenerated(1, 0, 0, 2, 0);
        runTest(FOO_TEST_CLASS_MULTIPLE_LAUNCHES, FOO_BAR_PACKAGE, TEST_PROJECT,
            () -> 
            new TestConfigurationSelectorDialog(bot)
                .choose(FOO_TEST_CLASS_MULTIPLE_LAUNCHES + " (All Mutators)"));
        // not just 2 mutants, but much more since in this launch
        // we enabled All Mutators
        coverageReportGenerated(1, 0, 0, 20, 0);

        @SuppressWarnings("serial")
        class MyException extends RuntimeException {
            
        }

        // cancel the launch
        // we also have to interrupt with an exception, otherwise it waits
        // for PIT to terminate
        try {
            runTest(FOO_TEST_CLASS_MULTIPLE_LAUNCHES, FOO_BAR_PACKAGE, TEST_PROJECT,
                () -> 
                {
                    new TestConfigurationSelectorDialog(bot)
                        .cancel();
                    throw new MyException();
                });
            fail("should not get here: missed MyException");
        } catch (MyException e) {
            // OK
        }
    }

    @Test
    public void runPitFromEditor() throws CoreException {
        createMethod(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "public int doFoo(int i) {\n"
              + "    return i + 1;\n"
              + "}");
        createMethod(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT,
                "@org.junit.Test\n"
              + "public void fooTest3() {\n"
              + "    org.junit.Assert.assertEquals(2,\n"
              + "            new Foo().doFoo(1));\n"
              + "}");
        runFromEditorTest(FOO_TEST_CLASS + ".java");
        mutationsAre(
        "KILLED | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    6 | Replaced integer addition with subtraction       \n" +
        "KILLED | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    6 | replaced int return with 0 for foo/bar/Foo::doFoo ");
        coverageReportGenerated(1, 100, 100, 2, 2);
    }

    @Test
    public void runPitFromEditorOnNonJavaFile() throws CoreException {
        PAGES.getPackageExplorer()
            .selectProjectFile(TEST_PROJECT, "README")
            .doubleClick();
        PAGES.getRunMenu().runPit();
        new NoTestsFoundDialog(bot).assertAppears();
    }

    @Test
    public void runWithRunToolbarButton() throws CoreException {
        removeMethods(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        removeMethods(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        PAGES.getPackageExplorer().selectClass(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        runAndWaitForPitTest(() ->
            bot.toolbarDropDownButtonWithTooltip("Run").click()
        );
        mutationsAre(Collections.emptyList());
        noCoverageReportGenerated();
    }

    @Test
    public void runWithRunToolbarButtonWithTwoSelectionsShowsDialogNoTestsFound() throws CoreException {
        removeMethods(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        removeMethods(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        PAGES.getPackageExplorer().selectFiles(TEST_PROJECT, FOO_BAR_PACKAGE,
                FOO_CLASS + ".java", FOO_TEST_CLASS + ".java");
        bot.toolbarDropDownButtonWithTooltip("Run").click();
        new NoTestsFoundDialog(bot).assertAppears();
    }
}
