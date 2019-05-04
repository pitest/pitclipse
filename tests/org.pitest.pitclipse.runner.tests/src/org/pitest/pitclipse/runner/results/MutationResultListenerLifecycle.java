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

package org.pitest.pitclipse.runner.results;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import org.pitest.coverage.CoverageDatabase;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResultListener;

public class MutationResultListenerLifecycle<T, F extends ListenerFactory<T>> {

    private final F factory;
    private final CoverageDatabase coverageData;

    public static <T, F extends ListenerFactory<T>> MutationResultListenerLifecycle<T, F> using(F factory,
            CoverageDatabase coverageData) {
        return new MutationResultListenerLifecycle<T, F>(factory, coverageData);
    }

    public Optional<T> handleMutationResults(ImmutableList<ClassMutationResults> results) {
        RecordingDispatcher<T> recordingDispatcher = new RecordingDispatcher<T>();
        ListenerContext<T> context = new ListenerContext<T>(recordingDispatcher, coverageData);
        MutationResultListener listener = factory.apply(context);
        listener.runStart();
        for (ClassMutationResults r : results) {
            listener.handleMutationResult(r);
        }
        listener.runEnd();
        return recordingDispatcher.getResult();
    }

    private MutationResultListenerLifecycle(F factory, CoverageDatabase coverageData) {
        this.factory = factory;
        this.coverageData = coverageData;
    }

    private static class RecordingDispatcher<T> implements Dispatcher<T> {

        private Optional<T> result = Optional.absent();

        @Override
        public void dispatch(T result) {
            this.result = Optional.of(result);
        }

        public Optional<T> getResult() {
            return result;
        }
    }
}
