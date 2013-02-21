package org.pitest.pitclipse.core.launch;

public interface PitclipseConstants {
	String PIT_CONFIGURATION_TYPE = "org.pitest.pitclipse.core.mutationTest";
	String ATTR_TEST_CONTAINER = "org.pitest.pitclipse.core.test.container";
	String ATTR_TEST_IN_PARALLEL = "org.pitest.pitclipse.core.test.parallel";
	String ATTR_TEST_INCREMENTALLY = "org.pitest.pitclipse.core.test.incrementalAnalysis";
	String ATTR_EXCLUDE_CLASSES = "org.pitest.pitclipse.core.test.excludeClasses";
	String LAUNCH_SHORTCUT_TITLE = "Test Selection";
	String TEST_TO_RUN = "Select Test To Run";
	String TEST_CONFIGURATION = "Select a Test Configuration";
	String TEST_RUN_CONFIGURATION = "Select JUnit configuration to run";
	String MUTATION_TESTS_RUN_IN_PARALLEL = "Mutation tests run in para&llel";
	String USE_INCREMENTAL_ANALYSIS = "Use &incremental analysis";
	String EXCLUDE_CLASSES_FROM_PIT = "E&xcluded classes (e.g.*IntTest)";
}