package org.pitest.pitclipse.pitrunner.results.mutations;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.pitest.pitclipse.pitrunner.TestFactory.TEST_FACTORY;
import static org.pitest.pitclipse.pitrunner.results.mutations.ListenerTestFixture.aClassMutationResult;
import static org.pitest.pitclipse.pitrunner.results.mutations.ListenerTestFixture.aMutationResult;
import static org.pitest.pitclipse.pitrunner.results.mutations.ListenerTestFixture.empty;
import static org.pitest.pitclipse.pitrunner.results.mutations.ListenerTestFixture.given;
import static org.pitest.pitclipse.pitrunner.results.mutations.ListenerTestFixture.givenNoMutations;
import static org.pitest.pitclipse.pitrunner.results.mutations.ListenerTestFixture.reset;
import static org.pitest.pitclipse.pitrunner.results.mutations.ListenerTestFixture.thenTheResultsWere;
import static org.pitest.pitclipse.pitrunner.results.mutations.ListenerTestFixture.whenPitIsExecuted;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.MutationStatusTestPair;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.pitclipse.example.Foo;
import org.pitest.pitclipse.pitrunner.results.DetectionStatus;
import org.pitest.pitclipse.pitrunner.results.Mutations;
import org.pitest.pitclipse.pitrunner.results.Mutations.Mutation;
import org.pitest.pitclipse.pitrunner.results.ObjectFactory;
import org.pitest.pitclipse.reloc.guava.base.MoreObjects;
import org.pitest.pitclipse.reloc.guava.base.MoreObjects.ToStringHelper;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

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

class ListenerTestFixture {
	private static final ObjectFactory JAXB_OBJECT_FACTORY = new ObjectFactory();

	public static final ClassMutationResults aClassMutationResult() {
		Location location = new Location(ClassName.fromClass(Foo.class), MethodName.fromString("doFoo"), "doFoo");
		MutationIdentifier id = new MutationIdentifier(location, 1, "SomeMutator");
		MutationDetails md = new MutationDetails(id, "org/pitest/pitclipse/example/Foo.java", TEST_FACTORY.aString(),
				20, TEST_FACTORY.aRandomInt(), TEST_FACTORY.aRandomBoolean(), TEST_FACTORY.aRandomBoolean());
		MutationStatusTestPair status = new MutationStatusTestPair(TEST_FACTORY.aRandomInt(),
				org.pitest.mutationtest.DetectionStatus.KILLED, "org.pitest.pitclipse.example.ExampleTest");
		MutationResult mutation = new MutationResult(md, status);
		return new ClassMutationResults(ImmutableList.of(mutation));
	}

	public static final Mutations aMutationResult() {
		Mutations mutations = JAXB_OBJECT_FACTORY.createMutations();
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
				ToStringHelper expectedResultAsString = MoreObjects.toStringHelper(expectedResult);
				for (Mutation m : expectedResult.getMutation()) {
					expectedResultAsString.add(
							"Mutation",
							MoreObjects.toStringHelper(m).add("index", m.getIndex())
									.add("killingTest", m.getKillingTest()).add("lineNumber", m.getLineNumber())
									.add("mutatedClass", m.getMutatedClass())
									.add("mutatedMethod", m.getMutatedMethod()).add("mutator", m.getMutator())
									.add("sourceFile", m.getSourceFile()).add("status", m.getStatus()).toString());
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
