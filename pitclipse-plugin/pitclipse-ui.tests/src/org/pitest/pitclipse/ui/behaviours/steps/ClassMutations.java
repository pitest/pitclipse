package org.pitest.pitclipse.ui.behaviours.steps;

import com.google.common.collect.Multimap;

public class ClassMutations {

	private final String className;
	private final Multimap<Integer, String> mutations;

	public ClassMutations(String className, Multimap<Integer, String> mutations) {
		this.className = className;
		this.mutations = mutations;
	}

}
