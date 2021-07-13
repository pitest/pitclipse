package org.pitest.pitclipse.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.pitest.pitclipse.runner.config.PitExecutionMode.PROJECT_ISOLATION;
import static org.pitest.pitclipse.runner.config.PitExecutionMode.WORKSPACE;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.core.preferences.PitPreferences;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitPreferenceSelector;

/**
 * @author Lorenzo Bettini
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class PitclipseMultipleProjectsTest extends AbstractPitclipseSWTBotTest {

    private static final String FOO_PROJECT = "org.pitest.pitclipse.testprojects.foo";
    private static final String BAR_PROJECT = "org.pitest.pitclipse.testprojects.bar";
    private static final String FOO_BAR_PROJECT = "org.pitest.pitclipse.testprojects.foobar";

    @BeforeClass
    public static void setupJavaProject() throws CoreException {
        importTestProject(FOO_PROJECT);
        importTestProject(BAR_PROJECT);
        importTestProject(FOO_BAR_PROJECT);
    }

    @Before
    public void removeLaunchConfigurations() throws CoreException {
        removePitLaunchConfigurations();
    }

    @Test
    public void testExecutionMode() throws CoreException {
        try {
            PitPreferenceSelector selector = PAGES.getWindowsMenu().openPreferences().andThen();
            assertEquals(PROJECT_ISOLATION, selector.getPitExecutionMode());
            selector.close();
            runProjectTest(FOO_BAR_PROJECT);
            // only the class in the project is mutated
            coverageReportGenerated(1, 100, 100);
            selector = PAGES.getWindowsMenu().openPreferences().andThen();
            selector.setPitExecutionMode(WORKSPACE);
            selector.close();
            runProjectTest(FOO_BAR_PROJECT);
            // also the classes of the used projects are mutated
            coverageReportGenerated(3, 100, 100);
        } finally {
            // reset default values
            IPreferenceStore preferenceStore = PitCoreActivator.getDefault().getPreferenceStore();
            preferenceStore.setValue(PitPreferences.PIT_EXECUTION_MODE, PROJECT_ISOLATION.toString());
        }
    }

}
