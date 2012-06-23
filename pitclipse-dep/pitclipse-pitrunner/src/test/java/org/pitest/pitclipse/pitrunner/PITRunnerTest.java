package org.pitest.pitclipse.pitrunner;

import static org.testng.AssertJUnit.assertNotNull;

import java.io.File;
import java.util.List;

import org.pitest.pitclipse.pitrunner.PITOptions.PITOptionsBuilder;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class PITRunnerTest {

	private static final String TEST_CLASS = PITOptionsTest.class.getCanonicalName();
	private static final List<String> CLASS_PATH = ImmutableList.of("org.pitest.pitclipse.pitrunner.*");//;PITOptions.class.getCanonicalName(), PITRunner.class.getCanonicalName(), PITResults.class.getCanonicalName());
	private final PITRunner runner = new PITRunner();
	
	@Test(enabled=false)
	public void runPIT() {
		PITOptions options = new PITOptionsBuilder().withSourceDirectory(new File("C:\\Users\\phil\\tycho\\org.eclipse.tycho-demo\\pitclipse\\pitclipse-pitrunner\\src\\main\\java")).withClassUnderTest(TEST_CLASS).withClassesToMutate(CLASS_PATH).build();
		PITResults results = runner.runPIT(options);
		assertNotNull(results);
	}

}
