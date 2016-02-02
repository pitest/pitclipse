package org.pitest.pitclipse.pitrunner.model;

import org.pitest.pitclipse.pitrunner.results.DetectionStatus;
import org.pitest.pitclipse.pitrunner.results.Mutations.Mutation;
import org.pitest.pitclipse.reloc.guava.collect.Multimap;

public interface MutationsByStatus extends Multimap<DetectionStatus, Mutation> {
}
