package org.pitest.pitclipse.pitrunner.results.summary;

import static org.pitest.mutationtest.DetectionStatus.KILLED;
import static org.pitest.mutationtest.DetectionStatus.NO_COVERAGE;
import static org.pitest.pitclipse.pitrunner.TestFactory.TEST_FACTORY;

import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.DetectionStatus;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.MutationStatusTestPair;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.pitclipse.example.Foo;
import org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestSugar.SummaryResultWrapper;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

class SummaryResultListenerTestData {
	public static ClassMutationResults anUncoveredMutationOnFoo() {
		return aClassMutationResultForFooWithStatus(NO_COVERAGE);
	}

	public static ClassMutationResults aCoveredMutationOnFoo() {
		return aClassMutationResultForFooWithStatus(KILLED);
	}

	public static SummaryResultWrapper aSummary() {
		return new SummaryResultWrapper(SummaryResult.EMPTY);
	}

	private static final ClassMutationResults aClassMutationResultForFooWithStatus(DetectionStatus detectionStatus) {
		Location location = new Location(ClassName.fromClass(Foo.class), MethodName.fromString("doFoo"), "doFoo");
		MutationIdentifier id = new MutationIdentifier(location, 1, "SomeMutator");
		MutationDetails md = new MutationDetails(id, "org/pitest/pitclipse/example/Foo.java", TEST_FACTORY.aString(),
				9, TEST_FACTORY.aRandomInt(), TEST_FACTORY.aRandomBoolean(), TEST_FACTORY.aRandomBoolean());
		MutationStatusTestPair status = new MutationStatusTestPair(TEST_FACTORY.aRandomInt(), detectionStatus,
				"org.pitest.pitclipse.example.ExampleTest");
		MutationResult mutation = new MutationResult(md, status);
		return new ClassMutationResults(ImmutableList.of(mutation));
	}

}