package org.pitest.pitclipse.core.launch;

import static org.pitest.pitclipse.core.PitCoreActivator.getDefault;
import static org.pitest.pitclipse.core.PitCoreActivator.log;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.pitest.pitclipse.core.extension.handler.ExtensionPointHandler;
import org.pitest.pitclipse.core.extension.point.PitRuntimeOptions;
import org.pitest.pitclipse.core.launch.config.ClassFinder;
import org.pitest.pitclipse.core.launch.config.LaunchConfigurationWrapper;
import org.pitest.pitclipse.core.launch.config.PackageFinder;
import org.pitest.pitclipse.core.launch.config.ProjectFinder;
import org.pitest.pitclipse.core.launch.config.SourceDirFinder;
import org.pitest.pitclipse.pitrunner.PitOptions;
import org.pitest.pitclipse.pitrunner.PitRunner;
import org.pitest.pitclipse.pitrunner.config.PitConfiguration;
import org.pitest.pitclipse.pitrunner.io.SocketProvider;

import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

public abstract class AbstractPitLaunchDelegate extends JavaLaunchDelegate {

	private static final String EXTENSION_POINT_ID = "org.pitest.pitclipse.core.executePit";
	private static final String PIT_RUNNER = PitRunner.class.getCanonicalName();
	private int portNumber;
	private final PitConfiguration pitConfiguration;

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
		List<String> newClasspath = ImmutableList.<String> builder()
				.addAll(ImmutableList.copyOf(super.getClasspath(launchConfig))).addAll(getDefault().getPitClasspath())
				.build();
		log("Classpath: " + newClasspath);
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

		PitOptions options = configWrapper.getPitOptions();

		super.launch(configuration, mode, launch, monitor);

		IExtensionRegistry registry = Platform.getExtensionRegistry();

		new ExtensionPointHandler<PitRuntimeOptions>(EXTENSION_POINT_ID).execute(registry, new PitRuntimeOptions(
				portNumber, options, configWrapper.getMutatedProjects()));

	}

	protected abstract ProjectFinder getProjectFinder();

	protected abstract SourceDirFinder getSourceDirFinder();

	protected abstract PackageFinder getPackageFinder();

	protected abstract ClassFinder getClassFinder();

}