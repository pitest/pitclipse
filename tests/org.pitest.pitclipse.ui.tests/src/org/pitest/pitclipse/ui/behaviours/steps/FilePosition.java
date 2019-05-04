/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
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

package org.pitest.pitclipse.ui.behaviours.steps;


public class FilePosition {

    public final String className;
    public final int lineNumber;

    private FilePosition(String className, int lineNumber) {
        this.className = className;
        this.lineNumber = lineNumber;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        public String fileName;
        public int lineNumber;

        public FilePosition build() {
            return new FilePosition(fileName, lineNumber);
        }

        public Builder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder withLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }
    }
}
