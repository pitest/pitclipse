package org.pitest.pitclipse.pitrunner;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Integer.valueOf;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import org.pitest.mutationtest.MutationCoverageReport;
import org.pitest.pitclipse.pitrunner.PitResults.PitResultsBuilder;
import org.pitest.pitclipse.pitrunner.client.PitClient;

@ThreadSafe
public class PitRunner {

	public PitResults runPIT(PitOptions options) {
		String[] cliArgs = PitCliArguments.from(options);
		MutationCoverageReport.main(cliArgs);
		File reportDir = options.getReportDirectory();
		File htmlResultFile = findResultFile(reportDir, "index.html");
		File xmlResultFile = findResultFile(reportDir, "mutations.xml");
		return new PitResultsBuilder().withPitOptions(options).withHtmlResults(htmlResultFile)
				.withXmlResults(xmlResultFile).build();
	}

	private File findResultFile(File reportDir, String fileName) {
		for (File file : reportDir.listFiles()) {
			if (fileName.equals(file.getName())) {
				return file;
			}
		}
		for (File file : reportDir.listFiles()) {
			if (file.isDirectory()) {
				File result = findResultFile(file, fileName);
				if (null != result) {
					return result;
				}
			}
		}
		return null;
	}

	public static void main(String[] args) {
		validateArgs(args);
		int port = valueOf(args[0]);
		PitClient client = new PitClient(port);
		try {
			client.connect();
			System.out.println("Connected");
			PitOptions options = client.readOptions();
			System.out.println("Received options: " + options);
			PitResults results = new PitRunner().runPIT(options);
			System.out.println("Sending results: " + results);
			client.sendResults(results);
		} finally {
			try {
				System.out.println("Closing server");
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Closed");
		}
	}

	private static void validateArgs(String[] args) {
		checkArgument(args.length == 1);
	}

}
