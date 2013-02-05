package org.pitest.pitclipse.core;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.io.Files.createParentDirs;
import static com.google.common.io.Files.createTempDir;
import static org.eclipse.core.runtime.FileLocator.getBundleFile;
import static org.eclipse.core.runtime.FileLocator.toFileURL;
import static org.pitest.pitclipse.core.PitExecutionMode.values;
import static org.pitest.pitclipse.core.preferences.PreferenceConstants.P_BOOLEAN;
import static org.pitest.pitclipse.core.preferences.PreferenceConstants.P_CHOICE;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.common.collect.ImmutableList;

/**
 * The activator class controls the plug-in life cycle
 */
public class PitCoreActivator extends AbstractUIPlugin {

	private static final String HTML_RESULTS_DIR = "html_results";
	private static final String HTML_FILE = "index.html";
	private static final String STATE_FILE = "state.out";
	private static final String HISTORY_DIR = "history";

	// The plug-in ID
	public static final String PLUGIN_ID = "org.pitest.pitclipse.core"; //$NON-NLS-1$

	// The shared instance
	private static PitCoreActivator plugin;

	private List<String> pitClasspath = of();

	private File resultDir;

	private File historyFile;

	public PitCoreActivator() {
	}

	public List<String> getPitClasspath() {
		return copyOf(pitClasspath);
	}

	private void setPITClasspath(List<String> classpath) {
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
		Enumeration<URL> jars = context.getBundle().findEntries("lib", "*.jar",
				false);
		setupStateDirectories();
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		builder.add(getBundleFile(Platform.getBundle("org.pitest.osgi"))
				.getCanonicalPath());
		builder.add(getBundleFile(
				Platform.getBundle("org.pitest.pitclipse-pitrunner"))
				.getCanonicalPath());
		builder.add(getBundleFile(Platform.getBundle("com.google.guava"))
				.getCanonicalPath());
		while (jars.hasMoreElements()) {
			URL jar = jars.nextElement();

			URI fileUri = locateAndEscapeUrl(jar).toURI();
			File jarFile = new File(fileUri);
			builder.add(jarFile.getCanonicalPath());
		}
		setPITClasspath(builder.build());
	}

	private void setupStateDirectories() {
		setupResultDir();
		setupHistoryFile();
	}

	private void setupHistoryFile() {
		IPath pluginLocation = getStateLocation();
		File stateFile = pluginLocation.append(HISTORY_DIR).append(STATE_FILE)
				.toFile();
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
		File stateFile = pluginLocation.append(HTML_RESULTS_DIR)
				.append(HTML_FILE).toFile();
		try {
			createParentDirs(stateFile);
			resultDir = stateFile.getParentFile();
		} catch (IOException e) {
			// Cannot write to workspace.
			// Probably shouldn't happen but lets use a temp dir instead
			resultDir = createTempDir();
		}
	}

	private URL locateAndEscapeUrl(URL url) throws IOException {
		// Nasty hack thanks to the following 6 year old bug in Eclipse
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=145096
		// Astonishingly, the reason given for not fixing is that many plugins
		// expect invalid Urls so would be broken!!!
		URL unescapedUrl = toFileURL(url);
		String escaped = unescapedUrl.getPath().replace(" ", "%20");
		return new URL("file", "", escaped);
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
		setPITClasspath(emptyPath);
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

	public static void warn(String msg) {
		warn(msg, null);
	}

	public static void warn(String msg, Throwable t) {
		log(Status.WARNING, msg, t);
	}

	private static void log(int status, String msg, Throwable t) {
		getDefault().getLog().log(
				new Status(status, PLUGIN_ID, Status.OK, msg, t));
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
		String executionMode = getPreferenceStore().getString(P_CHOICE);
		boolean parallelRun = getPreferenceStore().getBoolean(P_BOOLEAN);
		PitConfiguration.Builder builder = PitConfiguration.builder()
				.withParallelExecution(parallelRun);
		for (PitExecutionMode pitExecutionMode : values()) {
			if (pitExecutionMode.getId().equals(executionMode)) {
				builder.withExecutionMode(pitExecutionMode).build();
				break;
			}
		}

		return builder.build();
	}

	public void setExecutionMode(PitExecutionMode pitExecutionMode) {
		getPreferenceStore().setValue(P_CHOICE, pitExecutionMode.getId());
	}
}
