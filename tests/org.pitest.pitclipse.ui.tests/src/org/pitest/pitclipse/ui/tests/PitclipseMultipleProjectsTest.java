package org.pitest.pitclipse.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.pitest.pitclipse.runner.config.PitExecutionMode.PROJECT_ISOLATION;
import static org.pitest.pitclipse.runner.config.PitExecutionMode.WORKSPACE;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
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
    private static final String UNRELATED_PROJECT = "org.pitest.pitclipse.testprojects.emptyclasses";
    private static final String CLOSED_PROJECT = "org.pitest.pitclipse.testprojects.twoclasses";
    private static final String NON_JAVA_PROJECT = "org.pitest.pitclipse.testprojects.nonjava";

    @BeforeClass
    public static void setupJavaProject() throws CoreException, IOException {
        // must create a directory "externalclassfolder" in the workspace location
        // since it's referred by FOO_PROJECT as an external class folder
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        File workspacePath = root.getLocation().toFile();
        new File(workspacePath, "externalclassfolder").mkdir();
        // NON_JAVA_PROJECT contains "folder" linked as "externalsource"
        // from FOO_PROJECT
        importTestProject(NON_JAVA_PROJECT);
        importTestProject(FOO_PROJECT);
        importTestProject(BAR_PROJECT);
        importTestProject(FOO_BAR_PROJECT);
        importTestProject(UNRELATED_PROJECT);
        final IProject projectToClose = importTestProject(CLOSED_PROJECT);
        PAGES.getBuildProgress().listenForBuild();
        projectToClose.close(new NullProgressMonitor());
        PAGES.getBuildProgress().waitForBuild();
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
            // only the classes in the project is mutated
            coverageReportGenerated(2, 100, 100, 3, 3);
            selector = PAGES.getWindowsMenu().openPreferences().andThen();
            selector.setPitExecutionMode(WORKSPACE);
            selector.close();
            runProjectTest(FOO_BAR_PROJECT);
            // also the classes of the used projects are mutated
            // including the external linked source directory in FOO_PROJECT
            coverageReportGenerated(5, 100, 100, 8, 8);
        } finally {
            // reset default values
            IPreferenceStore preferenceStore = PitCoreActivator.getDefault().getPreferenceStore();
            preferenceStore.setValue(PitPreferences.EXECUTION_MODE, PROJECT_ISOLATION.toString());
            PitPreferenceSelector selector = PAGES.getWindowsMenu().openPreferences().andThen();
            assertEquals(PROJECT_ISOLATION, selector.getPitExecutionMode());
            selector.close();
        }
    }

}
