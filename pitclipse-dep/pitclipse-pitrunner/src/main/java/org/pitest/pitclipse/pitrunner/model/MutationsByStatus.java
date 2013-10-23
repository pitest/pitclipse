package org.pitest.pitclipse.pitrunner.model;

import org.pitest.pitclipse.pitrunner.results.DetectionStatus;
import org.pitest.pitclipse.pitrunner.results.Mutations.Mutation;

import com.google.common.collect.Multimap;

public interface MutationsByStatus extends Multimap<DetectionStatus, Mutation> {

}
