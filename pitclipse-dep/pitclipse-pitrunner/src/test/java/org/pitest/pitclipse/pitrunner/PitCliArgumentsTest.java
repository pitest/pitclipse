package org.pitest.pitclipse.pitrunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.pitest.pitclipse.pitrunner.config.PitConfiguration.DEFAULT_AVOID_CALLS_TO_LIST;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pitest.pitclipse.example.ExampleTest;
import org.pitest.pitclipse.reloc.guava.base.Function;
import org.pitest.pitclipse.reloc.guava.base.Splitter;
import org.pitest.pitclipse.reloc.guava.collect.Collections2;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList.Builder;

public class PitCliArgumentsTest {

	private static final String TEST_CLASS1 = PitOptionsTest.class.getCanonicalName();
	private static final String TEST_CLASS2 = PitRunner.class.getCanonicalName();
	private static final List<String> CLASS_PATH = ImmutableList.of(TEST_CLASS1, TEST_CLASS2);
	private static final String PACKAGE_1 = PitOptionsTest.class.getPackage().getName() + ".*";
	private static final String PACKAGE_2 = ExampleTest.class.getPackage().getName() + ".*";
	private static final List<String> PACKAGES = ImmutableList.of(PACKAGE_1, PACKAGE_2);
	private static final List<String> NO_EXCLUDED_CLASSES = ImmutableList.of();
	private static final List<String> EXCLUDED_CLASSES = ImmutableList.of("com*ITest", "*IntTest");
	private static final List<String> NO_EXCLUDED_METHODS = ImmutableList.of();
	private static final List<String> EXCLUDED_METHODS = ImmutableList.of("*toString*", "leaveMeAlone*");
	private static final File NO_HISTORY_FILE = null;
	private static final int DEFAULT_NUMBER_OF_THREADS = 1;
	private static final List<String> MUTATORS = ImmutableList.of("FOO", "BAR");

	private static final List<String> DEFAULT_AVOID_LIST = ImmutableList.copyOf(Splitter.on(',').trimResults()
			.omitEmptyStrings().split(DEFAULT_AVOID_CALLS_TO_LIST));

	private static final int DEFAULT_TIMEOUT = 3000;
	private static final BigDecimal DEFAULT_TIMEOUT_FACTOR = BigDecimal.valueOf(1.25);

	private final FileSystemSupport fileSystemSupport = new FileSystemSupport();

	private final File testTmpDir = fileSystemSupport.randomDir();
	private final File testSrcDir = fileSystemSupport.randomDir();
	private final File anotherTestSrcDir = fileSystemSupport.randomDir();
	private final File reportDir = fileSystemSupport.randomDir();

	private final File historyLocation = fileSystemSupport.randomFile();
	private String[] actualCliArgs;
	private final ImmutableList<File> defaultSrcDirs = ImmutableList.of(testSrcDir);

	@Before
	public void setup() {
		for (File dir : ImmutableList.of(testTmpDir, testSrcDir, anotherTestSrcDir, reportDir)) {
			dir.mkdirs();
			dir.deleteOnExit();
		}
		actualCliArgs = new String[0];
	}

	@Test
	public void minimumOptionsSet() throws IOException {
		PitOptions options = PitOptions.builder().withSourceDirectory(testSrcDir).withClassUnderTest(TEST_CLASS1)
				.withClassesToMutate(CLASS_PATH).withReportDirectory(reportDir).withMutators(MUTATORS).build();
		whenArgumentsAreMadeFrom(options);
		thenTheArgumentsAreMadeUpOf(reportDir, defaultSrcDirs, DEFAULT_NUMBER_OF_THREADS, TEST_CLASS1, CLASS_PATH,
				NO_HISTORY_FILE, NO_EXCLUDED_CLASSES, NO_EXCLUDED_METHODS, DEFAULT_AVOID_LIST, MUTATORS,
				DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_FACTOR);
	}

