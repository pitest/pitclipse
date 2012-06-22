package org.pitest.pitclipse.pitrunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

@Immutable
public final class PITOptions {

	private final File reportDir;
	private final String classUnderTest;
	private final List<String> classesToMutate;
	private final List<File> sourceDirs;

	private PITOptions(String classUnderTest, List<String> classesToMutate,
			List<File> sourceDirs, File reportDir) {
		this.classUnderTest = classUnderTest;
		this.classesToMutate = ImmutableList.copyOf(classesToMutate);
		this.sourceDirs = sourceDirs;
		this.reportDir = reportDir;
	}

	public File getReportDirectory() {
		return new File(reportDir.getPath());
	}

	public List<File> getSourceDirectories() {
		return copyOf(sourceDirs);
	}

	public String[] toCLIArgs() {
		List<String> args = Lists.newArrayList("--outputFormats", "HTML",
				"--reportDir", reportDir.getPath(), "--targetTests", classUnderTest,
				"--targetClasses");
		args.addAll(classpath());
		args.add("--sourceDirs");
		args.add(sourceDirs());
		return args.toArray(new String[args.size()]);
	}

	private String sourceDirs() {
		StringBuilder result = new StringBuilder();
		List<String> srcDirs = commaSeperate(fileAsStrings(sourceDirs));
		for (String src : srcDirs) {
			result.append(src);
		}
		return result.toString();
	}

	private List<String> fileAsStrings(List<File> files) {
		Builder<String> builder = ImmutableList.builder();
		for (File file : files) {
			builder.add(file.getPath());
		}		
		return builder.build();
	}

	private List<String> classpath() {
		return commaSeperate(classesToMutate);
	}

	private List<String> commaSeperate(List<String> candidates) {
		List<String> formattedCandidates = Lists.newArrayList();
		int size = candidates.size();
		for (int i = 0; i < size; i++) {
			String candidate = candidates.get(i).trim();
			if (i != (size - 1)) {
				formattedCandidates.add(candidate + ",");
			} else {
				formattedCandidates.add(candidate);
			}
		}
		return formattedCandidates;
	}

	private static File copyOf(File sourceDir) {
		return new File(sourceDir.getPath());
	}

	private static List<File> copyOf(List<File> sourceDirs) {
		Builder<File> builder = ImmutableList.builder();
		for (File file : sourceDirs) {
			builder.add(copyOf(file));
		}
		return builder.build();
	}
	
	public static final class PITOptionsBuilder {
		private String classUnderTest = null;
		private List<String> classesToMutate = ImmutableList.of();
		private File reportDir = null;
		private List<File> sourceDirs = ImmutableList.of();

		public PITOptionsBuilder withReportDirectory(File reportDir) {
			this.reportDir = copyOf(reportDir);
			return this;
		}

		public PITOptionsBuilder withSourceDirectory(File sourceDir) {
			return withSourceDirectories(ImmutableList.of(copyOf(sourceDir)));
		}

		public PITOptionsBuilder withSourceDirectories(List<File> sourceDirs) {
			this.sourceDirs = copyOf(sourceDirs); 
			return this;
		}
		
		public PITOptions build() {
			validateSourceDir();
			validateTestClass();
			initialiseReportDir();
			return new PITOptions(classUnderTest, classesToMutate, sourceDirs,
					reportDir);
		}

		private void initialiseReportDir() {
			if (null == reportDir) {
				reportDir = Files.createTempDir();
			}
			if (!reportDir.exists()) {
				try {
					Files.createParentDirs(this.reportDir);
				} catch (IOException e) {
					rethrow(reportDir, e);
				}
				this.reportDir.mkdir();
			}
		}

		private void validateSourceDir() {
			if (sourceDirs.isEmpty()) {
				throw new PITLaunchException("Source directory not set.");
			}
			for (File dir : sourceDirs) {
				if (!dir.exists()) {
					throw new PITLaunchException("Directory does not exist: "
							+ dir);
				}
			}
		}

		private void validateTestClass() {
			if (null == classUnderTest) {
				throw new PITLaunchException("Class under test not set.");
			}
		}

		private void rethrow(File reportDir, IOException e) {
			throw new PITLaunchException("Unable to use path: " + reportDir, e);
		}

		public PITOptionsBuilder withClassUnderTest(String testClass) {
			classUnderTest = testClass;
			return this;
		}

		public PITOptionsBuilder withClassesToMutate(List<String> classPath) {
			classesToMutate = ImmutableList.copyOf(classPath);
			return this;
		}
	}

	public static final class PITLaunchException extends
			IllegalArgumentException {
		private static final long serialVersionUID = -8657782829090737433L;

		public PITLaunchException(String msg, IOException e) {
			super(msg, e);
		}

		public PITLaunchException(String msg) {
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
}
