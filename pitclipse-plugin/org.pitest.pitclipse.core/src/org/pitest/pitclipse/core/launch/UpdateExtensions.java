package org.pitest.pitclipse.core.launch;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.pitest.pitclipse.core.extension.handler.ExtensionPointHandler;
import org.pitest.pitclipse.pitrunner.PitResults;

public class UpdateExtensions implements Runnable {
	private static final String EXTENSION_POINT_ID = "org.pitest.pitclipse.core.results";

	private final PitResults results;

	public UpdateExtensions(PitResults results) {
		this.results = results;
	}

	@Override
	public void run() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		new ExtensionPointHandler<PitResults>(EXTENSION_POINT_ID).execute(registry, results);
	}
}