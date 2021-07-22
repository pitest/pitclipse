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
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.osgi.framework.Bundle;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.config.Mutator;
import org.pitest.pitclipse.core.Mutators;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.runner.config.PitConfiguration;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static org.pitest.pitclipse.core.preferences.PitPreferences.INDIVIDUAL_MUTATORS;
import static org.pitest.pitclipse.core.preferences.PitPreferences.MUTATORS;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

/**
 * Tab allowing to configure a PIT analyze.
 */
public final class PitMutatorsTab extends AbstractLaunchConfigurationTab {
    private static final int NUMBER_OF_COLUMNS = Mutators.getMainGroup().length + 2;
    private static final String DESCRIPTION_TEXT = "Select the mutators used to alter the code.";
    private static final String MUTATOR_LINK_TEXT = "See the documentation on Pitest.org";
    private static final String MUTATOR_LINK = "https://pitest.org/quickstart/mutators/";
    private static final String CUSTOM_MUTATOR_RADIO_TEXT = "Mutators selected below";
    private static final String CUSTOM_MUTATOR_RADIO_DATA = "CUSTOM";
    private static final String COLUMN_DESCRIPTION = "Description";
    private static final String COLUMN_NAME = "Name";
    private static final String MUTATORS_FIELD_NAME = "MUTATORS";
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
        mutators = PitArgumentsTab.getAttributeFromConfig(config, MUTATORS, preferences.getMutators());
        if (!updateSelectionOfGroup(mutators)) {
            // no selection was made, because no match of data
            // select custom mutators button
            customMutatorsButton.setSelection(true);
        }
        // restore checked mutators
        intializeMutatorsTable(config);
        disableTableIfUnused();

    }

    private void intializeMutatorsTable(ILaunchConfiguration config) {
        final String individualMutatos = PitArgumentsTab.getAttributeFromConfig(config, INDIVIDUAL_MUTATORS, "");
        if (individualMutatos.equals("")) {
            // no mutators where set, use defaults
            for (String mutator : Mutators.getDefaultMutators()) {
                mutatorsTable.setChecked(mutator, true);
            }
        } else {
            for (String mutator : individualMutatos.split(",")) {
                mutatorsTable.setChecked(mutator, true);
            }
        }
    }

    public void createControl(Composite parent) {
        mainComp = new Composite(parent, SWT.NONE);
        setControl(mainComp);
        GridLayoutFactory.fillDefaults().numColumns(NUMBER_OF_COLUMNS).applyTo(mainComp);

        Font font = parent.getFont();
        mainComp.setFont(font);

        createDescription(mainComp);
        addSeparator(mainComp);
        createSpacer(mainComp);
        createMutatorGroupsWidgets(font, mainComp);
        createSpacer(mainComp);
        createMutatorsTable(mainComp);

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

        groupButtons = new Button[Mutators.getMainGroup().length];
        int i = 0;
        for (Mutators mutatorGroup : Mutators.getMainGroup()) {
            Button button = new Button(grouopComposite, SWT.RADIO);
            button.setText(mutatorGroup.getDescriptor());
            button.setData(mutatorGroup.name());
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
    private void createMutatorsTable(Composite parent) {
        mutatorsTable = CheckboxTableViewer.newCheckList(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        mutatorsTable.getTable().setHeaderVisible(true);
        mutatorsTable.setContentProvider(new ArrayContentProvider());
        GridDataFactory.swtDefaults().grab(true, true).span(NUMBER_OF_COLUMNS, 1).applyTo(mutatorsTable.getTable());

        TableViewerColumn colName = new TableViewerColumn(mutatorsTable, SWT.FILL | SWT.H_SCROLL | SWT.V_SCROLL);
        colName.getColumn().setText(COLUMN_NAME);
        colName.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(ViewerCell cell) {
                final String name = (String) cell.getElement();
                try {
                    final Mutators info = Mutators.valueOf(name);
                    cell.setText(info.getDescriptor());
                } catch (IllegalArgumentException e) {
                    // if no info in our enum is present use default
                    cell.setText(name);
                }
            }
        });

        TableViewerColumn colDescription = new TableViewerColumn(mutatorsTable, SWT.FILL | SWT.H_SCROLL | SWT.V_SCROLL);
        colDescription.getColumn().setText(COLUMN_DESCRIPTION);
        colDescription.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(ViewerCell cell) {
                final String name = (String) cell.getElement();
                try {
                    final Mutators info = Mutators.valueOf(name);
                    cell.setText(info.getDescription());
                } catch (IllegalArgumentException e) {
                    // if no info in our enum is present use default
                    cell.setText("Nothing found. Could be NON-FUNCTIONING.");
                }
            }
        });

        try {
            mutatorsTable.setInput(getPitMutators());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        colName.getColumn().pack();
        colDescription.getColumn().pack();

        mutatorsTable.getTable().setEnabled(true);

        // Update the tab when a hook is activated / deactivated
        mutatorsTable.addCheckStateListener(event -> updateLaunchConfigurationDialog());
    }

    /**
     * Hack because the Mutator.java from pit does not allow to get the keys of the
     * mutators yet.<br>
     * <b>Should be replaced</b>, if
     * <a href="https://github.com/hcoles/pitest/pull/917">Pit PR</a> gets merged.
     * @return keys of all mutators as Strings
     * @throws Exception if the reflection failed in any way
     */
    @SuppressWarnings("unchecked")
    private Collection<String> getPitMutators() throws Exception {
        Field field = Mutator.class.getDeclaredField(MUTATORS_FIELD_NAME);
        field.setAccessible(true);
        return ((Map<String, Iterable<MethodMutatorFactory>>) field.get(null)).keySet();
    }

    private void disableTableIfUnused() {
        mutatorsTable.getTable().setEnabled(customMutatorsButton.getSelection());
    }

    protected static void addSeparator(Composite parent) {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().span(NUMBER_OF_COLUMNS, 1).applyTo(separator);
    }

    public void performApply(ILaunchConfigurationWorkingCopy config) {
        config.setAttribute(INDIVIDUAL_MUTATORS, getIndividualMutators());
        if (isBasicMutatorGroup()) {
            config.setAttribute(MUTATORS, mutators);
        } else {
            config.setAttribute(MUTATORS, getIndividualMutators());
        }
        try {
            PitMigrationDelegate.mapResources(config);
        } catch (CoreException ce) {
            setErrorMessage(ce.getStatus().getMessage());
        }
    }

    private String getIndividualMutators() {
        StringBuilder sb = new StringBuilder("");
        Iterator<Object> iterator = Arrays.asList(mutatorsTable.getCheckedElements()).iterator();
        while (iterator.hasNext()) {
            sb.append((String) iterator.next());
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
