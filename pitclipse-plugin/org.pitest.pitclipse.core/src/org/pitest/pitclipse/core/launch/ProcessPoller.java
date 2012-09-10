package org.pitest.pitclipse.core.launch;

import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.ImmutableList;

public final class ProcessPoller implements Runnable {
	private static final long POLL_FREQUENCY = 50l;
	private final ImmutableList<IProcess> processes;
	
	private final Runnable update;

	public ProcessPoller(ImmutableList<IProcess> processes, Runnable update) {
		this.processes = processes;
		this.update = update;
	}

	public void run() {
		waitForCompletion();
		Display.getDefault().asyncExec(update);
	}

	private void waitForCompletion() {
		while (!isCompleted(processes)) {
			try {
				Thread.sleep(POLL_FREQUENCY);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private boolean isCompleted(List<IProcess> processes) {
		boolean completed = true;
		for (IProcess process : processes) {
			completed &= isCompleted(process);
		}
		return completed;
	}

	private boolean isCompleted(IProcess process) {
		try {
			process.getExitValue();
			return true;
		} catch (DebugException e) {
			return false;
		}
	}
}