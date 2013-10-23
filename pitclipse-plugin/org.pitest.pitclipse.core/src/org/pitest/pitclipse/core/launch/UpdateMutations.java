package org.pitest.pitclipse.core.launch;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.pitest.pitclipse.core.extension.handler.ExtensionPointHandler;
import org.pitest.pitclipse.pitrunner.model.MutationsModel;

public class UpdateMutations implements Runnable {
	private static final String EXTENSION_POINT_ID = "org.pitest.pitclipse.core.mutations.results";

	private final MutationsModel model;

	public UpdateMutations(MutationsModel model) {
		this.model = model;
	}

	@Override
	public void run() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		new ExtensionPointHandler<MutationsModel>(EXTENSION_POINT_ID).execute(registry, model);
	}
}