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
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.pitest.pitclipse.core.MutatorGroups;
import org.pitest.pitclipse.core.Mutators;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.runner.config.PitConfiguration;

import java.net.URL;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;
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

/**
 * Tab allowing to configure a PIT analyze. 
 */
public final class PitMutatorsTab extends AbstractLaunchConfigurationTab {
    private static final int NUMBER_OF_COLUMNS = MutatorGroups.values().length + 2;
    
    private Image icon;

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

    private CheckboxTableViewer mutatorsTable;

    private Button customMutatorsButton;

    @Override
    public String getName() {
        return "Mutators";
    }
    
    @Override
    public Image getImage() {
        Bundle bundle = Platform.getBundle(PitLaunchUiActivator.PLUGIN_ID);
        Path path = new Path("icons/pit.gif");
        URL iconURL = FileLocator.find(bundle, path, null);
        icon = ImageDescriptor.createFromURL(iconURL).createImage();
        return icon;
    }
    
    @Override
    public void dispose() {
        if (icon != null) {
            icon.dispose();
        }
    }

    public void initializeFrom(ILaunchConfiguration config) {
        System.out.println("PitMutatorsTab.initializeFrom()");
//        projectText.setText(getAttributeFromConfig(config, ATTR_PROJECT_NAME, ""));
//        String testClass = getAttributeFromConfig(config, ATTR_MAIN_TYPE_NAME, "");
//        containerId = getAttributeFromConfig(config, ATTR_TEST_CONTAINER, "");
//        testClassText.setText(testClass);
//        if (testClass.length() == 0 && containerId.length() > 0) {
//            testClassText.setText("");
//            IJavaElement containerElement = JavaCore.create(containerId);
//            testDirText.setText(new JavaElementLabelProvider().getText(containerElement));
//            testClassRadioButton.setSelection(false);
//            testDirectoryRadioButton.setSelection(true);
//        } else {
//            testClassText.setText(testClass);
//            testDirText.setText("");
//            testClassRadioButton.setSelection(true);
//            testDirectoryRadioButton.setSelection(false);
//        }
//        initialiseWithPreferenceDefaults(config);
//        testModeChanged();
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

        createDescription(comp);
        addSeparator(comp);
        createSpacer(comp);
        createMutatorGroupsWidgets(font, comp);
        createSpacer(comp);
        createMutatorsWidgets(comp);
        
        disableTableIfUnused();
        setDirty(false);
    }
    
    private void createDescription(Composite parent) {
        String description = " Select the mutators used to alter the code. See the documentation on Pitest.org";
        String link = "https://pitest.org/quickstart/mutators/";
        int linkStartIndex = description.indexOf("See");
        int linkLength = description.indexOf("Pitest") + "Pitest.org".length() - description.indexOf("See");
                
        StyledText styledText = new StyledText(parent, SWT.NONE);
        styledText.setText(description);
        styledText.setBackground(parent.getBackground());
        styledText.setMarginColor(parent.getBackground());

        GridDataFactory.fillDefaults().span(NUMBER_OF_COLUMNS, 1).applyTo(styledText);
        styledText.setLeftMargin(0);
        
        StyleRange style = new StyleRange();
        style.underline = true;
        style.underlineStyle = SWT.UNDERLINE_LINK;
        style.start = linkStartIndex;
        style.length = linkLength;
        styledText.setStyleRange(style);
        
        styledText.addListener(SWT.MouseDown, event -> {
            int clickOffset = styledText.getCaretOffset();
            if (linkStartIndex <= clickOffset && clickOffset < linkStartIndex + linkLength) {
                // Open the documentation with external browser
                Program.launch(link);
            }
        });
        styledText.setBottomMargin(5);
        styledText.setToolTipText(link);
    }
    
