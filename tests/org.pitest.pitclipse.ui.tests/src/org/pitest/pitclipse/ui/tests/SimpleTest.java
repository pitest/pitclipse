package org.pitest.pitclipse.ui.tests;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lorenzo Bettini
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class SimpleTest extends AbstractPitclipseSWTBotTest {

    @Test
    public void aTest() {
        createJavaProject("aProject");
        verifyProjectExists("aProject");
    }

}
