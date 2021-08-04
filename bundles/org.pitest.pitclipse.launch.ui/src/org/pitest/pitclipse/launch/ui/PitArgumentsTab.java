/*******************************************************************************
 * Copyright 2012-2021 Phil Glover and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.launch.ui;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.AVOID_CALLS_TO_LABEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.EXCLUDED_CLASSES_LABEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.EXCLUDED_METHODS_LABEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.INCREMENTAL_ANALYSIS_LABEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.RUN_IN_PARALLEL_LABEL;
import static org.pitest.pitclipse.launch.PitLaunchArgumentsConstants.ATTR_TEST_CONTAINER;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_AVOID_CALLS_TO;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_EXCLUDE_CLASSES;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_EXCLUDE_METHODS;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_TARGET_CLASSES;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_TEST_INCREMENTALLY;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_TEST_IN_PARALLEL;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.runner.config.PitConfiguration;
import org.pitest.pitclipse.ui.core.PitUiActivator;

/**
 * Tab allowing to configure a PIT analyze.
 */
public final class PitArgumentsTab extends AbstractLaunchConfigurationTab {
    public static final String NAME = "PIT";
    private static final int NUMBER_OF_COLUMNS = 3;
    public static final String TEST_CLASS_RADIO_TEXT = "Run mutations from a unit test";
    public static final String TEST_CLASS_TEXT = "Test Class:";
    public static final String TEST_DIR_RADIO_TEXT = "Run mutations against a package or directory";
    public static final String TEST_DIR_TEXT = "Directory:";
    public static final String FILTERS_GROUP_TEXT = " Filters ";
    public static final String SCOPE_GROUP_TEXT = " Mutation Scope ";
    public static final String PROJECT_TEXT = "Project to mutate: ";

    /**
     * Text which holds the information about which project to mutate.
     */
    private Text projectText;
    private String containerId;
    /**
     * Radio button, if selected a test class is specified with
     * {@link #testClassText}.
     */
    private Button testClassRadioButton;
    /**
     * Text which holds the information about which test class is used
     */
    private Text testClassText;
    /**
     * Radio button, if selected a test directory is specified with
     * {@link #testDirText}.
     */
    private Button testDirectoryRadioButton;
    /**
     * Text which holds the information about which directory the tests are located
     */
    private Text testDirText;
    /**
     * Check box button, if selected a single or more target class is specified with
     * {@link #targetClassText}.
     */
    private Button targetClassCheckBoxButton;
    /**
     * Text which holds the information about which classes should be mutated
     */
    private Text targetClassText;
    /**
     * Helper pattern for {@link #CLASS_PATTERN}
     */
    private static final String ID_PATTERN = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
    /**
     * Pattern which matches a valid java class
     */
    private static final String CLASS_PATTERN = ID_PATTERN + "(\\." + ID_PATTERN + ")*";
    /**
     * Pattern which matches multiple {@link #CLASS_PATTERN} separated by commas
     */
    private static final Pattern MULTI_CLASS_PATTERN = Pattern.compile(CLASS_PATTERN + "(,\\s?" + CLASS_PATTERN + ")*");
    /**
     * Target class error message. Which is displayed, if the text field has no
     * valid input
     */
    private static final String TARGET_CLASS_ERROR_MESSAGE = "The target class field can only contain classes seperated by commas, with their packages divided by dots and shouldn't end with .java.\n" + "Example: foo.bar.Foo";
    /**
     * Radio button, if selected the tests are run in parallel
     */
    private Button runInParallel;
    /**
     * Radio button, if selected the tests are run with incremental analysis
     */
    private Button incrementalAnalysis;
    /**
     * Text which holds the information about which classes should <b>not</b> be
     * mutated
     */
    private Text excludedClassesText;
    /**
     * Text which holds the information about which methods should <b>not</b> be
     * mutated
     */
    private Text excludedMethodsText;
    /**
     * Text which holds the information about which methods should <b>not</b> called
     */
    private Text avoidCallsTo;

    @Override
    public Image getImage() {
        return PitUiActivator.getDefault().getPitIcon();
    }

