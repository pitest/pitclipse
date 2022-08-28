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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lorenzo Bettini
 *
 */
public class PitUtils {

    private PitUtils() {
        // only static methods
    }

    public static List<String> splitBasedOnComma(String elements) {
        return Arrays.stream(elements.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
