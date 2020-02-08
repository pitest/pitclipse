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

package org.pitest.pitclipse.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;
import org.pitest.pitclipse.runner.config.PitConfiguration;
import org.pitest.pitclipse.runner.config.PitExecutionMode;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.io.Files.createParentDirs;
import static com.google.common.io.Files.createTempDir;
import static org.eclipse.core.runtime.FileLocator.getBundleFile;
import static org.pitest.pitclipse.core.preferences.PitPreferences.AVOID_CALLS_TO;
import static org.pitest.pitclipse.core.preferences.PitPreferences.EXCLUDED_CLASSES;
import static org.pitest.pitclipse.core.preferences.PitPreferences.EXCLUDED_METHODS;
import static org.pitest.pitclipse.core.preferences.PitPreferences.INCREMENTAL_ANALYSIS;
import static org.pitest.pitclipse.core.preferences.PitPreferences.PIT_EXECUTION_MODE;
import static org.pitest.pitclipse.core.preferences.PitPreferences.PIT_MUTATORS;
import static org.pitest.pitclipse.core.preferences.PitPreferences.RUN_IN_PARALLEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.TIMEOUT;
import static org.pitest.pitclipse.core.preferences.PitPreferences.TIMEOUT_FACTOR;

/**
 * The activator class controls the plug-in life cycle
 */
public class PitCoreActivator extends Plugin {

    private static final String ORG_PITEST_PITCLIPSE_LISTENERS = "org.pitest.pitclipse.listeners";
    private static final String ORG_PITEST_PITCLIPSE_RUNNER = "org.pitest.pitclipse.runner";
    private static final String ORG_PITEST = "org.pitest";
    private static final String HTML_RESULTS_DIR = "html_results";
    private static final String HTML_FILE = "index.html";
    private static final String STATE_FILE = "state-1.1.0.out";
    private static final String HISTORY_DIR = "history";
    
    // The plug-in ID
    public static final String PLUGIN_ID = "org.pitest.pitclipse.core"; //$NON-NLS-1$

    // The shared instance
    private static PitCoreActivator plugin;
    
    private IPreferenceStore preferences;

    private ImmutableList<String> pitClasspath = of();

    private File resultDir;

    private File historyFile;

    public List<String> getPitClasspath() {
        return pitClasspath;
    }

    private void setPitClasspath(List<String> classpath) {
        pitClasspath = copyOf(classpath);
    }
    
    public IPreferenceStore getPreferenceStore() {
        if (preferences == null) {
            preferences = new ScopedPreferenceStore(InstanceScope.INSTANCE, PLUGIN_ID);
        }
        return preferences;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext context) throws Exception { // NOPMD - Base
                                                                // class defines
                                                                // signature
        super.start(context);
        plugin = this;
        setActivator(this);
        setupStateDirectories();

        final String jarDir = "lib";
        ImmutableList<String> pitestClasspath = ImmutableList.of(
            getBundleFile(Platform.getBundle(ORG_PITEST)).getCanonicalPath(),
            getBundleFile(Platform.getBundle(ORG_PITEST_PITCLIPSE_RUNNER)).getCanonicalPath(),
            getBundleFile(Platform.getBundle(ORG_PITEST_PITCLIPSE_RUNNER)).getCanonicalPath() + File.separator + "bin",
            getBundleFile(Platform.getBundle(ORG_PITEST)).getCanonicalPath()
               + File.separator + jarDir + File.separator + "pitest.jar",
            getBundleFile(Platform.getBundle(ORG_PITEST)).getCanonicalPath()
               + File.separator + jarDir + File.separator + "pitest-entry.jar",
            getBundleFile(Platform.getBundle(ORG_PITEST)).getCanonicalPath()
               + File.separator + jarDir + File.separator + "pitest-command-line.jar",
            getBundleFile(Platform.getBundle(ORG_PITEST)).getCanonicalPath()
               + File.separator + jarDir + File.separator + "pitest-html-report.jar",
            getBundleFile(Platform.getBundle("com.google.guava")).getCanonicalPath()
        );
        Builder<String> pitclipseClasspath = ImmutableList.<String>builder().addAll(pitestClasspath);
        
        if (Platform.getBundle(ORG_PITEST_PITCLIPSE_LISTENERS) != null) {
            pitclipseClasspath.add(getBundleFile(Platform.getBundle(ORG_PITEST_PITCLIPSE_LISTENERS)).getCanonicalPath());
            pitclipseClasspath.add(getBundleFile(Platform.getBundle(ORG_PITEST_PITCLIPSE_LISTENERS)).getCanonicalPath() + File.separator + "bin");
        }
        setPitClasspath(pitclipseClasspath.build());
    }

