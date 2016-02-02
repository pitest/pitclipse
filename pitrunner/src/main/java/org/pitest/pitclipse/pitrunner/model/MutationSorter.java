package org.pitest.pitclipse.pitrunner.model;

import java.util.Comparator;

public enum MutationSorter implements Comparator<Mutation> {
	INSTANCE;

	private static final int LESS_THAN = -1;
	private static final int GREATER_THAN = 1;

	@Override
	public int compare(Mutation lhs, Mutation rhs) {
		if (lhs.getLineNumber() < rhs.getLineNumber())
			return LESS_THAN;
		else if (lhs.getLineNumber() > rhs.getLineNumber())
			return GREATER_THAN;
		else
			return lhs.getMutator().compareTo(rhs.getMutator());
	}

}
