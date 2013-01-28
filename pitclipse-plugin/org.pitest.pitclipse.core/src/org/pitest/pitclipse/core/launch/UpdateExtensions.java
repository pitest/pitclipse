package org.pitest.pitclipse.core.launch;

import java.io.File;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.pitest.pitclipse.core.extension.handler.ExtensionPointHandler;
import org.pitest.pitclipse.core.extension.point.PitCoreResults;

public class UpdateExtensions implements Runnable {
	private static final String EXTENSION_POINT_ID = "org.pitest.pitclipse.core.results";

	private final File reportDirectory;

	public UpdateExtensions(File reportDirectory) {
		this.reportDirectory = new File(reportDirectory.toURI());
	}

	public void run() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		PitCoreResults results = new PitCoreResults(reportDirectory.toURI());
		new ExtensionPointHandler<PitCoreResults>(EXTENSION_POINT_ID).execute(
				registry, results);
	}
}