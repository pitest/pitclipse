package org.pitest.pitclipse.pitrunner;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.io.Files.createParentDirs;
import static com.google.common.io.Files.createTempDir;
import static org.pitest.pitclipse.pitrunner.config.PitConfiguration.DEFAULT_AVOID_CALLS_TO_LIST;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

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
	private final List<String> excludedMethods;
	private final List<String> avoidCallsTo;

	private PitOptions(String classUnderTest, List<String> classesToMutate, List<File> sourceDirs, File reportDir,
			List<String> packages, int threads, File historyLocation, List<String> excludedClasses,
			List<String> excludedMethods, List<String> avoidCallsTo) {
		this.classUnderTest = classUnderTest;
		this.threads = threads;
		this.historyLocation = historyLocation;
		this.packages = copyOf(packages);
		this.classesToMutate = copyOf(classesToMutate);
		this.sourceDirs = sourceDirs;
		this.reportDir = reportDir;
		this.excludedClasses = copyOf(excludedClasses);
		this.excludedMethods = copyOf(excludedMethods);
		this.avoidCallsTo = copyOf(avoidCallsTo);
	}

	public File getReportDirectory() {
		return new File(reportDir.getPath());
	}

	public List<File> getSourceDirectories() {
		return copyOfFiles(sourceDirs);
	}

	private static File copyOfFile(File sourceDir) {
		return new File(sourceDir.getPath());
	}

	private static List<File> copyOfFiles(List<File> sourceDirs) {
		Builder<File> builder = ImmutableList.builder();
		for (File file : sourceDirs) {
			builder.add(copyOfFile(file));
		}
		return builder.build();
	}

	public static PitOptionsBuilder builder() {
		return new PitOptionsBuilder();
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
		private List<String> excludedMethods = of();
		private List<String> avoidCallsTo = ImmutableList.copyOf(Splitter.on(',').trimResults().omitEmptyStrings()
				.split(DEFAULT_AVOID_CALLS_TO_LIST));

		private PitOptionsBuilder() {
		}

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
			return new PitOptions(classUnderTest, classesToMutate, sourceDirs, reportDir, packages, threads,
					historyLocation, excludedClasses, excludedMethods, avoidCallsTo);
		}

		private void initialiseReportDir() {
			if (null == reportDir) {
				reportDir = createTempDir();
			}
			if (!reportDir.exists()) {
				try {
					createParentDirs(reportDir);
					if (!reportDir.mkdir()) {
						throw new PitLaunchException("Directory could not be created: " + reportDir);
					}
				} catch (IOException e) {
					rethrow(reportDir, e);
				}
			}
		}

		private void initialiseHistoryLocation() {
			if (null != historyLocation && !historyLocation.getParentFile().exists()) {
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
					throw new PitLaunchException("Directory does not exist: " + dir);
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

		public PitOptionsBuilder withExcludedClasses(List<String> excludedClasses) {
			this.excludedClasses = copyOf(excludedClasses);
			return this;
		}

		public PitOptionsBuilder withExcludedMethods(List<String> excludedMethods) {
			this.excludedMethods = copyOf(excludedMethods);
			return this;
		}

		public PitOptionsBuilder withAvoidCallsTo(List<String> avoidCallsTo) {
			this.avoidCallsTo = copyOf(avoidCallsTo);
			return this;
		}
	}

	public static final class PitLaunchException extends IllegalArgumentException {
		private static final long serialVersionUID = -8657782829090737433L;

		public PitLaunchException(String msg, Exception e) {
			super(msg, e);
		}

		public PitLaunchException(String msg) {
			super(msg);
		}
	}

	public String getClassUnderTest() {
		return classUnderTest;
	}

	public List<String> getTestPackages() {
		return packages;
	}

	public File getHistoryLocation() {
		return historyLocation;
	}

	public List<File> getSourceDirs() {
		return sourceDirs;
	}

	public int getThreads() {
		return threads;
	}

	public List<String> getExcludedClasses() {
		return copyOf(excludedClasses);
	}

	public List<String> getExcludedMethods() {
		return copyOf(excludedMethods);
	}

	public List<String> getClassesToMutate() {
		return copyOf(classesToMutate);
	}

	public List<String> getPackages() {
		return copyOf(packages);
	}

	public List<String> getAvoidCallsTo() {
		return avoidCallsTo;
	}

	@Override
	public String toString() {
		return "PitOptions [reportDir=" + reportDir + ", classUnderTest=" + classUnderTest + ", classesToMutate="
				+ classesToMutate + ", sourceDirs=" + sourceDirs + ", packages=" + packages + ", threads=" + threads
				+ ", historyLocation=" + historyLocation + ", excludedClasses=" + excludedClasses
				+ ", excludedMethods=" + excludedMethods + ", avoidCallsTo=" + avoidCallsTo + "]";
	}
}
