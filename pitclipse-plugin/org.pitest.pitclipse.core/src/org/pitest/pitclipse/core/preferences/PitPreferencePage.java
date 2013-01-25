package org.pitest.pitclipse.core.preferences;

import static org.pitest.pitclipse.core.PitExecutionMode.values;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.core.PitExecutionMode;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class PitPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PitPreferencePage() {
		super(GRID);
		setPreferenceStore(PitCoreActivator.getDefault().getPreferenceStore());
		setDescription("Pitclipse Preferences");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		PitExecutionMode[] values = values();
		String[][] executionModeValues = new String[values.length][2];
		for (int i = 0; i < values.length; i++) {
			executionModeValues[i] = new String[] { values[i].getLabel(),
					values[i].getId() };
		}
		addField(new RadioGroupFieldEditor(PreferenceConstants.P_CHOICE,
				"Pit execution scope", 1, executionModeValues,
				getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}