	@Test
	public void testPackagesSupplied() {
		PitOptions options = PitOptions.builder().withSourceDirectory(testSrcDir).withPackagesToTest(PACKAGES)
				.withClassesToMutate(CLASS_PATH).withReportDirectory(reportDir).withMutators(MUTATORS).build();
		whenArgumentsAreMadeFrom(options);
		thenTheArgumentsAreMadeUpOf(reportDir, defaultSrcDirs, DEFAULT_NUMBER_OF_THREADS, PACKAGE_1 + "," + PACKAGE_2,
				CLASS_PATH, NO_HISTORY_FILE, NO_EXCLUDED_CLASSES, NO_EXCLUDED_METHODS, DEFAULT_AVOID_LIST, MUTATORS,
				DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_FACTOR);
	}

	@Test
	public void multipleSourceDirectoriesExist() throws IOException {
		List<File> srcDirs = ImmutableList.of(testSrcDir, anotherTestSrcDir);
		PitOptions options = PitOptions.builder().withSourceDirectories(srcDirs).withClassUnderTest(TEST_CLASS1)
				.withClassesToMutate(CLASS_PATH).withReportDirectory(reportDir).withMutators(MUTATORS).build();
		whenArgumentsAreMadeFrom(options);
		thenTheArgumentsAreMadeUpOf(reportDir, srcDirs, DEFAULT_NUMBER_OF_THREADS, TEST_CLASS1, CLASS_PATH,
				NO_HISTORY_FILE, NO_EXCLUDED_CLASSES, NO_EXCLUDED_METHODS, DEFAULT_AVOID_LIST, MUTATORS,
				DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_FACTOR);
	}

	@Test
	public void differentNumberOfThreads() throws IOException {
		int nonStandardThreadCount = 123456;
		PitOptions options = PitOptions.builder().withSourceDirectory(testSrcDir).withClassUnderTest(TEST_CLASS1)
				.withClassesToMutate(CLASS_PATH).withReportDirectory(reportDir).withThreads(nonStandardThreadCount)
				.withMutators(MUTATORS).build();
		whenArgumentsAreMadeFrom(options);
		thenTheArgumentsAreMadeUpOf(reportDir, defaultSrcDirs, nonStandardThreadCount, TEST_CLASS1, CLASS_PATH,
				NO_HISTORY_FILE, NO_EXCLUDED_CLASSES, NO_EXCLUDED_METHODS, DEFAULT_AVOID_LIST, MUTATORS,
				DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_FACTOR);
	}

	@Test
	public void validHistoryLocationSupplied() {
		PitOptions options = PitOptions.builder().withSourceDirectory(testSrcDir).withClassUnderTest(TEST_CLASS1)
				.withClassesToMutate(CLASS_PATH).withReportDirectory(reportDir).withHistoryLocation(historyLocation)
				.withMutators(MUTATORS).build();
		whenArgumentsAreMadeFrom(options);
		thenTheArgumentsAreMadeUpOf(reportDir, defaultSrcDirs, DEFAULT_NUMBER_OF_THREADS, TEST_CLASS1, CLASS_PATH,
				historyLocation, NO_EXCLUDED_CLASSES, NO_EXCLUDED_METHODS, DEFAULT_AVOID_LIST, MUTATORS,
				DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_FACTOR);
	}

	@Test
	public void excludedClassesSet() throws IOException {
		PitOptions options = PitOptions.builder().withSourceDirectory(testSrcDir).withClassUnderTest(TEST_CLASS1)
				.withClassesToMutate(CLASS_PATH).withReportDirectory(reportDir).withExcludedClasses(EXCLUDED_CLASSES)
				.withMutators(MUTATORS).build();
		whenArgumentsAreMadeFrom(options);
		thenTheArgumentsAreMadeUpOf(reportDir, defaultSrcDirs, DEFAULT_NUMBER_OF_THREADS, TEST_CLASS1, CLASS_PATH,
				NO_HISTORY_FILE, EXCLUDED_CLASSES, NO_EXCLUDED_METHODS, DEFAULT_AVOID_LIST, MUTATORS, DEFAULT_TIMEOUT,
				DEFAULT_TIMEOUT_FACTOR);
	}

