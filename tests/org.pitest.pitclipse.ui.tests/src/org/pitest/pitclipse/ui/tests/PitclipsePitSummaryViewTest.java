package org.pitest.pitclipse.ui.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitSummaryView;
import org.pitest.pitclipse.ui.view.PitView;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

public class PitclipsePitSummaryViewTest extends AbstractPitclipseSWTBotTest {
    private static final String TEST_PROJECT = "org.pitest.pitclipse.testprojects.twoclasses";
    private static final String FOO_BAR_PACKAGE = "foo.bar";
    private static final String EXAMPLE_URL = "https://www.example.com/";
    private static final String BLANK_URL = "about:blank";

    private static PitSummaryView summaryView;

    @BeforeClass
    public static void setupJavaProject() throws CoreException {
        summaryView = PAGES.getPitSummaryView();
        importTestProject(TEST_PROJECT);
    }

    @Before
    public void openView() throws InterruptedException {
        openViewById(PitView.VIEW_ID);
    }

    @After
    public void resetView() {
        summaryView.closeView();
        bot.closeAllEditors();
    }

    @Test
    public void navigateWithButtons() throws CoreException {
        final long timeout = SWTBotPreferences.TIMEOUT;
        try {
            // set timeout to small time, because offline page loads should be quick and
            // some pages are expected to not change and need timeout
            SWTBotPreferences.TIMEOUT = 2;
            // should not change page, because no page was opened before
            assertThat(summaryView.clickHome(), equalTo(BLANK_URL));
            assertThat(summaryView.clickBack(), equalTo(BLANK_URL));
            assertThat(summaryView.clickForward(), equalTo(BLANK_URL));

            // coverageReportGenerated needs normal timeout
            runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
            SWTBotPreferences.TIMEOUT = timeout;
            coverageReportGenerated(2, 80, 0);

            SWTBotPreferences.TIMEOUT = 2;
            assertThat(summaryView.setLink(EXAMPLE_URL), equalTo(EXAMPLE_URL));
            assertThat(summaryView.clickForward(), equalTo(EXAMPLE_URL));
            assertThat(summaryView.clickBack(), endsWith("index.html"));
            assertThat(summaryView.clickForward(), equalTo(EXAMPLE_URL));
            assertThat(summaryView.clickHome(), endsWith("index.html"));
        } finally {
            // reset SWTBotPreferences.TIMEOUT
            SWTBotPreferences.TIMEOUT = timeout;
        }
    }
}
