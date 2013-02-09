package org.pitest.pitclipse.core.preferences;

import static org.pitest.pitclipse.core.preferences.PreferenceConstants.INCREMENTAL_ANALYSIS;
import static org.pitest.pitclipse.core.preferences.PreferenceConstants.PIT_EXECUTION_MODE;
import static org.pitest.pitclipse.core.preferences.PreferenceConstants.RUN_IN_PARALLEL;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.pitest.pitclipse.core.PitCoreActivator;

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
		IPreferenceStore store = PitCoreActivator.getDefault()
				.getPreferenceStore();
		store.setDefault(PIT_EXECUTION_MODE, "containingProject");
		store.setDefault(RUN_IN_PARALLEL, "true");
		store.setDefault(INCREMENTAL_ANALYSIS, "false");
	}

}
