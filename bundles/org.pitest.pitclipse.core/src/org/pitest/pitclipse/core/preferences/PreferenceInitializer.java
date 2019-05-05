package org.pitest.pitclipse.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.pitest.pitclipse.core.PitCoreActivator;

import static org.pitest.pitclipse.core.preferences.PitPreferences.AVOID_CALLS_TO;
import static org.pitest.pitclipse.core.preferences.PitPreferences.EXCLUDED_CLASSES;
import static org.pitest.pitclipse.core.preferences.PitPreferences.INCREMENTAL_ANALYSIS;
import static org.pitest.pitclipse.core.preferences.PitPreferences.PIT_EXECUTION_MODE;
import static org.pitest.pitclipse.core.preferences.PitPreferences.RUN_IN_PARALLEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.TIMEOUT;
import static org.pitest.pitclipse.core.preferences.PitPreferences.TIMEOUT_FACTOR;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_AVOID_CALLS_TO_LIST;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_MUTATORS;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_TIMEOUT;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_TIMEOUT_FACTOR;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
     * initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = PitCoreActivator.getDefault().getPreferenceStore();
        store.setDefault(PIT_EXECUTION_MODE, "containingProject");
        store.setDefault(RUN_IN_PARALLEL, true);
        store.setDefault(INCREMENTAL_ANALYSIS, false);
        store.setDefault(AVOID_CALLS_TO, DEFAULT_AVOID_CALLS_TO_LIST);
        store.setDefault(DEFAULT_MUTATORS, "defaultMutators");
        store.setDefault(TIMEOUT, DEFAULT_TIMEOUT);
        store.setDefault(TIMEOUT_FACTOR, DEFAULT_TIMEOUT_FACTOR.toString());
        store.setDefault(EXCLUDED_CLASSES, "*Test");
    }

}
