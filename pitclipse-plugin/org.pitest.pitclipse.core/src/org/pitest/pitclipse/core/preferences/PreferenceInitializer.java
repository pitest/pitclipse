package org.pitest.pitclipse.core.preferences;

import static org.pitest.pitclipse.core.PitExecutionMode.WORKSPACE;
import static org.pitest.pitclipse.core.preferences.PreferenceConstants.P_CHOICE;

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
		store.setDefault(P_CHOICE, "containingProject");
	}

}
