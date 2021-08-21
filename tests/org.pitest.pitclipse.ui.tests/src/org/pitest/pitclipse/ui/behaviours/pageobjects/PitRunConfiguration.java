/*******************************************************************************
 * Copyright 2012-2021 Phil Glover and contributors
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

package org.pitest.pitclipse.ui.behaviours.pageobjects;

import static com.google.common.collect.ImmutableList.copyOf;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_AVOID_CALLS_TO_LIST;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class PitRunConfiguration {
    /**
     * Name of the configuration
     */
    private final String name;
    /**
     * List of the projects which are in the scope of mutation
     */
    private final List<String> projects;
    /**
     * Name of the test object. Can be the name of a class or directory.
     */
    private final String testObject;
    /**
     * True, if the test object is a class
     */
    private final boolean testClass;
    /**
     * True, if the tests should be run in pararllel
     */
    private final boolean runInParallel;
    /**
     * True, if incremental analysis should be used
     */
    private final boolean incrementalAnalysis;
    /**
     * List of globs to match against class names. Matching classes will be excluded
     * from mutation.
     */
    private final String excludedClasses;
    /**
     * List of globs to match against method names. Methods matching the globs will
     * be excluded from mutation.
     */
    private final String excludedMethods;
    /**
     * List of packages and classes which are to be considered outside the scope of
     * mutation. Any lines of code containing calls to these classes will not be
     * mutated.
     */
    private final String avoidCallsTo;

    private PitRunConfiguration(String name, List<String> projects, String testObject, boolean testClass,
            boolean runInParallel, boolean incrementalAnalysis, String excludedClasses, String excludedMethods,
            String avoidCallsTo) {
        this.name = name;
        this.projects = projects;
        this.testObject = testObject;
        this.runInParallel = runInParallel;
        this.incrementalAnalysis = incrementalAnalysis;
        this.excludedClasses = excludedClasses;
        this.excludedMethods = excludedMethods;
        this.avoidCallsTo = avoidCallsTo;
        this.testClass = testClass;
    }

    public String getName() {
        return name;
    }

    public List<String> getProjects() {
        return projects;
    }

    public String getTestObject() {
        return testObject;
    }

    public static class Builder {
        private String name;
        private String testObject = null;
        private List<String> projects = ImmutableList.of();
        private boolean runInParallel = false;
        private boolean incrementalAnalysis = false;
        private boolean testClass = true;
        private String excludedClasses = "*Test";
        private String excludedMethods = "";
        private String avoidCallsTo = DEFAULT_AVOID_CALLS_TO_LIST;

        public Builder(PitRunConfiguration configuration) {
            this.name = configuration.getName();
            this.testObject = configuration.getTestObject();
            this.projects = copyOf(configuration.getProjects());
            this.runInParallel = configuration.isRunInParallel();
            this.incrementalAnalysis = configuration.isIncrementalAnalysis();
            this.testClass = configuration.isTestClass();
            this.excludedClasses = configuration.getExcludedClasses();
            this.excludedMethods = configuration.getExcludedMethods();
            this.avoidCallsTo = configuration.getAvoidCallsTo();
        }

        public Builder() {
            // intentionally empty
        }

        public PitRunConfiguration build() {
            return new PitRunConfiguration(name, projects, testObject, testClass, runInParallel, incrementalAnalysis,
                    excludedClasses, excludedMethods, avoidCallsTo);
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withProjects(String... projects) {
            this.projects = copyOf(projects);
            return this;
        }

        public Builder withTestObject(String testObject) {
            this.testObject = testObject;
            return this;
        }

        public Builder withTestDir(String testDir) {
            this.testClass = false;
            return withTestObject(testDir);
        }

        public Builder withTestClassOrDir(boolean isTestClass) {
            this.testClass = isTestClass;
            return this;
        }

        public Builder withTestClass(String testClass) {
            this.testClass = true;
            return withTestObject(testClass);
        }

        public Builder withRunInParallel(boolean runInParallel) {
            this.runInParallel = runInParallel;
            return this;
        }

        public Builder withIncrementalAnalysis(boolean incrementalAnalysis) {
            this.incrementalAnalysis = incrementalAnalysis;
            return this;
        }

        public Builder withExcludedClasses(String excludedClasses) {
            this.excludedClasses = excludedClasses;
            return this;
        }

        public Builder withExcludedMethods(String excludedMethods) {
            this.excludedMethods = excludedMethods;
            return this;
        }

        public Builder withAvoidCallsTo(String avoidCallsTo) {
            this.avoidCallsTo = avoidCallsTo;
            return this;
        }
    }

    public boolean isRunInParallel() {
        return runInParallel;
    }

    public boolean isIncrementalAnalysis() {
        return incrementalAnalysis;
    }

    public String getExcludedClasses() {
        return excludedClasses;
    }

    public String getExcludedMethods() {
        return excludedMethods;
    }

    public String getAvoidCallsTo() {
        return avoidCallsTo;
    }

    public boolean isTestClass() {
        return testClass;
    }
}
