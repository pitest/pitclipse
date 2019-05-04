package org.pitest.pitclipse.runner;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.io.Files.createParentDirs;
import static com.google.common.io.Files.createTempDir;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_AVOID_CALLS_TO_LIST;
import static org.pitest.pitclipse.runner.config.PitConfiguration.DEFAULT_MUTATORS;

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
    private final ImmutableList<String> mutators;
    private final int timeout;
    private final BigDecimal timeoutFactor;

    private PitOptions(String classUnderTest, ImmutableList<String> classesToMutate, ImmutableList<File> sourceDirs,
            File reportDir, ImmutableList<String> packages, ImmutableList<String> classPath, int threads, File historyLocation,
            ImmutableList<String> excludedClasses, ImmutableList<String> excludedMethods,
            ImmutableList<String> avoidCallsTo, ImmutableList<String> mutators, int timeout, BigDecimal timeoutFactor) {
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
    }

    public File getReportDirectory() {
        return new File(reportDir.getPath());
    }

    public ImmutableList<File> getSourceDirectories() {
        return sourceDirs;
    }

    private static File copyOfFile(File sourceDir) {
        return new File(sourceDir.getPath());
    }

    public static PitOptionsBuilder builder() {
        return new PitOptionsBuilder();
    }

    public static final class PitOptionsBuilder {
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
        private ImmutableList<String> mutators = copyOf(split(DEFAULT_MUTATORS));
        private int timeout = 3000;
        private BigDecimal timeoutFactor = BigDecimal.valueOf(1.25);

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
                    historyLocation, excludedClasses, excludedMethods, avoidCallsTo, mutators, timeout, timeoutFactor);
        }

        private void initialiseReportDir() {
            if (null == reportDir) {
                reportDir = createTempDir();
            }
            if (!reportDir.exists()) {
                try {
                    createParentDirs(reportDir);
                    if (!reportDir.mkdir()) {
                        throw new PitLaunchException("Directory could not be created: " + reportDir);
                    }
                } catch (IOException e) {
                    rethrow(reportDir, e);
                }
            }
        }

        private void initialiseHistoryLocation() {
            if (null != historyLocation) {
                File parentDir = historyLocation.getParentFile();
                if (parentDir == null) {
                    throw new PitLaunchException("Unable to use path: " + historyLocation);
                }
                if (!parentDir.exists()) {
                    try {
                        createParentDirs(historyLocation);
                    } catch (IOException e) {
                        rethrow(historyLocation, e);
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

        private void rethrow(File reportDir, IOException e) {
            throw new PitLaunchException("Unable to use path: " + reportDir, e);
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

        public PitOptionsBuilder withMutators(List<String> mutators) {
            this.mutators = copyOf(mutators);
            return this;
        }

        private static List<String> split(String toSplit) {
            return ImmutableList.copyOf(Splitter.on(',').trimResults().omitEmptyStrings().split(toSplit));
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

    public List<String> getMutators() {
        return mutators;
    }

    public int getTimeout() {
        return timeout;
    }

    public BigDecimal getTimeoutFactor() {
        return timeoutFactor;
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
