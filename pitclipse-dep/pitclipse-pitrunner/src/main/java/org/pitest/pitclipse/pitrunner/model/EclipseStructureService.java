package org.pitest.pitclipse.pitrunner.model;

public interface EclipseStructureService {
	String packageFrom(String mutatedClass);

	boolean isClassInProject(String mutatedClass, String project);
}
