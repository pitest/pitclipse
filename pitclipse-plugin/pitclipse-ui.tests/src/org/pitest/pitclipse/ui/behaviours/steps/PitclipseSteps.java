package org.pitest.pitclipse.ui.behaviours.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.pitest.pitclipse.core.PitExecutionMode.PROJECT_ISOLATION;
import static org.pitest.pitclipse.core.PitExecutionMode.WORKSPACE;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.INSTANCE;
import static org.pitest.pitclipse.ui.util.AssertUtil.assertDoubleEquals;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.pitest.pitclipse.core.PitExecutionMode;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PackageContext;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitView;

public class PitclipseSteps {

	public class SelectProject implements Runnable {

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
		PitView pitView = INSTANCE.getPitView();
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

	public void openPitConfig(String projectName) {
		INSTANCE.getRunMenu().runConfigurations();
	}

	@When("the isolate tests at project scope preference is selected")
	public void testProjectsInIsolation() {
		INSTANCE.getWindowsMenu().setPitExecutionMode(PROJECT_ISOLATION);
	}

	@Then("the project level scope preference is selected")
	public void projectScopePreferenceIsChosen() {
		assertEquals(PROJECT_ISOLATION, INSTANCE.getWindowsMenu()
				.getPitExecutionMode());
	}

	@When("the workspace level scope preference is selected")
	public void testProjectsInWorkspace() {
		INSTANCE.getWindowsMenu().setPitExecutionMode(WORKSPACE);
	}

	@Then("the workspace level scope preference is selected")
	public void workspacePreferenceIsChosen() {
		PitExecutionMode pitExecutionMode = INSTANCE.getWindowsMenu()
				.getPitExecutionMode();
		assertEquals(WORKSPACE, pitExecutionMode);
	}

	@Then("the mutation tests run in parallel preference is selected")
	public void runInParallelPreferenceIsChosen() {
		assertTrue(INSTANCE.getWindowsMenu().isPitRunInParallel());
	}

	@Then("the use incremental analysis preference is not selected")
	public void useIncrementalAnalysis() {
		assertFalse(INSTANCE.getWindowsMenu().isIncrementalAnalysisEnabled());
	}
}
