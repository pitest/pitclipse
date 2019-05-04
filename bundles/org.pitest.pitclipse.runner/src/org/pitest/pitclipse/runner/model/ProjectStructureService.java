package org.pitest.pitclipse.runner.model;

public interface ProjectStructureService {
    String packageFrom(String project, String mutatedClass);

    boolean isClassInProject(String mutatedClass, String project);
}
