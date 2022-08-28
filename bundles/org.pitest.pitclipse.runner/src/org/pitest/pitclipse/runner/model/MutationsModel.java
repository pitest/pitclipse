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

package org.pitest.pitclipse.runner.model;

import static org.pitest.pitclipse.runner.results.DetectionStatus.KILLED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.MEMORY_ERROR;
import static org.pitest.pitclipse.runner.results.DetectionStatus.NON_VIABLE;
import static org.pitest.pitclipse.runner.results.DetectionStatus.NOT_STARTED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.NO_COVERAGE;
import static org.pitest.pitclipse.runner.results.DetectionStatus.RUN_ERROR;
import static org.pitest.pitclipse.runner.results.DetectionStatus.STARTED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.SURVIVED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.TIMED_OUT;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.pitest.pitclipse.runner.results.DetectionStatus;

public class MutationsModel implements Visitable, Countable {

    private enum StatusComparator implements Comparator<Status> {
        INSTANCE;

        private static final Map<DetectionStatus, Integer> STATUSES_IN_ORDER = indexMap(
                Arrays.asList(
                    SURVIVED, NOT_STARTED, STARTED, KILLED, TIMED_OUT, NON_VIABLE,
                    MEMORY_ERROR, RUN_ERROR, NO_COVERAGE));

        /**
         * Returns a map from the ith element of list to i.
         * 
         * Similar to what Guava Orderig.explicit does
         */
        static Map<DetectionStatus, Integer> indexMap(Collection<DetectionStatus> list) {
            Map<DetectionStatus, Integer> map = new EnumMap<>(DetectionStatus.class);
            int i = 0;
            for (DetectionStatus e : list) {
                map.put(e, i++);
            }
            return map;
        }

        private static int rank(DetectionStatus value) {
            return STATUSES_IN_ORDER.get(value);
        }

        @Override
        public int compare(Status left, Status right) {
            return rank(left.getDetectionStatus()) - rank(right.getDetectionStatus());
        }

    }

    public static final MutationsModel EMPTY_MODEL = make(Collections.emptyList());

    private final List<Status> statuses;

    private MutationsModel(List<Status> statuses) {
        this.statuses = statuses.stream()
            .map(input -> input.copyOf().withModel(MutationsModel.this).build())
            .collect(Collectors.toList());
    }

    public static MutationsModel make(List<Status> statuses) {
        List<Status> sortedStatuses = statuses.stream()
                .sorted(StatusComparator.INSTANCE)
                .collect(Collectors.toList());
        return new MutationsModel(sortedStatuses);
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    @Override
    public <T> T accept(MutationsModelVisitor<T> visitor) {
        return visitor.visitModel(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MutationsModel that = (MutationsModel) o;
        return Objects.equals(statuses, that.statuses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statuses);
    }

    @Override
    public String toString() {
        return "MutationsModel [statuses=" + statuses + "]";
    }

    @Override
    public long count() {
        long sum = 0L;
        for (Status status : statuses) {
            sum += status.count();
        }
        return sum;
    }
}
