package org.pitest.pitclipse.ui.behaviours.steps;

import static com.google.common.collect.ImmutableSet.copyOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.INSTANCE;
import static org.pitest.pitclipse.ui.util.AssertUtil.assertDoubleEquals;

import java.io.File;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Parameters;
import org.pitest.pitclipse.pitrunner.PitOptions;
import org.pitest.pitclipse.pitrunner.results.Mutations.Mutation;
import org.pitest.pitclipse.pitrunner.results.ObjectFactory;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PackageContext;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitSummaryView;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class PitclipseSteps {

	private static class SelectProject implements Runnable {

		private final String projectName;

		public SelectProject(String projectName) {
			this.projectName = projectName;
		}

		public void run() {
			INSTANCE.getPackageExplorer().selectProject(projectName);
		}
	}

	private static final class SelectPackageRoot implements Runnable {
		private static final class PackageRootSelector implements
				PackageContext {
			private final String projectName;
			private final String packageRoot;

			public PackageRootSelector(String projectName, String packageRoot) {
				this.projectName = projectName;
				this.packageRoot = packageRoot;
			}

			public String getPackageName() {
				return null;
			}

			public String getProjectName() {
				return projectName;
			}

			public String getSourceDir() {
				return packageRoot;
			}
		}

		private final PackageRootSelector context;

		private SelectPackageRoot(String packageRoot, String projectName) {
			context = new PackageRootSelector(projectName, packageRoot);
		}

		public void run() {
			INSTANCE.getPackageExplorer().selectPackageRoot(context);
		}
	}

	private static final class SelectPackage implements Runnable {
		private static final class PackageSelector implements PackageContext {
			private final String projectName;
			private final String packageName;

			public PackageSelector(String projectName, String packageName) {
				this.projectName = projectName;
				this.packageName = packageName;
			}

			public String getPackageName() {
				return packageName;
			}

			public String getProjectName() {
				return projectName;
			}

			public String getSourceDir() {
				return null;
			}
		}

		private final PackageSelector context;

		private SelectPackage(String packageName, String projectName) {
			context = new PackageSelector(projectName, packageName);
		}

		public void run() {
			INSTANCE.getPackageExplorer().selectPackage(context);
		}
	}

	private static final class SelectTestClass implements Runnable {
		private final String testClassName;
		private final String packageName;
		private final String projectName;

		private SelectTestClass(String testClassName, String packageName,
				String projectName) {
			this.testClassName = testClassName;
			this.packageName = packageName;
			this.projectName = projectName;
		}

		public void run() {
			INSTANCE.getPackageExplorer().selectClass(testClassName,
					packageName, projectName);
		}
	}

	@When("test $testClassName in package $packageName is run for project $projectName")
	public void runTest(final String testClassName, final String packageName,
			final String projectName) {
		runPit(new SelectTestClass(testClassName, packageName, projectName));

	}

	private void runPit(Runnable runnable) {
		int retryCount = 20;
		int counter = 0;
		while (counter < retryCount) {
			try {
				runnable.run();
				INSTANCE.getRunMenu().runPit();
				return;
			} catch (TimeoutException te) {
				counter++;
			} catch (WidgetNotFoundException wfne) {
				counter++;
			}
		}

	}

	@Then("a coverage report is generated with $classes classes tested with overall coverage of $totalCoverage% and mutation coverage of $mutationCoverage%")
	public void coverageReportGenerated(int classes, double totalCoverage,
			double mutationCoverage) {
		PitSummaryView pitView = INSTANCE.getPitSummaryView();
		pitView.waitForUpdate();
		try {
			assertEquals(classes, pitView.getClassesTested());
			assertDoubleEquals(totalCoverage, pitView.getOverallCoverage());
			assertDoubleEquals(mutationCoverage, pitView.getMutationCoverage());
		} catch (Error e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Then("the mutation results are $tableOfMutations")
	public void mutationsAre(ExamplesTable tableOfMutations) {
		WorkspaceMutations expectedMutations = mutationsFromExampleTable(tableOfMutations);
		WorkspaceMutations actualMutations = INSTANCE.getPitMutationsView()
				.getMutations();
		assertThat(actualMutations.getProjectMutations(),
				is(equivalentTo(expectedMutations.getProjectMutations())));
	}

	private Matcher<List<ProjectMutations>> equivalentTo(
			final List<ProjectMutations> expectedMutations) {
		return new TypeSafeMatcher<List<ProjectMutations>>() {

			public void describeTo(Description description) {
				description.appendText(" is equivalent to ").appendValue(
						expectedMutations);
			}

			@Override
			protected boolean matchesSafely(
					List<ProjectMutations> actualMutations) {
				boolean match = false;
				if (listsAreTheSameSize(expectedMutations, actualMutations)) {
					Iterator<ProjectMutations> actualMutationsIterator = actualMutations
							.iterator();
					for (ProjectMutations expectedMutation : expectedMutations) {
						ProjectMutations actualMutation = actualMutationsIterator
								.next();
						match &= isSameProject(expectedMutation, actualMutation)
								&& areMutationsEquivalent(
										expectedMutation.getMutations(),
										actualMutation.getMutations());
					}
				}
				return match;
			}

			private boolean areMutationsEquivalent(
					List<Mutation> expectedMutations,
					List<Mutation> actualMutations) {
				boolean match = false;
				if (listsAreTheSameSize(expectedMutations, actualMutations)) {
					Iterator<Mutation> actualMutationsIterator = actualMutations
							.iterator();
					for (Mutation expectedMutation : expectedMutations) {
						Mutation actualMutation = actualMutationsIterator
								.next();
						match &= areTheSame(expectedMutation, actualMutation);
					}
				}
				return match;
			}

			private boolean areTheSame(Mutation expectedMutation,
					Mutation actualMutation) {
				return new EqualsBuilder()
						.append(expectedMutation.getMutatedClass(),
								actualMutation.getMutatedClass())
						.append(expectedMutation.getLineNumber(),
								actualMutation.getLineNumber())
						.append(expectedMutation.getMutator(),
								actualMutation.getMutator()).isEquals();

			}

			private boolean isSameProject(ProjectMutations expectedMutation,
					ProjectMutations actualMutation) {
				return expectedMutation.getProject().equals(
						actualMutation.getProject());
			}

			private <T> boolean listsAreTheSameSize(List<T> expectedMutations,
					List<T> actualMutations) {
				return expectedMutations.size() == actualMutations.size();
			}
		};
	}

	private static Set<String> projectsFrom(ExamplesTable examplesTable) {
		Builder<String> builder = ImmutableSet.builder();
		for (int i = 0; i < examplesTable.getRowCount(); i++) {
			builder.add(examplesTable.getRow(i).get("project"));
		}
		return builder.build();
	}

	private WorkspaceMutations mutationsFromExampleTable(
			ExamplesTable tableOfMutations) {
		Set<String> projects = projectsFrom(tableOfMutations);
		ImmutableList.Builder<ProjectMutations> projectsBuilder = ImmutableList
				.builder();
		ObjectFactory objectFactory = new ObjectFactory();
		for (String project : projects) {
			ProjectMutations.Builder projectBuilder = ProjectMutations
					.builder().withProjectName(project);
			for (Parameters mutationRow : tableOfMutations
					.getRowsAsParameters()) {
				long lines = mutationRow.valueAs("line", Long.class);
				// String mutation = mutationRow.valueAs("mutation",
				// String.class);

				Mutation mutation = objectFactory.createMutationsMutation();

				mutation.setLineNumber(BigInteger.valueOf(lines));
				mutation.setMutator(mutationRow.valueAs("mutation",
						String.class));
				projectBuilder.addMutation(mutation);
			}
			projectsBuilder.add(projectBuilder.build());
		}
		return WorkspaceMutations.builder()
				.withProjectMutations(projectsBuilder.build()).build();

	}

	@When("the PIT views are opened")
	public void thePitViewsAreOpened() {
		INSTANCE.getWindowsMenu().openPitSummaryView();
		INSTANCE.getWindowsMenu().openPitMutationsView();

	}

	@When("tests in package $packageName are run for project $projectName")
	public void runPackageTest(final String packageName,
			final String projectName) {
		runPit(new SelectPackage(packageName, projectName));
	}

	@When("tests in source root $packageRoot are run for project $projectName")
	public void runPackageRootTest(final String packageRoot,
			final String projectName) {
		runPit(new SelectPackageRoot(packageRoot, projectName));
	}

	@When("tests are run for project $projectName")
	public void runProjectTest(String projectName) {
		runPit(new SelectProject(projectName));
	}

	@Then("the options passed to Pit match: $configTable")
	public void runtimeOptionsMatch(ExamplesTable configTable) {
		PitOptions options = INSTANCE.getRunMenu().getLastUsedPitOptions();
		assertThat(configTable.getRowCount(), is(greaterThan(0)));
		assertThat(options, match(configTable.getRow(0)));
	}

	private Matcher<PitOptions> match(final Map<String, String> optionRow) {
		return new TypeSafeMatcher<PitOptions>() {
			public void describeTo(Description description) {
				description.appendText("Matches: ").appendValue(optionRow);
			}

			@Override
			public boolean matchesSafely(PitOptions options) {
				boolean match = true;
				match &= checkStringMatch(match, optionRow, "classUnderTest",
						options.getClassUnderTest());
				match &= checkSetMatch(match, optionRow, "classesToMutate",
						options.getClassesToMutate());
				match &= checkClasspathMatch(match, optionRow, "projects",
						options.getSourceDirectories());
				match &= checkSetMatch(match, optionRow, "excludedClasses",
						options.getExcludedClasses());
				match &= checkSetMatch(match, optionRow, "excludedMethods",
						options.getExcludedMethods());
				match &= checkSetMatch(match, optionRow, "packagesUnderTest",
						options.getPackages());
				match &= checkBooleanMatch(match, optionRow, "runInParallel",
						options.getThreads() == Runtime.getRuntime()
								.availableProcessors());
				match &= checkBooleanMatch(match, optionRow,
						"incrementalAnalysis",
						options.getHistoryLocation() != null);
				return match;
			}

			private boolean checkClasspathMatch(boolean match,
					Map<String, String> optionRow, String key,
					List<File> sourceDirectories) {
				if (match && optionRow.containsKey(key)) {
					List<String> paths = ImmutableList.copyOf(Splitter.on(',')
							.trimResults().omitEmptyStrings()
							.split(optionRow.get(key)));
					if (paths.size() == sourceDirectories.size()) {
						boolean allMatch = true;
						for (int i = 0; i < paths.size(); i++) {
							allMatch &= sourceDirectories.get(i).toString()
									.contains(paths.get(i));
						}
						return allMatch;
					}
				}
				return false;
			}

			private boolean checkStringMatch(boolean match,
					Map<String, String> optionRow, String key, String value) {
				if (match && optionRow.containsKey(key)) {
					return optionRow.get(key).equals(value);
				}
				return match;
			}

			private boolean checkBooleanMatch(boolean match,
					Map<String, String> optionRow, String key,
					boolean actualValue) {
				if (match && optionRow.containsKey(key)) {
					boolean expectedValue = Boolean.valueOf(optionRow.get(key));
					return expectedValue == actualValue;
				}
				return match;
			}

			private boolean checkSetMatch(boolean match,
					Map<String, String> optionRow, String key,
					List<String> actualValues) {
				if (match && optionRow.containsKey(key)) {
					Set<String> expectedValues = copyOf(Splitter.on(',')
							.trimResults().omitEmptyStrings()
							.split(optionRow.get(key)));
					return expectedValues.equals(copyOf(actualValues));
				}
				return match;
			}
		};
	}

}
