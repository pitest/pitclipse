package org.pitest.pitclipse.pitrunner;

import java.io.File;

import javax.annotation.concurrent.Immutable;

import org.pitest.pitclipse.pitrunner.PITOptions.PITLaunchException;

@Immutable
public class PITResults {
	//	Placeholder
	private PITResults() {} ;
	
	public static final class PITResultsBuilder {
		private File resultFile = null;

		public PITResults build() {
			validateResultsFile();
			System.out.println(resultFile.getPath());
			//validateResultsFile
			return new PITResults() ;
		}
		public PITResultsBuilder withXmlResults(File resultFile) {
			this.resultFile = new File(resultFile.getPath());
			return this;
		}
		
		private void validateResultsFile() {
			if (null == resultFile) {
				throw new PITLaunchException("PIT Result File not set");
			}
			if (!resultFile.exists()) {
				throw new PITLaunchException("File does not exist: "
						+ resultFile);
			}
		}
	}
}
