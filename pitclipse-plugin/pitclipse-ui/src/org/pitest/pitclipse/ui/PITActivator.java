package org.pitest.pitclipse.ui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * The activator class controls the plug-in life cycle
 */
public class PITActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "pitclipse-ui"; //$NON-NLS-1$

	// The shared instance
	private static PITActivator plugin;

	private static List<String> pitClasspath = ImmutableList.of();

	public PITActivator() {
	}

	public static List<String> getPITClasspath() {
		return pitClasspath;
	}

	private static void setPITClasspath(List<String> classpath) {
		pitClasspath = ImmutableList.copyOf(classpath);
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
		Builder<String> builder = ImmutableList.builder();
		while (jars.hasMoreElements()) {
			URL jar = jars.nextElement();

			URI fileUri = locateAndEscapeUrl(jar).toURI();
			File jarFile = new File(fileUri);
			builder.add(jarFile.getCanonicalPath());
		}
		setPITClasspath(builder.build());
	}

	private URL locateAndEscapeUrl(URL url) throws IOException {
		// Nasty hack thanks to the following 6 year old bug in Eclipse
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=145096
		// Astonishingly, the reason given for not fixing is that many plugins
		// expect invalid Urls so would be broken!!!
		URL unescapedUrl = FileLocator.toFileURL(url);
		String escaped = unescapedUrl.getPath().replace(" ", "%20");
		return new URL("file", "", escaped);
	}

	private static void setActivator(PITActivator pitActivator) {
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
		setActivator(null);
		setPITClasspath(ImmutableList.<String> of());
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static PITActivator getDefault() {
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
}
