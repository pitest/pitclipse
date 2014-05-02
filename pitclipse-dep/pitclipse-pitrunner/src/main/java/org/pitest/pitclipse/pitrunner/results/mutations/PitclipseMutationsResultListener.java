package org.pitest.pitclipse.pitrunner.results.mutations;

import java.math.BigInteger;

import org.pitest.functional.SideEffect1;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.pitclipse.pitrunner.results.DetectionStatus;
import org.pitest.pitclipse.pitrunner.results.Mutations;
import org.pitest.pitclipse.pitrunner.results.Mutations.Mutation;
import org.pitest.pitclipse.pitrunner.results.ObjectFactory;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

public class PitclipseMutationsResultListener implements MutationResultListener {

	private final MutationsDispatcher dispatcher;
	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
	private ImmutableList<Mutation> mutations = ImmutableList.of();

	public PitclipseMutationsResultListener(MutationsDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void runStart() {
	}

	@Override
	public void handleMutationResult(ClassMutationResults results) {
		for (final MutationResult result : results.getMutations()) {
			MutationDetails details = result.getDetails();
			final Mutation mutation = OBJECT_FACTORY.createMutationsMutation();
			mutation.setIndex(BigInteger.valueOf(details.getFirstIndex()));
			result.getKillingTest().forEach(new SideEffect1<String>() {
				@Override
				public void apply(String killingTest) {
					mutation.setKillingTest(killingTest);
				}
			});
			mutation.setLineNumber(BigInteger.valueOf(details.getLineNumber()));
			mutation.setMutatedClass(details.getClassName().asJavaName());
			mutation.setMutatedMethod(details.getMethod().name());
			mutation.setMutator(details.getMutator());
			mutation.setSourceFile(details.getFilename());
			mutation.setStatus(convert(result.getStatus()));
			mutation.setDetected(result.getStatus().isDetected());
			mutation.setDescription(details.getDescription());

			this.mutations = ImmutableList.<Mutation> builder().addAll(mutations).add(mutation).build();
		}

	}

	private DetectionStatus convert(org.pitest.mutationtest.DetectionStatus status) {
		switch (status) {
		case KILLED:
			return DetectionStatus.KILLED;
		case MEMORY_ERROR:
			return DetectionStatus.MEMORY_ERROR;
		case NON_VIABLE:
			return DetectionStatus.NON_VIABLE;
		case NOT_STARTED:
			return DetectionStatus.NOT_STARTED;
		case RUN_ERROR:
			return DetectionStatus.RUN_ERROR;
		case STARTED:
			return DetectionStatus.STARTED;
		case SURVIVED:
			return DetectionStatus.SURVIVED;
		case TIMED_OUT:
			return DetectionStatus.TIMED_OUT;
		case NO_COVERAGE:
		default:
			return DetectionStatus.NO_COVERAGE;
		}
	}

	@Override
	public void runEnd() {
		Mutations mutations = OBJECT_FACTORY.createMutations();
		mutations.getMutation().addAll(this.mutations);
		dispatcher.dispatch(mutations);
	}
}
