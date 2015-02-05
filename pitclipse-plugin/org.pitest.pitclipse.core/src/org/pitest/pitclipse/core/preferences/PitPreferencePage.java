package org.pitest.pitclipse.core.preferences;

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

public class PitPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String USE_INCREMENTAL_ANALYSIS = "Use &incremental analysis";
	public static final String EXCLUDE_CLASSES_FROM_PIT = "E&xcluded classes (e.g.*IntTest)";
	public static final String MUTATION_TESTS_RUN_IN_PARALLEL = "Mutation tests run in para&llel";
	public static final String EXCLUDE_METHODS_FROM_PIT = "Excluded &methods (e.g.*toString*)";
	public static final String AVOID_CALLS_FROM_PIT = "&Avoid calls to";
	public static final String PIT_TIMEOUT = "Pit Ti&meout";
	public static final String PIT_TIMEOUT_FACTOR = "Timeout &Factor";
	public static final String EXCLUDED_METHODS = "excludedMethods";
	public static final String AVOID_CALLS_TO = "avoidCallsTo";
	public static final String EXCLUDED_CLASSES = "excludedClasses";
	public static final String INCREMENTAL_ANALYSIS = "incrementalAnalysis";
	public static final String PIT_EXECUTION_MODE = "pitExecutionMode";
	public static final String RUN_IN_PARALLEL = "runInParallel";
	public static final String TIMEOUT = "pitTimeout";
	public static final String TIMEOUT_FACTOR = "pitTimeoutFactor";

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
		createAvoidCallsToField();
		createPitTimeoutField();
		createPitTimeoutFactorField();
	}

	private void createAvoidCallsToField() {
		addField(new StringFieldEditor(AVOID_CALLS_TO, AVOID_CALLS_FROM_PIT, getFieldEditorParent()));
	}

	private void createExcludeClassesField() {
		addField(new StringFieldEditor(EXCLUDED_CLASSES, EXCLUDE_CLASSES_FROM_PIT, getFieldEditorParent()));
	}

	private void createExcludeMethodsField() {
		addField(new StringFieldEditor(EXCLUDED_METHODS, EXCLUDE_METHODS_FROM_PIT, getFieldEditorParent()));
	}

	private void createUseIncrementalAnalysisOption() {
		addField(new BooleanFieldEditor(INCREMENTAL_ANALYSIS, USE_INCREMENTAL_ANALYSIS, getFieldEditorParent()));
	}

	private void createRunInParallelOption() {
		addField(new BooleanFieldEditor(RUN_IN_PARALLEL, MUTATION_TESTS_RUN_IN_PARALLEL, getFieldEditorParent()));
	}

	private void createPitTimeoutField() {
		addField(new StringFieldEditor(TIMEOUT, PIT_TIMEOUT, getFieldEditorParent()));
	}

	private void createPitTimeoutFactorField() {
		addField(new StringFieldEditor(TIMEOUT_FACTOR, PIT_TIMEOUT_FACTOR, getFieldEditorParent()));
	}

	private void createExecutionModeRadioButtons() {
		PitExecutionMode[] values = values();
		String[][] executionModeValues = new String[values.length][2];
		for (int i = 0; i < values.length; i++) {
			executionModeValues[i] = new String[] { values[i].getLabel(), values[i].getId() };
		}
		addField(new RadioGroupFieldEditor(PIT_EXECUTION_MODE, "Pit execution scope", 1, executionModeValues,
				getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
	}

}