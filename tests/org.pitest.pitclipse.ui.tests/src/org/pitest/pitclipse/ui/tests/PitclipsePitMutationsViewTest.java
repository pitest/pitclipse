package org.pitest.pitclipse.ui.tests;

import static org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory.withTitle;
import static org.eclipse.swtbot.eclipse.finder.waits.Conditions.waitForEditor;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitMutationsViewPageObject;
import org.pitest.pitclipse.ui.behaviours.steps.PitMutation;
import org.pitest.pitclipse.ui.behaviours.steps.PitclipseSteps;
import org.pitest.pitclipse.ui.view.mutations.PitMutationsView;

/**
 * @author Lorenzo Bettini
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class PitclipsePitMutationsViewTest extends AbstractPitclipseSWTBotTest {

    private static final String TEST_PROJECT = "org.pitest.pitclipse.testprojects.twoclasses";;
    private static final String TEST_PROJECT_WITH_DEFAULT_PACKAGE = "org.pitest.pitclipse.testprojects.threeclasses";;
    private static final String FOO_BAR_PACKAGE = "foo.bar";
    private static final String FOOBAR_PACKAGE = "foobar";
    private static final String FOO_CLASS = "Foo";
    private static final String FOO_TEST_CLASS = "FooTest";
    private static final String BAR_CLASS = "Bar";
    private static final String BAR_TEST_CLASS = "BarTest";

    @BeforeClass
    public static void setupJavaProject() throws CoreException {
        importTestProject(TEST_PROJECT);
        importTestProject(TEST_PROJECT_WITH_DEFAULT_PACKAGE);
    }

    @Test
    public void selectMutationOpensTheClassAtTheRightLineNumber() throws CoreException {
        openEditor(FOO_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        openEditor(FOO_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        openEditor(BAR_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        openEditor(BAR_TEST_CLASS, FOO_BAR_PACKAGE, TEST_PROJECT);
        runPackageTest(FOO_BAR_PACKAGE, TEST_PROJECT);
        coverageReportGenerated(2, 80, 0, 6, 0);
        PitclipseSteps pitclipseSteps = new PitclipseSteps();
        PitMutation mutation = fromMutationLine(
        "SURVIVED    | " + TEST_PROJECT + " | foo.bar | foo.bar.Foo |    7 | removed conditional - replaced equality check with false");
        pitclipseSteps.doubleClickMutationInMutationsView(mutation);

        bot.waitUntil(waitForEditor(withTitle(FOO_CLASS + ".java")));
        pitclipseSteps.mutationIsOpened(FOO_CLASS + ".java", 7);
        mutation = fromMutationLine(
        "SURVIVED    | " + TEST_PROJECT + " | foo.bar | foo.bar.Bar |    7 | removed conditional - replaced equality check with false");
        pitclipseSteps.doubleClickMutationInMutationsView(mutation);
        
        bot.waitUntil(waitForEditor(withTitle(BAR_CLASS + ".java")));
        pitclipseSteps.mutationIsOpened(BAR_CLASS + ".java", 7);
    }

    @Test
    public void expandAndCollapse() throws CoreException {
        runPackageTest(FOOBAR_PACKAGE, TEST_PROJECT_WITH_DEFAULT_PACKAGE);
        final PitMutationsViewPageObject pitMutationsView = new PitMutationsViewPageObject(bot);
        SWTBotTree mutationTreeRoot = pitMutationsView.mutationTreeRoot();
        final SWTBotTreeItem firstItem = mutationTreeRoot.getAllItems()[0];
        assertFalse("should be collapsed", firstItem.isExpanded());
        // with toolbar buttons
        bot.toolbarButtonWithTooltip
            (PitMutationsView.EXPAND_ALL_BUTTON_TEXT).click();
        assertTrue("should be expanded", firstItem.isExpanded());
        bot.toolbarButtonWithTooltip
            (PitMutationsView.COLLAPSE_ALL_BUTTON_TEXT).click();
        assertFalse("should be collapsed", firstItem.isExpanded());
        // with double-click
        firstItem.doubleClick();
        assertTrue("should be expanded", firstItem.isExpanded());
        firstItem.doubleClick();
        assertFalse("should be collapsed", firstItem.isExpanded());
    }
}
