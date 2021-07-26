package org.pitest.pitclipse.ui.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

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
    private String FOO_BAR_PACKAGE_RESULT = "foo.bar";
    private String FOO_CLASS_RESULT = "Foo.java.html";
    private String INDEX = "index.html";

    private static PitSummaryView summaryView;

    @BeforeClass
    public static void setupJavaProject() throws CoreException {
        summaryView = PAGES.getPitSummaryView();
        importTestProject(TEST_PROJECT);
    }

    @Before
    public void resetView() throws InterruptedException {
        summaryView.closeView();
        openViewById(PitView.VIEW_ID);
    }

    @Test
    public void navigateWithButtons() throws CoreException {
        final long timeout = SWTBotPreferences.TIMEOUT;
        try {
            // set timeout to small time, because offline page loads should be quick and
            // some pages are expected to not change and need timeout
            SWTBotPreferences.TIMEOUT = 50;
            // should not change page
            String lastUrl = summaryView.getCurrentBrowserUrl();
            assertThat(summaryView.clickHome(), equalTo(lastUrl));
            assertThat(summaryView.clickForward(), equalTo(lastUrl));
            assertThat(summaryView.clickBack(), equalTo(lastUrl));

            // coverageReportGenerated needs normal timeout
            SWTBotPreferences.TIMEOUT = timeout;
            runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
            coverageReportGenerated(2, 80, 0);

            assertThat(summaryView.getCurrentBrowserUrl(), endsWith(INDEX));
            assertThat(summaryView.setUrl(summaryView.getCurrentBrowserUrl().replace("/index.html",
                    "/" + FOO_BAR_PACKAGE_RESULT + "/" + FOO_CLASS_RESULT)), endsWith(FOO_CLASS_RESULT));
            assertThat(summaryView.clickBack(), endsWith(INDEX));
            // back again should not change url
            assertThat(summaryView.clickBack(), endsWith(INDEX));
            assertThat(summaryView.clickForward(), endsWith(FOO_CLASS_RESULT));
            // forward again should not change url
            assertThat(summaryView.clickForward(), endsWith(FOO_CLASS_RESULT));
            assertThat(summaryView.clickHome(), endsWith(INDEX));
        } finally {
            // reset SWTBotPreferences.TIMEOUT
            SWTBotPreferences.TIMEOUT = timeout;
        }
    }
}