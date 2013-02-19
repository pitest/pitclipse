package org.pitest.pitclipse.pitrunner;

import static com.google.common.collect.ImmutableList.builder;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.io.Files.createParentDirs;
import static com.google.common.io.Files.createTempDir;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

@Immutable
public final class PitOptions implements Serializable {

	private static final long serialVersionUID = 1543633254516962868L;
	private final File reportDir;
	private final String classUnderTest;
	private final List<String> classesToMutate;
	private final List<File> sourceDirs;
	private final List<String> packages;
	private final int threads;
	private final File historyLocation;
	private final List<String> excludedClasses;

	private PitOptions(String classUnderTest, List<String> classesToMutate,
			List<File> sourceDirs, File reportDir, List<String> packages,
			int threads, File historyLocation, List<String> excludedClasses) {
		this.classUnderTest = classUnderTest;
		this.threads = threads;
		this.historyLocation = historyLocation;
		this.packages = copyOf(packages);
		this.classesToMutate = copyOf(classesToMutate);
		this.sourceDirs = sourceDirs;
		this.reportDir = reportDir;
		this.excludedClasses = copyOf(excludedClasses);
	}

	public File getReportDirectory() {
		return new File(reportDir.getPath());
	}

	public List<File> getSourceDirectories() {
		return copyOfFiles(sourceDirs);
	}

	public String[] toCLIArgs() {
		Builder<String> builder = ImmutableList.builder();
		builder.add("--failWhenNoMutations", "false", "--outputFormats",
				"HTML,XML", "--threads", Integer.toString(threads),
				"--reportDir", reportDir.getPath(), "--targetTests", toTest(),
				"--targetClasses", classpath(), "--sourceDirs", sourceDirs(),
				"--verbose");
		builder.addAll(historyLocation());
		builder.addAll(excludedClasses());
		List<String> args = builder.build();
		return args.toArray(new String[args.size()]);
	}

	private List<String> excludedClasses() {
		Builder<String> builder = ImmutableList.builder();
		if (!excludedClasses.isEmpty()) {
			builder.add("--excludedClasses");
			builder.add(concat(commaSeperate(excludedClasses)));
		}
		return builder.build();
	}

	private List<String> historyLocation() {
		Builder<String> builder = ImmutableList.builder();
		if (null != historyLocation) {
			builder.add("--historyInputLocation");
			builder.add(historyLocation.getPath());
			builder.add("--historyOutputLocation");
			builder.add(historyLocation.getPath());
		}
		return builder.build();
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
		private File historyLocation = null;
		private List<String> excludedClasses = of();

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
			initialiseHistoryLocation();
			return new PitOptions(classUnderTest, classesToMutate, sourceDirs,
					reportDir, packages, threads, historyLocation,
					excludedClasses);
		}

		private void initialiseReportDir() {
			if (null == reportDir) {
				reportDir = createTempDir();
			}
			if (!reportDir.exists()) {
				try {
					createParentDirs(reportDir);
					if (!reportDir.mkdir()) {
						throw new PitLaunchException(
								"Directory could not be created: " + reportDir);
					}
				} catch (IOException e) {
					rethrow(reportDir, e);
				}
			}
		}

		private void initialiseHistoryLocation() {
			if (null != historyLocation
					&& !historyLocation.getParentFile().exists()) {
				try {
					createParentDirs(historyLocation);
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

		public PitOptionsBuilder withHistoryLocation(File historyLocation) {
			this.historyLocation = historyLocation;
			return this;
		}

		public PitOptionsBuilder withExcludedClasses(
				List<String> excludedClasses) {
			this.excludedClasses = copyOf(excludedClasses);
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
				+ ", sourceDirs=" + sourceDirs + ", packages=" + packages
				+ ", threads=" + threads + ", historyLocation="
				+ historyLocation + "]";
	}

	public File getHistoryLocation() {
		return historyLocation;
	}

}
