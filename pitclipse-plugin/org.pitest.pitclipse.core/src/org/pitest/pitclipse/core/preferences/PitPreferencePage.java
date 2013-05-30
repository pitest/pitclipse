package org.pitest.pitclipse.core.preferences;

import static org.pitest.pitclipse.core.preferences.PreferenceConstants.EXCLUDED_CLASSES;
import static org.pitest.pitclipse.core.preferences.PreferenceConstants.EXCLUDED_METHODS;
import static org.pitest.pitclipse.core.preferences.PreferenceConstants.INCREMENTAL_ANALYSIS;
import static org.pitest.pitclipse.core.preferences.PreferenceConstants.PIT_EXECUTION_MODE;
import static org.pitest.pitclipse.core.preferences.PreferenceConstants.RUN_IN_PARALLEL;
import static org.pitest.pitclipse.pitrunner.config.PitExecutionMode.values;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.pitrunner.config.PitExecutionMode;

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

	public static final String USE_INCREMENTAL_ANALYSIS = "Use &incremental analysis";
	public static final String EXCLUDE_CLASSES_FROM_PIT = "E&xcluded classes (e.g.*IntTest)";
	public static final String MUTATION_TESTS_RUN_IN_PARALLEL = "Mutation tests run in para&llel";
	public static final String EXCLUDE_METHODS_FROM_PIT = "Excluded &methods (e.g.*toString*)";

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
		createExecutionModeRadioButtons();
		createRunInParallelOption();
		createUseIncrementalAnalysisOption();
		createExcludeClassesField();
		createExcludeMethodsField();
	}

	private void createExcludeClassesField() {
		addField(new StringFieldEditor(EXCLUDED_CLASSES,
				PitPreferencePage.EXCLUDE_CLASSES_FROM_PIT,
				getFieldEditorParent()));
	}

	private void createExcludeMethodsField() {
		addField(new StringFieldEditor(EXCLUDED_METHODS,
				PitPreferencePage.EXCLUDE_METHODS_FROM_PIT,
				getFieldEditorParent()));
	}

	private void createUseIncrementalAnalysisOption() {
		addField(new BooleanFieldEditor(INCREMENTAL_ANALYSIS,
				PitPreferencePage.USE_INCREMENTAL_ANALYSIS,
				getFieldEditorParent()));
	}

	private void createRunInParallelOption() {
		addField(new BooleanFieldEditor(RUN_IN_PARALLEL,
				PitPreferencePage.MUTATION_TESTS_RUN_IN_PARALLEL,
				getFieldEditorParent()));
	}

	private void createExecutionModeRadioButtons() {
		PitExecutionMode[] values = values();
		String[][] executionModeValues = new String[values.length][2];
		for (int i = 0; i < values.length; i++) {
			executionModeValues[i] = new String[] { values[i].getLabel(),
					values[i].getId() };
		}
		addField(new RadioGroupFieldEditor(PIT_EXECUTION_MODE,
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