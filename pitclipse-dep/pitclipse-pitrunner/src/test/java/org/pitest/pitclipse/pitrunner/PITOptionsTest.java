package org.pitest.pitclipse.pitrunner;

import static java.lang.Integer.toHexString;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
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
	private static final File REALLY_BAD_PATH = new File("BADDRIVE:\\");
	private static final String TEST_CLASS1 = PITOptionsTest.class.getCanonicalName();
	private static final String TEST_CLASS2 = PITRunner.class.getCanonicalName();
	private static final List<String> CLASS_PATH = ImmutableList.of(TEST_CLASS1, TEST_CLASS2);
	
	@Before
	public void setup() {
		testTmpDir.mkdirs();
		testSrcDir.mkdirs();
	}
	
	@AfterClass
	public static void cleanupFiles() {
		
	}
	
	@Test(expected = PITLaunchException.class)
	public void defaultOptionsThrowException() throws IOException {
		new PITOptionsBuilder().build();
	}

	@Test(expected = PITLaunchException.class)
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
	}
	
	@Test(expected = PITLaunchException.class)
	public void sourceDirectoryDoesNotExist() throws IOException {
		new PITOptionsBuilder().withSourceDirectory(randomDir()).withClassUnderTest(TEST_CLASS1).build();
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
	}

	@Test(expected = PITLaunchException.class)
	public void useInvalidReportDirectory() {
		new PITOptionsBuilder().withReportDirectory(REALLY_BAD_PATH).withSourceDirectory(testSrcDir).withClassUnderTest(TEST_CLASS1).
				build();
	}
	
	@Test(expected = PITLaunchException.class)
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
	}

	private String randomString() {
		return toHexString(random.nextInt());
	}
	
	private Object[] expectedArgs(File reportDir, File sourceDir, String classUnderTest,
			String... classpath) {
		List<String> args = Lists.newArrayList("--outputFormats", "XML", "--reportDir", reportDir.getPath(), "--sourceDirs", sourceDir.getPath(), "--targetTests", classUnderTest, "--targetClasses");
		if (null != classpath) {
			for (int i = 0; i < classpath.length; i++) {
				if (i == (classpath.length - 1)) {
					args.add(classpath[i]);
				} else {
					args.add(classpath[i] + ",");
				}
			}
		}
		return args.toArray();
	}
	
	private File randomDir() {
		File randomDir = new File(TMP_DIR, randomString());
		randomDir.deleteOnExit();
		return randomDir;
	}
}
