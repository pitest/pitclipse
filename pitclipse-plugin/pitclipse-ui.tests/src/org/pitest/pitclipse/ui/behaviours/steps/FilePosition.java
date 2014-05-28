package org.pitest.pitclipse.ui.behaviours.steps;


public class FilePosition {

	public final String className;
	public final int lineNumber;

	private FilePosition(String className, int lineNumber) {
		this.className = className;
		this.lineNumber = lineNumber;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		public String fileName;
		public int lineNumber;

		public FilePosition build() {
			return new FilePosition(fileName, lineNumber);
		}

		public Builder withFileName(String fileName) {
			this.fileName = fileName;
			return this;
		}

		public Builder withLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
			return this;
		}
	}
}
