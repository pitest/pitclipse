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

package org.pitest.pitclipse.runner.results;

/**
 * Converts from {@link org.pitest.mutationtest.DetectionStatus} to
 * {@link DetectionStatus}
 */
public class DetectionStatusCoverter {

    private DetectionStatusCoverter() {
    }

    public static DetectionStatus convert(org.pitest.mutationtest.DetectionStatus status) {
        switch (status) {
            case KILLED:
                return DetectionStatus.KILLED;
            case MEMORY_ERROR:
                return DetectionStatus.MEMORY_ERROR;
            case NON_VIABLE:
                return DetectionStatus.NON_VIABLE;
            case NOT_STARTED:
                return DetectionStatus.NOT_STARTED;
            case RUN_ERROR:
                return DetectionStatus.RUN_ERROR;
            case STARTED:
                return DetectionStatus.STARTED;
            case SURVIVED:
                return DetectionStatus.SURVIVED;
            case TIMED_OUT:
                return DetectionStatus.TIMED_OUT;
            case NO_COVERAGE:
            default:
                return DetectionStatus.NO_COVERAGE;
        }
    }

}
