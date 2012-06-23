package org.pitest.pitclipse.pitrunner;

import static org.testng.AssertJUnit.assertArrayEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static java.lang.Integer.toHexString;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.pitest.pitclipse.pitrunner.PITOptions.PITLaunchException;
import org.pitest.pitclipse.pitrunner.PITOptions.PITOptionsBuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class PITOptionsTest {

	private static final File TMP_DIR = new File(
			System.getProperty("java.io.tmpdir"));
	private final Random random = new Random();
	private final File testTmpDir = randomDir();
	private final File testSrcDir = randomDir();
	private final File anotherTestSrcDir = randomDir();
	private static final File REALLY_BAD_PATH = new File("BADDRIVE:\\");
	private static final String TEST_CLASS1 = PITOptionsTest.class.getCanonicalName();
	private static final String TEST_CLASS2 = PITRunner.class.getCanonicalName();
	private static final List<String> CLASS_PATH = ImmutableList.of(TEST_CLASS1, TEST_CLASS2);
	
	@BeforeMethod
	public void setup() {
		for (File dir : ImmutableList.of(testTmpDir, testSrcDir, anotherTestSrcDir)) {
			dir.mkdirs();
			dir.deleteOnExit();
		}
	}
	
	@AfterClass
	public static void cleanupFiles() {
		
	}
	
	@Test(expectedExceptions = PITLaunchException.class)
	public void defaultOptionsThrowException() throws IOException {
		new PITOptionsBuilder().build();
	}

	@Test(expectedExceptions = PITLaunchException.class)
	public void validSourceDirButNoTestClassThrowsException() throws IOException {
		new PITOptionsBuilder().withSourceDirectory(testSrcDir).build();
	}
	
	@Test
	public void minimumOptionsSet() throws IOException {
		PITOptions options = new PITOptionsBuilder().withSourceDirectory(testSrcDir).withClassUnderTest(TEST_CLASS1).build();
		File reportDir = options.getReportDirectory();
		assertTrue(reportDir.isDirectory());
		assertTrue(reportDir.exists());
		assertEquals(TMP_DIR, reportDir.getParentFile());
		assertEquals(TEST_CLASS1, options.getClassUnderTest());
		assertArrayEquals(expectedArgs(reportDir, testSrcDir, TEST_CLASS1),
				options.toCLIArgs());
		assertEquals(expectedArgsAsString(reportDir, testSrcDir, TEST_CLASS1), options.toCLIArgsAsString());
	}
	
	@Test(expectedExceptions = PITLaunchException.class)
	public void sourceDirectoryDoesNotExist() throws IOException {
		new PITOptionsBuilder().withSourceDirectory(randomDir()).withClassUnderTest(TEST_CLASS1).build();
	}
	
	@Test(expectedExceptions = PITLaunchException.class)
	public void multipleSourceDirectoriesOneDoesNotExist() throws IOException {
		new PITOptionsBuilder().withSourceDirectories(ImmutableList.of(testSrcDir, randomDir())).withClassUnderTest(TEST_CLASS1).build();
	}
	
	@Test
	public void multipleSourceDirectoriesExist() throws IOException {
		List<File> srcDirs = ImmutableList.of(testSrcDir, anotherTestSrcDir);
		PITOptions options = new PITOptionsBuilder().withSourceDirectories(srcDirs).withClassUnderTest(TEST_CLASS1).build();
		File reportDir = options.getReportDirectory();
		assertTrue(reportDir.isDirectory());
		assertTrue(reportDir.exists());
		assertEquals(TMP_DIR, reportDir.getParentFile());
		assertEquals(TEST_CLASS1, options.getClassUnderTest());
		assertArrayEquals(expectedArgs(reportDir, srcDirs, TEST_CLASS1),
				options.toCLIArgs());
		assertEquals(expectedArgsAsString(reportDir, srcDirs, TEST_CLASS1), options.toCLIArgsAsString());
	}
	
	@Test
	public void useDifferentReportDirectory() {
		File expectedDir = new File(testTmpDir, randomString());
		assertFalse(expectedDir.exists());
		PITOptions options = new PITOptionsBuilder().withReportDirectory(
				expectedDir).withSourceDirectory(testSrcDir).withClassUnderTest(TEST_CLASS1).build();
		File actualDir = options.getReportDirectory();
		assertTrue(actualDir.isDirectory());
		assertEquals(expectedDir, actualDir);
		assertTrue(expectedDir.exists());
		assertArrayEquals(expectedArgs(expectedDir,testSrcDir , TEST_CLASS1),
				options.toCLIArgs());
		assertEquals(expectedArgsAsString(expectedDir, testSrcDir, TEST_CLASS1), options.toCLIArgsAsString());
	}

	@Test(expectedExceptions = PITLaunchException.class)
	public void useInvalidReportDirectory() {
		new PITOptionsBuilder().withReportDirectory(REALLY_BAD_PATH).withSourceDirectory(testSrcDir).withClassUnderTest(TEST_CLASS1).
				build();
	}
	
	@Test(expectedExceptions = PITLaunchException.class)
	public void useInvalidSourceDirectory() {
		new PITOptionsBuilder().withSourceDirectory(REALLY_BAD_PATH).withClassUnderTest(TEST_CLASS1).
				build();
	}
	
	@Test
	public void useClasspath() throws IOException {
		PITOptions options = new PITOptionsBuilder().withSourceDirectory(testSrcDir).withClassUnderTest(TEST_CLASS1).withClassesToMutate(CLASS_PATH).build();
		File reportDir = options.getReportDirectory();
		assertTrue(reportDir.isDirectory());
		assertTrue(reportDir.exists());
		assertEquals(TMP_DIR, reportDir.getParentFile());
		assertEquals(TEST_CLASS1, options.getClassUnderTest());
		assertArrayEquals(expectedArgs(reportDir, testSrcDir, TEST_CLASS1, TEST_CLASS1, TEST_CLASS2),
				options.toCLIArgs());
		assertEquals(expectedArgsAsString(reportDir, testSrcDir, TEST_CLASS1, TEST_CLASS1, TEST_CLASS2), options.toCLIArgsAsString());
	}

	private String randomString() {
		return toHexString(random.nextInt());
	}
	
	private Object[] expectedArgs(File reportDir, File sourceDir, String classUnderTest,
			String... classpath) {
		return expectedArgs(reportDir, ImmutableList.of(sourceDir), classUnderTest, classpath);
	}
	
	private Object[] expectedArgs(File reportDir, List<File> sourceDirs, String classUnderTest,
			String... classpath) {
		List<String> args = Lists.newArrayList("--outputFormats", "HTML", "--reportDir", reportDir.getPath(), "--targetTests", classUnderTest, "--targetClasses");
		if (null != classpath) {
			for (int i = 0; i < classpath.length; i++) {
				if (i == (classpath.length - 1)) {
					args.add(classpath[i]);
				} else {
					args.add(classpath[i] + ",");
				}
			}
		}
		args.add("--sourceDirs");
		if (null != sourceDirs) {
			String result = "";
			for (int i = 0; i < sourceDirs.size(); i++) {
				if (i == (sourceDirs.size() - 1)) {
					result += sourceDirs.get(i).getPath();
				} else {
					result += sourceDirs.get(i).getPath() + ",";
				}
			}
			args.add(result);
		}
		return args.toArray();
	}
	
	private String expectedArgsAsString(File reportDir, File sourceDir, String classUnderTest,
			String... classpath) {
		return expectedArgsAsString(reportDir, ImmutableList.of(sourceDir), classUnderTest, classpath);
	}
	
	private String expectedArgsAsString(File reportDir, List<File> sourceDirs, String classUnderTest,
			String... classpath) {
		Object[] args = expectedArgs(reportDir, sourceDirs, classUnderTest, classpath);
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
}