    @Override
    public void initializeFrom(ILaunchConfiguration config) {
        projectText.setText(getAttributeFromConfig(config, ATTR_PROJECT_NAME, ""));
        String testClass = getAttributeFromConfig(config, ATTR_MAIN_TYPE_NAME, "");
        containerId = getAttributeFromConfig(config, ATTR_TEST_CONTAINER, "");
        testClassText.setText(testClass);
        if (testClass.length() == 0 && containerId.length() > 0) {
            testClassText.setText("");
            IJavaElement containerElement = JavaCore.create(containerId);
            testDirText.setText(new JavaElementLabelProvider().getText(containerElement));
            testClassRadioButton.setSelection(false);
            testDirectoryRadioButton.setSelection(true);
        } else {
            testClassText.setText(testClass);
            testDirText.setText("");
            testClassRadioButton.setSelection(true);
            testDirectoryRadioButton.setSelection(false);
        }
        final String targetClass = getAttributeFromConfig(config, ATTR_TARGET_CLASSES, "");
        targetClassCheckBoxButton.setSelection(!targetClass.equals(""));
        targetClassText.setText(targetClass);
        targetClassText.setEnabled(!targetClass.equals(""));
        initialiseWithPreferenceDefaults(config);
        testModeChanged();
    }

    private void initialiseWithPreferenceDefaults(ILaunchConfiguration config) {
        PitConfiguration preferences = PitCoreActivator.getDefault()
                .getConfiguration();
        runInParallel.setSelection(
                getBooleanAttributeFromConfig(config,
                        ATTR_TEST_IN_PARALLEL,
                        preferences.isParallelExecution()));
        incrementalAnalysis.setSelection(
                getBooleanAttributeFromConfig(config,
                        ATTR_TEST_INCREMENTALLY,
                        preferences.isIncrementalAnalysis()));
        excludedClassesText.setText(getAttributeFromConfig(config,
                ATTR_EXCLUDE_CLASSES, preferences.getExcludedClasses()));
        excludedMethodsText.setText(getAttributeFromConfig(config,
                ATTR_EXCLUDE_METHODS, preferences.getExcludedMethods()));
        avoidCallsTo.setText(getAttributeFromConfig(config,
                ATTR_AVOID_CALLS_TO, preferences.getExcludedMethods()));
    }

    @Override
    public void createControl(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        setControl(comp);
        GridLayout topLayout = new GridLayout();
        topLayout.verticalSpacing = 0;
        topLayout.numColumns = NUMBER_OF_COLUMNS;
        comp.setLayout(topLayout);

        Font font = parent.getFont();
        comp.setFont(font);

        createSpacer(comp);
        createProjectWidgets(font, comp);
        createSpacer(comp);
        createMutationScopeWidgets(font, comp);
        createSpacer(comp);
        createFilters(font, comp);
        createSpacer(comp);
        createPreferences(font, comp);
    }

    private void createMutationScopeWidgets(Font font, Composite comp) {
        Group group = new Group(comp, SWT.NONE);
        group.setText(SCOPE_GROUP_TEXT);
        group.setFont(font);
        GridData groupGrid = new GridData(FILL_HORIZONTAL);
        groupGrid.horizontalSpan = NUMBER_OF_COLUMNS;
        group.setLayoutData(groupGrid);
        GridLayout groupLayout = new GridLayout(2, false);
        group.setLayout(groupLayout);

        createTestClassWidgets(font, group, groupLayout.numColumns);
        createSpacer(group);
        createTestDirWidgets(font, group, groupLayout.numColumns);
        createSpacer(group);
        createTargetClassWidgets(font, group);
    }

    private void createProjectWidgets(Font font, Composite comp) {
        Label projectLabel = new Label(comp, SWT.NONE);
        projectLabel.setText(PROJECT_TEXT);
        GridData gd = new GridData();
        gd.horizontalSpan = 1;
        projectLabel.setLayoutData(gd);
        projectLabel.setFont(font);

        gd = new GridData(FILL_HORIZONTAL);
        // 1 column less to fit label in front of the text field
        gd.horizontalSpan = NUMBER_OF_COLUMNS - 1;
        projectText = new Text(comp, SWT.SINGLE | SWT.BORDER);
        projectText.setLayoutData(gd);
        projectText.addModifyListener(new UpdateOnModifyListener());
    }

    private void createTestClassWidgets(Font font, Composite comp, int columnsInParent) {
        testClassRadioButton = createTestRadioButton(comp, TEST_CLASS_RADIO_TEXT);
        GridData testClassGrid = new GridData(FILL_HORIZONTAL);
        testClassGrid.horizontalSpan = columnsInParent;
        testClassRadioButton.setLayoutData(testClassGrid);

        Label testClassLabel = new Label(comp, SWT.NONE);
        testClassLabel.setText(TEST_CLASS_TEXT);
        GridData labelGrid = new GridData();
        labelGrid.horizontalIndent = 25;
        labelGrid.horizontalSpan = 1;
        testClassLabel.setLayoutData(labelGrid);
        testClassLabel.setFont(font);

        GridData textGrid = new GridData(FILL_HORIZONTAL);
        textGrid.horizontalSpan = columnsInParent - 1;
        textGrid.grabExcessHorizontalSpace = true;
        testClassText = new Text(comp, SWT.SINGLE | SWT.BORDER);
        testClassText.setLayoutData(textGrid);
        testClassText.addModifyListener(new UpdateOnModifyListener());
    }

