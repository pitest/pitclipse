/*******************************************************************************
 * Copyright 2021 Lorenzo Bettini and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.pitest.pitclipse.ui.tests;

import static java.lang.Integer.parseInt;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.intro.IIntroManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.runner.results.DetectionStatus;
import org.pitest.pitclipse.ui.behaviours.steps.ClassSteps;
import org.pitest.pitclipse.ui.behaviours.steps.PitMutation;
import org.pitest.pitclipse.ui.behaviours.steps.PitclipseSteps;

/**
 * @author Lorenzo Bettini
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public abstract class AbstractPitclipseSWTBotTest {
    protected static SWTWorkbenchBot bot;

    @BeforeClass
    public static void beforeClass() throws Exception {
        bot = new SWTWorkbenchBot();

        closeWelcomePage();
        openJavaPerspective();
    }

    @AfterClass
    public static void afterClass() {
        deleteAllProjects();
        bot.resetWorkbench();
    }

    protected static void closeWelcomePage() throws InterruptedException {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
                if (introManager.getIntro() != null) {
                    introManager
                            .closeIntro(introManager.getIntro());
                }
            }
        });
    }

    protected static void openJavaPerspective() throws InterruptedException {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                IWorkbench workbench = PlatformUI.getWorkbench();
                try {
                    workbench.showPerspective("org.eclipse.jdt.ui.JavaPerspective",
                            workbench.getActiveWorkbenchWindow());
                } catch (WorkbenchException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected static void createJavaProject(String projectName) {
        PAGES.getBuildProgress().listenForBuild();
        PAGES.getFileMenu().newJavaProject(projectName);
        PAGES.getAbstractSyntaxTree().addJUnitToClassPath(projectName);
        PAGES.getBuildProgress().waitForBuild();
    }

    protected static void verifyProjectExists(String projectName) {
        for (String project : PAGES.getPackageExplorer().getProjectsInWorkspace()) {
            if (projectName.equals(project)) {
                // Project does indeed exist
                return;
            }
        }
        fail("Project: " + projectName + " not found.");
    }

    protected static void deleteAllProjects() {
        for (String project : PAGES.getPackageExplorer().getProjectsInWorkspace()) {
            PAGES.getAbstractSyntaxTree().deleteProject(project);
        }
        File historyFile = PitCoreActivator.getDefault().getHistoryFile();
        if (historyFile.exists()) {
            assertTrue(historyFile.delete());
        }
    }

    protected static void deleteSrcContents(String projectName) throws CoreException {
        PAGES.getBuildProgress().listenForBuild();
        IJavaProject javaProject = PAGES.getAbstractSyntaxTree().getJavaProject(projectName);
        javaProject.getProject().getFolder("src").delete(true, null);
        javaProject.getProject().getFolder("src").create(true, true, null);
        PAGES.getBuildProgress().waitForBuild();
    }

    protected static void addToBuildPath(String dependentProject, String projectName) {
        PAGES.getPackageExplorer().selectProject(projectName);
        PAGES.getAbstractSyntaxTree().addProjectToClassPathOfProject(projectName, dependentProject);
    }

    protected static void createClassWithMethod(String className, String packageName, String projectName,
            String method) {
        createClass(className, packageName, projectName);
        createMethod(className, packageName, projectName, method);
    }

    protected static void createClass(String className, String packageName, String projectName) {
        PAGES.getBuildProgress().listenForBuild();
        PAGES.getPackageExplorer().selectPackageRoot(projectName, "src");
        // Cannot use the Package explorer right click context menu
        // to create a class due to SWTBot bug 261360
        PAGES.getFileMenu().createClass(packageName, className);
        PAGES.getBuildProgress().waitForBuild();
    }

    protected static void createMethod(String className, String packageName, String projectName,
            String method) {
        ClassSteps classSteps = new ClassSteps();
        classSteps.selectClass(className, packageName, projectName);
        classSteps.createMethod(method);
    }

    protected static void runTest(final String testClassName, final String packageName, final String projectName) throws CoreException {
        new PitclipseSteps().runTest(testClassName, packageName, projectName);
    }

    protected static void runPackageTest(final String packageName, final String projectName) throws CoreException {
        new PitclipseSteps().runPackageTest(packageName, projectName);
    }

    protected static void runPackageRootTest(final String packageRoot, final String projectName) throws CoreException {
        new PitclipseSteps().runPackageRootTest(packageRoot, projectName);
    }

    protected static void runProjectTest(final String projectName) throws CoreException {
        new PitclipseSteps().runProjectTest(projectName);
    }

    /**
     * If the Console view can be found then clear it, to make sure we don't get
     * output from previous test runs
     */
    protected static void clearConsole() {
        SWTBotView consoleView;
        try {
            consoleView = bot.viewByPartName("Console");
            consoleView.show();
        } catch (WidgetNotFoundException e) {
            e.printStackTrace();
            return;
        }
        if (consoleView.bot().styledText().getText().isEmpty())
            return;
        // use the toolbar button instead of .bot().styledText().setText("")
        // which does not seem to work synchronously
        consoleView.toolbarButton("Clear Console").click();
    }

    protected static void consoleContains(int generatedMutants, int killedMutants,
            int killedPercentage,
            int testsRun,
            int testsPerMutations) {
        PAGES.views().waitForTestsAreRunOnConsole();
        SWTBotView consoleView = bot.viewByPartName("Console");
        consoleView.show();
        String consoleText = consoleView.bot()
                .styledText().getText()
                .replace("\r", "");
        System.out.println(consoleText);
        assertThat(consoleText,
            containsString(
                String.format(
                    ">> Generated %d mutations Killed %d (%d%%)\n"
                  + ">> Ran %d tests (%d tests per mutation)",
                  generatedMutants, killedMutants,
                  killedPercentage, testsRun, testsPerMutations)
            )
        );
    }

    /**
     * The expectedMutationsTable String argument represents the expected
     * mutations table, this is an example of String:
     * (the headers of the table are the following ones:)
     * <pre>
     *  status      | project  | package | class       | line | mutation
     * </pre>
     * 
     * <pre>
     * "NO_COVERAGE | project1 | foo.bar | foo.bar.Foo | 6 | Replaced integer addition with subtraction       \n" +
     * "NO_COVERAGE | project1 | foo.bar | foo.bar.Foo | 6 | replaced int return with 0 for foo/bar/Foo::doFoo "
     * </pre>
     * 
     * @param expectedMutationsTable
     */
    protected static void mutationsAre(String expectedMutationsTable) {
        List<PitMutation> expectedMutations = new ArrayList<>();
        String[] lines = expectedMutationsTable.split("\n");
        final int statusIndex = 0;
        final int projectIndex = 1;
        final int packageIndex = 2;
        final int classIndex = 3;
        final int lineIndex = 4;
        final int mutationIndex = 5;
        for (String string : lines) {
            String[] mutationRow = string.split("\\|");
            DetectionStatus status = DetectionStatus.valueOf(mutationRow[statusIndex].trim());
            String project = mutationRow[projectIndex].trim();
            String pkg = mutationRow[packageIndex].trim();
            String className = mutationRow[classIndex].trim();
            int line = parseInt(mutationRow[lineIndex].trim());
            String mutation = mutationRow[mutationIndex].trim();
            PitMutation pitMutation = PitMutation.builder().withStatus(status).withProject(project).withPackage(pkg)
                    .withClassName(className).withLineNumber(line).withMutation(mutation).build();
            expectedMutations.add(pitMutation);
        }
        mutationsAre(expectedMutations);
    }

    protected static void mutationsAre(List<PitMutation> expectedMutations) {
        List<PitMutation> actualMutations = PAGES.getPitMutationsView().getMutations();
        assertThat(actualMutations, equalTo(expectedMutations));
    }

}
