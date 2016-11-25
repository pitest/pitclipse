package org.pitest.pitclipse.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.pitest.pitclipse.pitrunner.config.PitConfiguration;
import org.pitest.pitclipse.pitrunner.config.PitExecutionMode;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.eclipse.core.runtime.FileLocator.getBundleFile;
import static org.pitest.pitclipse.core.preferences.PitMutatorsPreferencePage.PIT_MUTATORS;
import static org.pitest.pitclipse.core.preferences.PitPreferencePage.AVOID_CALLS_TO;
import static org.pitest.pitclipse.core.preferences.PitPreferencePage.EXCLUDED_CLASSES;
import static org.pitest.pitclipse.core.preferences.PitPreferencePage.EXCLUDED_METHODS;
import static org.pitest.pitclipse.core.preferences.PitPreferencePage.INCREMENTAL_ANALYSIS;
import static org.pitest.pitclipse.core.preferences.PitPreferencePage.PIT_EXECUTION_MODE;
import static org.pitest.pitclipse.core.preferences.PitPreferencePage.RUN_IN_PARALLEL;
import static org.pitest.pitclipse.core.preferences.PitPreferencePage.TIMEOUT;
import static org.pitest.pitclipse.core.preferences.PitPreferencePage.TIMEOUT_FACTOR;
import static org.pitest.pitclipse.reloc.guava.collect.ImmutableList.copyOf;
import static org.pitest.pitclipse.reloc.guava.collect.ImmutableList.of;
import static org.pitest.pitclipse.reloc.guava.io.Files.createParentDirs;
import static org.pitest.pitclipse.reloc.guava.io.Files.createTempDir;

/**
 * The activator class controls the plug-in life cycle
 */
public class PitCoreActivator extends AbstractUIPlugin {

    private static final String HTML_RESULTS_DIR = "html_results";
    private static final String HTML_FILE = "index.html";
    private static final String STATE_FILE = "state-1.1.0.out";
    private static final String HISTORY_DIR = "history";

    // The plug-in ID
    public static final String PLUGIN_ID = "org.pitest.pitclipse.core"; //$NON-NLS-1$

    // The shared instance
    private static PitCoreActivator plugin;

    private ImmutableList<String> pitClasspath = of();

    private File resultDir;

    private File historyFile;

    public List<String> getPitClasspath() {
        return pitClasspath;
    }

    private void setPitClasspath(List<String> classpath) {
        pitClasspath = copyOf(classpath);
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
        setActivator(this);
        setupStateDirectories();
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        builder.add(getBundleFile(Platform.getBundle("org.pitest.command-line-osgi")).getCanonicalPath());
        builder.add(getBundleFile(Platform.getBundle("org.pitest.html-report-osgi")).getCanonicalPath());
        builder.add(getBundleFile(Platform.getBundle("org.pitest.osgi")).getCanonicalPath());
        builder.add(getBundleFile(Platform.getBundle("org.pitest.pitrunner")).getCanonicalPath());
        builder.add(getBundleFile(Platform.getBundle("org.pitest.guava-shade-osgi")).getCanonicalPath());
        setPitClasspath(builder.build());
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

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
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

    public static Shell getActiveWorkbenchShell() {
        IWorkbenchWindow workBenchWindow = getActiveWorkbenchWindow();
        if (workBenchWindow == null) {
            return null;
        }
        return workBenchWindow.getShell();
    }

    /**
     * Returns the active workbench window
     * 
     * @return the active workbench window
     */
    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        if (plugin == null) {
            return null;
        }
        IWorkbench workBench = plugin.getWorkbench();
        if (workBench == null) {
            return null;
        }
        return workBench.getActiveWorkbenchWindow();
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
