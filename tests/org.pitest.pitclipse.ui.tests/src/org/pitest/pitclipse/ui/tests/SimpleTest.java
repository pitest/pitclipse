package org.pitest.pitclipse.ui.tests;

import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lorenzo Bettini
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class SimpleTest extends AbstractPitclipseSWTBotTest {

    private static final String TEST_PROJECT = "project1";

    @BeforeClass
    public static void setupJavaProject() {
        createJavaProject(TEST_PROJECT);
        verifyProjectExists(TEST_PROJECT);
    }

    @Test
    public void aTest() throws CoreException {
        createClass("Foo", "foo.bar", TEST_PROJECT);
        createClass("FooTest", "foo.bar", TEST_PROJECT);
        runTest("FooTest", "foo.bar", TEST_PROJECT);
        consoleContains(0, 0);
        mutationsAre(Collections.emptyList());
    }

}
