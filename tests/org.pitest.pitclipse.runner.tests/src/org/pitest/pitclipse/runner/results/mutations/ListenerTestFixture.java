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

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.MutationStatusTestPair;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.pitclipse.example.Foo;
import org.pitest.pitclipse.runner.results.DetectionStatus;
import org.pitest.pitclipse.runner.results.Mutations;
import org.pitest.pitclipse.runner.results.Mutations.Mutation;
import org.pitest.pitclipse.runner.results.ObjectFactory;

import java.math.BigInteger;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.pitest.pitclipse.runner.TestFactory.TEST_FACTORY;

class ListenerTestFixture {
    private static final ObjectFactory JAXB_OBJECT_FACTORY = new ObjectFactory();

    public static ClassMutationResults aClassMutationResult() {
        Location location = new Location(ClassName.fromClass(Foo.class), "doFoo", "doFoo");
        MutationIdentifier id = new MutationIdentifier(location, 1, "SomeMutator");
        MutationDetails md = new MutationDetails(id, "org/pitest/pitclipse/example/Foo.java", TEST_FACTORY.aString(),
                20, TEST_FACTORY.aRandomInt());
        MutationStatusTestPair status = new MutationStatusTestPair(TEST_FACTORY.aRandomInt(),
                org.pitest.mutationtest.DetectionStatus.KILLED, "org.pitest.pitclipse.example.ExampleTest");
        MutationResult mutation = new MutationResult(md, status);
        return new ClassMutationResults(ImmutableList.of(mutation));
    }

    public static Mutations aMutationResult() {
        Mutation mutation = JAXB_OBJECT_FACTORY.createMutationsMutation();
        mutation.setDetected(true);
        mutation.setIndex(BigInteger.ONE);
        mutation.setKillingTest("org.pitest.pitclipse.example.ExampleTest");
        mutation.setLineNumber(BigInteger.valueOf(20));
        mutation.setMutatedClass("org.pitest.pitclipse.example.Foo");
        mutation.setMutatedMethod("doFoo");
        mutation.setMutator("SomeMutator");
        mutation.setSourceFile("org/pitest/pitclipse/example/Foo.java");
        mutation.setStatus(DetectionStatus.KILLED);
        
        Mutations mutations = JAXB_OBJECT_FACTORY.createMutations();
        mutations.getMutation().add(mutation);
        
        return mutations;
    }

    private static State state;

    public static void givenNoMutations() {
        ImmutableList<ClassMutationResults> noMutants = ImmutableList.of();
        state.setInput(noMutants);
    }

    public static void given(ClassMutationResults result) {
        ImmutableList<ClassMutationResults> noMutants = ImmutableList.of(result);
        state.setInput(noMutants);
    }

    public static void whenPitIsExecuted() {
        PitclipseMutationsResultListener listener = aListener();
        listener.runStart();
        for (ClassMutationResults classMutationResults : state.getInput()) {
            listener.handleMutationResult(classMutationResults);
        }
        listener.runEnd();
    }

    public static void thenTheResultsWere(Mutations expectedResult) {
        verify(state.getMutationsDispatcher()).dispatch(argThat(isEquivalentTo(expectedResult)));
    }

    private static Matcher<Mutations> isEquivalentTo(final Mutations expectedResult) {
        return new TypeSafeMatcher<Mutations>() {

            @Override
            public void describeTo(Description description) {
                ToStringBuilder expectedResultAsString = new ToStringBuilder(expectedResult);
                for (Mutation m : expectedResult.getMutation()) {
                    expectedResultAsString.append(
                            "Mutation",
                            new ToStringBuilder(m).append("index", m.getIndex())
                                    .append("killingTest", m.getKillingTest())
                                    .append("lineNumber", m.getLineNumber())
                                    .append("mutatedClass", m.getMutatedClass())
                                    .append("mutatedMethod", m.getMutatedMethod())
                                    .append("mutator", m.getMutator())
                                    .append("sourceFile", m.getSourceFile())
                                    .append("status", m.getStatus()).toString());
                }
                description.appendText("is equivalent to: ").appendValue(expectedResultAsString);
            }

            @Override
            protected boolean matchesSafely(Mutations actualResult) {
                List<Mutation> expectedMutations = expectedResult.getMutation();
                List<Mutation> actualMutations = actualResult.getMutation();
                if (expectedMutations.size() == actualMutations.size()) {
                    EqualsBuilder eb = new EqualsBuilder();
                    for (int i = 0; i < expectedMutations.size(); i++) {
                        Mutation expectedMutation = expectedMutations.get(i);
                        Mutation actualMutation = actualMutations.get(i);
                        eb.append(expectedMutation.getIndex(), actualMutation.getIndex());
                        eb.append(expectedMutation.getKillingTest(), actualMutation.getKillingTest());
                        eb.append(expectedMutation.getLineNumber(), actualMutation.getLineNumber());
                        eb.append(expectedMutation.getMutatedClass(), actualMutation.getMutatedClass());
                        eb.append(expectedMutation.getMutatedMethod(), actualMutation.getMutatedMethod());
                        eb.append(expectedMutation.getMutator(), actualMutation.getMutator());
                        eb.append(expectedMutation.getSourceFile(), actualMutation.getSourceFile());
                        eb.append(expectedMutation.getStatus(), actualMutation.getStatus());
                        eb.append(expectedMutation.isDetected(), actualMutation.isDetected());
                    }
                    return eb.isEquals();
                }
                return false;
            }

        };
    }

    public static void reset(MutationsDispatcher mutationsDispatcher) {
        state = new State(mutationsDispatcher);
    }

    public static Mutations empty() {
        return JAXB_OBJECT_FACTORY.createMutations();
    }

    private static PitclipseMutationsResultListener aListener() {
        return new PitclipseMutationsResultListener(state.getMutationsDispatcher());
    }

    private static class State {

        private ImmutableList<ClassMutationResults> input = ImmutableList.of();
        private final MutationsDispatcher mutationsDispatcher;

        public State(MutationsDispatcher mutationsDispatcher) {
            this.mutationsDispatcher = mutationsDispatcher;
        }

        public void setInput(ImmutableList<ClassMutationResults> input) {
            this.input = input;
        }

        public ImmutableList<ClassMutationResults> getInput() {
            return input;
        }

        public MutationsDispatcher getMutationsDispatcher() {
            return mutationsDispatcher;
        }
    }
}
