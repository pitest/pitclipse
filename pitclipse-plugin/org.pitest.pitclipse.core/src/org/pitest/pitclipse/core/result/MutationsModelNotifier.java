package org.pitest.pitclipse.core.result;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Multimaps.filterKeys;
import static com.google.common.collect.Multimaps.transformValues;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Display;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.core.launch.MutatedClassNotFoundException;
import org.pitest.pitclipse.core.launch.ProjectNotFoundException;
import org.pitest.pitclipse.core.launch.UpdateMutations;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.model.ClassMutations;
import org.pitest.pitclipse.pitrunner.model.Mutation;
import org.pitest.pitclipse.pitrunner.model.MutationsModel;
import org.pitest.pitclipse.pitrunner.model.PackageMutations;
import org.pitest.pitclipse.pitrunner.model.ProjectMutations;
import org.pitest.pitclipse.pitrunner.results.DetectionStatus;
import org.pitest.pitclipse.pitrunner.results.Mutations;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class MutationsModelNotifier implements ResultNotifier<PitResults> {

	@Override
	public void handleResults(PitResults results) {
		Mutations mutations = results.getMutations();
		List<String> projects = results.getProjects();

		List<ProjectMutations> projectMutations = buildMutationModelFor(projects, mutations);
		MutationsModel mutationModel = MutationsModel.make(projectMutations);
		Display.getDefault().asyncExec(new UpdateMutations(mutationModel));
	}

	private List<ProjectMutations> buildMutationModelFor(List<String> projects, Mutations mutations) {
		ImmutableList.Builder<ProjectMutations> builder = ImmutableList.builder();
		for (DetectionStatus status : DetectionStatus.values()) {
			List<org.pitest.pitclipse.pitrunner.results.Mutations.Mutation> mutationsForStatus = selectMutationsByStatus(
					mutations, status);
			for (String project : projects) {
				builder.add(buildProjectMutation(project, mutationsForStatus));
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

	private List<PackageMutations> packageMutationsFrom(String project, List<ClassMutations> classMutations) {
		final IJavaProject javaProject = javaProject(project);
		Multimap<String, ClassMutations> mutationsByPackage = Multimaps.index(classMutations,
				new Function<ClassMutations, String>() {
					@Override
					public String apply(ClassMutations mutations) {
						String mutatedClass = mutations.getClassName();
						try {
							IType type = javaProject.findType(mutatedClass);
							IPackageFragment pkg = type.getPackageFragment();
							return pkg.getElementName();
						} catch (JavaModelException e) {
							throw new MutatedClassNotFoundException(mutatedClass);
						}
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
						return isClassInProject(mutatedClass, project);
					}
				});
		Multimap<String, Mutation> transformedMutations = transformValues(mutationsForProject,
				new Function<org.pitest.pitclipse.pitrunner.results.Mutations.Mutation, Mutation>() {
					@Override
					public Mutation apply(org.pitest.pitclipse.pitrunner.results.Mutations.Mutation dtoMutation) {
						Mutation mutation = Mutation.builder().withKillingTest(dtoMutation.getKillingTest())
								.withLineNumber(dtoMutation.getLineNumber().intValue())
								.withMutatedMethod(dtoMutation.getMutatedMethod())
								.withMutator(dtoMutation.getMutator()).withStatus(dtoMutation.getStatus()).build();
						return mutation;
					}
				});
		return classMutationsFrom(transformedMutations);
	}

	private List<ClassMutations> classMutationsFrom(Multimap<String, Mutation> mutationsByClass) {
		ImmutableList.Builder<ClassMutations> builder = ImmutableList.builder();
		for (String className : mutationsByClass.keySet()) {
			Collection<Mutation> mutations = mutationsByClass.get(className);
			builder.add(ClassMutations.builder().withClassName(className).withMutations(mutations).build());
		}
		return builder.build();
	}

	private boolean isClassInProject(String mutatedClass, String projectName) {
		IJavaProject project = javaProject(projectName);
		try {
			return null != project.findType(mutatedClass);
		} catch (Exception e) {
			return false;
		}
	}

	private IJavaProject javaProject(String projectName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject project : root.getProjects()) {
			if (projectName.equals(project.getName())) {
				if (project.isOpen()) {
					IJavaProject javaProject = JavaCore.create(project);
					return javaProject;
				}
			}
		}
		throw new ProjectNotFoundException(projectName);
	}

}
