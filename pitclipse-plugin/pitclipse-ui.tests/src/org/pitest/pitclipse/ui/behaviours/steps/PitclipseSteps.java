package org.pitest.pitclipse.ui.behaviours.steps;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.pitest.pitclipse.reloc.guava.collect.ImmutableSet.copyOf;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;
import static org.pitest.pitclipse.ui.util.AssertUtil.assertDoubleEquals;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.pitest.pitclipse.pitrunner.results.DetectionStatus;
import org.pitest.pitclipse.reloc.guava.base.Splitter;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PackageContext;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitSummaryView;

public class PitclipseSteps {

	private static class SelectProject implements Runnable {

		private final String projectName;

		public SelectProject(String projectName) {
			this.projectName = projectName;
		}

		@Override
		public void run() {
			PAGES.getPackageExplorer().selectProject(projectName);
		}
	}

	private static final class SelectPackageRoot implements Runnable {
		private static final class PackageRootSelector implements PackageContext {
			private final String projectName;
			private final String packageRoot;

			public PackageRootSelector(String projectName, String packageRoot) {
				this.projectName = projectName;
				this.packageRoot = packageRoot;
			}

			@Override
			public String getPackageName() {
				return null;
			}

			@Override
			public String getProjectName() {
				return projectName;
			}

			@Override
			public String getSourceDir() {
				return packageRoot;
			}
		}

		private final PackageRootSelector context;

		private SelectPackageRoot(String packageRoot, String projectName) {
			context = new PackageRootSelector(projectName, packageRoot);
		}

		@Override
		public void run() {
			PAGES.getPackageExplorer().selectPackageRoot(context);
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

			@Override
			public String getPackageName() {
				return packageName;
			}

			@Override
			public String getProjectName() {
				return projectName;
			}

			@Override
			public String getSourceDir() {
				return null;
			}
		}

		private final PackageSelector context;

		private SelectPackage(String packageName, String projectName) {
			context = new PackageSelector(projectName, packageName);
		}

		@Override
		public void run() {
			PAGES.getPackageExplorer().selectPackage(context);
		}
	}

	private static final class SelectTestClass implements Runnable {
		private final String testClassName;
		private final String packageName;
		private final String projectName;

		private SelectTestClass(String testClassName, String packageName, String projectName) {
			this.testClassName = testClassName;
			this.packageName = packageName;
			this.projectName = projectName;
		}

		@Override
		public void run() {
			PAGES.getPackageExplorer().selectClass(testClassName, packageName, projectName);
		}
	}

	@When("test $testClassName in package $packageName is run for project $projectName")
	public void runTest(final String testClassName, final String packageName, final String projectName) {
		runPit(new SelectTestClass(testClassName, packageName, projectName));

	}

	private void runPit(Runnable runnable) {
		int retryCount = 20;
		int counter = 0;
		while (counter < retryCount) {
			try {
				runnable.run();
				PAGES.getRunMenu().runPit();
				return;
			} catch (TimeoutException te) {
				counter++;
			} catch (WidgetNotFoundException wfne) {
				counter++;
			}
		}

	}

	@Then("a coverage report is generated with $classes classes tested with overall coverage of $totalCoverage% and mutation coverage of $mutationCoverage%")
	public void coverageReportGenerated(int classes, double totalCoverage, double mutationCoverage) {
		PitSummaryView pitView = PAGES.getPitSummaryView();
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
		List<PitMutation> expectedMutations = mutationsFromExampleTable(tableOfMutations);
		List<PitMutation> actualMutations = PAGES.getPitMutationsView().getMutations();
		assertThat(actualMutations, is(equalTo(expectedMutations)));
	}

	@When("the following mutation is selected $tableOfMutations")
	public void mutationIsSelected(ExamplesTable tableOfMutations) {
		PitMutation mutation = mutationsFromExampleTable(tableOfMutations).get(0);
		PAGES.getPitMutationsView().select(mutation);
	}

	@Then("the file $fileName is opened at line number $lineNumber")
	public void mutationIsOpened(String fileName, int lineNumber) {
		FilePosition position = PAGES.getPitMutationsView().getLastSelectedMutation();
		assertThat(position.className, is(equalTo(fileName)));
		assertThat(position.lineNumber, is(equalTo(lineNumber)));
	}

	private List<PitMutation> mutationsFromExampleTable(ExamplesTable tableOfMutations) {
		ImmutableList.Builder<PitMutation> projectsBuilder = ImmutableList.builder();
		for (Parameters mutationRow : tableOfMutations.getRowsAsParameters()) {
			DetectionStatus status = statusValueFrom(mutationRow, "status");
			String project = stringValueFrom(mutationRow, "project");
			String pkg = stringValueFrom(mutationRow, "package");
			String className = stringValueFrom(mutationRow, "class");
			int line = intValueFrom(mutationRow, "line");
			String mutation = stringValueFrom(mutationRow, "mutation");
			PitMutation pitMutation = PitMutation.builder().withStatus(status).withProject(project).withPackage(pkg)
					.withClassName(className).withLineNumber(line).withMutation(mutation).build();
			projectsBuilder.add(pitMutation);
		}
		return projectsBuilder.build();

	}

