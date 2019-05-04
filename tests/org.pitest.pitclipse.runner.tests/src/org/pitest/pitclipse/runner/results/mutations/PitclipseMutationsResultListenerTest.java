package org.pitest.pitclipse.runner.results.mutations;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    public void noMutations() {
        givenNoMutations();
        whenPitIsExecuted();
        thenTheResultsWere(empty());
    }

    @Test
    public void aClassMutationResultFromPitIsConvertedAndDispatched() {
        given(aClassMutationResult());
        whenPitIsExecuted();
        thenTheResultsWere(aMutationResult());
    }

}
