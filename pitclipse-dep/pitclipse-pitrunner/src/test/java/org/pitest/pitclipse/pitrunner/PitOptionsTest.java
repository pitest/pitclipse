package org.pitest.pitclipse.pitrunner;

import static com.google.common.collect.ImmutableList.of;
import static java.lang.Integer.toHexString;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.pitest.pitclipse.pitrunner.config.PitConfiguration.DEFAULT_AVOID_CALLS_TO_LIST;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.pitest.pitclipse.example.ExampleTest;
import org.pitest.pitclipse.pitrunner.PitOptions.PitLaunchException;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class PitOptionsTest {

	private static final File TMP_DIR = new File(
			System.getProperty("java.io.tmpdir"));
	private final Random random = new Random();
	private final File testTmpDir = randomDir();
	private final File testSrcDir = randomDir();
	private final File anotherTestSrcDir = randomDir();
	private static final File REALLY_BAD_PATH = getBadPath();

	private static File getBadPath() {
		if (IS_OS_WINDOWS) {
			return new File("BADDRIVE:\\");
		}
		return new File("/HOPEFULLY/DOES/NOT/EXIST/SO/IS/BAD/");
	}

	private static final String TEST_CLASS1 = PitOptionsTest.class
			.getCanonicalName();
	private static final String TEST_CLASS2 = PitRunner.class
			.getCanonicalName();
	private static final List<String> CLASS_PATH = of(TEST_CLASS1, TEST_CLASS2);
	private static final String PACKAGE_1 = PitOptionsTest.class.getPackage()
			.getName() + ".*";
	private static final String PACKAGE_2 = ExampleTest.class.getPackage()
			.getName() + ".*";
	private static final List<String> PACKAGES = of(PACKAGE_1, PACKAGE_2);
	private static final List<String> EXCLUDED_CLASSES = of("com*ITest",
			"*IntTest");
	private static final List<String> EXCLUDED_METHODS = of("*toString*",
			"leaveMeAlone*");

	private static final List<String> EMPTY_LIST = of();
	private static final List<String> DEFAULT_AVOID_LIST = ImmutableList
			.copyOf(Splitter.on(',').trimResults().omitEmptyStrings()
					.split(DEFAULT_AVOID_CALLS_TO_LIST));
	private final File historyLocation = randomFile();

	@Before
	public void setup() {
		for (File dir : of(testTmpDir, testSrcDir, anotherTestSrcDir)) {
			dir.mkdirs();
			dir.deleteOnExit();
		}
	}

	@AfterClass
	public static void cleanupFiles() {

	}

	@Test(expected = PitLaunchException.class)
	public void defaultOptionsThrowException() throws IOException {
		PitOptions.builder().build();
	}

	@Test(expected = PitLaunchException.class)
	public void validSourceDirButNoTestClassThrowsException()
			throws IOException {
		PitOptions.builder().withSourceDirectory(testSrcDir).build();
	}

	@Test
	public void minimumOptionsSet() throws IOException {
		PitOptions options = PitOptions.builder()
				.withSourceDirectory(testSrcDir)
				.withClassUnderTest(TEST_CLASS1).build();
		File reportDir = options.getReportDirectory();
		assertTrue(reportDir.isDirectory());
		assertTrue(reportDir.exists());
		assertEquals(TMP_DIR, reportDir.getParentFile());
		assertEquals(TEST_CLASS1, options.getClassUnderTest());
		assertArrayEquals(expectedArgs(reportDir, testSrcDir, TEST_CLASS1),
				options.toCLIArgs());
		assertEquals(
				expectedArgsAsString(reportDir, testSrcDir, EMPTY_LIST,
						TEST_CLASS1), options.toCLIArgsAsString());
	}

	@Test(expected = PitLaunchException.class)
	public void sourceDirectoryDoesNotExist() throws IOException {
		PitOptions.builder().withSourceDirectory(randomDir())
				.withClassUnderTest(TEST_CLASS1).build();
	}

	@Test(expected = PitLaunchException.class)
	public void multipleSourceDirectoriesOneDoesNotExist() throws IOException {
		PitOptions.builder().withSourceDirectories(of(testSrcDir, randomDir()))
				.withClassUnderTest(TEST_CLASS1).build();
	}

	@Test
	public void multipleSourceDirectoriesExist() throws IOException {
		List<File> srcDirs = of(testSrcDir, anotherTestSrcDir);
		PitOptions options = PitOptions.builder()
				.withSourceDirectories(srcDirs).withClassUnderTest(TEST_CLASS1)
				.build();
		File reportDir = options.getReportDirectory();
		assertTrue(reportDir.isDirectory());
		assertTrue(reportDir.exists());
		assertEquals(TMP_DIR, reportDir.getParentFile());
		assertEquals(TEST_CLASS1, options.getClassUnderTest());
		assertArrayEquals(expectedArgs(reportDir, srcDirs, TEST_CLASS1),
				options.toCLIArgs());
		assertEquals(expectedArgsAsString(reportDir, srcDirs, TEST_CLASS1),
				options.toCLIArgsAsString());
	}

	@Test
	public void useDifferentReportDirectory() {
		File expectedDir = new File(testTmpDir, randomString());
		assertFalse(expectedDir.exists());
		PitOptions options = PitOptions.builder()
				.withReportDirectory(expectedDir)
				.withSourceDirectory(testSrcDir)
				.withClassUnderTest(TEST_CLASS1).build();
		File actualDir = options.getReportDirectory();
		assertTrue(actualDir.isDirectory());
		assertEquals(expectedDir, actualDir);
		assertTrue(expectedDir.exists());
		assertArrayEquals(expectedArgs(expectedDir, testSrcDir, TEST_CLASS1),
				options.toCLIArgs());
		assertEquals(
				expectedArgsAsString(expectedDir, testSrcDir, EMPTY_LIST,
						TEST_CLASS1), options.toCLIArgsAsString());
	}

	@Test(expected = PitLaunchException.class)
	public void useInvalidReportDirectory() {
		PitOptions.builder().withReportDirectory(REALLY_BAD_PATH)
				.withSourceDirectory(testSrcDir)
				.withClassUnderTest(TEST_CLASS1).build();
	}

	@Test(expected = PitLaunchException.class)
	public void useInvalidSourceDirectory() {
		PitOptions.builder().withSourceDirectory(REALLY_BAD_PATH)
				.withClassUnderTest(TEST_CLASS1).build();
	}

	@Test
	public void useClasspath() throws IOException {
		PitOptions options = PitOptions.builder()
				.withSourceDirectory(testSrcDir)
				.withClassUnderTest(TEST_CLASS1)
				.withClassesToMutate(CLASS_PATH).build();
		File reportDir = options.getReportDirectory();
		assertTrue(reportDir.isDirectory());
		assertTrue(reportDir.exists());
		assertEquals(TMP_DIR, reportDir.getParentFile());
		assertEquals(TEST_CLASS1, options.getClassUnderTest());
		assertArrayEquals(
				expectedArgs(reportDir, testSrcDir, TEST_CLASS1, TEST_CLASS1,
						TEST_CLASS2), options.toCLIArgs());
		assertEquals(
				expectedArgsAsString(reportDir, testSrcDir, EMPTY_LIST,
						TEST_CLASS1, TEST_CLASS1, TEST_CLASS2),
				options.toCLIArgsAsString());
	}

	@Test
	public void testPackagesSupplied() {
		PitOptions options = PitOptions.builder()
				.withSourceDirectory(testSrcDir).withPackagesToTest(PACKAGES)
				.withClassesToMutate(CLASS_PATH).build();
		File reportDir = options.getReportDirectory();
		assertTrue(reportDir.isDirectory());
		assertTrue(reportDir.exists());
		assertEquals(TMP_DIR, reportDir.getParentFile());
		assertNull(options.getClassUnderTest());
		assertEquals(PACKAGES, options.getTestPackages());
		assertArrayEquals(
				expectedArgs(reportDir, testSrcDir,
						PACKAGE_1 + "," + PACKAGE_2, TEST_CLASS1, TEST_CLASS2),
				options.toCLIArgs());
		assertEquals(
				expectedArgsAsString(reportDir, testSrcDir, EMPTY_LIST,
						PACKAGE_1 + "," + PACKAGE_2, TEST_CLASS1, TEST_CLASS2),
				options.toCLIArgsAsString());
	}

	@Test(expected = PitLaunchException.class)
	public void invalidHistoryFileSupplied() {
		PitOptions.builder().withSourceDirectory(testSrcDir)
				.withPackagesToTest(PACKAGES).withClassesToMutate(CLASS_PATH)
				.withHistoryLocation(getBadPath()).build();
	}

	@Test
	public void validHistoryLocationSupplied() {
		PitOptions options = PitOptions.builder()
				.withSourceDirectory(testSrcDir).withPackagesToTest(PACKAGES)
				.withClassesToMutate(CLASS_PATH)
				.withHistoryLocation(historyLocation).build();
		File location = options.getHistoryLocation();
		assertFalse(location.isDirectory());
		assertTrue(location.getParentFile().exists());
		File reportDir = options.getReportDirectory();
		assertArrayEquals(
				expectedArgs(reportDir, testSrcDir, historyLocation, PACKAGE_1
						+ "," + PACKAGE_2, TEST_CLASS1, TEST_CLASS2),
				options.toCLIArgs());
		assertEquals(
				expectedArgsAsString(reportDir, testSrcDir, historyLocation,
						PACKAGE_1 + "," + PACKAGE_2, TEST_CLASS1, TEST_CLASS2),
				options.toCLIArgsAsString());
	}

	@Test
	public void excludedClassesSet() throws IOException {
		PitOptions options = PitOptions.builder()
				.withSourceDirectory(testSrcDir)
				.withClassUnderTest(TEST_CLASS1)
				.withExcludedClasses(EXCLUDED_CLASSES).build();
		File reportDir = options.getReportDirectory();
		assertTrue(reportDir.isDirectory());
		assertTrue(reportDir.exists());
		assertEquals(TMP_DIR, reportDir.getParentFile());
		assertEquals(TEST_CLASS1, options.getClassUnderTest());
		assertArrayEquals(
				expectedArgs(reportDir, of(testSrcDir), EXCLUDED_CLASSES,
						EMPTY_LIST, DEFAULT_AVOID_LIST, null, TEST_CLASS1),
				options.toCLIArgs());
		assertEquals(
				expectedArgsAsString(reportDir, of(testSrcDir),
						EXCLUDED_CLASSES, EMPTY_LIST, DEFAULT_AVOID_LIST, null,
						TEST_CLASS1), options.toCLIArgsAsString());
	}

	@Test
	public void excludedClassesAndMethodsSet() throws IOException {
		PitOptions options = PitOptions.builder()
				.withSourceDirectory(testSrcDir)
				.withClassUnderTest(TEST_CLASS1)
				.withExcludedClasses(EXCLUDED_CLASSES)
				.withExcludedMethods(EXCLUDED_METHODS).build();
		File reportDir = options.getReportDirectory();
		assertTrue(reportDir.isDirectory());
		assertTrue(reportDir.exists());
		assertEquals(TMP_DIR, reportDir.getParentFile());
		assertEquals(TEST_CLASS1, options.getClassUnderTest());
		assertArrayEquals(
				expectedArgs(reportDir, of(testSrcDir), EXCLUDED_CLASSES,
						EXCLUDED_METHODS, DEFAULT_AVOID_LIST, null, TEST_CLASS1),
				options.toCLIArgs());
		assertEquals(
				expectedArgsAsString(reportDir, of(testSrcDir),
						EXCLUDED_CLASSES, EXCLUDED_METHODS, DEFAULT_AVOID_LIST,
						null, TEST_CLASS1), options.toCLIArgsAsString());
	}

	@Test
	public void alternatveAvoidListUsed() throws IOException {
		ImmutableList<String> alternativeAvoidList = ImmutableList
				.of("org.springframework");
		PitOptions options = PitOptions.builder()
				.withSourceDirectory(testSrcDir)
				.withClassUnderTest(TEST_CLASS1)
				.withExcludedClasses(EXCLUDED_CLASSES)
				.withAvoidCallsTo(alternativeAvoidList).build();
		File reportDir = options.getReportDirectory();
		assertTrue(reportDir.isDirectory());
		assertTrue(reportDir.exists());
		assertEquals(TMP_DIR, reportDir.getParentFile());
		assertEquals(TEST_CLASS1, options.getClassUnderTest());
		assertArrayEquals(
				expectedArgs(reportDir, of(testSrcDir), EXCLUDED_CLASSES,
						EMPTY_LIST, alternativeAvoidList, null, TEST_CLASS1),
				options.toCLIArgs());
		assertEquals(
				expectedArgsAsString(reportDir, of(testSrcDir),
						EXCLUDED_CLASSES, EMPTY_LIST, alternativeAvoidList,
						null, TEST_CLASS1), options.toCLIArgsAsString());
	}

	private String randomString() {
		return toHexString(random.nextInt());
	}

	private Object[] expectedArgs(File reportDir, File sourceDir,
			String classUnderTest, String... classpath) {
		return expectedArgs(reportDir, sourceDir, null, classUnderTest,
				classpath);
	}

	private Object[] expectedArgs(File reportDir, File sourceDir,
			File historyLocation, String classUnderTest, String... classpath) {
		return expectedArgs(reportDir, of(sourceDir), EMPTY_LIST, EMPTY_LIST,
				DEFAULT_AVOID_LIST, historyLocation, classUnderTest, classpath);
	}

	private Object[] expectedArgs(File reportDir, List<File> sourceDirs,
			String classUnderTest, String... classpath) {
		return expectedArgs(reportDir, sourceDirs, null, null,
				DEFAULT_AVOID_LIST, null, classUnderTest, classpath);
	}

	private Object[] expectedArgs(File reportDir, List<File> sourceDirs,
			List<String> excludedClasses, List<String> excludedMethods,
			List<String> avoidCallsTo, File historyLocation,
			String classUnderTest, String... classpath) {
		List<String> args = Lists.newArrayList("--failWhenNoMutations",
				"false", "--outputFormats", "HTML", "--threads", "1",
				"--reportDir", reportDir.getPath(), "--targetTests",
				classUnderTest, "--targetClasses");
		if (null != classpath) {
			String result = "";
			for (int i = 0; i < classpath.length; i++) {
				if (i == classpath.length - 1) {
					result += classpath[i];
				} else {
					result += classpath[i] + ",";
				}
			}
			args.add(result);
		}
		args.add("--sourceDirs");
		if (null != sourceDirs) {
			String result = "";
			for (int i = 0; i < sourceDirs.size(); i++) {
				if (i == sourceDirs.size() - 1) {
					result += sourceDirs.get(i).getPath();
				} else {
					result += sourceDirs.get(i).getPath() + ",";
				}
			}
			args.add(result);
		}
		args.add("--verbose");
		if (null != historyLocation) {
			args.add("--historyInputLocation");
			args.add(historyLocation.getPath());
			args.add("--historyOutputLocation");
			args.add(historyLocation.getPath());
		}

		if (null != excludedClasses && !excludedClasses.isEmpty()) {
			args.add("--excludedClasses");
			String result = "";
			for (int i = 0; i < excludedClasses.size(); i++) {
				if (i == excludedClasses.size() - 1) {
					result += excludedClasses.get(i);
				} else {
					result += excludedClasses.get(i) + ",";
				}
			}
			args.add(result);
		}
		if (null != excludedMethods && !excludedMethods.isEmpty()) {
			args.add("--excludedMethods");
			String result = "";
			for (int i = 0; i < excludedMethods.size(); i++) {
				if (i == excludedMethods.size() - 1) {
					result += excludedMethods.get(i);
				} else {
					result += excludedMethods.get(i) + ",";
				}
			}
			args.add(result);
		}
		if (null != avoidCallsTo && !avoidCallsTo.isEmpty()) {
			args.add("--avoidCallsTo");
			String result = "";
			for (int i = 0; i < avoidCallsTo.size(); i++) {
				if (i == avoidCallsTo.size() - 1) {
					result += avoidCallsTo.get(i);
				} else {
					result += avoidCallsTo.get(i) + ",";
				}
			}
			args.add(result);
		}
		return args.toArray();
	}

	private String expectedArgsAsString(File reportDir, File sourceDir,
			List<String> excludedClasses, String classUnderTest,
			String... classpath) {
		return expectedArgsAsString(reportDir, of(sourceDir), excludedClasses,
				EMPTY_LIST, DEFAULT_AVOID_LIST, null, classUnderTest, classpath);
	}

	private String expectedArgsAsString(File reportDir, File sourceDir,
			File historyLocation, String classUnderTest, String... classpath) {
		return expectedArgsAsString(reportDir, of(sourceDir), EMPTY_LIST,
				EMPTY_LIST, DEFAULT_AVOID_LIST, historyLocation,
				classUnderTest, classpath);
	}

	private String expectedArgsAsString(File reportDir, List<File> sourceDirs,
			String classUnderTest, String... classpath) {
		return expectedArgsAsString(reportDir, sourceDirs, EMPTY_LIST,
				EMPTY_LIST, DEFAULT_AVOID_LIST, null, classUnderTest, classpath);
	}

	private String expectedArgsAsString(File reportDir, List<File> sourceDirs,
			List<String> excludedClasses, List<String> excludedMethods,
			List<String> avoidCallsTo, File historyLocation,
			String classUnderTest, String... classpath) {
		Object[] args = expectedArgs(reportDir, sourceDirs, excludedClasses,
				excludedMethods, avoidCallsTo, historyLocation, classUnderTest,
				classpath);
		StringBuilder argsBuilder = new StringBuilder();
		for (Object arg : args) {
			argsBuilder.append(' ').append(arg);
		}
		return argsBuilder.toString().trim();
	}

	private File randomDir() {
		File randomDir = new File(TMP_DIR, randomString());
		randomDir.deleteOnExit();
		return randomDir;
	}

	private File randomFile() {
		File randomFile = new File(randomDir(), randomString());
		randomFile.deleteOnExit();
		return randomFile;
	}
}