	private String stringValueFrom(Parameters row, String column) {
		return row.valueAs(column, String.class);
	}

	private int intValueFrom(Parameters row, String column) {
		return row.valueAs(column, Integer.class);
	}

	private DetectionStatus statusValueFrom(Parameters row, String column) {
		String status = stringValueFrom(row, column);
		return DetectionStatus.fromValue(status);
	}

	@When("the PIT views are opened")
	public void thePitViewsAreOpened() {
		PAGES.getWindowsMenu().openPitSummaryView();
		PAGES.getWindowsMenu().openPitMutationsView();
	}

	@When("the Console view is closed")
	public void closeTheConsole() {
		PAGES.views().closeConsole();
	}

	@When("tests in package $packageName are run for project $projectName")
	public void runPackageTest(final String packageName, final String projectName) {
		runPit(new SelectPackage(packageName, projectName));
	}

	@When("tests in source root $packageRoot are run for project $projectName")
	public void runPackageRootTest(final String packageRoot, final String projectName) {
		runPit(new SelectPackageRoot(packageRoot, projectName));
	}

	@When("tests are run for project $projectName")
	public void runProjectTest(String projectName) {
		runPit(new SelectProject(projectName));
	}

	@Then("the options passed to Pit match: $configTable")
	public void runtimeOptionsMatch(ExamplesTable configTable) {
		PitOptions options = PAGES.getRunMenu().getLastUsedPitOptions();
		assertThat(configTable.getRowCount(), is(greaterThan(0)));
		assertThat(options, match(configTable.getRow(0)));
	}

	private Matcher<PitOptions> match(final Map<String, String> optionRow) {
		return new TypeSafeMatcher<PitOptions>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("Matches: ").appendValue(optionRow);
			}

			@Override
			public boolean matchesSafely(PitOptions options) {
				boolean match = true;
				match &= checkStringMatch(match, optionRow, "classUnderTest", options.getClassUnderTest());
				match &= checkSetMatch(match, optionRow, "classesToMutate", options.getClassesToMutate());
				match &= checkClasspathMatch(match, optionRow, "projects", options.getSourceDirectories());
				match &= checkSetMatch(match, optionRow, "excludedClasses", options.getExcludedClasses());
				match &= checkSetMatch(match, optionRow, "excludedMethods", options.getExcludedMethods());
				match &= checkSetMatch(match, optionRow, "packagesUnderTest", options.getPackages());
				match &= checkBooleanMatch(match, optionRow, "runInParallel",
						options.getThreads() == Runtime.getRuntime().availableProcessors());
				match &= checkBooleanMatch(match, optionRow, "incrementalAnalysis",
						options.getHistoryLocation() != null);
				match &= checkIntMatch(match, optionRow, "timeoutConst", options.getTimeout());
				match &= checkBigDecimalMatch(match, optionRow, "timeoutFactor", options.getTimeoutFactor());
				return match;
			}

			private boolean checkClasspathMatch(boolean match, Map<String, String> optionRow, String key,
					List<File> sourceDirectories) {
				if (match && optionRow.containsKey(key)) {
					List<String> paths = ImmutableList
							.copyOf(Splitter.on(',').trimResults().omitEmptyStrings().split(optionRow.get(key)));
					if (paths.size() == sourceDirectories.size()) {
						boolean allMatch = true;
						for (int i = 0; i < paths.size(); i++) {
							allMatch &= sourceDirectories.get(i).toString().contains(paths.get(i));
						}
						return allMatch;
					} else
						return false;
				}
				return match;
			}

			private boolean checkStringMatch(boolean match, Map<String, String> optionRow, String key, String value) {
				if (match && optionRow.containsKey(key)) {
					return optionRow.get(key).equals(value);
				}
				return match;
			}

			private boolean checkBooleanMatch(boolean match, Map<String, String> optionRow, String key,
					boolean actualValue) {
				if (match && optionRow.containsKey(key)) {
					boolean expectedValue = Boolean.valueOf(optionRow.get(key));
					return expectedValue == actualValue;
				}
				return match;
			}

			private boolean checkIntMatch(boolean match, Map<String, String> optionRow, String key, int actualValue) {
				if (match && optionRow.containsKey(key)) {
					int expectedValue = Integer.valueOf(optionRow.get(key));
					return expectedValue == actualValue;
				}
				return match;
			}

			private boolean checkBigDecimalMatch(boolean match, Map<String, String> optionRow, String key,
					BigDecimal actualValue) {
				if (match && optionRow.containsKey(key)) {
					BigDecimal expectedValue = new BigDecimal(optionRow.get(key));
					return expectedValue.compareTo(actualValue) == 0;
				}
				return match;
			}

			private boolean checkSetMatch(boolean match, Map<String, String> optionRow, String key,
					List<String> actualValues) {
				if (match && optionRow.containsKey(key)) {
					Set<String> expectedValues = copyOf(
							Splitter.on(',').trimResults().omitEmptyStrings().split(optionRow.get(key)));
					return expectedValues.equals(copyOf(actualValues));
				}
				return match;
			}
		};
	}
}
