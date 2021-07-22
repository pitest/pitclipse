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

import java.io.File;
import java.io.IOException;

/**
 * @author Lorenzo Bettini
 *
 */
public class PitFileUtils {

    private PitFileUtils() {
        // only static methods
    }

    public static void createParentDirs(File file) throws IOException {
        File parent = file.getCanonicalFile().getParentFile();
        parent.mkdirs();
        // make sure the parent directory has been effectively created
        if (!parent.isDirectory()) {
            throw new IOException("Cannot create parent directories of " + file);
        }
    }

    /**
     * Searches for a file with the given name in the given directory,
     * recursively.
     * 
     * @param dir
     * @param fileName
     * @return null if no such a file exists
     */
    public static File findFile(File dir, String fileName) {
        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }
        for (File file : files) {
            if (fileName.equals(file.getName())) {
                return file;
            }
        }
        for (File file : files) {
            if (file.isDirectory()) {
                File result = findFile(file, fileName);
                if (null != result) {
                    return result;
                }
            }
        }
        return null;
    }

}
