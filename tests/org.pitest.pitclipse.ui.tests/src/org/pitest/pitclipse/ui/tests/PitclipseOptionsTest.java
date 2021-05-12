package org.pitest.pitclipse.ui.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_AVOID_CALLS_TO_LIST;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_MUTATORS;
import static org.pitest.pitclipse.runner.config.PitExecutionMode.PROJECT_ISOLATION;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import java.math.BigDecimal;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitPreferenceSelector;

/**
 * @author Lorenzo Bettini
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class PitclipseOptionsTest extends AbstractPitclipseSWTBotTest {

    @Test
    public void defaultOptions() {
        PitPreferenceSelector selector = PAGES.getWindowsMenu().openPreferences().andThen();
        assertEquals(PROJECT_ISOLATION, selector.getPitExecutionMode());
        assertTrue(selector.isPitRunInParallel());
        assertFalse(selector.isIncrementalAnalysisEnabled());
        assertEquals("The 'Excluded Classes' preference has not the expected value", 
                "*Test", selector.getExcludedClasses());
        assertTrue(selector.getExcludedMethods().isEmpty());
        assertThat(selector.getAvoidCallsTo(), equalTo(DEFAULT_AVOID_CALLS_TO_LIST));
        assertThat(selector.getTimeout(), equalTo(3000));
        assertEquals(selector.getPitTimeoutFactor(), new BigDecimal(1.25));
        assertThat(selector.getMutators().toString(), equalTo(DEFAULT_MUTATORS));
        selector.close();
    }

}
