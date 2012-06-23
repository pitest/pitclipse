package org.pitest.pitclipse.pitrunner;

import java.io.File;

import javax.annotation.concurrent.ThreadSafe;

import org.pitest.mutationtest.MutationCoverageReport;
import org.pitest.pitclipse.pitrunner.PITResults.PITResultsBuilder;

@ThreadSafe
public class PITRunner {
	public PITResults runPIT(PITOptions options) {
		MutationCoverageReport.main(options.toCLIArgs());
		File reportDir = options.getReportDirectory();
		File resultFile = findResultFile(reportDir);
		return new PITResultsBuilder().withXmlResults(resultFile).build() ;
	}

	private File findResultFile(File reportDir) {
		for (File file : reportDir.listFiles()) {
			if ("index.html".equals(file.getName())) {
				return file;
			}
			if (file.isDirectory()) {
				File result = findResultFile(file);
				if (null != result) {
					return result;
				}
			}
		}
		return null;
	}
	
	
}