    private Button createTestRadioButton(Composite comp, String label) {
        Button button = new Button(comp, SWT.RADIO);
        button.setText(label);
        button.addSelectionListener(new ButtonSelectionAdapter(button));
        return button;
    }

    private void createTestDirWidgets(Font font, Composite comp, int columnsInParent) {
        testDirectoryRadioButton = createTestRadioButton(comp, TEST_DIR_RADIO_TEXT);
        GridData testDirGrid = new GridData(FILL_HORIZONTAL);
        testDirGrid.horizontalSpan = columnsInParent;
        testDirectoryRadioButton.setLayoutData(testDirGrid);

        GridData labelGrid = new GridData();
        labelGrid.horizontalIndent = 25;
        labelGrid.horizontalSpan = 1;
        Label testDirectoryLabel = new Label(comp, SWT.NONE);
        testDirectoryLabel.setText(TEST_DIR_TEXT);
        testDirectoryLabel.setLayoutData(labelGrid);
        testDirectoryLabel.setFont(font);

        GridData textGrid = new GridData(FILL_HORIZONTAL);
        textGrid.horizontalSpan = columnsInParent - 1;
        testDirText = new Text(comp, SWT.SINGLE | SWT.BORDER);
        testDirText.setLayoutData(textGrid);
        testDirText.addModifyListener(new UpdateOnModifyListener());
    }