    private void createMutatorGroupsWidgets(Font font, Composite group) {
//        Group group = new Group(comp, SWT.NONE);
//        group.setFont(font);
//        group.setText(" Mutator Groups ");
//        GridDataFactory.fillDefaults().grab(true, false).span(NUMBER_OF_COLUMNS, 1).applyTo(group);
//        GridLayoutFactory.fillDefaults().margins(8, 8).numColumns(MutatorGroups.values().length).applyTo(group);
        
        Label mutateWith = new Label(group, SWT.NONE);
        mutateWith.setFont(font);
        mutateWith.setText("Mutate with: ");
        
        boolean aMutatorGroupIsSelected = false;
        
        for (MutatorGroups mutatorGroup : MutatorGroups.values()) {
            Button mutatorGroupButton = new Button(group, SWT.RADIO);
            mutatorGroupButton.setText(mutatorGroup.getLabel());
            mutatorGroupButton.addSelectionListener(widgetSelectedAdapter(event -> {
                disableTableIfUnused();
                setDirty();
            }));
            
            if (mutatorGroup == MutatorGroups.DEFAULTS) {
                mutatorGroupButton.setSelection(true);
                aMutatorGroupIsSelected = true;
            }
        }
        customMutatorsButton = new Button(group, SWT.RADIO);
        customMutatorsButton.setText("Mutators selected below");
        customMutatorsButton.addSelectionListener(widgetSelectedAdapter(event -> {
            disableTableIfUnused();
            setDirty();
        }));
        if (!aMutatorGroupIsSelected) {
            customMutatorsButton.setSelection(true);
        }
    }
    
    private void setDirty() {
//        setDirty(true);
        updateLaunchConfigurationDialog();
    }
    
    /**
     * Creates a group containing a table showing available Execution Hooks. Execution Hooks are found from extension points.
     * Each execution hook provide a checkbox that can be used to activate / deactivate the hook.
     */
    private void createMutatorsWidgets(Composite parent) {
        mutatorsTable = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        mutatorsTable.getTable().setHeaderVisible(true);
        mutatorsTable.setContentProvider(new ArrayContentProvider());
        GridDataFactory.swtDefaults().grab(false, false).span(NUMBER_OF_COLUMNS, 1).applyTo(mutatorsTable.getTable());
        
        TableViewerColumn colName = new TableViewerColumn(mutatorsTable, SWT.FILL | SWT.H_SCROLL | SWT.V_SCROLL);
        colName.getColumn().setText("Name");
        colName.setLabelProvider(new LambdaLabelProvider<Mutators>(Mutators::getName));
        
        TableViewerColumn colDescription = new TableViewerColumn(mutatorsTable, SWT.FILL | SWT.H_SCROLL | SWT.V_SCROLL);
        colDescription.getColumn().setText("Description");
        colDescription.setLabelProvider(new LambdaLabelProvider<Mutators>(Mutators::getDescription));
        
        mutatorsTable.setInput(Mutators.values());
        
        // Init checked state based on whether the hook is activated by default
        for (Mutators mutator : Mutators.values()) {
            if (mutator.isActiveByDefault()) {
                mutatorsTable.setChecked(mutator, true);
            }
        }
        colName.getColumn().pack();
        colDescription.getColumn().pack();
        
        mutatorsTable.getTable().setEnabled(true);
        
        // Dirty the tab when a hook is activated / deactivated
        mutatorsTable.addCheckStateListener(event -> setDirty());
    }
    
    private void disableTableIfUnused() {
        mutatorsTable.getTable().setEnabled(customMutatorsButton.getSelection());
    }
    
    private void createMutationScopeWidgets(Font font, Composite comp) {
        Group group = new Group(comp, SWT.NONE);
        group.setText(" Mutation Scope ");
        group.setFont(font);
        GridData groupGrid = new GridData(FILL_HORIZONTAL);
        groupGrid.horizontalSpan = NUMBER_OF_COLUMNS;
        group.setLayoutData(groupGrid);
        GridLayout groupLayout = new GridLayout(2, false);
        group.setLayout(groupLayout);
        
        createTestClassWidgets(font, group, groupLayout.numColumns);
        createSpacer(group);
        createTestDirWidgets(font, group, groupLayout.numColumns);
    }

    private void createProjectWidgets(Font font, Composite comp) {
        Label projectLabel = new Label(comp, SWT.NONE);
        projectLabel.setText("Project to mutate: ");
        GridData gd = new GridData();
        gd.horizontalSpan = 1;
        projectLabel.setLayoutData(gd);
        projectLabel.setFont(font);

        gd = new GridData(FILL_HORIZONTAL);
        gd.horizontalSpan = NUMBER_OF_COLUMNS - 1;
        projectText = new Text(comp, SWT.SINGLE | SWT.BORDER);
        projectText.setLayoutData(gd);
        projectText.addModifyListener(new UpdateOnModifyListener());
    }

