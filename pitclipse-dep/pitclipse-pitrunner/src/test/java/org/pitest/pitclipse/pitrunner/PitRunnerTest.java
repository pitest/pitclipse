package org.pitest.pitclipse.pitrunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class PitRunnerTest {

	private static final String TEST_CLASS = PitOptionsTest.class
			.getCanonicalName();
	private static final List<String> CLASS_PATH = ImmutableList
			.of("org.pitest.pitclipse.pitrunner.*");// ;PITOptions.class.getCanonicalName(),
													// PITRunner.class.getCanonicalName(),
													// PITResults.class.getCanonicalName());
	private final PitRunner runner = new PitRunner();

	@Test
	public void runPIT() {
		File srcDir = new File(System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "main" + File.separator + "java");

		PitOptions options = PitOptions.builder().withSourceDirectory(srcDir)
				.withClassUnderTest(TEST_CLASS).withClassesToMutate(CLASS_PATH)
				.build();
		PitResults results = runner.runPIT(options);
		assertNotNull(results);
		assertTrue(fileExists(results.getHtmlResultFile()));
		assertTrue(fileExists(results.getXmlResultFile()));
		assertThat(results.getMutations(), is(notNullValue()));
		assertThat(results.getPitOptions(), is(equalTo(options)));
	}

	private boolean fileExists(File file) {
		return null != file && file.exists();
	}


}
