package org.pitest.pitclipse.pitrunner.results;

public class ObjectFactory {

	public ObjectFactory() {
	}

	public Mutations.Mutation createMutationsMutation() {
		return new Mutations.Mutation();
	}

	public Mutations createMutations() {
		return new Mutations();
	}

}
