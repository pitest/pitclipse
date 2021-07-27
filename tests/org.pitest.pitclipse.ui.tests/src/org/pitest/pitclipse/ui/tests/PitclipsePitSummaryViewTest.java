package org.pitest.pitclipse.ui.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;
import static org.pitest.pitclipse.ui.view.PitView.BLANK_PAGE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitSummaryView;
import org.pitest.pitclipse.ui.view.PitView;

@RunWith(SWTBotJunit4ClassRunner.class)
public class PitclipsePitSummaryViewTest extends AbstractPitclipseSWTBotTest {
    private static final String TEST_PROJECT = "org.pitest.pitclipse.testprojects.twoclasses";
    private static final String FOO_BAR_PACKAGE = "foo.bar";
    private static final String FOO_BAR_PACKAGE_RESULT = "foo.bar";
    private static final String FOO_CLASS_HEADER = "Foo.java";
    private static final String FOO_CLASS_RESULT_URL_END = FOO_CLASS_HEADER + ".html";
    private static final String INDEX_URL_END = "index.html";
    private static final String INDEX_HEADER = "Pit Test Coverage Report";

    private static PitSummaryView summaryView;

    @BeforeClass
    public static void setupJavaProject() throws CoreException {
        importTestProject(TEST_PROJECT);
    }

    @Before
    public void resetView() throws InterruptedException {
        // If this class is run alone, the view can't be closed and a RuntimeException
        // is thrown and expected
        closeViewById(PitView.VIEW_ID);
        openViewById(PitView.VIEW_ID);
        summaryView = PAGES.getPitSummaryView();
    }

    @Test
    public void navigateWithButtons() throws CoreException {
        final long timeout = SWTBotPreferences.TIMEOUT;
        try {
            // set timeout to small time, because offline page loads should be quick and
            // some pages are expected to not change and need timeout
            SWTBotPreferences.TIMEOUT = 50;
            // should not change page
            assertThat(summaryView.clickHome(BLANK_PAGE), equalTo(BLANK_PAGE));
            assertThat(summaryView.clickForward(BLANK_PAGE), equalTo(BLANK_PAGE));
            assertThat(summaryView.clickBack(BLANK_PAGE), equalTo(BLANK_PAGE));

            // coverageReportGenerated needs normal timeout
            SWTBotPreferences.TIMEOUT = timeout;
            runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
            coverageReportGenerated(2, 80, 0);

            assertThat(summaryView.getCurrentBrowserUrl(), endsWith(INDEX_URL_END));
            assertThat(summaryView.setUrl(summaryView.getCurrentBrowserUrl().replace("/index.html",
                    "/" + FOO_BAR_PACKAGE_RESULT + "/" + FOO_CLASS_RESULT_URL_END)),
                    endsWith(FOO_CLASS_RESULT_URL_END));
            assertThat(summaryView.clickBack(INDEX_HEADER), endsWith(INDEX_URL_END));
            // back again should not change url
            assertThat(summaryView.clickBack(INDEX_HEADER), endsWith(INDEX_URL_END));
            assertThat(summaryView.clickForward(FOO_CLASS_HEADER), endsWith(FOO_CLASS_RESULT_URL_END));
            // forward again should not change url
            assertThat(summaryView.clickForward(FOO_CLASS_HEADER), endsWith(FOO_CLASS_RESULT_URL_END));
            assertThat(summaryView.clickHome(INDEX_HEADER), endsWith(INDEX_URL_END));
        } finally {
            // reset SWTBotPreferences.TIMEOUT
            SWTBotPreferences.TIMEOUT = timeout;
        }
    }
}