    private void createTargetClassWidgets(Font font, Composite comp) {
        targetClassCheckBoxButton = createNewCheckBox(font, comp, NUMBER_OF_COLUMNS,
                "Run mutations against specific target classes");
        targetClassCheckBoxButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                targetClassText.setEnabled(targetClassCheckBoxButton.getSelection());
                updateLaunchConfigurationDialog();
            }
        });
    
        Label targetClassLabel = new Label(comp, SWT.NONE);
        targetClassLabel.setText("Target Class(es):");
        GridData labelGrid = new GridData(FILL_HORIZONTAL);
        labelGrid.horizontalIndent = 25;
        labelGrid.horizontalSpan = NUMBER_OF_COLUMNS;
        targetClassLabel.setLayoutData(labelGrid);
        targetClassLabel.setFont(font);
    
        GridData textGrid = new GridData(FILL_HORIZONTAL);
        textGrid.horizontalSpan = NUMBER_OF_COLUMNS;
        textGrid.horizontalIndent = 25;
        targetClassText = new Text(comp, SWT.SINGLE | SWT.BORDER);
        targetClassText.setLayoutData(textGrid);
        targetClassText.addModifyListener(new UpdateOnModifyListener());
    }

    private void createFilters(Font font, Composite comp) {
        Group group = new Group(comp, SWT.NONE);
        group.setText(FILTERS_GROUP_TEXT);
        group.setFont(font);
        GridData groupGrid = new GridData(FILL_HORIZONTAL);
        groupGrid.horizontalSpan = NUMBER_OF_COLUMNS;
        group.setLayoutData(groupGrid);
        GridLayout groupLayout = new GridLayout(1, false);
        group.setLayout(groupLayout);

        runInParallel = createNewCheckBox(font, group, groupLayout.numColumns,
                RUN_IN_PARALLEL_LABEL);
        incrementalAnalysis = createNewCheckBox(font, group, groupLayout.numColumns,
                INCREMENTAL_ANALYSIS_LABEL);
    }

    private void createPreferences(Font font, Composite comp) {
        Group misc = new Group(comp, SWT.NONE);
        misc.setText(" Preferences ");
        misc.setFont(font);
        GridData miscGrid = new GridData(FILL_HORIZONTAL);
        miscGrid.horizontalSpan = NUMBER_OF_COLUMNS;
        misc.setLayoutData(miscGrid);
        GridLayout miscLayout = new GridLayout(2, false);
        misc.setLayout(miscLayout);

        excludedClassesText = createTextPreference(font, misc, miscLayout.numColumns,
                EXCLUDED_CLASSES_LABEL);
        excludedMethodsText = createTextPreference(font, misc, miscLayout.numColumns,
                EXCLUDED_METHODS_LABEL);
        avoidCallsTo = createTextPreference(font, misc, miscLayout.numColumns,
                AVOID_CALLS_TO_LABEL);
    }

    private Text createTextPreference(Font font, Composite comp, int columnsInParent, String label) {
        GridData labelGrid = new GridData();
        labelGrid.horizontalSpan = 1;
        Label testDirectoryLabel = new Label(comp, SWT.NONE);
        testDirectoryLabel.setText(label);
        testDirectoryLabel.setLayoutData(labelGrid);
        testDirectoryLabel.setFont(font);

        GridData textGrid = new GridData(FILL_HORIZONTAL);
        textGrid.horizontalSpan = columnsInParent - 1;
        Text textBox = new Text(comp, SWT.SINGLE | SWT.BORDER);
        textBox.setLayoutData(textGrid);
        textBox.addModifyListener(new UpdateOnModifyListener());
        return textBox;
    }

    private Button createNewCheckBox(Font font, Composite comp, int columnsInParent, String label) {
        Button checkBox = new Button(comp, SWT.CHECK);
        checkBox.setText(label);
        GridData labelGrid = new GridData(FILL_HORIZONTAL);
        labelGrid.horizontalSpan = columnsInParent - 1;
        checkBox.setLayoutData(labelGrid);
        checkBox.setFont(font);
        return checkBox;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy workingCopy) {
        workingCopy.setAttribute(ATTR_PROJECT_NAME, projectText.getText()
                .trim());
        if (testClassRadioButton.getSelection()) {
            workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, testClassText.getText().trim());
            workingCopy.setAttribute(ATTR_TEST_CONTAINER, "");
        } else {
            workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, "");
            workingCopy.setAttribute(ATTR_TEST_CONTAINER, containerId);
        }
        workingCopy.setAttribute(ATTR_TARGET_CLASSES,
                targetClassText.getText().trim());
        workingCopy.setAttribute(ATTR_TEST_IN_PARALLEL,
                runInParallel.getSelection());
        workingCopy.setAttribute(ATTR_TEST_INCREMENTALLY,
                incrementalAnalysis.getSelection());
        workingCopy.setAttribute(ATTR_EXCLUDE_CLASSES,
                excludedClassesText.getText());
        workingCopy.setAttribute(ATTR_EXCLUDE_METHODS,
                excludedMethodsText.getText());
        workingCopy.setAttribute(ATTR_AVOID_CALLS_TO, avoidCallsTo.getText());
        try {
            PitMigrationDelegate.mapResources(workingCopy);
        } catch (CoreException ce) {
            setErrorMessage(ce.getStatus().getMessage());
        }
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy workingCopy) {
        /*
         * IJavaElement javaElement = getContext(); if (javaElement != null) {
         * initializeJavaProject(javaElement, workingCopy); } else {
         * workingCopy.setAttribute(
         * IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); }
         */
    }

    public static String getAttributeFromConfig(ILaunchConfiguration config,
            String attribute, String defaultValue) {
        String result = defaultValue;
        try {
            result = config.getAttribute(attribute, defaultValue);
        } catch (CoreException e) {
            // Swallowed
        }
        return result;
    }

    public static boolean getBooleanAttributeFromConfig(ILaunchConfiguration config,
            String attribute, boolean defaultValue) {
        boolean result = defaultValue;
        try {
            result = config.getAttribute(attribute, defaultValue);
        } catch (CoreException e) {
            // Swallowed
        }
        return result;
    }

    private void createSpacer(Composite comp) {
        Label label = new Label(comp, SWT.NONE);
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        label.setLayoutData(gd);
    }

    private void testModeChanged() {
        boolean isSingleTestMode = testClassRadioButton.getSelection();
        testClassText.setEnabled(isSingleTestMode);
        testDirText.setEnabled(!isSingleTestMode);
        updateLaunchConfigurationDialog();
    }

    private class ButtonSelectionAdapter extends SelectionAdapter {
        private final Button button;

        private ButtonSelectionAdapter(Button button) {
            this.button = button;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            if (button.getSelection()) {
                testModeChanged();
            }
        }
    }

    private final class UpdateOnModifyListener implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent evt) {
            updateLaunchConfigurationDialog();
        }
    }

    @Override
    public boolean canSave() {
        return !targetClassCheckBoxButton.getSelection()
                || targetClassCheckBoxButton.getSelection()
                        && MULTI_CLASS_PATTERN.matcher(targetClassText.getText()).matches();
    }

    @Override
    public boolean isValid(ILaunchConfiguration launchConfig) {
        return canSave();
    }

    @Override
    protected void updateLaunchConfigurationDialog() {
        if (canSave()) {
            setErrorMessage(null);
        } else {
            setErrorMessage(TARGET_CLASS_ERROR_MESSAGE);
        }
        super.updateLaunchConfigurationDialog();
    }
}
