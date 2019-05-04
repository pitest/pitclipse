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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.core.PitMutators;

import static org.pitest.pitclipse.core.preferences.PitPreferences.PIT_MUTATORS;

public class PitMutatorsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

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

    @Override
    public void init(IWorkbench workbench) { /* Intentionally Empty */ }
}