    private void setupStateDirectories() {
        setupResultDir();
        setupHistoryFile();
    }
    
    private void setupHistoryFile() {
        IPath pluginLocation = getStateLocation();
        File stateFile = pluginLocation.append(HISTORY_DIR).append(STATE_FILE).toFile();
        try {
            createParentDirs(stateFile);
            historyFile = stateFile;
        } catch (IOException e) {
            // Cannot write to workspace.
            // Probably shouldn't happen but lets use a temp file instead
            historyFile = new File(createTempDir(), STATE_FILE);
        }
    }

    private void setupResultDir() {
        IPath pluginLocation = getStateLocation();
        File stateFile = pluginLocation.append(HTML_RESULTS_DIR).append(HTML_FILE).toFile();
        try {
            createParentDirs(stateFile);
            resultDir = stateFile.getParentFile();
        } catch (IOException e) {
            // Cannot write to workspace.
            // Probably shouldn't happen but lets use a temp dir instead
            resultDir = createTempDir();
        }
    }


    private static void setActivator(PitCoreActivator pitActivator) {
        plugin = pitActivator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(BundleContext context) throws Exception { // NOPMD - Base
                                                                // class defines
                                                                // signature
        List<String> emptyPath = of();
        setPitClasspath(emptyPath);
        setActivator(null);
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static PitCoreActivator getDefault() {
        return plugin;
    }

    public static void log(String msg) {
        log(Status.INFO, msg, null);
    }

    private static void log(int status, String msg, Throwable t) {
        getDefault().getLog().log(new Status(status, PLUGIN_ID, Status.OK, msg, t));
    }
    
    public static void warn(String msg) {
        warn(msg, null);
    }

    public static void warn(String msg, Throwable t) {
        log(Status.WARNING, msg, t);
    }

    public File emptyResultDir() {
        recursiveClean(resultDir);
        return resultDir;
    }

    private void recursiveClean(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                recursiveClean(file);
            }
            if (!file.delete()) {
                file.deleteOnExit();
            }
        }
    }

    public File getHistoryFile() {
        return historyFile;
    }

    public PitConfiguration getConfiguration() {
        IPreferenceStore preferenceStore = getPreferenceStore();
        String executionMode = preferenceStore.getString(PIT_EXECUTION_MODE);
        String mutators = preferenceStore.getString(PIT_MUTATORS);
        boolean parallelRun = preferenceStore.getBoolean(RUN_IN_PARALLEL);
        boolean incrementalAnalysis = preferenceStore.getBoolean(INCREMENTAL_ANALYSIS);
        String excludedClasses = preferenceStore.getString(EXCLUDED_CLASSES);
        String excludedMethods = preferenceStore.getString(EXCLUDED_METHODS);
        String avoidCallsTo = preferenceStore.getString(AVOID_CALLS_TO);
        String timeout = preferenceStore.getString(TIMEOUT);
        String timeoutFactor = preferenceStore.getString(TIMEOUT_FACTOR);
        PitConfiguration.Builder builder = PitConfiguration.builder().withParallelExecution(parallelRun)
                .withIncrementalAnalysis(incrementalAnalysis).withExcludedClasses(excludedClasses)
                .withExcludedMethods(excludedMethods).withAvoidCallsTo(avoidCallsTo);
        try {
            builder.withTimeout(Integer.valueOf(timeout));
            builder.withTimeoutFactor(new BigDecimal(timeoutFactor));
        } catch (NumberFormatException ignoreMe) {
            // defaults will be used
        }

        for (PitExecutionMode pitExecutionMode : PitExecutionMode.values()) {
            if (pitExecutionMode.getId().equals(executionMode)) {
                builder.withExecutionMode(pitExecutionMode);
                break;
            }
        }
        for (PitMutators mutatorMode : PitMutators.values()) {
            if (mutatorMode.getId().equals(mutators)) {
                builder.withMutators(mutatorMode.toString());
                break;
            }
        }
        return builder.build();
    }

    public void setExecutionMode(PitExecutionMode pitExecutionMode) {
        getPreferenceStore().setValue(PIT_EXECUTION_MODE, pitExecutionMode.getId());
    }

    public void setMutators(PitMutators mutators) {
        getPreferenceStore().setValue(PIT_MUTATORS, mutators.getId());
    }
}
