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

import static com.google.common.collect.ImmutableList.copyOf;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_AVOID_CALLS_TO_LIST;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_MUTATORS;
import static org.pitest.pitclipse.runner.util.PitFileUtils.createParentDirs;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

/**
 * <p>Options used to parameterize a PIT analysis.</p>
 * 
 * <p>An instance of this class is <strong>immutable</strong> and, once built,
 * is inherently <strong>thread-safe</strong>.</p>
 */
public final class PitOptions implements Serializable {

    private static final long serialVersionUID = 1543633254516962868L;
    private final File reportDir;
    private final String classUnderTest;
    private final ImmutableList<String> classesToMutate;
    private final ImmutableList<File> sourceDirs;
    private final ImmutableList<String> packages;
    private final ImmutableList<String> classPath;
    private final int threads;
    private final File historyLocation;
    private final ImmutableList<String> excludedClasses;
    private final ImmutableList<String> excludedMethods;
    private final ImmutableList<String> avoidCallsTo;
    private final String mutators;
    private final int timeout;
    private final BigDecimal timeoutFactor;
    private final boolean useJUnit5;

    private PitOptions(String classUnderTest, ImmutableList<String> classesToMutate, ImmutableList<File> sourceDirs, // NOSONAR this is used by our builder
            File reportDir, ImmutableList<String> packages, ImmutableList<String> classPath, int threads, File historyLocation,
            ImmutableList<String> excludedClasses, ImmutableList<String> excludedMethods,
            ImmutableList<String> avoidCallsTo, String mutators, int timeout, BigDecimal timeoutFactor,
            boolean useJUnit5) {
        this.classUnderTest = classUnderTest;
        this.threads = threads;
        this.historyLocation = historyLocation;
        this.packages = packages;
        this.classPath = classPath;
        this.classesToMutate = classesToMutate;
        this.sourceDirs = sourceDirs;
        this.reportDir = reportDir;
        this.excludedClasses = excludedClasses;
        this.excludedMethods = excludedMethods;
        this.avoidCallsTo = avoidCallsTo;
        this.mutators = mutators;
        this.timeout = timeout;
        this.timeoutFactor = timeoutFactor;
        this.useJUnit5 = useJUnit5;
    }

    public File getReportDirectory() {
        return new File(reportDir.getPath());
    }

    public ImmutableList<File> getSourceDirectories() {
        return sourceDirs;
    }

    public static PitOptionsBuilder builder() {
        return new PitOptionsBuilder();
    }

    public static final class PitOptionsBuilder {
        private static final String UNABLE_TO_USE_PATH = "Unable to use path: ";
        private String classUnderTest = null;
        private ImmutableList<String> classesToMutate = ImmutableList.of();
        private File reportDir = null;
        private ImmutableList<File> sourceDirs = ImmutableList.of();
        private ImmutableList<String> packages = ImmutableList.of();
        private ImmutableList<String> classPath = ImmutableList.of();
        private int threads = 1;
        private File historyLocation = null;
        private ImmutableList<String> excludedClasses = ImmutableList.of();
        private ImmutableList<String> excludedMethods = ImmutableList.of();
        private ImmutableList<String> avoidCallsTo = copyOf(split(DEFAULT_AVOID_CALLS_TO_LIST));
        private String mutators = DEFAULT_MUTATORS;
        private int timeout = 3000;
        private BigDecimal timeoutFactor = BigDecimal.valueOf(1.25);
        private boolean useJUnit5 = false;

        private PitOptionsBuilder() {
        }

        public PitOptionsBuilder withReportDirectory(File reportDir) {
            this.reportDir = copyOfFile(reportDir);
            return this;
        }

        public PitOptionsBuilder withSourceDirectory(File sourceDir) {
            return withSourceDirectories(ImmutableList.of(copyOfFile(sourceDir)));
        }

        public PitOptionsBuilder withSourceDirectories(List<File> sourceDirs) {
            this.sourceDirs = copyOf(sourceDirs);
            return this;
        }

        public PitOptionsBuilder withTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public PitOptionsBuilder withTimeoutFactor(BigDecimal factor) {
            this.timeoutFactor = factor;
            return this;
        }

        public PitOptions build() {
            validateSourceDir();
            validateTestClass();
            initialiseReportDir();
            initialiseHistoryLocation();
            return new PitOptions(classUnderTest, classesToMutate, sourceDirs, reportDir, packages, classPath, threads,
                    historyLocation, excludedClasses, excludedMethods, avoidCallsTo, mutators, timeout, timeoutFactor,
                    useJUnit5);
        }

