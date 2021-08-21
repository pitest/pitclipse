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

package org.pitest.pitclipse.preferences.ui;

import static org.pitest.pitclipse.core.preferences.PitPreferences.MUTATOR_GROUP;
import static org.pitest.pitclipse.core.preferences.PitPreferences.MUTATORS_DESCRIPTION_LABEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.MUTATORS_LABEL;

import java.util.List;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.pitest.pitclipse.core.Mutators;
import org.pitest.pitclipse.core.PitCoreActivator;

public class PitMutatorsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public PitMutatorsPreferencePage() {
        super(GRID);
        setPreferenceStore(PitCoreActivator.getDefault().getPreferenceStore());
        setDescription(MUTATORS_DESCRIPTION_LABEL);
    }

    @Override
    public void createFieldEditors() {
        createExecutionModeRadioButtons();
    }

    private void createExecutionModeRadioButtons() {
        List<Mutators> values = Mutators.getMainGroup();
        String[][] mutatorValues = new String[values.size()][2];
        int i = 0;
        for (Mutators mutator : values) {
            mutatorValues[i++] = new String[] { mutator.getDescriptor(), mutator.name() };
        }
        addField(new RadioGroupFieldEditor(MUTATOR_GROUP, MUTATORS_LABEL, 1, mutatorValues, getFieldEditorParent()));
    }

    @Override
    public void init(IWorkbench workbench) {
        // Intentionally Empty
    }
}
