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

package org.pitest.pitclipse.runner.results.summary;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

import org.pitest.classinfo.ClassInfo;
import org.pitest.coverage.CoverageData;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResult;

/**
 * <p>Summary of the PIT analysis about a specific class.</p>
 * 
 * <p>This class is serializable so that it can be sent from the PIT
 * application running in a background VM to the Eclipse listeners.</p>
 */
class ClassSummary implements Serializable {

    private static final long serialVersionUID = 6039947777282909605L;
    private final Coverage lineCoverage;
    private final String className;
    private final Coverage mutationCoverage;

    private ClassSummary(String className, Coverage lineCoverage, Coverage mutationCoverage) {
        this.className = className;
        this.lineCoverage = lineCoverage;
        this.mutationCoverage = mutationCoverage;
    }

    public static ClassSummary from(ClassMutationResults results, int numberOfCodeLines, int linesCovered) {
        Collection<MutationResult> mutations = results.getMutations();
        int totalMutations = mutations.size();
        int survivedMutations = (int) mutations.stream()
                .filter(m -> !m.getStatus().isDetected())
                .count();
        CoverageData data;
        Coverage lineCoverage = Coverage.from(linesCovered, numberOfCodeLines);
        Coverage mutationCoverage = Coverage.from(totalMutations - survivedMutations, totalMutations);

        return from(results.getMutatedClass().asJavaName(), lineCoverage, mutationCoverage);
    }

    public static ClassSummary from(String className, Coverage lineCoverage, Coverage mutationCoverage) {
        return new ClassSummary(className, lineCoverage, mutationCoverage);
    }

    public Coverage getLineCoverage() {
        return lineCoverage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClassSummary that = (ClassSummary) o;

        return Objects.equals(className, that.className) &&
            Objects.equals(lineCoverage, that.lineCoverage) &&
            Objects.equals(mutationCoverage, that.mutationCoverage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineCoverage, className, mutationCoverage);
    }


    @Override
    public String toString() {
        return "ClassSummary [lineCoverage=" + lineCoverage + ", className=" + className + ", mutationCoverage="
                + mutationCoverage + "]";
    }

}