    private void createTestClassWidgets(Font font, Composite comp, int columnsInParent) {
        testClassRadioButton = createTestRadioButton(comp,
                "Run mutations from a unit test");
        GridData testClassGrid = new GridData(FILL_HORIZONTAL);
        testClassGrid.horizontalSpan = columnsInParent;
        testClassRadioButton.setLayoutData(testClassGrid);

        Label testClassLabel = new Label(comp, SWT.NONE);
        testClassLabel.setText("Test Class:");
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
        testDirectoryRadioButton = createTestRadioButton(comp,
                "Run mutations against a package or directory");
        GridData testDirGrid = new GridData(FILL_HORIZONTAL);
        testDirGrid.horizontalSpan = columnsInParent;
        testDirectoryRadioButton.setLayoutData(testDirGrid);
        
        GridData labelGrid = new GridData();
        labelGrid.horizontalIndent = 25;
        labelGrid.horizontalSpan = 1;
        Label testDirectoryLabel = new Label(comp, SWT.NONE);
        testDirectoryLabel.setText("Directory:");
        testDirectoryLabel.setLayoutData(labelGrid);
        testDirectoryLabel.setFont(font);

        GridData textGrid = new GridData(FILL_HORIZONTAL);
        textGrid.horizontalSpan = columnsInParent - 1;
        testDirText = new Text(comp, SWT.SINGLE | SWT.BORDER);
        testDirText.setLayoutData(textGrid);
        testDirText.addModifyListener(new UpdateOnModifyListener());
    }

    private void createFilters(Font font, Composite comp) {
        Group group = new Group(comp, SWT.NONE);
        group.setText(" Filters ");
        group.setFont(font);
        GridData groupGrid = new GridData(FILL_HORIZONTAL);
        groupGrid.horizontalSpan = NUMBER_OF_COLUMNS;
        group.setLayoutData(groupGrid);
        GridLayout groupLayout = new GridLayout(1, false);
        group.setLayout(groupLayout);
        
        runInParallel = createNewCheckBox(font, group, groupLayout.numColumns,
                MUTATION_TESTS_RUN_IN_PARALLEL);
        incrementalAnalysis = createNewCheckBox(font, group, groupLayout.numColumns,
                USE_INCREMENTAL_ANALYSIS);
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
                EXCLUDE_CLASSES_FROM_PIT);
        excludedMethodsText = createTextPreference(font, misc, miscLayout.numColumns,
                EXCLUDE_METHODS_FROM_PIT);
        avoidCallsTo = createTextPreference(font, misc, miscLayout.numColumns,
                AVOID_CALLS_FROM_PIT);
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
    
    protected static void addSeparator(Composite parent) {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().span(NUMBER_OF_COLUMNS, 1).applyTo(separator);
    }

    public void performApply(ILaunchConfigurationWorkingCopy workingCopy) {
        System.out.println("PitMutatorsTab.performApply()");
//        workingCopy.setAttribute(ATTR_PROJECT_NAME, projectText.getText()
//                .trim());
//        if (testClassRadioButton.getSelection()) {
//            workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, testClassText.getText().trim());
//            workingCopy.setAttribute(ATTR_TEST_CONTAINER, "");
//        } else {
//            workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, "");
//            workingCopy.setAttribute(ATTR_TEST_CONTAINER, containerId);
//        }
//        workingCopy.setAttribute(ATTR_TEST_IN_PARALLEL,
//                runInParallel.getSelection());
//        workingCopy.setAttribute(ATTR_TEST_INCREMENTALLY,
//                incrementalAnalysis.getSelection());
//        workingCopy.setAttribute(ATTR_EXCLUDE_CLASSES,
//                excludedClassesText.getText());
//        workingCopy.setAttribute(ATTR_EXCLUDE_METHODS,
//                excludedMethodsText.getText());
//        workingCopy.setAttribute(ATTR_AVOID_CALLS_TO, avoidCallsTo.getText());
//        try {
//            PitMigrationDelegate.mapResources(workingCopy);
//        } catch (CoreException ce) {
//            setErrorMessage(ce.getStatus().getMessage());
//        }
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
        gd.horizontalSpan = NUMBER_OF_COLUMNS;
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
