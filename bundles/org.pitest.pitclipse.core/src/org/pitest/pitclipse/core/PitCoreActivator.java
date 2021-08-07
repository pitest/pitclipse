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

import static org.eclipse.core.runtime.FileLocator.getBundleFile;
import static org.pitest.pitclipse.core.preferences.PitPreferences.AVOID_CALLS_TO;
import static org.pitest.pitclipse.core.preferences.PitPreferences.EXCLUDED_CLASSES;
import static org.pitest.pitclipse.core.preferences.PitPreferences.EXCLUDED_METHODS;
import static org.pitest.pitclipse.core.preferences.PitPreferences.EXECUTION_MODE;
import static org.pitest.pitclipse.core.preferences.PitPreferences.INCREMENTAL_ANALYSIS;
import static org.pitest.pitclipse.core.preferences.PitPreferences.INDIVIDUAL_MUTATORS;
import static org.pitest.pitclipse.core.preferences.PitPreferences.MUTATOR_GROUP;
import static org.pitest.pitclipse.core.preferences.PitPreferences.RUN_IN_PARALLEL;
import static org.pitest.pitclipse.core.preferences.PitPreferences.TIMEOUT;
import static org.pitest.pitclipse.core.preferences.PitPreferences.TIMEOUT_FACTOR;
import static org.pitest.pitclipse.runner.util.PitFileUtils.createParentDirs;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;
import org.pitest.pitclipse.runner.config.PitConfiguration;
import org.pitest.pitclipse.runner.config.PitExecutionMode;

/**
 * The activator class controls the plug-in life cycle
 */
public class PitCoreActivator extends Plugin {

    /**
     * Where Eclipse and Maven will generate .class files
     */
    private static final String BUILD_OUTPUT_DIR = "target/classes";

    /**
     * Where Maven JAR are located in our projects
     */
    private static final String JAR_DIR = "lib";

    private static final String ORG_PITEST_JUNIT5_PLUGIN = "org.pitest.pitest-junit5-plugin";
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

    private List<String> pitClasspath = new ArrayList<>();
    
    private List<String> pitestJunit5PluginClasspath = new ArrayList<>();

    private File resultDir;

    private File historyFile;

    public List<String> getPitClasspath() {
        return pitClasspath;
    }

    private void setPitClasspath(List<String> classpath) {
        pitClasspath = classpath;
    }

    public List<String> getPitestJunit5PluginClasspath() {
        return pitestJunit5PluginClasspath;
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
        plugin = this; // NOSONAR typical in Eclipse
        setActivator(this);
        setupStateDirectories();

        List<String> pitestClasspath = new ArrayList<>();
        pitestClasspath.add(getBundleCanonicalPath(ORG_PITEST));
        addOurBundleToClasspath(pitestClasspath, ORG_PITEST_PITCLIPSE_RUNNER);
        addMavenJarToClasspath(pitestClasspath, ORG_PITEST, "pitest.jar");
        addMavenJarToClasspath(pitestClasspath, ORG_PITEST, "pitest-entry.jar");
        addMavenJarToClasspath(pitestClasspath, ORG_PITEST, "pitest-command-line.jar");
        addMavenJarToClasspath(pitestClasspath, ORG_PITEST, "pitest-html-report.jar");
        pitestClasspath.add(getBundleCanonicalPath("com.google.guava"));

        if (Platform.getBundle(ORG_PITEST_PITCLIPSE_LISTENERS) != null) {
            addOurBundleToClasspath(pitestClasspath, ORG_PITEST_PITCLIPSE_LISTENERS);
        }
        setPitClasspath(pitestClasspath);

        if (Platform.getBundle(ORG_PITEST_JUNIT5_PLUGIN) != null) {
            pitestJunit5PluginClasspath = new ArrayList<>();
            addMavenJarToClasspath(pitestJunit5PluginClasspath, ORG_PITEST_JUNIT5_PLUGIN, "pitest-junit5-plugin.jar");
        }
    }

    private void addMavenJarToClasspath(List<String> classpath, String bundleName, String jarFile) throws IOException {
        classpath.add(getBundleCanonicalPath(bundleName)
               + File.separator + JAR_DIR + File.separator + jarFile);
    }

    private void addOurBundleToClasspath(List<String> classpath, String bundleName) throws IOException {
        classpath.add(
            getBundleCanonicalPath(bundleName));
        classpath.add(
            getBundleCanonicalPath(bundleName)
                + File.separator + BUILD_OUTPUT_DIR);
    }

    private String getBundleCanonicalPath(String bundleName) throws IOException {
        return getBundleFile(Platform.getBundle(bundleName)).getCanonicalPath();
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
            historyFile = new File(createTemporaryDirectory(), STATE_FILE);
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
            resultDir = createTemporaryDirectory();
        }
    }

    private File createTemporaryDirectory() {
        try {
            return Files.createTempDirectory(null).toFile();
        } catch (IOException e1) {
            throw new IllegalStateException("Cannot create temporary directory", e1);
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
        List<String> emptyPath = Collections.emptyList();
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
        log(IStatus.INFO, msg, null);
    }

    private static void log(int status, String msg, Throwable t) {
        getDefault().getLog().log(new Status(status, PLUGIN_ID, IStatus.OK, msg, t));
    }
    
    public static void warn(String msg) {
        warn(msg, null);
    }

    public static void warn(String msg, Throwable t) {
        log(IStatus.WARNING, msg, t);
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
        String executionMode = preferenceStore.getString(EXECUTION_MODE);
        String mutatorGroup = preferenceStore.getString(MUTATOR_GROUP);
        String mutators = preferenceStore.getString(INDIVIDUAL_MUTATORS);
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

        if (mutatorGroup.equals(Mutators.CUSTOM.name())) {
            builder.withMutators(mutators);
        } else {
            builder.withMutators(mutatorGroup);
        }

        return builder.build();
    }

    public void setExecutionMode(PitExecutionMode pitExecutionMode) {
        getPreferenceStore().setValue(EXECUTION_MODE, pitExecutionMode.getId());
    }

    public void setDefaultMutatorGroup(Mutators mutators) {
        getPreferenceStore().setValue(MUTATOR_GROUP, mutators.name());
    }

    public String getDefaultMutatorGroup() {
        return getPreferenceStore().getString(MUTATOR_GROUP);
    }
}
