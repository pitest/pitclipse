package org.pitest.pitclipse.pitrunner.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.pitest.pitclipse.pitrunner.results.DetectionStatus.KILLED;
import static org.pitest.pitclipse.pitrunner.results.DetectionStatus.SURVIVED;
import static org.pitest.pitclipse.reloc.guava.collect.Collections2.filter;
import static org.pitest.pitclipse.reloc.guava.collect.Collections2.transform;
import static org.pitest.pitclipse.reloc.guava.collect.ImmutableList.copyOf;
import static org.pitest.pitclipse.reloc.guava.collect.Iterables.concat;
import static org.pitest.pitclipse.reloc.guava.collect.Lists.newArrayList;

import java.io.File;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.results.DetectionStatus;
import org.pitest.pitclipse.pitrunner.results.Mutations;
import org.pitest.pitclipse.reloc.guava.base.Function;
import org.pitest.pitclipse.reloc.guava.base.Predicate;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList.Builder;
import org.pitest.pitclipse.reloc.guava.collect.LinkedListMultimap;
import org.pitest.pitclipse.reloc.guava.collect.Multimap;

@RunWith(MockitoJUnitRunner.class)
public class ModelBuilderTest {

	private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));

	private static final String CLASS_A = "foo.bar.A";
	private static final String PACKAGE_A = "foo.bar";
	private static final String CLASS_B = "foo.blip.B";
	private static final String PACKAGE_B = "foo.blip";
	private static final String PROJECT_1 = "project1";
	private static final String PROJECT_2 = "project2";
	private static final String MUTATOR = "org.pitest.mutationtest.engine.gregor.mutators.MathMutator";

	private static final List<String> PROJECTS = ImmutableList.of(PROJECT_1, PROJECT_2);

	@Mock
	private ProjectStructureService eclipseStructureService;

	private Mutations mutations;
	private List<TestClassContext> testContexts;
	private MutationsModel actualModel;

	private MutationsModel expectedModel;

	@Before
	public void setup() {
		mutations = null;
		testContexts = newArrayList();
	}

	@Test
	public void mutationsInTheSameProject() {
		givenAClass(CLASS_A).inPackage(PACKAGE_A).isIn(PROJECT_1).hasADetectedMutationOnLine(124)
				.andHasADetectedMutationOnLine(123).andHasASurvivingMutationOnLine(234);
		andClass(CLASS_B).inPackage(PACKAGE_B).isIn(PROJECT_1).hasASurvivingMutationOnLine(345);
		whenTheModelIsBuilt();
		thenTheResultIsAsExpected();
	}

	@Test
	public void mutationsCanBeCounted() {
		givenAClass(CLASS_A).inPackage(PACKAGE_A).isIn(PROJECT_1).hasADetectedMutationOnLine(124)
				.andHasADetectedMutationOnLine(123).andHasASurvivingMutationOnLine(234);
		andClass(CLASS_B).inPackage(PACKAGE_B).isIn(PROJECT_1).hasASurvivingMutationOnLine(345);
		whenTheModelIsBuilt();
		thenTheNumberOfModelMutationsWillBe(4).andTheNumberOfDetectedPackageMutationsIs(PACKAGE_A, 2)
				.andTheNumberOfSurvivedPackageMutationsIs(PACKAGE_A, 1)
				.andTheNumberOfDetectedPackageMutationsIs(PACKAGE_B, 0)
				.andTheNumberOfSurvivedPackageMutationsIs(PACKAGE_B, 1)
				.andTheNumberOfDetectedClassMutationsWillBe(PACKAGE_A, CLASS_A, 2)
				.andTheNumberOfSurvivedClassMutationsWillBe(PACKAGE_A, CLASS_A, 1)
				.andTheNumberOfDetectedClassMutationsWillBe(PACKAGE_B, CLASS_B, 0)
				.andTheNumberOfSurvivedClassMutationsWillBe(PACKAGE_B, CLASS_B, 1);
	}

	private CountContext thenTheNumberOfModelMutationsWillBe(long count) {
		CountContext context = new CountContext();
		return context.thenTheNumberOfModelMutationsWillBe(count);
	}

	private TestClassContext andClass(String className) {
		return givenAClass(className);
	}

	private TestClassContext givenAClass(String className) {
		TestClassContext context = new TestClassContext(className);
		testContexts.add(context);
		return context;
	}

	private void whenTheModelIsBuilt() {
		setupMocks();
		setupMutations();
		setupExpectedModel();
		PitResults results = PitResults.builder().withHtmlResults(TMP_DIR).withMutations(mutations)
				.withProjects(PROJECTS).build();
		actualModel = new ModelBuilder(eclipseStructureService).buildFrom(results);
	}

	@SuppressWarnings("serial")
	private void setupMutations() {
		final Builder<Mutations.Mutation> builder = ImmutableList.builder();
		for (TestClassContext testClassContext : testContexts) {
			List<Mutations.Mutation> mutations = testClassContext.toExpectedMutation();
			builder.addAll(mutations);
		}
		mutations = new Mutations() {
			{
				mutation = builder.build();
			}
		};
	}

	private void setupMocks() {
		for (TestClassContext testClassContext : testContexts) {
			testClassContext.setupMocks();
		}
	}

	private void setupExpectedModel() {
		List<ProjectMutations> project1KilledMutations = ImmutableList.of(expectedKilledMutationsForProject1());
		List<ProjectMutations> project1SurvivedMutations = ImmutableList.of(expectedSurvivedMutationsForProject1());
		List<Status> statuses = ImmutableList.of(
				Status.builder().withDetectionStatus(SURVIVED).withProjectMutations(project1SurvivedMutations).build(),
				Status.builder().withDetectionStatus(KILLED).withProjectMutations(project1KilledMutations).build());
		expectedModel = MutationsModel.make(statuses);
	}

	private ProjectMutations expectedKilledMutationsForProject1() {
		PackageMutations packageA = packageAKilledMutations();
		List<PackageMutations> packages = ImmutableList.of(packageA);
		return ProjectMutations.builder().withProjectName(PROJECT_1).withPackageMutations(packages).build();
	}

	private ProjectMutations expectedSurvivedMutationsForProject1() {
		PackageMutations packageA = packageASurvivedMutations();
		PackageMutations packageB = packageBMutations();
		List<PackageMutations> packages = ImmutableList.of(packageA, packageB);
		return ProjectMutations.builder().withProjectName(PROJECT_1).withPackageMutations(packages).build();
	}

	private PackageMutations packageBMutations() {
		List<Mutation> mutationsForClassB = ImmutableList.of(Mutation.builder().withStatus(SURVIVED)
				.withMutator(MUTATOR).withLineNumber(345).build());
		ClassMutations classB = ClassMutations.builder().withClassName(CLASS_B).withMutations(mutationsForClassB)
				.build();
		List<ClassMutations> packageBMutations = ImmutableList.of(classB);
		PackageMutations packageB = PackageMutations.builder().withPackageName(PACKAGE_B)
				.withClassMutations(packageBMutations).build();
		return packageB;
	}

	private PackageMutations packageAKilledMutations() {
		List<Mutation> mutationsForClassA = ImmutableList.of(Mutation.builder().withStatus(KILLED).withLineNumber(123)
				.withMutator(MUTATOR).build(),
				Mutation.builder().withStatus(KILLED).withLineNumber(124).withMutator(MUTATOR).build());
		ClassMutations classA = ClassMutations.builder().withClassName(CLASS_A).withMutations(mutationsForClassA)
				.build();
		List<ClassMutations> packageAMutations = ImmutableList.of(classA);
		PackageMutations packageA = PackageMutations.builder().withPackageName(PACKAGE_A)
				.withClassMutations(packageAMutations).build();
		return packageA;
	}

	private PackageMutations packageASurvivedMutations() {
		List<Mutation> mutationsForClassA = ImmutableList.of(Mutation.builder().withStatus(SURVIVED)
				.withMutator(MUTATOR).withLineNumber(234).build());
		ClassMutations classA = ClassMutations.builder().withClassName(CLASS_A).withMutations(mutationsForClassA)
				.build();
		List<ClassMutations> packageAMutations = ImmutableList.of(classA);
		PackageMutations packageA = PackageMutations.builder().withPackageName(PACKAGE_A)
				.withClassMutations(packageAMutations).build();
		return packageA;
	}

	private void thenTheResultIsAsExpected() {
		assertThat(actualModel, is(equalTo(expectedModel)));
	}

	private class TestClassContext {
		private final String className;
		private String pkg;
		private String project;
		private final Multimap<Integer, DetectionStatus> mutations = LinkedListMultimap.create();

		public TestClassContext(String className) {
			this.className = className;
		}

		public TestClassContext andHasASurvivingMutationOnLine(int line) {
			return hasASurvivingMutationOnLine(line);
		}

		public TestClassContext hasASurvivingMutationOnLine(int line) {
			mutations.put(line, SURVIVED);
			return this;
		}

		public TestClassContext hasADetectedMutationOnLine(int line) {
			mutations.put(line, KILLED);
			return this;
		}

		public TestClassContext andHasADetectedMutationOnLine(int line) {
			return hasADetectedMutationOnLine(line);
		}

		public TestClassContext inPackage(String pkg) {
			this.pkg = pkg;
			return this;
		}

		public TestClassContext isIn(String project) {
			this.project = project;
			return this;
		}

		public void setupMocks() {
			when(eclipseStructureService.isClassInProject(className, PROJECT_1)).thenReturn(PROJECT_1.equals(project));
			when(eclipseStructureService.isClassInProject(className, PROJECT_2)).thenReturn(PROJECT_2.equals(project));
			when(eclipseStructureService.packageFrom(project, className)).thenReturn(pkg);
		}

		public List<Mutations.Mutation> toExpectedMutation() {
			ImmutableList.Builder<Mutations.Mutation> builder = ImmutableList.builder();
			for (Entry<Integer, DetectionStatus> entry : mutations.entries()) {
				Mutations.Mutation mutation = new Mutations.Mutation();
				mutation.setMutatedClass(className);
				DetectionStatus detectionStatus = entry.getValue();
				mutation.setStatus(detectionStatus);
				mutation.setLineNumber(BigInteger.valueOf(entry.getKey()));
				mutation.setDetected(detectionStatus == KILLED);
				mutation.setMutator(MUTATOR);
				builder.add(mutation);
			}
			return builder.build();
		}
	}

	private class CountContext {
		private class ClassNameFilter implements Predicate<ClassMutations> {
			private final String className;

			public ClassNameFilter(String className) {
				this.className = className;
			}

			@Override
			public boolean apply(ClassMutations classMutations) {
				return className.equals(classMutations.getClassName());
			}
		}

		private class PackageFilter implements Predicate<PackageMutations> {
			private final String pkg;

			public PackageFilter(String pkg) {
				this.pkg = pkg;
			}

			@Override
			public boolean apply(PackageMutations pkgMutations) {
				return pkg.equals(pkgMutations.getPackageName());
			}
		}

		private class StatusFilter implements Predicate<Status> {
			private final DetectionStatus status;

			public StatusFilter(DetectionStatus status) {
				this.status = status;
			}

			@Override
			public boolean apply(Status status) {
				return this.status == status.getDetectionStatus();
			}
		}

		public CountContext thenTheNumberOfModelMutationsWillBe(long count) {
			assertThat(actualModel.count(), is(equalTo(count)));
			return this;
		}

		public CountContext andTheNumberOfDetectedPackageMutationsIs(String pkg, long count) {
			assertThat(countMutationsInPackage(pkg, KILLED), is(equalTo(count)));
			return this;
		}

		public CountContext andTheNumberOfSurvivedPackageMutationsIs(String pkg, long count) {
			assertThat(countMutationsInPackage(pkg, SURVIVED), is(equalTo(count)));
			return this;
		}

		public CountContext andTheNumberOfDetectedClassMutationsWillBe(String pkg, String className, long count) {
			assertThat(countMutationsForClass(pkg, className, KILLED), is(equalTo(count)));
			return this;
		}

		public CountContext andTheNumberOfSurvivedClassMutationsWillBe(String pkg, String className, long count) {
			assertThat(countMutationsForClass(pkg, className, SURVIVED), is(equalTo(count)));
			return this;
		}

		private long countMutationsForClass(String pkg, String className, DetectionStatus status) {
			Collection<Status> statuses = filter(actualModel.getStatuses(), new StatusFilter(status));
			Collection<ClassMutations> mutationsForClass = filterByPackageAndClass(statuses, pkg, className);
			long sum = total(mutationsForClass);
			return sum;
		}

		private long countMutationsInPackage(String pkg, DetectionStatus status) {
			Collection<Status> statuses = filter(actualModel.getStatuses(), new StatusFilter(status));
			return total(filterByPackage(statuses, pkg));
		}

		private long total(Collection<? extends Countable> counts) {
			long sum = 0L;
			for (Countable count : counts) {
				sum += count.count();
			}
			return sum;
		}

		private Collection<PackageMutations> filterByPackage(Collection<Status> statuses, String pkg) {
			List<PackageMutations> allPackageMutations = packageMutationsFrom(statuses);
			Collection<PackageMutations> filteredForPackage = filter(allPackageMutations, new PackageFilter(pkg));
			return filteredForPackage;
		}

		private List<PackageMutations> packageMutationsFrom(Collection<Status> statuses) {
			return copyOf(concat(transform(statuses, new Function<Status, List<PackageMutations>>() {
				@Override
				public List<PackageMutations> apply(Status status) {
					Builder<PackageMutations> pkgBuilder = ImmutableList.builder();
					List<ProjectMutations> projectMutations = status.getProjectMutations();
					for (ProjectMutations projectMutation : projectMutations) {
						pkgBuilder.addAll(projectMutation.getPackageMutations());
					}
					return pkgBuilder.build();
				}
			})));
		}

		private Collection<ClassMutations> filterByPackageAndClass(Collection<Status> statuses, String pkg,
				String className) {
			Collection<PackageMutations> filteredForPackage = filterByPackage(statuses, pkg);
			Collection<ClassMutations> mutationsForClass = filter(
					copyOf(concat(transform(filteredForPackage, new Function<PackageMutations, List<ClassMutations>>() {
						@Override
						public List<ClassMutations> apply(PackageMutations pkg) {
							return pkg.getClassMutations();
						}
					}))), new ClassNameFilter(className));
			return mutationsForClass;
		}
	}
}
