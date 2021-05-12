package org.pitest.pitclipse.ui.tests;

import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lorenzo Bettini
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class PitclipseOptionsTest extends AbstractPitclipseSWTBotTest {

    @Test
    public void defaultOptions() {
        openPreferences();
    }

}
