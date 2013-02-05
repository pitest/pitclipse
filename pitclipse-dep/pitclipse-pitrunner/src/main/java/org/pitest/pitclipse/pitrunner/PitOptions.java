package org.pitest.pitclipse.pitrunner;

import static com.google.common.collect.ImmutableList.builder;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableList.of;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

@Immutable
public final class PitOptions implements Serializable {

	private static final long serialVersionUID = 1543633254516962868L;
	private final File reportDir;
	private final String classUnderTest;
	private final List<String> classesToMutate;
	private final List<File> sourceDirs;
	private final List<String> packages;
	private final int threads;

	private PitOptions(String classUnderTest, List<String> classesToMutate,
			List<File> sourceDirs, File reportDir, List<String> packages,
			int threads) {
		this.classUnderTest = classUnderTest;
		this.threads = threads;
		this.packages = copyOf(packages);
		this.classesToMutate = copyOf(classesToMutate);
		this.sourceDirs = sourceDirs;
		this.reportDir = reportDir;
	}

	public File getReportDirectory() {
		return new File(reportDir.getPath());
	}

	public List<File> getSourceDirectories() {
		return copyOfFiles(sourceDirs);
	}

	public String[] toCLIArgs() {
		List<String> args = of("--failWhenNoMutations", "false",
				"--outputFormats", "HTML,XML", "--threads",
				Integer.toString(threads), "--reportDir", reportDir.getPath(),
				"--targetTests", toTest(), "--targetClasses", classpath(),
				"--sourceDirs", sourceDirs(), "--verbose");
		return args.toArray(new String[args.size()]);
	}

	private String toTest() {
		if (packages.isEmpty()) {
			return classUnderTest;
		} else {
			return concat(commaSeperate(packages));
		}
	}

	private String sourceDirs() {
		return concat(commaSeperate(fileAsStrings(sourceDirs)));
	}

	private List<String> fileAsStrings(List<File> files) {
		Builder<String> builder = builder();
		for (File file : files) {
			builder.add(file.getPath());
		}
		return builder.build();
	}

	private String classpath() {
		return concat(commaSeperate(classesToMutate));
	}

	private String concat(List<String> entries) {
		StringBuilder result = new StringBuilder();
		for (String entry : entries) {
			result.append(entry);
		}
		return result.toString();
	}

	private List<String> commaSeperate(List<String> candidates) {
		List<String> formattedCandidates = Lists.newArrayList();
		int size = candidates.size();
		for (int i = 0; i < size; i++) {
			String candidate = candidates.get(i).trim();
			if (i != size - 1) {
				formattedCandidates.add(candidate + ",");
			} else {
				formattedCandidates.add(candidate);
			}
		}
		return formattedCandidates;
	}

	private static File copyOfFile(File sourceDir) {
		return new File(sourceDir.getPath());
	}

	private static List<File> copyOfFiles(List<File> sourceDirs) {
		Builder<File> builder = builder();
		for (File file : sourceDirs) {
			builder.add(copyOfFile(file));
		}
		return builder.build();
	}

	public static final class PitOptionsBuilder {
		private String classUnderTest = null;
		private List<String> classesToMutate = of();
		private File reportDir = null;
		private List<File> sourceDirs = of();
		private List<String> packages = of();
		private int threads = 1;

		public PitOptionsBuilder withReportDirectory(File reportDir) {
			this.reportDir = copyOfFile(reportDir);
			return this;
		}

		public PitOptionsBuilder withSourceDirectory(File sourceDir) {
			return withSourceDirectories(of(copyOfFile(sourceDir)));
		}

		public PitOptionsBuilder withSourceDirectories(List<File> sourceDirs) {
			this.sourceDirs = copyOf(sourceDirs);
			return this;
		}

		public PitOptions build() {
			validateSourceDir();
			validateTestClass();
			initialiseReportDir();
			return new PitOptions(classUnderTest, classesToMutate, sourceDirs,
					reportDir, packages, threads);
		}

		private void initialiseReportDir() {
			if (null == reportDir) {
				reportDir = Files.createTempDir();
			}
			if (!reportDir.exists()) {
				try {
					Files.createParentDirs(reportDir);
					if (!reportDir.mkdir()) {
						throw new PitLaunchException(
								"Directory could not be created: " + reportDir);
					}
				} catch (IOException e) {
					rethrow(reportDir, e);
				}
			}
		}

		private void validateSourceDir() {
			if (sourceDirs.isEmpty()) {
				throw new PitLaunchException("Source directory not set.");
			}
			for (File dir : sourceDirs) {
				if (!dir.exists()) {
					throw new PitLaunchException("Directory does not exist: "
							+ dir);
				}
			}
		}

		private void validateTestClass() {
			if (null == classUnderTest && packages.isEmpty()) {
				throw new PitLaunchException("Class under test not set.");
			}
		}

		private void rethrow(File reportDir, IOException e) {
			throw new PitLaunchException("Unable to use path: " + reportDir, e);
		}

		public PitOptionsBuilder withClassUnderTest(String testClass) {
			classUnderTest = testClass;
			return this;
		}

		public PitOptionsBuilder withClassesToMutate(List<String> classPath) {
			classesToMutate = copyOf(classPath);
			return this;
		}

		public PitOptionsBuilder withPackagesToTest(List<String> packages) {
			this.packages = copyOf(packages);
			return this;
		}

		public PitOptionsBuilder withThreads(int threads) {
			this.threads = threads;
			return this;
		}
	}

	public static final class PitLaunchException extends
			IllegalArgumentException {
		private static final long serialVersionUID = -8657782829090737433L;

		public PitLaunchException(String msg, IOException e) {
			super(msg, e);
		}

		public PitLaunchException(String msg) {
			super(msg);
		}
	}

	public String getClassUnderTest() {
		return classUnderTest;
	}

	public String toCLIArgsAsString() {
		StringBuilder argsBuilder = new StringBuilder();
		for (String arg : toCLIArgs()) {
			argsBuilder.append(' ').append(arg);
		}
		return argsBuilder.toString().trim();
	}

	public List<String> getTestPackages() {
		return packages;
	}

	@Override
	public String toString() {
		return "PitOptions [reportDir=" + reportDir + ", classUnderTest="
				+ classUnderTest + ", classesToMutate=" + classesToMutate
				+ ", sourceDirs=" + sourceDirs + ", packages=" + packages + "]";
	}

}
