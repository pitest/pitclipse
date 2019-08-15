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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.core.PitMutators;
import org.pitest.pitclipse.runner.config.PitExecutionMode;

import java.math.BigDecimal;

public class WindowsMenu {

    private static final String WINDOWS = "Window";
    private static final String OPEN_PERSPECTIVES = "Open Perspective";
    private static final String OTHER = "Other...";
    private static final String PREFERENCES = "Preferences";
    private static final String SHOW_VIEW = "Show View";
    private SWTWorkbenchBot bot;
    private final PerspectiveSelector perspectiveSelector;
    private final PitPreferenceSelector preferenceSelector;
    private final ViewSelector viewSelector;

    public WindowsMenu(SWTWorkbenchBot bot) {
        this.bot = bot;
        perspectiveSelector = new PerspectiveSelector(bot);
        preferenceSelector = new PitPreferenceSelector(bot);
        viewSelector = new ViewSelector(bot);
    }

    public void openJavaPerspective() {
        bot.menu(WINDOWS)
           .menu("Perspective")
           .menu(OPEN_PERSPECTIVES)
           .menu(OTHER)
           .click();
        perspectiveSelector.selectPerspective("Java");
//        for (IPerspectiveDescriptor descriptor : PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives()) {
//            System.out.println(descriptor.getLabel() + " -- " + descriptor.getId());
//        }
//        IPerspectiveDescriptor javaPerspective = PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.eclipse.jdt.ui.JavaPerspective");
//        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(javaPerspective);
    }

    public void setPitExecutionMode(PitExecutionMode mode) {
        selectExecutionMode(mode);
    }

    public PitExecutionMode getPitExecutionMode() {
        openPreferences();
        return preferenceSelector.getPitExecutionMode();
    }

    private void selectExecutionMode(PitExecutionMode mode) {
        // The workaround for Eclipse bug 344484.didn't seem to work here
        // so for now we'll set the property directly. We have assertions
        // on reading back the property which should suffice
        PitCoreActivator.getDefault().setExecutionMode(mode);
    }

    public boolean isPitRunInParallel() {
        openPreferences();
        return preferenceSelector.isPitRunInParallel();
    }

    public void setPitRunInParallel(boolean inParallel) {
        openPreferences();
        preferenceSelector.setPitRunInParallel(inParallel);
    }

    public boolean isIncrementalAnalysisEnabled() {
        openPreferences();
        return preferenceSelector.isIncrementalAnalysisEnabled();
    }

    public void setIncrementalAnalysisEnabled(boolean incremental) {
        openPreferences();
        preferenceSelector.setPitIncrementalAnalysisEnabled(incremental);
    }

    public String getExcludedClasses() {
        openPreferences();
        return preferenceSelector.getExcludedClasses();
    }

    private PreferenceDsl openPreferences() {
        bot.menu(WINDOWS).menu(PREFERENCES).click();
        return new PreferenceDsl();
    }

    public void setExcludedClasses(String excludedClasses) {
        openPreferences().andThen().setExcludedClasses(excludedClasses);
    }

    public String getExcludedMethods() {
        return openPreferences().andThen().getExcludedMethods();
    }

    public void setExcludedMethods(String excludedMethods) {
        openPreferences().andThen().setExcludedMethods(excludedMethods);
    }

    public String getAvoidCallsTo() {
        return openPreferences().andThen().getAvoidCallsTo();
    }

    public void setAvoidCallsTo(String avoidCallsTo) {
        openPreferences().andThen().setAvoidCallsTo(avoidCallsTo);
    }

    public void openPitSummaryView() {
        bot.menu(WINDOWS).menu(SHOW_VIEW).menu(OTHER).click();
        viewSelector.selectView("PIT", "PIT Summary");
    }

    public void openPitMutationsView() {
        bot.menu(WINDOWS).menu(SHOW_VIEW).menu(OTHER).click();
        viewSelector.selectView("PIT", "PIT Mutations");
    }

    public PitMutators getMutators() {
        return openPreferences().andThen().getMutators();
    }

    public void setMutators(PitMutators mutators) {
        PitCoreActivator.getDefault().setMutators(mutators);
    }

    public void setTimeoutConstant(int timeout) {
        openPreferences().andThen().setPitTimeoutConst(timeout);
    }

    public void setTimeoutFactor(int factor) {
        openPreferences().andThen().setPitTimeoutFactor(factor);
    }

    public int getTimeout() {
        return openPreferences().andThen().getTimeout();
    }

    public BigDecimal getTimeoutFactor() {
        return openPreferences().andThen().getPitTimeoutFactor();
    }

    private class PreferenceDsl {
        public PitPreferenceSelector andThen() {
            return preferenceSelector;
        }
    }
}
