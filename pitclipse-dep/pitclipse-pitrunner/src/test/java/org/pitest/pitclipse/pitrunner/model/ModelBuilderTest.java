package org.pitest.pitclipse.pitrunner.model;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.pitest.pitclipse.pitrunner.results.DetectionStatus.KILLED;
import static org.pitest.pitclipse.pitrunner.results.DetectionStatus.SURVIVED;

import java.io.File;
import java.math.BigInteger;
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

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
	private EclipseStructureService eclipseStructureService;

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
		givenAClass(CLASS_A).hasPackage(PACKAGE_A).isIn(PROJECT_1).hasADetectedMutationOnLine(123)
				.andHasASurvivingMutationOnLine(234);
		andClass(CLASS_B).hasPackage(PACKAGE_B).isIn(PROJECT_1).hasASurvivingMutationOnLine(345);
		whenTheModelIsBuilt();
		thenTheResultIsAsExpected();
	}

	private TestClassContext andClass(String className) {
		return givenAClass(className);
	}

	private TestClassContext givenAClass(String className) {
		TestClassContext context = new TestClassContext(className);
		testContexts.add(context);
		return context;
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

		public TestClassContext hasPackage(String pkg) {
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
				Status.builder().withDetectionStatus(KILLED).withProjectMutations(project1KilledMutations).build(),
				Status.builder().withDetectionStatus(SURVIVED).withProjectMutations(project1SurvivedMutations).build());
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
				.withMutator(MUTATOR).build());
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
}
