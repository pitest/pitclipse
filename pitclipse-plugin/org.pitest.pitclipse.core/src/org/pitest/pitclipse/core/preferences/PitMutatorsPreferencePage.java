package org.pitest.pitclipse.core.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.core.PitMutators;

public class PitMutatorsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String PIT_MUTATORS = "pitMutators";

	public PitMutatorsPreferencePage() {
		super(GRID);
		setPreferenceStore(PitCoreActivator.getDefault().getPreferenceStore());
		setDescription("Mutator Preferences");
	}

	@Override
	public void createFieldEditors() {
		createExecutionModeRadioButtons();
	}

	private void createExecutionModeRadioButtons() {
		PitMutators[] values = PitMutators.values();
		String[][] mutatorValues = new String[values.length][2];
		for (int i = 0; i < values.length; i++) {
			mutatorValues[i] = new String[] { values[i].getLabel(), values[i].getId() };
		}
		addField(new RadioGroupFieldEditor(PIT_MUTATORS, "Mutators", 1, mutatorValues, getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench )
	 */
	@Override
	public void init(IWorkbench workbench) {
	}

}
