package org.pitest.pitclipse.pitrunner;

import java.io.File;
import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

import org.pitest.pitclipse.pitrunner.PitOptions.PitLaunchException;

@Immutable
public final class PitResults implements Serializable {

	private static final long serialVersionUID = 5271802933404287709L;
	private final File resultFile;

	private PitResults(File resultFile) {
		this.resultFile = resultFile;
	};

	public File getResultFile() {
		return resultFile;
	}

	public static final class PitResultsBuilder {
		private File resultFile = null;

		public PitResults build() {
			validateResultsFile();
			return new PitResults(resultFile);
		}

		public PitResultsBuilder withResults(File resultFile) {
			this.resultFile = new File(resultFile.getPath());
			return this;
		}

		private void validateResultsFile() {
			if (null == resultFile) {
				throw new PitLaunchException("PIT Result File not set");
			}
			if (!resultFile.exists()) {
				throw new PitLaunchException("File does not exist: "
						+ resultFile);
			}
		}
	}

	@Override
	public String toString() {
		return "PitResults [resultFile=" + resultFile + "]";
	}

}
