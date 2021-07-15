package org.pitest.pitclipse.ui.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitSummaryView;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;


/**
 * @author Jonas Kutscha
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class PitclipsePitSummaryViewTest extends AbstractPitclipseSWTBotTest {
    private static final String TEST_PROJECT = "org.pitest.pitclipse.testprojects.twoclasses";
    private static final String FOO_BAR_PACKAGE = "foo.bar";
    private static final String FOO_CLASS = "Foo";
    private static final String TEST_PROJECT_WITH_DIFFERENT_SRC = "org.pitest.pitclipse.testprojects.differentSrc";
    private static final String FOO_PACKAGE = "foo";
    private static PitSummaryView summaryView;

    @BeforeClass
    public static void setupJavaProject() throws CoreException {
        summaryView = PAGES.getPitSummaryView();
        importTestProject(TEST_PROJECT);
        importTestProject(TEST_PROJECT_WITH_DIFFERENT_SRC);
    }
    
    @After
    public void resetView() {
        summaryView.resetView();
        bot.closeAllEditors();
    }

    @Test
    public void openPitSummaryViewAtCorrectUrl() throws CoreException {
        runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
        coverageReportGenerated(2, 80, 0);
        assertThat(summaryView.getCurrentBrowserUrl(), endsWith("index.html"));
        assertThat(summaryView.getCurrentBrowserUrl(), not(endsWith("/foo/index.html")));
        assertThat(summaryView.getCurrentBrowserUrl(), not(endsWith("/foo/bar/index.html")));

        openEditor(FOO_CLASS, FOO_PACKAGE, TEST_PROJECT_WITH_DIFFERENT_SRC);
        runPackageTest(FOO_PACKAGE, TEST_PROJECT_WITH_DIFFERENT_SRC);
        coverageReportGenerated(1, 80, 0);
        assertThat(summaryView.getCurrentBrowserUrl(), endsWith("foo/Foo.java.html"));
    }

    @Test
    public void navigateWithButtons() throws CoreException {
        final long timeout = SWTBotPreferences.TIMEOUT;
        openEditor(FOO_CLASS, FOO_PACKAGE, TEST_PROJECT_WITH_DIFFERENT_SRC);
        runPackageTest(FOO_PACKAGE, TEST_PROJECT_WITH_DIFFERENT_SRC);
        coverageReportGenerated(1, 80, 0);
        // set timeout to 5 ms, because the browser should not load pages and run into
        // timeout
        SWTBotPreferences.TIMEOUT = 5;
        // should not change url, because no other page was opened
        assertThat(summaryView.clickBack(), endsWith("foo/Foo.java.html"));
        assertThat(summaryView.clickForward(), endsWith("foo/Foo.java.html"));

        assertThat(summaryView.clickHome(), endsWith("/index.html"));
        assertThat(summaryView.clickHome(), not(endsWith("/foo/index.html")));

        assertThat(summaryView.clickBack(), endsWith("/foo/Foo.java.html"));
        assertThat(summaryView.clickForward(), endsWith("/index.html"));
        // set timeout back to previous value
        SWTBotPreferences.TIMEOUT = timeout;
    }
}
