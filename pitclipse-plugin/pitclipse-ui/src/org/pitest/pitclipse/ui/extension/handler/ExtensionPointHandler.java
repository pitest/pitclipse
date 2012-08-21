package org.pitest.pitclipse.ui.extension.handler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.pitest.pitclipse.ui.extension.ResultNotifier;

public class ExtensionPointHandler<T> {
	private static final class NotifierRunnable<U> implements Runnable {
		private final U results;
		private final ResultNotifier<U> notifier;

		private NotifierRunnable(ResultNotifier<U> notifier, U results) {
			this.results = results;
			this.notifier = notifier;
		}

		public void run() {
			notifier.handleResults(results);
		}
	}

	private static final String EXTENSION_POINT_ID = "org.pitest.pitclipse.ui.view.PitView";

	public <U> void execute(IExtensionRegistry registry, U results) {
		evaluate(registry, results);
	}

	private <U> void evaluate(IExtensionRegistry registry, final U results) {
		IConfigurationElement[] config = registry
				.getConfigurationElementsFor(EXTENSION_POINT_ID);
		try {
			for (IConfigurationElement e : config) {
				System.out.println("Evaluating extension");
				final Object o = e.createExecutableExtension("class");
				if (o instanceof ResultNotifier) {
					@SuppressWarnings("unchecked")
					final ResultNotifier<U> notifier = (ResultNotifier<U>) o;
					executeExtension(new NotifierRunnable<U>(notifier, results));
				}
			}
		} catch (CoreException ex) {
			System.out.println(ex.getMessage());
		}
	}

	private <U> void executeExtension(final Runnable extension) {
		ISafeRunnable runnable = new ISafeRunnable() {
			public void handleException(Throwable e) {
				System.out.println("Exception in client");
				e.printStackTrace();
			}

			public void run() throws Exception {
				extension.run();
			}
		};
		SafeRunner.run(runnable);
	}

}
