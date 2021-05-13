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

package org.pitest.pitclipse.runner.results.mutations;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.pitest.pitclipse.runner.results.mutations.ListenerTestFixture.aClassMutationResult;
import static org.pitest.pitclipse.runner.results.mutations.ListenerTestFixture.aMutationResult;
import static org.pitest.pitclipse.runner.results.mutations.ListenerTestFixture.empty;
import static org.pitest.pitclipse.runner.results.mutations.ListenerTestFixture.given;
import static org.pitest.pitclipse.runner.results.mutations.ListenerTestFixture.givenNoMutations;
import static org.pitest.pitclipse.runner.results.mutations.ListenerTestFixture.reset;
import static org.pitest.pitclipse.runner.results.mutations.ListenerTestFixture.thenTheResultsWere;
import static org.pitest.pitclipse.runner.results.mutations.ListenerTestFixture.whenPitIsExecuted;

@RunWith(MockitoJUnitRunner.class)
public class PitclipseMutationsResultListenerTest {

    @Mock
    private MutationsDispatcher mutationsDispatcher;

    @Before
    public void setup() {
        reset(mutationsDispatcher);
    }

    @Test
    public void noMutations() { // NOSONAR: assertions made by called methods
        givenNoMutations();
        whenPitIsExecuted();
        thenTheResultsWere(empty());
    }

    @Test
    public void aClassMutationResultFromPitIsConvertedAndDispatched() { // NOSONAR: assertions made by called methods
        given(aClassMutationResult());
        whenPitIsExecuted();
        thenTheResultsWere(aMutationResult());
    }

}
