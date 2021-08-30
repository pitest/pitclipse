/*******************************************************************************
 * Copyright 2021 Jonas Kutscha and contributors
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

package org.pitest.pitclipse.ui.mutation.marker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.runner.model.ClassMutations;
import org.pitest.pitclipse.runner.model.Mutation;
import org.pitest.pitclipse.runner.model.MutationsModel;
import org.pitest.pitclipse.runner.model.PackageMutations;
import org.pitest.pitclipse.runner.model.ProjectMutations;
import org.pitest.pitclipse.runner.model.Status;
import org.pitest.pitclipse.ui.core.PitUiActivator;

/**
 * Class which creates mutation markers after a PIT run
 * @author Jonas Kutscha
 */
public class PitclipseMutantMarkerFactory implements ResultNotifier<MutationsModel> {
    /**
     * Id where all Pitclipse markers can be found
     */
    public static final String PITCLIPSE_MUTANT_MARKER = PitUiActivator.PLUGIN_ID + ".pitclipsemarker";
    /**
     * Id for markers of <b>surviving</b> mutants
     */
    public static final String SURVIVING_MUTANT_MARKER = PitUiActivator.PLUGIN_ID + ".survived";
    /**
     * Id of the marker attribute fix hint
     */
    public static final String SURVIVING_MUTANT_MARKER_ATTRIBUTE = PitUiActivator.PLUGIN_ID + ".fixHint";
    /**
     * Id for markers of <b>killed</b> mutants
     */
    public static final String KILLED_MUTANT_MARKER = PitUiActivator.PLUGIN_ID + ".killed";
    /**
     * Id for markers of <b>no coverage</b> mutants
     */
    public static final String NO_COVERAGE_MUTANT_MARKER = PitUiActivator.PLUGIN_ID + ".nocoverage";
    /**
     * Id for markers of <b>timeout</b> mutants
     */
    public static final String TIMEOUT_MUTANT_MARKER = PitUiActivator.PLUGIN_ID + ".timeout";
    /**
     * Id for markers of <b>non viable</b> mutants
     */
    public static final String NON_VIABLE_MUTANT_MARKER = PitUiActivator.PLUGIN_ID + ".nonViable";
    /**
     * Id for markers of <b>memory error</b> mutants
     */
    public static final String MEMORY_ERROR_MARKER = PitUiActivator.PLUGIN_ID + ".memError";
    /**
     * Id for markers of <b>run error</b> mutants
     */
    public static final String RUN_ERROR_MARKER = PitUiActivator.PLUGIN_ID + ".runError";
    /**
     * Id of the task view from Eclipse
     */
    private static final String TASKS_VIEW_ID = "org.eclipse.ui.views.TaskList";


    /**
     * Uses the results to create a marker for each mutant and shows the task view
     * of Eclipse
     * @param results from pit which holds the information about the mutants
     */
    @Override
    public void handleResults(MutationsModel results) {
        createMarkers(results);
        // open and show tasks view after the marker are created
        Display.getDefault().asyncExec(() -> {
            try {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                IViewPart view = page.showView(TASKS_VIEW_ID);
                page.activate(view);
            } catch (PartInitException e) {
                throw new RuntimeException("Could not show task view.", e);
            }
        });
    }

