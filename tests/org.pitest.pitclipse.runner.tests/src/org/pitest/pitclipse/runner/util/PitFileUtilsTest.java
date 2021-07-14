/*******************************************************************************
 * Copyright 2021 Lorenzo Bettini and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.pitest.pitclipse.runner.util;

import static java.lang.Integer.toHexString;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;

public class PitFileUtilsTest {

    private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));
    private static final Random RANDOM = new Random();

    private static File getBadPath() {
        if (IS_OS_WINDOWS) {
            return new File("BAD_DRIVE:\\");
        }
        return new File("/HOPEFULLY/DOES/NOT/EXIST/SO/IS/BAD/");
    }

    @Test(expected = IOException.class)
    public void testBadPathForCreateParentDirs() throws IOException {
        PitFileUtils.createParentDirs(getBadPath());
    }

    @Test
    public void testGoodPathForCreateParentDir() throws IOException {
        File f = randomFile();
        PitFileUtils.createParentDirs(f);
        assertFalse(f.isDirectory());
        assertTrue(f.getParentFile().exists());
    }

    private String randomString() {
        return toHexString(RANDOM.nextInt());
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
