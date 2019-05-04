package org.pitest.pitclipse.runner;

import java.io.File;
import java.util.Random;

import static java.lang.Integer.toHexString;

public class FileSystemSupport {

    private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));
    private final Random random = new Random();

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
