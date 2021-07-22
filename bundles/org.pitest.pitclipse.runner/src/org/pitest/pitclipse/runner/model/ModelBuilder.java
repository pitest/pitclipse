/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.runner.model;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Multimaps.filterKeys;
import static com.google.common.collect.Multimaps.transformValues;

import java.util.Collection;
import java.util.List;

import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.runner.results.DetectionStatus;
import org.pitest.pitclipse.runner.results.Mutations;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;

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
            List<org.pitest.pitclipse.runner.results.Mutations.Mutation> mutationsForStatus = selectMutationsByStatus(
                    mutations, status);
            if (!mutationsForStatus.isEmpty()) {
                ImmutableList.Builder<ProjectMutations> projectMutations = ImmutableList.builder();
                for (String project : projects) {
                    ProjectMutations projectMutation = buildProjectMutation(project, mutationsForStatus);
                    if (!projectMutation.getPackageMutations().isEmpty()) {
                        projectMutations.add(projectMutation);
                    }
                }
                builder.add(Status.builder().withDetectionStatus(status).withProjectMutations(projectMutations.build())
                        .build());
            }
        }
        return builder.build();
    }

    private List<org.pitest.pitclipse.runner.results.Mutations.Mutation> selectMutationsByStatus(
            Mutations mutations, final DetectionStatus status) {
        List<org.pitest.pitclipse.runner.results.Mutations.Mutation> allMutations = copyOf(mutations.getMutation());
        Collection<org.pitest.pitclipse.runner.results.Mutations.Mutation> filtered = filter(allMutations,
                new Predicate<org.pitest.pitclipse.runner.results.Mutations.Mutation>() {
                    @Override
                    public boolean apply(org.pitest.pitclipse.runner.results.Mutations.Mutation mutation) {
                        return status == mutation.getStatus();
                    }
                });
        return ImmutableList.copyOf(filtered);
    }

    private ProjectMutations buildProjectMutation(String project,
            List<org.pitest.pitclipse.runner.results.Mutations.Mutation> mutations) {
        List<PackageMutations> packages = buildPackageMutationsFor(project, mutations);
        return ProjectMutations.builder().withProjectName(project)
                .withPackageMutations(packages).build();
    }

    private List<PackageMutations> buildPackageMutationsFor(String project,
            List<org.pitest.pitclipse.runner.results.Mutations.Mutation> mutations) {
        List<ClassMutations> classMutations = buildClassMutationsFor(project, mutations);
        return packageMutationsFrom(project, classMutations);
    }

    private List<PackageMutations> packageMutationsFrom(final String project, List<ClassMutations> classMutations) {
        Multimap<String, ClassMutations> mutationsByPackage = Multimaps.index(classMutations,
                mutations -> eclipseStructureService.packageFrom(project, mutations.getClassName()));
        ImmutableList.Builder<PackageMutations> builder = ImmutableList.builder();
        for (String pkg : mutationsByPackage.keySet()) {
            builder.add(PackageMutations.builder().withPackageName(pkg).withClassMutations(mutationsByPackage.get(pkg))
                    .build());
        }
        return builder.build();
    }

    private List<ClassMutations> buildClassMutationsFor(final String project,
            List<org.pitest.pitclipse.runner.results.Mutations.Mutation> mutations) {
        Multimap<String, org.pitest.pitclipse.runner.results.Mutations.Mutation> mutationsByClass = Multimaps.index(
                mutations, Mutations.Mutation::getMutatedClass);
        Multimap<String, org.pitest.pitclipse.runner.results.Mutations.Mutation> mutationsForProject = filterKeys(
                mutationsByClass, new Predicate<String>() {
                    @Override
                    public boolean apply(String mutatedClass) {
                        return eclipseStructureService.isClassInProject(mutatedClass, project);
                    }
                });
        Multimap<String, Mutation> transformedMutations = transformValues(mutationsForProject,
                dtoMutation -> Mutation.builder().withKillingTest(dtoMutation.getKillingTest())
                        .withLineNumber(dtoMutation.getLineNumber().intValue())
                        .withMutatedMethod(dtoMutation.getMutatedMethod())
                        .withMutator(dtoMutation.getMutator()).withStatus(dtoMutation.getStatus())
                        .withDescription(dtoMutation.getDescription()).build());
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
