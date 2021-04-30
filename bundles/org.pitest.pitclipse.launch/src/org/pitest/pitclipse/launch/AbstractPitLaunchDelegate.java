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

package org.pitest.pitclipse.launch;

import static org.pitest.pitclipse.core.PitCoreActivator.getDefault;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.pitest.pitclipse.core.extension.handler.ExtensionPointHandler;
import org.pitest.pitclipse.core.extension.point.PitRuntimeOptions;
import org.pitest.pitclipse.launch.config.ClassFinder;
import org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper;
import org.pitest.pitclipse.launch.config.PackageFinder;
import org.pitest.pitclipse.launch.config.ProjectFinder;
import org.pitest.pitclipse.launch.config.SourceDirFinder;
import org.pitest.pitclipse.runner.PitOptions;
import org.pitest.pitclipse.runner.PitOptions.PitOptionsBuilder;
import org.pitest.pitclipse.runner.PitRunner;
import org.pitest.pitclipse.runner.config.PitConfiguration;
import org.pitest.pitclipse.runner.io.SocketProvider;

import com.google.common.collect.ImmutableList;

/**
 * <p>Abstract launch configuration used to execute PIT in a background VM.</p>
 * 
 * <p>Pitest is executed by calling {@link PitRunner#main(String[])}.</p>
 * 
 * <p>Right after the VM has been launched, contributions to the {@code results}
 * extension points are notified thanks to {@link ExtensionPointHandler}.</p>
 */
public abstract class AbstractPitLaunchDelegate extends JavaLaunchDelegate {

    private static final String EXTENSION_POINT_ID = "org.pitest.pitclipse.core.executePit";
    private static final String PIT_RUNNER = PitRunner.class.getCanonicalName();
    private int portNumber;
    private final PitConfiguration pitConfiguration;
    private boolean projectUsesJunit5 = false;

    public AbstractPitLaunchDelegate(PitConfiguration pitConfiguration) {
        this.pitConfiguration = pitConfiguration;
    }

    protected void generatePortNumber() {
        portNumber = new SocketProvider().getFreePort();
    }

    @Override
    public String getMainTypeName(ILaunchConfiguration launchConfig) throws CoreException {
        return PIT_RUNNER;
    }

    @Override
    public String[] getClasspath(ILaunchConfiguration launchConfig) throws CoreException {
        ImmutableList.Builder<String> builder = ImmutableList.<String>builder()
                .addAll(getDefault().getPitClasspath());
        builder.addAll(ImmutableList.copyOf(super.getClasspath(launchConfig)));
        if (projectUsesJunit5) {
            // Allow Pitest to detect Junit5 tests
            builder.addAll(getDefault().getPitestJunit5PluginClasspath());
        }
        List<String> newClasspath = builder.build();
        
        return newClasspath.toArray(new String[newClasspath.size()]);
    }

    @Override
    public String getProgramArguments(ILaunchConfiguration launchConfig) throws CoreException {
        return new StringBuilder(super.getProgramArguments(launchConfig)).append(' ').append(portNumber).toString();
    }

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
            throws CoreException {
        generatePortNumber();
        LaunchConfigurationWrapper configWrapper = LaunchConfigurationWrapper.builder()
                .withLaunchConfiguration(configuration).withProjectFinder(getProjectFinder())
                .withPackageFinder(getPackageFinder()).withClassFinder(getClassFinder())
                .withSourceDirFinder(getSourceDirFinder()).withPitConfiguration(pitConfiguration).build();

        projectUsesJunit5 = isJUnit5InClasspathOf(configWrapper.getProject());
        PitOptionsBuilder optionsBuilder = configWrapper.getPitOptionsBuilder();
        PitOptions options = optionsBuilder.withUseJUnit5(projectUsesJunit5)
                                           .build();

        super.launch(configuration, mode, launch, monitor);

        IExtensionRegistry registry = Platform.getExtensionRegistry();

        new ExtensionPointHandler<PitRuntimeOptions>(EXTENSION_POINT_ID).execute(registry, new PitRuntimeOptions(
                portNumber, options, configWrapper.getMutatedProjects()));

    }
    
    private static boolean isJUnit5InClasspathOf(IJavaProject project) throws JavaModelException {
    	// FIXME Naive implementation, won't handle every case (e.g. JUnit 5 provided through a junit5.jar archive)
    	// 		 A better implementation may rely on JDT to scan the classpath / source files for definition / use
    	//		 of JUnit 5 Test annotation
    	//
    	//		 See also https://github.com/redhat-developer/vscode-java/issues/204
    	
    	for (IClasspathEntry classpathEntry : project.getRawClasspath()) {
    		if (JUnitCore.JUNIT5_CONTAINER_PATH.equals(classpathEntry.getPath())) {
    			return true;
    		}
    	}
        for (IClasspathEntry classpathEntry : project.getResolvedClasspath(true)) {
        	Map<String, Object> attributes = Arrays.stream(classpathEntry.getExtraAttributes()).collect(Collectors.toMap(IClasspathAttribute::getName, IClasspathAttribute::getValue, (value1, value2) -> value1));
        	if (isJUnit5FromMaven(attributes)) {
        		return true;
        	}
        	if (isJUnit5FromGradle(classpathEntry, attributes)) {
        		return true;
        	}
        	if (pointsToJunitJupiterEngineJar(classpathEntry)) {
        		return true;
        	}
        }
        return false;
    }
    
    private static boolean isJUnit5FromMaven(Map<String, Object> attributes) {
    	if (!attributes.containsKey("maven.pomderived") || !attributes.containsKey("maven.groupId") || !attributes.containsKey("maven.artifactId")) {
    		return false;
    	}
		return "true".equals(attributes.get("maven.pomderived")) 
			&& "org.junit.jupiter".equals(attributes.get("maven.groupId")) 
			&& "junit-jupiter-engine".equals(attributes.get("maven.artifactId"));
    }
    
    private static boolean isJUnit5FromGradle(IClasspathEntry classpathEntry, Map<String, Object> attributes) {
    	if (!attributes.containsKey("gradle_use_by_scope")) {
    		return false;
    	}
    	return pointsToJunitJupiterEngineJar(classpathEntry);
    }
    
    private static boolean pointsToJunitJupiterEngineJar(IClasspathEntry classpathEntry) {
    	try {
    		String[] pathElements = classpathEntry.getPath().toString().split("/");
        	String file = pathElements[pathElements.length - 1];
        	return file.startsWith("junit-jupiter-engine") && file.endsWith(".jar");
    	}
    	catch (IndexOutOfBoundsException e) {
    		// path doesn't have expected format, never mind
    	}
    	return false;
    }

    protected abstract ProjectFinder getProjectFinder();

    protected abstract SourceDirFinder getSourceDirFinder();

    protected abstract PackageFinder getPackageFinder();

    protected abstract ClassFinder getClassFinder();

}
