package org.pitest.pitclipse.pitrunner;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.pitest.pitclipse.pitrunner.PitOptions.PitOptionsBuilder;

import com.google.common.collect.ImmutableList;

public class PitRunnerTest {

	private static final String TEST_CLASS = PitOptionsTest.class.getCanonicalName();
	private static final List<String> CLASS_PATH = ImmutableList.of("org.pitest.pitclipse.pitrunner.*");//;PITOptions.class.getCanonicalName(), PITRunner.class.getCanonicalName(), PITResults.class.getCanonicalName());
	private final PitRunner runner = new PitRunner();
	
	@Test
	public void runPIT() {
		File srcDir = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java") ;
		
		PitOptions options = new PitOptionsBuilder().withSourceDirectory(srcDir).withClassUnderTest(TEST_CLASS).withClassesToMutate(CLASS_PATH).build();
		PitResults results = runner.runPIT(options);
		assertNotNull(results);
	}

}
