package org.pitest.pitclipse.pitrunner;

import java.io.File;
import java.util.List;

import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList.Builder;
import org.pitest.pitclipse.reloc.guava.collect.Lists;

public class PitCliArguments {

	private static final PitCliArguments instance = new PitCliArguments();

	private PitCliArguments() {
	}

	public static String[] from(PitOptions options) {
		return instance.toCLIArgs(options);
	}

	private String[] toCLIArgs(PitOptions options) {
		Builder<String> builder = ImmutableList.builder();
		int threads = options.getThreads();
		File reportDir = options.getReportDirectory();
		builder.add("--failWhenNoMutations", "false", "--outputFormats", "HTML,PITCLIPSE_MUTATIONS",
				"--verbose", "--threads", Integer.toString(threads), "--reportDir", reportDir.getPath(),
				"--targetTests", testsToRunFrom(options));
		builder.addAll(targetClassesFrom(options));
		builder.add("--sourceDirs").add(sourceDirsFrom(options));
		builder.addAll(historyLocationFrom(options));
		builder.addAll(excludedClassesFrom(options));
		builder.addAll(excludedMethodsFrom(options));
		builder.addAll(avoidedCallsFrom(options));
		builder.addAll(mutatorsFrom(options));
		builder.add("--timeoutConst", Integer.toString(options.getTimeout()));
		builder.add("--timeoutFactor", options.getTimeoutFactor().toPlainString());
		List<String> args = builder.build();
		return args.toArray(new String[args.size()]);
	}

	private List<String> excludedClassesFrom(PitOptions options) {
		Builder<String> builder = ImmutableList.builder();
		List<String> excludedClasses = options.getExcludedClasses();
		if (!excludedClasses.isEmpty()) {
			builder.add("--excludedClasses");
			builder.add(concat(commaSeperate(excludedClasses)));
		}
		return builder.build();
	}

	private List<String> excludedMethodsFrom(PitOptions options) {
		Builder<String> builder = ImmutableList.builder();
		List<String> excludedMethods = options.getExcludedMethods();
		if (!excludedMethods.isEmpty()) {
			builder.add("--excludedMethods");
			builder.add(concat(commaSeperate(excludedMethods)));
		}
		return builder.build();
	}

	private List<String> avoidedCallsFrom(PitOptions options) {
		Builder<String> builder = ImmutableList.builder();
		List<String> avoidCallsTo = options.getAvoidCallsTo();
		if (!avoidCallsTo.isEmpty()) {
			builder.add("--avoidCallsTo");
			builder.add(concat(commaSeperate(avoidCallsTo)));
		}
		return builder.build();
	}

	private List<String> mutatorsFrom(PitOptions options) {
		Builder<String> builder = ImmutableList.builder();
		List<String> mutators = options.getMutators();
		if (!mutators.isEmpty()) {
			builder.add("--mutators");
			builder.add(concat(commaSeperate(mutators)));
		}
		return builder.build();
	}

	private List<String> historyLocationFrom(PitOptions options) {
		Builder<String> builder = ImmutableList.builder();
		File historyLocation = options.getHistoryLocation();
		if (null != historyLocation) {
			builder.add("--historyInputLocation");
			builder.add(historyLocation.getPath());
			builder.add("--historyOutputLocation");
			builder.add(historyLocation.getPath());
		}
		return builder.build();
	}

	private List<String> targetClassesFrom(PitOptions options) {
		Builder<String> builder = ImmutableList.builder();
		List<String> classesToMutate = options.getClassesToMutate();
		if (null != classesToMutate && !classesToMutate.isEmpty()) {
			builder.add("--targetClasses");
			builder.add(classpath(options));
		}
		return builder.build();
	}

	private String testsToRunFrom(PitOptions options) {
		List<String> packages = options.getPackages();
		if (packages.isEmpty()) {
			return options.getClassUnderTest();
		} else {
			return concat(commaSeperate(packages));
		}
	}

	private String sourceDirsFrom(PitOptions options) {
		List<File> sourceDirs = options.getSourceDirectories();
		return concat(commaSeperate(fileAsStrings(sourceDirs)));
	}

	private String classpath(PitOptions options) {
		List<String> classesToMutate = options.getClassesToMutate();
		return concat(commaSeperate(classesToMutate));
	}

	private List<String> fileAsStrings(List<File> files) {
		Builder<String> builder = ImmutableList.builder();
		for (File file : files) {
			builder.add(file.getPath());
		}
		return builder.build();
	}

	private String concat(List<String> entries) {
		StringBuilder result = new StringBuilder();
		for (String entry : entries) {
			result.append(entry);
		}
		return result.toString();
	}

	private List<String> commaSeperate(List<String> candidates) {
		List<String> formattedCandidates = Lists.newArrayList();
		int size = candidates.size();
		for (int i = 0; i < size; i++) {
			String candidate = candidates.get(i).trim();
			if (i != size - 1) {
				formattedCandidates.add(candidate + ",");
			} else {
				formattedCandidates.add(candidate);
			}
		}
		return formattedCandidates;
	}
}