        private void initialiseReportDir() {
            try {
                if (null == reportDir) {
                    reportDir =
                        Files.createTempDirectory(null).toFile(); // NOSONAR we get a security hotspot
                        // but we're safe about that
                }
                if (!reportDir.exists()) {
                    createParentDirs(reportDir);
                    // if we could create parent dirs we can create the reportDir
                    reportDir.mkdir();
                }
            } catch (IOException e) {
                throw new PitLaunchException(UNABLE_TO_USE_PATH + reportDir, e);
            }
        }

        private void initialiseHistoryLocation() {
            if (null != historyLocation) {
                File parentDir = historyLocation.getParentFile();
                if (parentDir == null) {
                    throw new PitLaunchException(UNABLE_TO_USE_PATH + historyLocation);
                }
                if (!parentDir.exists()) {
                    try {
                        createParentDirs(historyLocation);
                    } catch (IOException e) {
                        throw new PitLaunchException(UNABLE_TO_USE_PATH + historyLocation, e);
                    }
                }
            }
        }

        private void validateSourceDir() {
            if (sourceDirs.isEmpty()) {
                throw new PitLaunchException("Source directory not set.");
            }
            for (File dir : sourceDirs) {
                if (!dir.exists()) {
                    throw new PitLaunchException("Directory does not exist: " + dir);
                }
            }
        }

        private void validateTestClass() {
            if (null == classUnderTest && packages.isEmpty()) {
                throw new PitLaunchException("Class under test not set.");
            }
        }

        public PitOptionsBuilder withClassUnderTest(String testClass) {
            classUnderTest = testClass;
            return this;
        }

        public PitOptionsBuilder withClassesToMutate(List<String> classPath) {
            classesToMutate = copyOf(classPath);
            return this;
        }

        public PitOptionsBuilder withPackagesToTest(List<String> packages) {
            this.packages = copyOf(packages);
            return this;
        }

        public PitOptionsBuilder withClassPath(List<String> classPath) {
            this.classPath = copyOf(classPath);
            return this;
        }

        public PitOptionsBuilder withThreads(int threads) {
            this.threads = threads;
            return this;
        }

        public PitOptionsBuilder withHistoryLocation(File historyLocation) {
            this.historyLocation = historyLocation;
            return this;
        }

        public PitOptionsBuilder withExcludedClasses(List<String> excludedClasses) {
            this.excludedClasses = copyOf(excludedClasses);
            return this;
        }

        public PitOptionsBuilder withExcludedMethods(List<String> excludedMethods) {
            this.excludedMethods = copyOf(excludedMethods);
            return this;
        }

        public PitOptionsBuilder withAvoidCallsTo(List<String> avoidCallsTo) {
            this.avoidCallsTo = copyOf(avoidCallsTo);
            return this;
        }

        public PitOptionsBuilder withMutators(String mutators) {
            this.mutators = mutators;
            return this;
        }

        public PitOptionsBuilder withUseJUnit5(boolean useJUnit5) {
            this.useJUnit5 = useJUnit5;
            return this;
        }

        private static List<String> split(String toSplit) {
            return ImmutableList.copyOf(Splitter.on(',').trimResults().omitEmptyStrings().split(toSplit));
        }

        private static File copyOfFile(File sourceDir) {
            return new File(sourceDir.getPath());
        }
    }

    public static final class PitLaunchException extends IllegalArgumentException {
        private static final long serialVersionUID = -8657782829090737433L;

        public PitLaunchException(String msg, Exception e) {
            super(msg, e);
        }

        public PitLaunchException(String msg) {
            super(msg);
        }
    }

    public String getClassUnderTest() {
        return classUnderTest;
    }

    public File getHistoryLocation() {
        return historyLocation;
    }

    public int getThreads() {
        return threads;
    }

    public List<String> getExcludedClasses() {
        return excludedClasses;
    }

    public List<String> getExcludedMethods() {
        return excludedMethods;
    }

    public List<String> getClassesToMutate() {
        return classesToMutate;
    }

    public List<String> getPackages() {
        return packages;
    }

    public List<String> getClassPath() {
        return classPath;
    }

    public List<String> getAvoidCallsTo() {
        return avoidCallsTo;
    }

    public String getMutators() {
        return mutators;
    }

    public int getTimeout() {
        return timeout;
    }

    public BigDecimal getTimeoutFactor() {
        return timeoutFactor;
    }

    public boolean getUseJUnit5() {
        return useJUnit5;
    }

    @Override
    public String toString() {
        return "PitOptions [reportDir=" + reportDir + ", classUnderTest=" + classUnderTest + ", classesToMutate=" +
                classesToMutate + ", sourceDirs=" + sourceDirs + ", packages=" + packages + ", threads=" + threads +
                ", historyLocation=" + historyLocation + ", excludedClasses=" + excludedClasses +
                ", excludedMethods=" + excludedMethods + ", avoidCallsTo=" + avoidCallsTo + ", mutators=" + mutators +
                ", timeoutConst=" + timeout + ", timeoutFactor=" + timeoutFactor + "]";
    }
}
