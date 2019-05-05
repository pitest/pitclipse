package org.pitest.pitclipse.ui.behaviours.pageobjects;

import static java.math.BigDecimal.ZERO;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.SwtBotTreeHelper.selectAndExpand;

import java.io.Closeable;
import java.math.BigDecimal;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.core.PitMutators;
import org.pitest.pitclipse.runner.config.PitExecutionMode;
import com.google.common.base.Optional;

public class PitPreferenceSelector implements Closeable {

    private static final String USE_INCREMENTAL_ANALYSIS_LABEL = "Use incremental analysis";
    private static final String MUTATION_TESTS_RUN_IN_PARALLEL_LABEL = "Mutation tests run in parallel";
    private static final String EXCLUDED_CLASSES_LABEL = "Excluded classes (e.g.*IntTest)";
    private static final String EXCLUDED_METHODS_LABEL = "Excluded methods (e.g.*toString*)";
    private static final String AVOID_CALLS_TO_LABEL = "Avoid calls to";
    private static final String MUTATORS_LABEL = "Mutators";
    private static final String EXECUTION_MODE_LABEL = "Pit Execution Scope";
    private static final String PIT_TIMEOUT_LABEL = "Pit Timeout";
    private static final String PIT_TIMEOUT_FACTOR_LABEL = "Timeout Factor";
    private final SWTWorkbenchBot bot;

    public PitPreferenceSelector(SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    public void setPitExecutionMode(PitExecutionMode mode) {
        activatePreferenceShell();
        expandPitPreferences();
        selectExecutionMode(mode);
        close();
    }

    private void selectExecutionMode(PitExecutionMode mode) {
        // The workaround for Eclipse bug 344484.didn't seem to work here
        // so for now we'll set the property directly. We have assertions
        // on reading back the property which should suffice
        PitCoreActivator.getDefault().setExecutionMode(mode);
    }

    @Override
    public void close() {
        bot.button("OK").click();
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
        return getBoolean().from(MUTATION_TESTS_RUN_IN_PARALLEL_LABEL);
    }

    public boolean isIncrementalAnalysisEnabled() {
        return getBoolean().from(USE_INCREMENTAL_ANALYSIS_LABEL);
    }

    public void setPitRunInParallel(boolean inParallel) {
        setSelectionFor(MUTATION_TESTS_RUN_IN_PARALLEL_LABEL).to(inParallel);
    }

    public void setPitIncrementalAnalysisEnabled(boolean incremental) {
        setSelectionFor(USE_INCREMENTAL_ANALYSIS_LABEL).to(incremental);
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

    public PitMutators getMutators() {
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
        setTextFor(PIT_TIMEOUT_LABEL).to(timeout);
    }

    public void setPitTimeoutFactor(int factor) {
        setTextFor(PIT_TIMEOUT_FACTOR_LABEL).to(factor);
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
            try {
                expandPitPreferences();
                return getter.getPreference(label);
            } finally {
                close();
            }
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
                    if (value)
                        bot.checkBox(label).select();
                    else
                        bot.checkBox(label).deselect();
                }
            });
        }

        public void to(final int value) {
            to(Integer.toString(value));
        }

        private <T> void updatePreference(PreferenceSetter<T> s) {
            activatePreferenceShell();
            try {
                expandPitPreferences();
                s.setPreference();
            } finally {
                close();
            }
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

    private PreferenceGetterBuilder<PitMutators> getSelectedMutators() {
        return new PreferenceGetterBuilder<PitMutators>(new PreferenceGetter<PitMutators>() {
            @Override
            public PitMutators getPreference(String label) {
                expandPitMutatorPreferences();
                for (PitMutators mutator : PitMutators.values()) {
                    if (bot.radio(mutator.getLabel()).isSelected()) {
                        return mutator;
                    }
                }
                return PitMutators.DEFAULTS;
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
        return getInteger().from(PIT_TIMEOUT_LABEL);
    }

    public BigDecimal getPitTimeoutFactor() {
        return getBigDecimal().from(PIT_TIMEOUT_FACTOR_LABEL);
    }
}
