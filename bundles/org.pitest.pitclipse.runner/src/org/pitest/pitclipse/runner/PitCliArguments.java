/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.runner;

import java.io.File;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

/**
 * Turns {@link PitOptions} instances into CLI arguments
 * that can be understand by PIT.
 */
public class PitCliArguments {

    private static final PitCliArguments instance = new PitCliArguments();

    private PitCliArguments() {
    }

    public static String[] from(PitOptions options) {
        return instance.toCliArgs(options);
    }

    private String[] toCliArgs(PitOptions options) {
        Builder<String> builder = ImmutableList.builder();
        int threads = options.getThreads();
        File reportDir = options.getReportDirectory();
        builder.add("--failWhenNoMutations", "false", "--outputFormats", "HTML,PITCLIPSE_MUTATIONS,PITCLIPSE_SUMMARY",
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
        if (options.getUseJUnit5()) {
            // Specify that the 'pitest-junit5-plugin' should be used to discover tests
            builder.add("--testPlugin", "junit5");
        }
        String additionalClasspath = additionalClassPath(options);
        if (!additionalClasspath.isEmpty()) {
            builder.add("--classPath", additionalClasspath);
        }
        List<String> args = builder.build();
        return args.toArray(new String[args.size()]);
    }

    private List<String> excludedClassesFrom(PitOptions options) {
        Builder<String> builder = ImmutableList.builder();
        List<String> excludedClasses = options.getExcludedClasses();
        if (!excludedClasses.isEmpty()) {
            builder.add("--excludedClasses");
            builder.add(concat(commaSeparate(excludedClasses)));
        }
        return builder.build();
    }

    private List<String> excludedMethodsFrom(PitOptions options) {
        Builder<String> builder = ImmutableList.builder();
        List<String> excludedMethods = options.getExcludedMethods();
        if (!excludedMethods.isEmpty()) {
            builder.add("--excludedMethods");
            builder.add(concat(commaSeparate(excludedMethods)));
        }
        return builder.build();
    }

    private List<String> avoidedCallsFrom(PitOptions options) {
        Builder<String> builder = ImmutableList.builder();
        List<String> avoidCallsTo = options.getAvoidCallsTo();
        if (!avoidCallsTo.isEmpty()) {
            builder.add("--avoidCallsTo");
            builder.add(concat(commaSeparate(avoidCallsTo)));
        }
        return builder.build();
    }

    private List<String> mutatorsFrom(PitOptions options) {
        Builder<String> builder = ImmutableList.builder();
        String mutators = options.getMutators();
        if (!mutators.trim().isEmpty()) {
            builder.add("--mutators");
            builder.add(mutators);
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
            return concat(commaSeparate(packages));
        }
    }

    private String sourceDirsFrom(PitOptions options) {
        List<File> sourceDirs = options.getSourceDirectories();
        return concat(commaSeparate(fileAsStrings(sourceDirs)));
    }

    private String classpath(PitOptions options) {
        List<String> classesToMutate = options.getClassesToMutate();
        return concat(commaSeparate(classesToMutate));
    }

    private String additionalClassPath(PitOptions options) {
        List<String> classPath = options.getClassPath();
        return concat(commaSeparate(classPath));
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

    private List<String> commaSeparate(List<String> candidates) {
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
