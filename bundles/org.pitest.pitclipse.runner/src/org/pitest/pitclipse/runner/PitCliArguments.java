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

import static java.util.Collections.emptyList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        int threads = options.getThreads();
        File reportDir = options.getReportDirectory();
        List<String> args = new ArrayList<>(Arrays.asList(
                "--failWhenNoMutations", "false", "--outputFormats", "HTML,PITCLIPSE_MUTATIONS,PITCLIPSE_SUMMARY",
                "--verbose", "--threads", Integer.toString(threads), "--reportDir", reportDir.getPath(),
                "--targetTests", testsToRunFrom(options)));
        args.addAll(targetClassesFrom(options));
        args.add("--sourceDirs");
        args.add(sourceDirsFrom(options));
        args.addAll(historyLocationFrom(options));
        args.addAll(excludedClassesFrom(options));
        args.addAll(excludedMethodsFrom(options));
        args.addAll(avoidedCallsFrom(options));
        args.addAll(mutatorsFrom(options));
        args.add("--timeoutConst");
        args.add(Integer.toString(options.getTimeout()));
        args.add("--timeoutFactor");
        args.add(options.getTimeoutFactor().toPlainString());
        if (options.getUseJUnit5()) {
            // Specify that the 'pitest-junit5-plugin' should be used to discover tests
            args.add("--testPlugin");
            args.add("junit5");
        }
        String additionalClasspath = additionalClassPath(options);
        if (!additionalClasspath.isEmpty()) {
            args.add("--classPath");
            args.add(additionalClasspath);
        }
        return args.toArray(new String[args.size()]);
    }

    private List<String> excludedClassesFrom(PitOptions options) {
        List<String> excludedClasses = options.getExcludedClasses();
        if (!excludedClasses.isEmpty()) {
            List<String> args = new ArrayList<>();
            args.add("--excludedClasses");
            args.add(concat(commaSeparate(excludedClasses)));
            return args;
        }
        return emptyList();
    }

    private List<String> excludedMethodsFrom(PitOptions options) {
        List<String> excludedMethods = options.getExcludedMethods();
        if (!excludedMethods.isEmpty()) {
            List<String> args = new ArrayList<>();
            args.add("--excludedMethods");
            args.add(concat(commaSeparate(excludedMethods)));
            return args;
        }
        return emptyList();
    }

    private List<String> avoidedCallsFrom(PitOptions options) {
        List<String> avoidCallsTo = options.getAvoidCallsTo();
        if (!avoidCallsTo.isEmpty()) {
            List<String> args = new ArrayList<>();
            args.add("--avoidCallsTo");
            args.add(concat(commaSeparate(avoidCallsTo)));
            return args;
        }
        return emptyList();
    }

    private List<String> mutatorsFrom(PitOptions options) {
        String mutators = options.getMutators();
        if (!mutators.trim().isEmpty()) {
            List<String> args = new ArrayList<>();
            args.add("--mutators");
            args.add(mutators);
            return args;
        }
        return emptyList();
    }

    private List<String> historyLocationFrom(PitOptions options) {
        File historyLocation = options.getHistoryLocation();
        if (null != historyLocation) {
            List<String> args = new ArrayList<>();
            args.add("--historyInputLocation");
            args.add(historyLocation.getPath());
            args.add("--historyOutputLocation");
            args.add(historyLocation.getPath());
            return args;
        }
        return emptyList();
    }

    private List<String> targetClassesFrom(PitOptions options) {
        List<String> classesToMutate = options.getClassesToMutate();
        if (!classesToMutate.isEmpty()) {
            List<String> args = new ArrayList<>();
            args.add("--targetClasses");
            args.add(classpath(options));
            return args;
        }
        return emptyList();
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
        List<String> args = new ArrayList<>();
        for (File file : files) {
            args.add(file.getPath());
        }
        return args;
    }

    private String concat(List<String> entries) {
        StringBuilder result = new StringBuilder();
        for (String entry : entries) {
            result.append(entry);
        }
        return result.toString();
    }

    private List<String> commaSeparate(List<String> candidates) {
        List<String> formattedCandidates = new ArrayList<>();
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
