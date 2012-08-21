package org.pitest.pitclipse.ui.swtbot;

import static org.eclipse.core.resources.IResourceChangeEvent.POST_BUILD;

import java.io.Closeable;
import java.io.IOException;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

public class WaitForBuildCondition extends DefaultCondition implements
		Closeable {

	private volatile boolean completed = false;
	private IResourceChangeListener listener = null;

	public WaitForBuildCondition() {
	}

	public void subscribe() {
		listener = new BuiltResourceChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener,
				POST_BUILD);
	}

	public boolean test() throws Exception {
		return completed;
	}

	public String getFailureMessage() {
		return "Unable to determine if build completed.";
	}

	public void close() throws IOException {
		unsubscribe();
	}

	public void unsubscribe() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
	}

	private class BuiltResourceChangeListener implements
			IResourceChangeListener {
		public void resourceChanged(IResourceChangeEvent event) {
			completed = true;
		}
	}

}
