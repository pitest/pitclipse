package org.pitest.pitclipse.ui.behaviours.steps;

import static org.junit.Assert.assertEquals;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.INSTANCE;
import static org.pitest.pitclipse.ui.util.AssertUtil.assertDoubleEquals;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.pitest.pitclipse.ui.behaviours.Then;
import org.pitest.pitclipse.ui.behaviours.When;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PackageContext;

public class PitclipseSteps {

	@When("test $testClassName in package $packageName is run for project $projectName")
	public void runTest(final String projectName, final String packageName,
			final String testClassName) {
		runPit(new Runnable() {
			public void run() {
				INSTANCE.getPackageExplorer().selectClass(testClassName,
						packageName, projectName);
			}
		});

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

	@Then("a coverage report is generated with $classes classes tested with overall coverage of $totalCoverage and mutation coverage of $mutationCoverage")
	public void coverageReportGenerated(int classes, double totalCoverage,
			double mutationCoverage) {
		INSTANCE.getPitView().waitForUpdate();
		assertEquals(classes, INSTANCE.getPitView().getClassesTested());
		assertDoubleEquals(totalCoverage, INSTANCE.getPitView()
				.getOverallCoverage());
		assertDoubleEquals(mutationCoverage, INSTANCE.getPitView()
				.getMutationCoverage());
	}

	@When("tests in package $packageName are run for project $projectName")
	public void runPackageTest(final String projectName,
			final String packageName) {
		runPit(new Runnable() {
			public void run() {
				INSTANCE.getPackageExplorer().selectPackage(
						new PackageContext() {
							public String getPackageName() {
								return packageName;
							}

							public String getProjectName() {
								return projectName;
							}

							public String getSourceDir() {
								return null;
							}
						});
			}
		});
	}

	public void runPackageRootTest(final String projectName,
			final String packageRoot) {
		runPit(new Runnable() {
			public void run() {
				INSTANCE.getPackageExplorer().selectPackageRoot(
						new PackageContext() {
							public String getPackageName() {
								return null;
							}

							public String getProjectName() {
								return projectName;
							}

							public String getSourceDir() {
								return packageRoot;
							}
						});
			}
		});

	}
}
