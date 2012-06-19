package org.pitest.pitclipse.ui;

import java.io.File;
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
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		Enumeration<URL> jars = context.getBundle().findEntries("lib", "*.jar",
				false);
		Builder<String> builder = ImmutableList.builder();
		while (jars.hasMoreElements()) {
			URL jar = jars.nextElement();
			URI fileUri = FileLocator.toFileURL(jar).toURI();
			builder.add(new File(fileUri).getCanonicalPath());
		}
		setPITClasspath(builder.build());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
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

	public static void warn(String msg, Exception e) {
		log(Status.WARNING, msg, e);
	}
	
	private static void log(int status, String msg, Exception e) {
		getDefault().getLog().log(new Status(status, PLUGIN_ID, Status.OK, msg, e));
	}
}
