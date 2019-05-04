package org.pitest.pitclipse.runner.results;

public class ObjectFactory {

    public Mutations.Mutation createMutationsMutation() {
        return new Mutations.Mutation();
    }

    public Mutations createMutations() {
        return new Mutations();
    }

}
