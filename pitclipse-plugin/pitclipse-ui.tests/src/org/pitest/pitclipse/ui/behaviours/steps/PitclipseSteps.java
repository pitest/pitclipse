package org.pitest.pitclipse.ui.behaviours.steps;

import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.INSTANCE;
import static org.pitest.pitclipse.ui.util.AssertUtil.assertDoubleEquals;

import org.pitest.pitclipse.ui.behaviours.Then;
import org.pitest.pitclipse.ui.behaviours.When;

public class PitclipseSteps {

	@When("test $testClassName in package $packageName is run for project $projectName")
	public void runTest(String projectName, String packageName,
			String testClassName) {
		INSTANCE.getPackageExplorer().selectClass(testClassName, packageName,
				projectName);
		INSTANCE.getRunMenu().runPit();

	}

	@Then("a coverage report is generated with overall coverage of $coverage")
	public void coverageReportGenerated(double expectedCoverage) {
		INSTANCE.getPitView().waitForUpdate();
		double coverage = INSTANCE.getPitView().getOverallCoverage();
		assertDoubleEquals(expectedCoverage, coverage);
	}
}
