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

package org.pitest.pitclipse.ui.swtbot;

public class PitResultsView {

    private final double totalCoverage;
    private final double mutationCoverage;
    private final int classesTested;

    public PitResultsView(int classesTested, double totalCoverage,
            double mutationCoverage) {
        this.classesTested = classesTested;
        this.totalCoverage = totalCoverage;
        this.mutationCoverage = mutationCoverage;
    }

    public int getClassesTested() {
        return classesTested;
    }

    public double getMutationCoverage() {
        return mutationCoverage;
    }

    public double getTotalCoverage() {
        return totalCoverage;
    }

    @Override
    public String toString() {
        return "PitResultsView [totalCoverage=" + totalCoverage
                + ", mutationCoverage=" + mutationCoverage + ", classesTested="
                + classesTested + "]";
    }

    public static final class Builder {

        private double totalCoverage = 0.0d;
        private double mutationCoverage = 0.0d;
        private int classesTested = 0;

        private Builder() {
        };

        public PitResultsView build() {
            return new PitResultsView(classesTested, totalCoverage,
                    mutationCoverage);
        }

        public Builder withTotalCoverage(double totalCoverage) {
            this.totalCoverage = totalCoverage;
            return this;
        }

        public Builder withMutationCoverage(double mutationCoverage) {
            this.mutationCoverage = mutationCoverage;
            return this;
        }

        public Builder withClassesTested(int classesTested) {
            this.classesTested = classesTested;
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