    /**
     * Extracts all mutants of the results to create a marker for each one
     * @param results which hold the mutants
     */
    private void createMarkers(MutationsModel results) {
        removeOldMarkers();
        List<Mutation> mutations = ModelsVisitor.VISITOR.extractAllMutations(results);
        int i = 0;
        final IResource[] resources = new IResource[mutations.size()];
        final String[] types = new String[mutations.size()];
        @SuppressWarnings("unchecked")
        final Map<String, Object>[] attributes = new Map[mutations.size()];

        for (Mutation m : mutations) {
            resources[i] = findClass(getProjectName(m), getClassName(m));
            types[i] = getType(m);
            attributes[i] = new HashMap<>();
            attributes[i].put(IMarker.LINE_NUMBER, m.getLineNumber());
            attributes[i].put(IMarker.MESSAGE, m.getStatus().toString() + ": " + m.getDescription());

            switch (types[i]) {
            case SURVIVING_MUTANT_MARKER:
                // TODO: if mutation survived, add hint how to probably kill it
                attributes[i].put(SURVIVING_MUTANT_MARKER_ATTRIBUTE, "NOT IMPLEMENTED YET");
                attributes[i].put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
                break;
            case TIMEOUT_MUTANT_MARKER:
            case NO_COVERAGE_MUTANT_MARKER:
                attributes[i].put(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
                break;
            default:
                // default. Used for killed mutants
                attributes[i].put(IMarker.DONE, true);
                attributes[i].put(IMarker.PRIORITY, IMarker.PRIORITY_LOW);
            }
            createMarker(resources[i], types[i], attributes[i]);
            i++;
        }
    }

    /**
     * Removes all old markers, which are Pitclipse markers
     */
    private void removeOldMarkers() {
        try {
            IMarker[] marker = ResourcesPlugin.getWorkspace().getRoot().findMarkers(PITCLIPSE_MUTANT_MARKER, true,
                    IResource.DEPTH_INFINITE);
            for (IMarker iMarker : marker) {
                iMarker.delete();
            }
        } catch (CoreException e) {
            throw new RuntimeException("Error while deleting old markers occured.", e);
        }
    }

    /**
     * Tries to create a marker for the given resource with the given type and
     * attributes.
     * @param iResource  resource where to create the marker
     * @param type       of the marker to create
     * @param attributes which should be added to the marker
     * @return the created marker or null, if the marker could not be created
     */
    private IMarker createMarker(IResource iResource, String type, Map<String, Object> attributes) {
        try {
            IMarker marker = iResource.createMarker(type);
            marker.setAttributes(attributes);
            return marker;
        } catch (CoreException e) {
            // could not create marker
            return null;
        }
    }

    /**
     * Maps the detection status of the given mutation to the corresponding marker
     * id. If the detection status has no corresponding marker id the non viable
     * marker is used.
     * @param mutation which detection status is mapped
     * @return the marker id which corresponds to the detection status of the mutant
     */
    private String getType(Mutation mutation) {
        switch (mutation.getStatus()) {
        case SURVIVED:
            return SURVIVING_MUTANT_MARKER;
        case KILLED:
            return KILLED_MUTANT_MARKER;
        case NO_COVERAGE:
            return NO_COVERAGE_MUTANT_MARKER;
        case TIMED_OUT:
            return TIMEOUT_MUTANT_MARKER;
        case MEMORY_ERROR:
            return MEMORY_ERROR_MARKER;
        case RUN_ERROR:
            return RUN_ERROR_MARKER;
        default:
            return NON_VIABLE_MUTANT_MARKER;
        }
    }

    /**
     * Tries to find the class specified by its name in the project which name is
     * given.
     * @param projectName where the class file is located
     * @param className   which identifies the class
     * @return the file handle or null, if the file was not found
     */
    private IFile findClass(final String projectName, final String className) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (IProject project : root.getProjects()) {
            if (project.getName().equals(projectName) && project.isOpen()) {
                IJavaProject javaProject = JavaCore.create(project);
                if (javaProject != null) {
                    try {
                        IType type = javaProject.findType(className);
                        return root.getFile(type.getPath());
                    } catch (JavaModelException e) {
                        // Maybe type no longer exists. Do nothing
                    }
                }
            }
        }
        return null;
    }

    /**
     * Extracts the project name from the given mutation where it occured.
     * @param mutation from which the project is desired
     * @return the project name where the mutation occured
     */
    private String getProjectName(Mutation mutation) {
        return mutation.getClassMutations().getPackageMutations().getProjectMutations().getProjectName();
    }

    /**
     * Extracts the class name from the given mutation where it occured.
     * @param mutation from which the class is desired
     * @return the class name where the mutation occured
     */
    private String getClassName(Mutation mutation) {
        return mutation.getClassMutations().getClassName();
    }

    /**
     * Visitor which is used to extract all mutations from the mutation model
     */
    private enum ModelsVisitor {
        VISITOR;
        public List<Mutation> extractAllMutations(MutationsModel mutationsModel) {
            final LinkedList<Mutation> mutations = new LinkedList<>();
            for (Status s : mutationsModel.getStatuses()) {
                VISITOR.visitStatus(s, mutations);
            }
            return mutations;
        }

        public void visitStatus(Status status, List<Mutation> mutations) {
            for (ProjectMutations p : status.getProjectMutations()) {
                VISITOR.visitProject(p, mutations);
            }
        }

        public void visitProject(ProjectMutations projectMutations, List<Mutation> mutations) {
            for (PackageMutations p : projectMutations.getPackageMutations()) {
                VISITOR.visitPackage(p, mutations);
            }
        }

        public void visitPackage(PackageMutations packageMutations, List<Mutation> mutations) {
            for (ClassMutations c : packageMutations.getClassMutations()) {
                VISITOR.visitClass(c, mutations);
            }
        }

        public void visitClass(ClassMutations classMutations, List<Mutation> mutations) {
            mutations.addAll(classMutations.getMutations());
        }

    }
}
