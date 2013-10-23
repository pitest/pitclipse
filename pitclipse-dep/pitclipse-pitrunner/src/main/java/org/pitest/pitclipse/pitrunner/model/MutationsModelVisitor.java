package org.pitest.pitclipse.pitrunner.model;

public interface MutationsModelVisitor<T> {
	T visitModel(MutationsModel mutationsModel);

	T visitProject(ProjectMutations projectMutations);

	T visitPackage(PackageMutations packageMutations);

	T visitClass(ClassMutations classMutations);

	T visitMutation(Mutation mutation);

	T visitStatus(Status status);
}