	@Test
	public void excludedClassesAndMethodsSet() throws IOException {
		PitOptions options = PitOptions.builder().withSourceDirectory(testSrcDir).withClassUnderTest(TEST_CLASS1)
				.withClassesToMutate(CLASS_PATH).withReportDirectory(reportDir).withExcludedClasses(EXCLUDED_CLASSES)
				.withMutators(MUTATORS).withExcludedMethods(EXCLUDED_METHODS).build();
		whenArgumentsAreMadeFrom(options);
		thenTheArgumentsAreMadeUpOf(reportDir, defaultSrcDirs, DEFAULT_NUMBER_OF_THREADS, TEST_CLASS1, CLASS_PATH,
				NO_HISTORY_FILE, EXCLUDED_CLASSES, EXCLUDED_METHODS, DEFAULT_AVOID_LIST, MUTATORS, DEFAULT_TIMEOUT,
				DEFAULT_TIMEOUT_FACTOR);
	}

	@Test
	public void alternatveAvoidListUsed() throws IOException {
		List<String> alternativeAvoidList = ImmutableList.of("org.springframework");
		PitOptions options = PitOptions.builder().withSourceDirectory(testSrcDir).withClassUnderTest(TEST_CLASS1)
				.withClassesToMutate(CLASS_PATH).withReportDirectory(reportDir).withAvoidCallsTo(alternativeAvoidList)
				.withMutators(MUTATORS).build();
		whenArgumentsAreMadeFrom(options);
		thenTheArgumentsAreMadeUpOf(reportDir, defaultSrcDirs, DEFAULT_NUMBER_OF_THREADS, TEST_CLASS1, CLASS_PATH,
				NO_HISTORY_FILE, NO_EXCLUDED_CLASSES, NO_EXCLUDED_METHODS, alternativeAvoidList, MUTATORS,
				DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_FACTOR);
	}

	private void whenArgumentsAreMadeFrom(PitOptions options) {
		actualCliArgs = PitCliArguments.from(options);
	}

	private void thenTheArgumentsAreMadeUpOf(File reportDir, List<File> testSrcDirs, int threadCount, String testClass,
			List<String> classesToMutate, File historyFile, List<String> excludedClasses, List<String> excludedMethods,
			List<String> avoidCallsTo, List<String> mutators, int timeout, BigDecimal timeoutFactor) {
		Object[] expectedCliArgs = new ExpectedArgsBuilder().withThreadCount(threadCount).withReportDir(reportDir)
				.withClassUnderTest(testClass).withTargetClasses(classesToMutate)
				.withSourceDirectories(filesAsStrings(testSrcDirs)).withHistoryLocation(historyFile)
				.withExcludedClasses(excludedClasses).withExcludedMethods(excludedMethods).withMutators(MUTATORS)
				.withAvoidCallsTo(avoidCallsTo).withMutators(mutators).build();
		assertThat(actualCliArgs, is(equalTo(expectedCliArgs)));
	}

	private List<String> filesAsStrings(List<File> files) {
		return ImmutableList.copyOf(Collections2.transform(files, new Function<File, String>() {
			@Override
			public String apply(File file) {
				return file.getPath();
			}
		}));
	}

	private static final class ExpectedArgsBuilder {

		private static final ImmutableList<String> DEFAULT_PIT_ARGS = ImmutableList.of("--failWhenNoMutations",
				"false", "--outputFormats", "HTML,PITCLIPSE_MUTATIONS", "--verbose");
		private File reportDir;
		private int threadCount = DEFAULT_NUMBER_OF_THREADS;
		private String classUnderTest;
		private List<String> targetClasses = CLASS_PATH;
		private List<String> sourceDirs;
		private File historyLocation = NO_HISTORY_FILE;
		private List<String> excludedClasses = NO_EXCLUDED_CLASSES;
		private List<String> excludedMethods = NO_EXCLUDED_METHODS;
		private List<String> avoidCallsTo = DEFAULT_AVOID_LIST;
		private List<String> mutators = MUTATORS;
		private final int timeout = DEFAULT_TIMEOUT;
		private final BigDecimal timeoutFactor = DEFAULT_TIMEOUT_FACTOR;

