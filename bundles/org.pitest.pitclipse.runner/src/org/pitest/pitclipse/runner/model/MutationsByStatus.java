package org.pitest.pitclipse.runner.model;

import com.google.common.collect.Multimap;

import org.pitest.pitclipse.runner.results.DetectionStatus;
import org.pitest.pitclipse.runner.results.Mutations.Mutation;

public interface MutationsByStatus extends Multimap<DetectionStatus, Mutation> {
}
