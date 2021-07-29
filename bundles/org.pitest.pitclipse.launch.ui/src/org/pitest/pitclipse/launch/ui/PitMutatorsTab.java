/*******************************************************************************
 * Copyright 2012-2021 Jonas Kutscha and contributors
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

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;
import static org.pitest.pitclipse.core.preferences.PitPreferences.INDIVIDUAL_MUTATORS;
import static org.pitest.pitclipse.core.preferences.PitPreferences.MUTATORS;

import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;

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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
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
import org.pitest.mutationtest.engine.gregor.config.Mutator;
import org.pitest.pitclipse.core.Mutators;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.runner.config.PitConfiguration;

/**
 * Tab allowing to configure a PIT analyze.
 * @author Jonas Kutscha
 */
public final class PitMutatorsTab extends AbstractLaunchConfigurationTab {
    public static final String NAME = "Mutators";

    private static final int NUMBER_OF_COLUMNS = Mutators.getMainGroup().size() + 2;
    private static final String DESCRIPTION_TEXT = "Select the mutators used to alter the code.";
    private static final String MUTATOR_LINK_TEXT = "See the documentation on Pitest.org";
    private static final String MUTATOR_LINK = "https://pitest.org/quickstart/mutators/";
    public static final String CUSTOM_MUTATOR_RADIO_TEXT = "Mutators selected below";
    private static final String CUSTOM_MUTATOR_RADIO_DATA = "CUSTOM";
    private static final String COLUMN_DESCRIPTION = "Description";
    private static final String COLUMN_NAME = "Name";
    private static final String NO_DESCRIPTION_TEXT = "No description found yet.";
    private static final String ERROR_MESSAGE = "At least one mutator or mutator group needs to be selected!";
    private Image icon;
    private String mutators;
    private CheckboxTableViewer mutatorsTable;
    private Button customMutatorsButton;
    private Button[] groupButtons;
    private Composite mainComp;

    @Override
    public String getName() {
        return NAME;
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
        if (icon != null && !icon.isDisposed()) {
            icon.dispose();
        }
        if (mainComp != null && !mainComp.isDisposed()) {
            mainComp.dispose();
        }
        // always call super.dispose() last, if dispose() is overridden.
        super.dispose();
    }

    @Override
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

    @Override
    public void createControl(Composite parent) {
        mainComp = new Composite(parent, SWT.NONE);
        setControl(mainComp);
        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(mainComp);
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
        mainComp.pack();
    }

    /**
     * Creates an description what mutants are and prvides a link to the
     * documentation of pitest.org
     * @param parent where to add the description
     */
    private void createDescription(Composite parent) {
        final Label descriptionLabel = new Label(parent, SWT.NONE);
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
        GridDataFactory.swtDefaults().applyTo(link);
    }

    /**
     * Creates an radio Button for each group present in MutatorGroups and adds one
     * for custom selection of mutants
     * @param font   which is used for text
     * @param parent where to add the widget
     */
    private void createMutatorGroupsWidgets(Font font, Composite parent) {
        // add own composite to group options closer together
        final Composite grouopComposite = new Composite(parent, SWT.NONE);
        GridDataFactory.swtDefaults().span(NUMBER_OF_COLUMNS, 1).applyTo(grouopComposite);
        GridLayoutFactory.swtDefaults().numColumns(NUMBER_OF_COLUMNS).applyTo(grouopComposite);
        final Label mutateWithLabel = new Label(grouopComposite, SWT.NONE);
        mutateWithLabel.setFont(font);
        mutateWithLabel.setText("Mutate with: ");
        GridDataFactory.swtDefaults().applyTo(mutateWithLabel);

        groupButtons = new Button[Mutators.getMainGroup().size()];
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
     * a check box that can be used to activate / deactivate the hook.
     */
    private void createMutatorsTable(Composite parent) {
        mutatorsTable = CheckboxTableViewer.newCheckList(parent, SWT.MULTI | SWT.BORDER);
        mutatorsTable.getTable().setHeaderVisible(true);
        mutatorsTable.setContentProvider(new ArrayContentProvider());
        // filter out groups which are present in the main group
        mutatorsTable.setFilters(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                try {
                    return !Mutators.getMainGroup().contains(Mutators.valueOf((String) element));
                } catch (IllegalArgumentException e) {
                    // not in predefined mutators, is okay
                    return true;
                }
            }
        });
        GridDataFactory.swtDefaults().grab(false, false).span(NUMBER_OF_COLUMNS, 1).applyTo(mutatorsTable.getTable());

        TableViewerColumn colName = new TableViewerColumn(mutatorsTable, SWT.FILL);
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

        TableViewerColumn colDescription = new TableViewerColumn(mutatorsTable, SWT.FILL);
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
                    cell.setText(NO_DESCRIPTION_TEXT);
                }
            }
        });
        mutatorsTable.setInput(Mutator.allMutatorIds());
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

    @Override
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

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy workingCopy) {
        // Intentionally left empty
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

    /**
     * Only allow save, if one of the main mutator groups is selected or at least
     * one mutator is selected inside the mutatorTable
     */
    @Override
    public boolean canSave() {
        return isBasicMutatorGroup() || (!isBasicMutatorGroup() && mutatorsTable.getCheckedElements().length > 0);
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
            setErrorMessage(ERROR_MESSAGE);
        }
        super.updateLaunchConfigurationDialog();
    }
}
