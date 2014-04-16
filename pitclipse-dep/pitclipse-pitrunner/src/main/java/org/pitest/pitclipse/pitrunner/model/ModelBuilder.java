package org.pitest.pitclipse.pitrunner.model;

import static org.pitest.pitclipse.reloc.guava.collect.Collections2.filter;
import static org.pitest.pitclipse.reloc.guava.collect.ImmutableList.copyOf;
import static org.pitest.pitclipse.reloc.guava.collect.Multimaps.filterKeys;
import static org.pitest.pitclipse.reloc.guava.collect.Multimaps.transformValues;

import java.util.Collection;
import java.util.List;

import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.results.DetectionStatus;
import org.pitest.pitclipse.pitrunner.results.Mutations;
import org.pitest.pitclipse.reloc.guava.base.Function;
import org.pitest.pitclipse.reloc.guava.base.Predicate;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.Multimap;
import org.pitest.pitclipse.reloc.guava.collect.Multimaps;
import org.pitest.pitclipse.reloc.guava.collect.Ordering;

public class ModelBuilder {

	private static final Ordering<Mutation> MUTATION_ORDERING = Ordering.from(MutationSorter.INSTANCE);
	private final ProjectStructureService eclipseStructureService;

	public ModelBuilder(ProjectStructureService jdtHelper) {
		this.eclipseStructureService = jdtHelper;
	}

	public MutationsModel buildFrom(PitResults results) {
		Mutations mutations = results.getMutations();
		List<String> projects = results.getProjects();

		List<Status> statuses = buildMutationModelFor(projects, mutations);
		return MutationsModel.make(statuses);
	}

	private List<Status> buildMutationModelFor(List<String> projects, Mutations mutations) {
		ImmutableList.Builder<Status> builder = ImmutableList.builder();
		for (DetectionStatus status : DetectionStatus.values()) {
			List<org.pitest.pitclipse.pitrunner.results.Mutations.Mutation> mutationsForStatus = selectMutationsByStatus(
					mutations, status);
			if (!mutationsForStatus.isEmpty()) {
				ImmutableList.Builder<ProjectMutations> projectMutations = ImmutableList.builder();
				for (String project : projects) {
					ProjectMutations projectMutation = buildProjectMutation(project, mutationsForStatus);
					if (!projectMutation.getPackageMutations().isEmpty())
						projectMutations.add(projectMutation);
				}
				builder.add(Status.builder().withDetectionStatus(status).withProjectMutations(projectMutations.build())
						.build());
			}
		}
		return builder.build();
	}

	private List<org.pitest.pitclipse.pitrunner.results.Mutations.Mutation> selectMutationsByStatus(
			Mutations mutations, final DetectionStatus status) {
		List<org.pitest.pitclipse.pitrunner.results.Mutations.Mutation> allMutations = copyOf(mutations.getMutation());
		Collection<org.pitest.pitclipse.pitrunner.results.Mutations.Mutation> filtered = filter(allMutations,
				new Predicate<org.pitest.pitclipse.pitrunner.results.Mutations.Mutation>() {
					@Override
					public boolean apply(org.pitest.pitclipse.pitrunner.results.Mutations.Mutation mutation) {
						return status == mutation.getStatus();
					}
				});
		return ImmutableList.copyOf(filtered);
	}

	private ProjectMutations buildProjectMutation(String project,
			List<org.pitest.pitclipse.pitrunner.results.Mutations.Mutation> mutations) {
		List<PackageMutations> packages = buildPackageMutationsFor(project, mutations);
		ProjectMutations projectMutations = ProjectMutations.builder().withProjectName(project)
				.withPackageMutations(packages).build();
		return projectMutations;
	}

	private List<PackageMutations> buildPackageMutationsFor(String project,
			List<org.pitest.pitclipse.pitrunner.results.Mutations.Mutation> mutations) {
		List<ClassMutations> classMutations = buildClassMutationsFor(project, mutations);
		return packageMutationsFrom(project, classMutations);
	}

	private List<PackageMutations> packageMutationsFrom(final String project, List<ClassMutations> classMutations) {
		Multimap<String, ClassMutations> mutationsByPackage = Multimaps.index(classMutations,
				new Function<ClassMutations, String>() {
					@Override
					public String apply(ClassMutations mutations) {
						String mutatedClass = mutations.getClassName();
						return eclipseStructureService.packageFrom(project, mutatedClass);
					}
				});
		ImmutableList.Builder<PackageMutations> builder = ImmutableList.builder();
		for (String pkg : mutationsByPackage.keySet()) {
			builder.add(PackageMutations.builder().withPackageName(pkg).withClassMutations(mutationsByPackage.get(pkg))
					.build());
		}
		return builder.build();
	}

	private List<ClassMutations> buildClassMutationsFor(final String project,
			List<org.pitest.pitclipse.pitrunner.results.Mutations.Mutation> mutations) {
		Multimap<String, org.pitest.pitclipse.pitrunner.results.Mutations.Mutation> mutationsByClass = Multimaps.index(
				mutations, new Function<org.pitest.pitclipse.pitrunner.results.Mutations.Mutation, String>() {
					@Override
					public String apply(org.pitest.pitclipse.pitrunner.results.Mutations.Mutation mutation) {
						return mutation.getMutatedClass();
					}
				});
		Multimap<String, org.pitest.pitclipse.pitrunner.results.Mutations.Mutation> mutationsForProject = filterKeys(
				mutationsByClass, new Predicate<String>() {
					@Override
					public boolean apply(String mutatedClass) {
						return eclipseStructureService.isClassInProject(mutatedClass, project);
					}
				});
		Multimap<String, Mutation> transformedMutations = transformValues(mutationsForProject,
				new Function<org.pitest.pitclipse.pitrunner.results.Mutations.Mutation, Mutation>() {
					@Override
					public Mutation apply(org.pitest.pitclipse.pitrunner.results.Mutations.Mutation dtoMutation) {
						Mutation mutation = Mutation.builder().withKillingTest(dtoMutation.getKillingTest())
								.withLineNumber(dtoMutation.getLineNumber().intValue())
								.withMutatedMethod(dtoMutation.getMutatedMethod())
								.withMutator(dtoMutation.getMutator()).withStatus(dtoMutation.getStatus())
								.withDescription(dtoMutation.getDescription()).build();
						return mutation;
					}
				});
		return classMutationsFrom(transformedMutations);
	}

	private List<ClassMutations> classMutationsFrom(Multimap<String, Mutation> mutationsByClass) {
		ImmutableList.Builder<ClassMutations> builder = ImmutableList.builder();
		for (String className : mutationsByClass.keySet()) {
			ImmutableList<Mutation> unsortedMutations = copyOf(mutationsByClass.get(className));
			ImmutableList<Mutation> sortedMutations = MUTATION_ORDERING.immutableSortedCopy(unsortedMutations);
			builder.add(ClassMutations.builder().withClassName(className).withMutations(sortedMutations).build());
		}
		return builder.build();
	}
}
