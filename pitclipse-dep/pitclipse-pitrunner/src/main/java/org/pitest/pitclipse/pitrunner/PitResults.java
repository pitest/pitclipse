package org.pitest.pitclipse.pitrunner;

import java.io.File;
import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

import org.pitest.pitclipse.pitrunner.PitOptions.PitLaunchException;

@Immutable
public final class PitResults implements Serializable {

	private static final long serialVersionUID = 5271802933404287709L;
	private final File htmlResultFile;
	private final File xmlResultFile;

	private PitResults(File htmlResultFile, File xmlResultFile) {
		this.htmlResultFile = htmlResultFile;
		this.xmlResultFile = xmlResultFile;
	};

	public File getHtmlResultFile() {
		return htmlResultFile;
	}

	public static final class PitResultsBuilder {
		private File htmlResultFile = null;
		private File xmlResultFile = null;

		public PitResults build() {
			validateResultsFile();
			return new PitResults(htmlResultFile, xmlResultFile);
		}

		public PitResultsBuilder withHtmlResults(File htmlResultFile) {
			this.htmlResultFile = new File(htmlResultFile.getPath());
			return this;
		}

		public PitResultsBuilder withXmlResults(File xmlResultFile) {
			this.xmlResultFile = new File(xmlResultFile.getPath());
			return this;
		}

		private void validateResultsFile() {
			if (null == htmlResultFile) {
				throw new PitLaunchException("PIT HTML Result File not set");
			}
			if (null == xmlResultFile) {
				throw new PitLaunchException("PIT XML Result File not set");
			}
			if (!htmlResultFile.exists()) {
				throw new PitLaunchException("File does not exist: "
						+ htmlResultFile);
			}
			if (!xmlResultFile.exists()) {
				throw new PitLaunchException("File does not exist: "
						+ xmlResultFile);
			}
		}
	}

	@Override
	public String toString() {
		return "PitResults [htmlResultFile=" + htmlResultFile
				+ ", xmlResultFile=" + xmlResultFile + "]";
	}

}
