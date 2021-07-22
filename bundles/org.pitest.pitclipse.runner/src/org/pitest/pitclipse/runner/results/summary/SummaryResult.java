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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Summary of the whole PIT analysis.</p>
 * 
 * <p>This class is serializable so that it can be sent from the PIT
 * application running in a background VM to the Eclipse listeners.</p>
 */
public class SummaryResult implements Serializable {

    public static final SummaryResult EMPTY = new SummaryResult();
    private static final long serialVersionUID = 5598868063090306204L;
    private final ImmutableList<ClassSummary> summaries;

    private SummaryResult() {
        this(ImmutableList.<ClassSummary>of());
    }

    private SummaryResult(ImmutableList<ClassSummary> summaries) {
        this.summaries = summaries;
    }

    public SummaryResult update(ClassSummary classSummary) {
        Builder<ClassSummary> builder = ImmutableList.builder();
        builder.addAll(getSummaries());
        builder.add(classSummary);
        return new SummaryResult(builder.build());
    }

    @Override
    public String toString() {
        return "SummaryResult [summaries=" + summaries + "]";
    }

    public List<ClassSummary> getSummaries() {
        return summaries;
    }
}
