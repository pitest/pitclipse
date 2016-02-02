package org.pitest.pitclipse.pitrunner;

import static java.lang.Integer.toHexString;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

import java.io.File;
import java.util.Random;

public class FileSystemSupport {

	private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));
	private final Random random = new Random();

	public File getBadPath() {
		if (IS_OS_WINDOWS) {
			return new File("BADDRIVE:\\");
		}
		return new File("/HOPEFULLY/DOES/NOT/EXIST/SO/IS/BAD/");
	}

	public File randomFile() {
		File randomFile = new File(randomDir(), randomString());
		randomFile.deleteOnExit();
		return randomFile;
	}

	public File randomDir() {
		File randomDir = new File(TMP_DIR, randomString());
		randomDir.deleteOnExit();
		return randomDir;
	}

	private String randomString() {
		return toHexString(random.nextInt());
	}
}