		public String[] build() {
			Builder<String> resultBuilder = ImmutableList.builder();
			resultBuilder.addAll(defaultPitArgs());
			resultBuilder.add("--threads").add(Integer.toString(threadCount));
			resultBuilder.addAll(addIfKnown("--reportDir", reportDir));
			resultBuilder.addAll(addIfKnown("--targetTests", classUnderTest));
			resultBuilder.addAll(addIfKnown("--targetClasses", targetClasses));
			resultBuilder.addAll(addIfKnown("--sourceDirs", sourceDirs));
			resultBuilder.addAll(addIfKnown("--historyInputLocation", historyLocation));
			resultBuilder.addAll(addIfKnown("--historyOutputLocation", historyLocation));
			resultBuilder.addAll(addIfKnown("--excludedClasses", excludedClasses));
			resultBuilder.addAll(addIfKnown("--excludedMethods", excludedMethods));
			resultBuilder.addAll(addIfKnown("--avoidCallsTo", avoidCallsTo));
			resultBuilder.addAll(addIfKnown("--mutators", mutators));
			resultBuilder.add("--timeoutConst", Integer.toString(timeout));
			resultBuilder.add("--timeoutFactor", timeoutFactor.toPlainString());
			return asStrings(resultBuilder.build());
		}

		public ExpectedArgsBuilder withMutators(List<String> mutators) {
			this.mutators = ImmutableList.copyOf(mutators);
			return this;
		}

		public ExpectedArgsBuilder withTargetClasses(List<String> targetClasses) {
			this.targetClasses = ImmutableList.copyOf(targetClasses);
			return this;
		}

		public ExpectedArgsBuilder withSourceDirectories(List<String> sourceDirs) {
			this.sourceDirs = ImmutableList.copyOf(sourceDirs);
			return this;
		}

		public ExpectedArgsBuilder withThreadCount(int threadCount) {
			this.threadCount = threadCount;
			return this;
		}

		public ExpectedArgsBuilder withExcludedClasses(List<String> excludedClasses) {
			this.excludedClasses = ImmutableList.copyOf(excludedClasses);
			return this;
		}

		public ExpectedArgsBuilder withExcludedMethods(List<String> excludedMethods) {
			this.excludedMethods = ImmutableList.copyOf(excludedMethods);
			return this;
		}

		public ExpectedArgsBuilder withAvoidCallsTo(List<String> avoidCallsTo) {
			this.avoidCallsTo = ImmutableList.copyOf(avoidCallsTo);
			return this;
		}

		private List<String> addIfKnown(String param, List<String> someCollection) {
			if (null == someCollection || someCollection.isEmpty()) {
				return ImmutableList.of();
			}
			String result = "";
			for (int i = 0; i < someCollection.size(); i++) {
				String value = someCollection.get(i);
				if (i == someCollection.size() - 1) {
					result += value;
				} else {
					result += value + ",";
				}
			}
			return ImmutableList.of(param, result);
		}

		private List<String> addIfKnown(String param, String someValue) {
			if (null == someValue) {
				return ImmutableList.of();
			}
			return ImmutableList.of(param, someValue);
		}

		private List<String> addIfKnown(String param, File someFile) {
			if (null == someFile) {
				return ImmutableList.of();
			}
			return ImmutableList.of(param, someFile.getPath());
		}

		public ExpectedArgsBuilder withClassUnderTest(String testClass) {
			this.classUnderTest = testClass;
			return this;
		}

		public ExpectedArgsBuilder withReportDir(File reportDir) {
			this.reportDir = reportDir;
			return this;
		}

		private List<String> defaultPitArgs() {
			return DEFAULT_PIT_ARGS;
		}

		private String[] asStrings(ImmutableList<String> listOfStrings) {
			return listOfStrings.toArray(new String[listOfStrings.size()]);
		}

		public ExpectedArgsBuilder withHistoryLocation(File historyLocation) {
			this.historyLocation = historyLocation;
			return this;
		}
	}
}
