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
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.osgi.framework.Bundle;
import org.pitest.pitclipse.core.MutatorGroups;
import org.pitest.pitclipse.core.Mutators;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.runner.config.PitConfiguration;

import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;

import static org.pitest.pitclipse.core.preferences.PitPreferences.PIT_MUTATORS;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

/**
 * Tab allowing to configure a PIT analyze.
 */
public final class PitMutatorsTab extends AbstractLaunchConfigurationTab {
    private static final int NUMBER_OF_COLUMNS = MutatorGroups.values().length + 2;
    private static final String CUSTOM_MUTATOR_RADIO_TEXT = "Mutators selected below";
    private static final String CUSTOM_MUTATOR_RADIO_DATA = "CUSTOM";
    private static final String MUTATOR_LINK_TEXT = "See the documentation on Pitest.org";
    private static final String DESCRIPTION_TEXT = "Select the mutators used to alter the code.";
    private static final String MUTATOR_LINK = "https://pitest.org/quickstart/mutators/";
    private Image icon;
    private String mutators;
    private CheckboxTableViewer mutatorsTable;
    private Button customMutatorsButton;
    private Button[] groupButtons;
    private Composite mainComp;
    private Label descriptionLabel;
    private Composite grouopComposite;
    private Label mutateWithLabel;

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
        PitConfiguration preferences = PitCoreActivator.getDefault().getConfiguration();
        mutators = PitArgumentsTab.getAttributeFromConfig(config, PIT_MUTATORS, preferences.getMutators());
        System.out.println(mutators);
        if (!updateSelectionOfGroup(mutators)) {
            // no selection was made, because no match of data
            // select custom mutators button
            customMutatorsButton.setSelection(true);
            // TODO: select mutators which are set
        }

    }

    public void createControl(Composite parent) {
        mainComp = new Composite(parent, SWT.NONE);
        setControl(mainComp);
        GridLayout topLayout = new GridLayout();
        topLayout.verticalSpacing = 0;
        topLayout.numColumns = NUMBER_OF_COLUMNS;
        mainComp.setLayout(topLayout);

        Font font = parent.getFont();
        mainComp.setFont(font);

        createDescription(mainComp);
        addSeparator(mainComp);
        createSpacer(mainComp);
        createMutatorGroupsWidgets(font, mainComp);
        createSpacer(mainComp);
        createMutatorsWidgets(mainComp);

        disableTableIfUnused();
        setDirty(false);
    }

    /**
     * Creates an description what mutants are and prvides a link to the
     * documentation of pitest.org
     * @param parent where to add the description
     */
    private void createDescription(Composite parent) {
        descriptionLabel = new Label(parent, SWT.NONE);
        descriptionLabel.setText(DESCRIPTION_TEXT);
        GridDataFactory.swtDefaults().indent(5, 0).applyTo(descriptionLabel);
        Link link = new Link(parent, SWT.NONE);
        link.setText("<a href=\"" + MUTATOR_LINK + "\">" + MUTATOR_LINK_TEXT + "</a>");
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(MUTATOR_LINK);
            }

        });
    }

    /**
     * Creates an radio Button for each group present in MutatorGroups and adds one
     * for custom selection of mutants
     * @param font   which is used for text
     * @param parent where to add the widget
     */
    private void createMutatorGroupsWidgets(Font font, Composite parent) {
        // add own composite to group options closer together
        grouopComposite = new Composite(parent, SWT.NONE);
        GridDataFactory.swtDefaults().span(NUMBER_OF_COLUMNS, 1).applyTo(grouopComposite);
        GridLayoutFactory.swtDefaults().numColumns(NUMBER_OF_COLUMNS).applyTo(grouopComposite);
        mutateWithLabel = new Label(grouopComposite, SWT.NONE);
        mutateWithLabel.setFont(font);
        mutateWithLabel.setText("Mutate with: ");
        GridDataFactory.swtDefaults().applyTo(mutateWithLabel);

        groupButtons = new Button[MutatorGroups.values().length];
        int i = 0;
        for (MutatorGroups mutatorGroup : MutatorGroups.values()) {
            Button button = new Button(grouopComposite, SWT.RADIO);
            button.setText(mutatorGroup.getLabel());
            button.setData(mutatorGroup.getId());
            button.setFont(font);
            button.addSelectionListener(widgetSelectedAdapter(event -> {
                final String old = mutators;
                mutators = (String) event.widget.getData();
                if (((Button) event.widget).getSelection() && !old.equals(mutators)) {
                    updateLaunchConfigurationDialog();
                    disableTableIfUnused();
                }
            }));
            groupButtons[i++] = button;
            GridDataFactory.swtDefaults().applyTo(button);
        }
        customMutatorsButton = new Button(grouopComposite, SWT.RADIO);
        customMutatorsButton.setText(CUSTOM_MUTATOR_RADIO_TEXT);
        customMutatorsButton.setData(CUSTOM_MUTATOR_RADIO_DATA);
        customMutatorsButton.setFont(font);
        customMutatorsButton.addSelectionListener(widgetSelectedAdapter(event -> {
            final String old = mutators;
            mutators = (String) event.widget.getData();
            if (!old.equals(mutators)) {
                updateLaunchConfigurationDialog();
                disableTableIfUnused();
            }
        }));
        GridDataFactory.swtDefaults().applyTo(customMutatorsButton);
    }

    /**
     * Creates a group containing a table showing available Execution Hooks.
     * Execution Hooks are found from extension points. Each execution hook provide
     * a checkbox that can be used to activate / deactivate the hook.
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

        colName.getColumn().pack();
        colDescription.getColumn().pack();

        mutatorsTable.getTable().setEnabled(true);

        // Update the tab when a hook is activated / deactivated
        mutatorsTable.addCheckStateListener(event -> updateLaunchConfigurationDialog());
    }

    private void disableTableIfUnused() {
        mutatorsTable.getTable().setEnabled(customMutatorsButton.getSelection());
    }

    protected static void addSeparator(Composite parent) {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().span(NUMBER_OF_COLUMNS, 1).applyTo(separator);
    }

    public void performApply(ILaunchConfigurationWorkingCopy config) {
        config.setAttribute(PIT_MUTATORS, getMutators());
        try {
            PitMigrationDelegate.mapResources(config);
        } catch (CoreException ce) {
            setErrorMessage(ce.getStatus().getMessage());
        }
    }

    /**
     * Returns the mutators as a String and if individual mutators are used,
     * mutators are separated with commas
     * @return mutators as string
     */
    private String getMutators() {
        if (isBasicMutatorGroup()) {
            return mutators;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<Object> iterator = Arrays.asList(mutatorsTable.getCheckedElements()).iterator();
        while (iterator.hasNext()) {
            sb.append(((Mutators) iterator.next()).getId());
            if (iterator.hasNext()) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    public void setDefaults(ILaunchConfigurationWorkingCopy workingCopy) {
        /*
         * IJavaElement javaElement = getContext(); if (javaElement != null) {
         * initializeJavaProject(javaElement, workingCopy); } else {
         * workingCopy.setAttribute(
         * IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); }
         */
    }

    private void createSpacer(Composite comp) {
        Label label = new Label(comp, SWT.NONE);
        GridData gd = new GridData();
        gd.horizontalSpan = NUMBER_OF_COLUMNS;
        label.setLayoutData(gd);
    }

    /**
     * Selects the button that conforms to the given value.
     * @param selectedValue the selected value
     * @param buttons       array of buttons to select one
     * @return true, if a button was selected
     */
    private boolean updateSelectionOfGroup(String value) {
        if (groupButtons == null || value == null) {
            return false;
        }
        boolean found = false;
        for (Button button : groupButtons) {
            boolean selection = false;
            if (((String) button.getData()).equals(value)) {
                selection = true;
                found = true;
            }
            button.setSelection(selection);
        }

        return found;
    }

    /**
     * @return <b>true</b>, if one of the groups from PIT is selected.<br>
     *         <b>false</b>, if individual mutators are selected.
     */
    private boolean isBasicMutatorGroup() {
        return !customMutatorsButton.getSelection();
    }
}
