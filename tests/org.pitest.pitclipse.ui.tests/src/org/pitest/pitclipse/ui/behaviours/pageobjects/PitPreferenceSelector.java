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

package org.pitest.pitclipse.ui.behaviours.pageobjects;

import com.google.common.base.Optional;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.pitest.pitclipse.core.Mutators;
import org.pitest.pitclipse.runner.config.PitExecutionMode;

import java.io.Closeable;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static org.pitest.pitclipse.core.preferences.PitPreferences.AVOID_CALLS_TO_LABEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.EXCLUDED_CLASSES_LABEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.EXCLUDED_METHODS_LABEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.EXECUTION_MODE_LABEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.RUN_IN_PARALLEL_LABEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.TIMEOUT_LABEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.TIMEOUT_FACTOR_LABEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.INCREMENTAL_ANALYSIS_LABEL;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.SwtBotTreeHelper.selectAndExpand;

public class PitPreferenceSelector implements Closeable {

    private static final String MUTATORS_LABEL = "Mutators";
    private final SWTWorkbenchBot bot;

    public PitPreferenceSelector(SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    public void setPitExecutionMode(PitExecutionMode mode) {
        activatePreferenceShell();
        expandPitPreferences();
        selectExecutionMode(mode);
    }

    private void selectExecutionMode(PitExecutionMode mode) {
        bot.radio(mode.getLabel()).click();
    }

    @Override
    public void close() {
        bot.button("Apply and Close").click();
    }

    private SWTBotTreeItem expandPitPreferences() {
        return selectAndExpand(bot.tree().getTreeItem("Pitest"));
    }

    private void activatePreferenceShell() {
        SWTBotShell shell = bot.shell("Preferences");
        shell.activate();
    }

    public PitExecutionMode getPitExecutionMode() {
        return getSelectedExecutionMode().from(EXECUTION_MODE_LABEL);
    }

    private PitExecutionMode getActiveExecutionMode() {
        for (PitExecutionMode mode : PitExecutionMode.values()) {
            if (bot.radio(mode.getLabel()).isSelected()) {
                return mode;
            }
        }
        return null;
    }

    public boolean isPitRunInParallel() {
        return getBoolean().from(RUN_IN_PARALLEL_LABEL);
    }

    public boolean isIncrementalAnalysisEnabled() {
        return getBoolean().from(INCREMENTAL_ANALYSIS_LABEL);
    }

    public void setPitRunInParallel(boolean inParallel) {
        setSelectionFor(RUN_IN_PARALLEL_LABEL).to(inParallel);
    }

    public void setPitIncrementalAnalysisEnabled(boolean incremental) {
        setSelectionFor(INCREMENTAL_ANALYSIS_LABEL).to(incremental);
    }

    public String getExcludedClasses() {
        return getText().from(EXCLUDED_CLASSES_LABEL);
    }

    public void setExcludedClasses(String excludedClasses) {
        setTextFor(EXCLUDED_CLASSES_LABEL).to(excludedClasses);
    }

    public String getExcludedMethods() {
        return getText().from(EXCLUDED_METHODS_LABEL);
    }

    public void setExcludedMethods(String excludedMethods) {
        setTextFor(EXCLUDED_METHODS_LABEL).to(excludedMethods);
    }

    public String getAvoidCallsTo() {
        return getText().from(AVOID_CALLS_TO_LABEL);
    }

    public void setAvoidCallsTo(String avoidCallsTo) {
        setTextFor(AVOID_CALLS_TO_LABEL).to(avoidCallsTo);
    }

    public Mutators getMutators() {
        return getSelectedMutators().from(MUTATORS_LABEL);
    }

    private Optional<SWTBotTreeItem> expandPitMutatorPreferences() {
        SWTBotTreeItem pitPrefs = expandPitPreferences();
        for (SWTBotTreeItem t : pitPrefs.getItems()) {
            if (t.getText().equals("Mutators")) {
                return Optional.fromNullable(selectAndExpand(t));
            }
        }
        return Optional.absent();
    }

    public void setPitTimeoutConst(int timeout) {
        setTextFor(TIMEOUT_LABEL).to(timeout);
    }

    public void setPitTimeoutFactor(int factor) {
        setTextFor(TIMEOUT_FACTOR_LABEL).to(factor);
    }

    private static interface PreferenceGetter<T> {
        T getPreference(String label);
    }

    private class PreferenceGetterBuilder<T> {
        private final PreferenceGetter<T> getter;

        public PreferenceGetterBuilder(PreferenceGetter<T> getter) {
            this.getter = getter;
        }

        public T from(String label) {
            activatePreferenceShell();
            expandPitPreferences();
            return getter.getPreference(label);
        }
    }

    private class PreferenceSetterBuilder {
        private final String label;

        public PreferenceSetterBuilder(String label) {
            this.label = label;
        }

        public void to(final String value) {
            updatePreference(new PreferenceSetter<String>() {
                @Override
                public void setPreference() {
                    bot.textWithLabel(label).setText(value);
                }
            });
        }

        public void to(final boolean value) {
            updatePreference(new PreferenceSetter<Boolean>() {
                @Override
                public void setPreference() {
                    if (value) {
                        bot.checkBox(label).select();
                    }
                    else {
                        bot.checkBox(label).deselect();
                    }
                }
            });
        }

        public void to(final int value) {
            to(Integer.toString(value));
        }

        private <T> void updatePreference(PreferenceSetter<T> s) {
            activatePreferenceShell();
            expandPitPreferences();
            s.setPreference();
        }
    }

    private static interface PreferenceSetter<T> {
        void setPreference();
    }

    private PreferenceSetterBuilder setTextFor(final String label) {
        return new PreferenceSetterBuilder(label);
    }

    private PreferenceSetterBuilder setSelectionFor(final String label) {
        return new PreferenceSetterBuilder(label);
    }

    private PreferenceGetterBuilder<String> getText() {
        return new PreferenceGetterBuilder<String>(new PreferenceGetter<String>() {
            @Override
            public String getPreference(String label) {
                return bot.textWithLabel(label).getText();
            }
        });
    }

    private PreferenceGetterBuilder<Integer> getInteger() {
        return new PreferenceGetterBuilder<Integer>(new PreferenceGetter<Integer>() {
            @Override
            public Integer getPreference(String label) {
                String value = bot.textWithLabel(label).getText();
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        });
    }

    private PreferenceGetterBuilder<BigDecimal> getBigDecimal() {
        return new PreferenceGetterBuilder<BigDecimal>(new PreferenceGetter<BigDecimal>() {
            @Override
            public BigDecimal getPreference(String label) {
                String value = bot.textWithLabel(label).getText();
                try {
                    return new BigDecimal(value);
                } catch (NumberFormatException e) {
                    return ZERO;
                }
            }
        });
    }

    private PreferenceGetterBuilder<Boolean> getBoolean() {
        return new PreferenceGetterBuilder<Boolean>(new PreferenceGetter<Boolean>() {
            @Override
            public Boolean getPreference(String label) {
                return bot.checkBox(label).isChecked();
            }
        });
    }

    private PreferenceGetterBuilder<Mutators> getSelectedMutators() {
        return new PreferenceGetterBuilder<Mutators>(new PreferenceGetter<Mutators>() {
            @Override
            public Mutators getPreference(String label) {
                expandPitMutatorPreferences();
                for (Mutators mutator : Mutators.getMainGroup()) {
                    if (bot.radio(mutator.getDescriptor()).isSelected()) {
                        return mutator;
                    }
                }
                return Mutators.DEFAULTS;
            }
        });
    }

    private PreferenceGetterBuilder<PitExecutionMode> getSelectedExecutionMode() {
        return new PreferenceGetterBuilder<PitExecutionMode>(new PreferenceGetter<PitExecutionMode>() {
            @Override
            public PitExecutionMode getPreference(String label) {
                return getActiveExecutionMode();
            }
        });
    }

    public int getTimeout() {
        return getInteger().from(TIMEOUT_LABEL);
    }

    public BigDecimal getPitTimeoutFactor() {
        return getBigDecimal().from(TIMEOUT_FACTOR_LABEL);
    }
}
