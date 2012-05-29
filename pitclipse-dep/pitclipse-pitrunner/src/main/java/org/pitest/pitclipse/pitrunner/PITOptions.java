package org.pitest.pitclipse.pitrunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

@Immutable
public final class PITOptions {

	private final File reportDir;
	private final String classUnderTest;
	private final List<String> classesToMutate;
	private final File sourceDir;

	private PITOptions(String classUnderTest, List<String> classesToMutate,
			File sourceDir, File reportDir) {
		this.classUnderTest = classUnderTest;
		this.classesToMutate = ImmutableList.copyOf(classesToMutate);
		this.sourceDir = sourceDir;
		this.reportDir = reportDir;
	}

	public File getReportDirectory() {
		return new File(reportDir.getPath());
	}

	public File getSourceDirectory() {
		return new File(sourceDir.getPath());
	}

	public String[] toCLIArgs() {
		List<String> args = Lists.newArrayList("--outputFormats", "HTML",
				"--reportDir", reportDir.getPath(), "--sourceDirs",
				sourceDir.getPath(), "--targetTests", classUnderTest,
				"--targetClasses");
		args.addAll(classpath());
		return args.toArray(new String[args.size()]);
	}

	private List<String> classpath() {
		List<String> classpath = Lists.newArrayList();
		int classes = classesToMutate.size();
		for (int i = 0; i < classes; i++) {
			String clazz = classesToMutate.get(i);
			if (i != (classes - 1)) {
				classpath.add(clazz + ",");
			} else {
				classpath.add(clazz);
			}
		}
		return classpath;
	}

	public static final class PITOptionsBuilder {
		private String classUnderTest = null;
		private List<String> classesToMutate = ImmutableList.of();
		private File reportDir = null;
		private File sourceDir = null;

		public PITOptionsBuilder withReportDirectory(File reportDir) {
			this.reportDir = new File(reportDir.getPath());
			return this;
		}

		public PITOptionsBuilder withSourceDirectory(File sourceDir) {
			this.sourceDir = new File(sourceDir.getPath());
			return this;
		}

		public PITOptions build() {
			validateSourceDir();
			validateTestClass();
			initialiseReportDir();
			return new PITOptions(classUnderTest, classesToMutate, sourceDir,
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
			if (null == sourceDir) {
				throw new PITLaunchException("Source directory not set.");
			}
			if (!sourceDir.exists()) {
				throw new PITLaunchException("Directory does not exist: "
						+ sourceDir);
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
}
