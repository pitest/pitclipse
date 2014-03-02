package org.pitest.pitclipse.core.extension.handler;

import static org.pitest.pitclipse.core.PitCoreActivator.warn;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;

public class ExtensionPointHandler<T> {
	private final String extensionPointId;

	public ExtensionPointHandler(String extensionPointId) {
		this.extensionPointId = extensionPointId;
	}

	public <U> void execute(IExtensionRegistry registry, U results) {
		evaluate(registry, results);
	}

	private <U> void evaluate(IExtensionRegistry registry, final U results) {
		IConfigurationElement[] config = registry.getConfigurationElementsFor(extensionPointId);
		try {
			for (IConfigurationElement e : config) {
				Object o = e.createExecutableExtension("class");
				if (o instanceof ResultNotifier) {
					@SuppressWarnings("unchecked")
					final ResultNotifier<U> notifier = (ResultNotifier<U>) o;
					executeExtension(new NotifierRunnable<U>(notifier, results));
				}
			}
		} catch (CoreException ex) {
			warn("Error thrown notifying results", ex);
		}
	}

	private <U> void executeExtension(final Runnable extension) {
		ISafeRunnable runnable = new ISafeRunnable() {
			@Override
			public void handleException(Throwable e) {
				warn("Exception in client", e);
			}

			@Override
			public void run() throws Exception {
				extension.run();
			}
		};
		SafeRunner.run(runnable);
	}

}
