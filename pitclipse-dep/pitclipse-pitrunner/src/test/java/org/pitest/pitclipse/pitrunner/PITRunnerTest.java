package org.pitest.pitclipse.pitrunner;

import static org.testng.AssertJUnit.assertNotNull;

import java.io.File;
import java.util.List;

import org.pitest.pitclipse.pitrunner.PITOptions.PITOptionsBuilder;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test
public class PITRunnerTest {

	private static final String TEST_CLASS = PITOptionsTest.class.getCanonicalName();
	private static final List<String> CLASS_PATH = ImmutableList.of("org.pitest.pitclipse.pitrunner.*");//;PITOptions.class.getCanonicalName(), PITRunner.class.getCanonicalName(), PITResults.class.getCanonicalName());
	private final PITRunner runner = new PITRunner();
	
	public void runPIT() {
		File srcDir = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java") ;
		
		PITOptions options = new PITOptionsBuilder().withSourceDirectory(srcDir).withClassUnderTest(TEST_CLASS).withClassesToMutate(CLASS_PATH).build();
		PITResults results = runner.runPIT(options);
		assertNotNull(results);
	}

}
