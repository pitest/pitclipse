/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.runner.config.PitConfiguration;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.AVOID_CALLS_FROM_PIT;
import static org.pitest.pitclipse.core.preferences.PitPreferences.EXCLUDE_CLASSES_FROM_PIT;
import static org.pitest.pitclipse.core.preferences.PitPreferences.EXCLUDE_METHODS_FROM_PIT;
import static org.pitest.pitclipse.core.preferences.PitPreferences.MUTATION_TESTS_RUN_IN_PARALLEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.USE_INCREMENTAL_ANALYSIS;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_AVOID_CALLS_TO;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_EXCLUDE_CLASSES;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_EXCLUDE_METHODS;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_TEST_INCREMENTALLY;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_TEST_IN_PARALLEL;
import static org.pitest.pitclipse.launch.PitLaunchArgumentsConstants.ATTR_TEST_CONTAINER;

/**
 * Tab allowing to configure a PIT analyze. 
 */
public final class PitArgumentsTab extends AbstractLaunchConfigurationTab {
    private static final int NUMBER_OF_COLUMNS = 3;

    private Text testClassText;
    private Text projectText;
    private Button testClassRadioButton;
    private Button testDirectoryRadioButton;
    private Text testDirText;
    private String containerId;
    private Button runInParallel;
    private Button incrementalAnalysis;
    private Text excludedClassesText;
    private Text excludedMethodsText;
    private Text avoidCallsTo;

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
        initialiseWithPreferenceDefaults(config);
        testModeChanged();
    }

    private void initialiseWithPreferenceDefaults(ILaunchConfiguration config) {
        PitConfiguration preferences = PitCoreActivator.getDefault()
                .getConfiguration();
        runInParallel.setSelection(Boolean
                .valueOf(getBooleanAttributeFromConfig(config,
                        ATTR_TEST_IN_PARALLEL,
                        preferences.isParallelExecution())));
        incrementalAnalysis.setSelection(Boolean
                .valueOf(getBooleanAttributeFromConfig(config,
                        ATTR_TEST_INCREMENTALLY,
                        preferences.isIncrementalAnalysis())));
        excludedClassesText.setText(getAttributeFromConfig(config,
                ATTR_EXCLUDE_CLASSES, preferences.getExcludedClasses()));
        excludedMethodsText.setText(getAttributeFromConfig(config,
                ATTR_EXCLUDE_METHODS, preferences.getExcludedMethods()));
        avoidCallsTo.setText(getAttributeFromConfig(config,
                ATTR_AVOID_CALLS_TO, preferences.getExcludedMethods()));
    }

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
        createTestClassWidgets(font, comp);
        createSpacer(comp);
        createTestDirWidgets(font, comp);
        createSpacer(comp);
        createPreferences(font, comp);
    }

    private void createProjectWidgets(Font font, Composite comp) {
        Label projectLabel = new Label(comp, SWT.NONE);
        projectLabel.setText("Project to mutate:");
        GridData gd = new GridData(FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
        projectLabel.setLayoutData(gd);
        projectLabel.setFont(font);

        gd = new GridData(FILL_HORIZONTAL);
        gd.horizontalSpan = NUMBER_OF_COLUMNS;
        projectText = new Text(comp, SWT.SINGLE | SWT.BORDER);
        projectText.setLayoutData(gd);
        projectText.addModifyListener(new UpdateOnModifyListener());
    }

    private void createTestClassWidgets(Font font, Composite comp) {
        testClassRadioButton = createTestRadioButton(comp,
                "Run mutations from a unit test");

        Label testClassLabel = new Label(comp, SWT.NONE);
        testClassLabel.setText("Test Class:");
        GridData labelGrid = new GridData(FILL_HORIZONTAL);
        labelGrid.horizontalIndent = 25;
        labelGrid.horizontalSpan = NUMBER_OF_COLUMNS;
        testClassLabel.setLayoutData(labelGrid);
        testClassLabel.setFont(font);

        GridData textGrid = new GridData(FILL_HORIZONTAL);
        textGrid.horizontalSpan = NUMBER_OF_COLUMNS;
        textGrid.horizontalIndent = 25;
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

    private void createTestDirWidgets(Font font, Composite comp) {
        testDirectoryRadioButton = createTestRadioButton(comp,
                "Run mutations against a package or directory");

        GridData labelGrid = new GridData(FILL_HORIZONTAL);
        labelGrid.horizontalIndent = 25;
        labelGrid.horizontalSpan = NUMBER_OF_COLUMNS;
        Label testDirectoryLabel = new Label(comp, SWT.NONE);
        testDirectoryLabel.setText("Directory:");
        testDirectoryLabel.setLayoutData(labelGrid);
        testDirectoryLabel.setFont(font);

        GridData textGrid = new GridData(FILL_HORIZONTAL);
        textGrid.horizontalSpan = NUMBER_OF_COLUMNS;
        textGrid.horizontalIndent = 25;
        testDirText = new Text(comp, SWT.SINGLE | SWT.BORDER);
        testDirText.setLayoutData(textGrid);
        testDirText.addModifyListener(new UpdateOnModifyListener());
    }

    private void createPreferences(Font font, Composite comp) {
        runInParallel = createNewCheckBox(font, comp,
                MUTATION_TESTS_RUN_IN_PARALLEL);
        incrementalAnalysis = createNewCheckBox(font, comp,
                USE_INCREMENTAL_ANALYSIS);
        excludedClassesText = createTextPreference(font, comp,
                EXCLUDE_CLASSES_FROM_PIT);
        excludedMethodsText = createTextPreference(font, comp,
                EXCLUDE_METHODS_FROM_PIT);
        avoidCallsTo = createTextPreference(font, comp, AVOID_CALLS_FROM_PIT);
    }

    private Text createTextPreference(Font font, Composite comp, String label) {
        GridData labelGrid = new GridData(FILL_HORIZONTAL);
        labelGrid.horizontalIndent = 25;
        labelGrid.horizontalSpan = NUMBER_OF_COLUMNS;
        Label testDirectoryLabel = new Label(comp, SWT.NONE);
        testDirectoryLabel.setText(label);
        testDirectoryLabel.setLayoutData(labelGrid);
        testDirectoryLabel.setFont(font);

        GridData textGrid = new GridData(FILL_HORIZONTAL);
        textGrid.horizontalSpan = NUMBER_OF_COLUMNS;
        textGrid.horizontalIndent = 25;
        Text textBox = new Text(comp, SWT.SINGLE | SWT.BORDER);
        textBox.setLayoutData(textGrid);
        textBox.addModifyListener(new UpdateOnModifyListener());
        return textBox;
    }

    private Button createNewCheckBox(Font font, Composite comp, String label) {
        Button checkBox = new Button(comp, SWT.CHECK);
        checkBox.setText(label);
        GridData labelGrid = new GridData(FILL_HORIZONTAL);
        labelGrid.horizontalSpan = NUMBER_OF_COLUMNS;
        checkBox.setLayoutData(labelGrid);
        checkBox.setFont(font);
        return checkBox;
    }

    public String getName() {
        return "PIT";
    }

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

    public void setDefaults(ILaunchConfigurationWorkingCopy workingCopy) {
        /*
         * IJavaElement javaElement = getContext(); if (javaElement != null) {
         * initializeJavaProject(javaElement, workingCopy); } else {
         * workingCopy.setAttribute(
         * IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); }
         */
    }

    public String getAttributeFromConfig(ILaunchConfiguration config,
            String attribute, String defaultValue) {
        String result = defaultValue;
        try {
            result = config.getAttribute(attribute, defaultValue);
        } catch (CoreException e) {
            // Swallowed
        }
        return result;
    }

    public boolean getBooleanAttributeFromConfig(ILaunchConfiguration config,
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
        public void modifyText(ModifyEvent evt) {
            updateLaunchConfigurationDialog();
        }
    }
}
