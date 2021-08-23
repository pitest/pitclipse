package org.pitest.pitclipse.ui.highlighting;

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

public class PitclipseMutantHighlighter implements ResultNotifier<MutationsModel> {
    /**
     * Id where all pitclipse markers can be found
     */
    public static final String PITCLIPSE_MUTANT_MARKER = PitUiActivator.PLUGIN_ID + ".pitclipsemarker";
    public static final String SURVIVING_MUTANT_MARKER = PitUiActivator.PLUGIN_ID + ".survived";
    public static final String SURVIVING_MUTANT_MARKER_ATTRIBUTE = PitUiActivator.PLUGIN_ID + ".fixHint";
    public static final String KILLED_MUTANT_MARKER = PitUiActivator.PLUGIN_ID + ".killed";
    public static final String NO_COVERAGE_MUTANT_MARKER = PitUiActivator.PLUGIN_ID + ".nocoverage";
    public static final String TIMEOUT_MUTANT_MARKER = PitUiActivator.PLUGIN_ID + ".timeout";
    public static final String NON_VIABLE_MUTANT_MARKER = PitUiActivator.PLUGIN_ID + ".nonViable";
    public static final String MEMORY_ERROR_MARKER = PitUiActivator.PLUGIN_ID + ".memError";
    public static final String RUN_ERROR_MARKER = PitUiActivator.PLUGIN_ID + ".runError";
    private static final String TASKS_VIEW_ID = "org.eclipse.ui.views.TaskList";


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
                e.printStackTrace();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void createMarkers(MutationsModel results) {
        removeOldMarkers();
        List<Mutation> mutations = ModelsVisitor.VISITOR.extractAllMutations(results);

        int i = 0;
        final IResource[] resources = new IResource[mutations.size()];
        final String[] types = new String[mutations.size()];
        final Map<String, Object>[] attributes = new Map[mutations.size()];

        for (Mutation m : mutations) {
            resources[i] = findClass(getProjectName(m), getClassName(m));
            types[i] = getType(m);
            attributes[i] = new HashMap<>();
            attributes[i].put(IMarker.LINE_NUMBER, m.getLineNumber());
            attributes[i].put(IMarker.MESSAGE, m.getStatus().toString() + ": " + m.getDescription());

            switch (types[i]) {
            case SURVIVING_MUTANT_MARKER:
                // if mutation survived, add hint how to probably kill it
                attributes[i].put(SURVIVING_MUTANT_MARKER_ATTRIBUTE, "NOT IMPLEMENTED YET");
                attributes[i].put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
                break;
            case TIMEOUT_MUTANT_MARKER:
            case NO_COVERAGE_MUTANT_MARKER:
                attributes[i].put(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
                break;
            default:
                attributes[i].put(IMarker.DONE, true);
                attributes[i].put(IMarker.PRIORITY, IMarker.PRIORITY_LOW);
            }
            createMarker(resources[i], types[i], attributes[i]);
            i++;
        }
    }

    private void removeOldMarkers() {
        IMarker[] marker = null;
        int depth = IResource.DEPTH_INFINITE;
        try {
            marker = ResourcesPlugin.getWorkspace().getRoot().findMarkers(PITCLIPSE_MUTANT_MARKER, true, depth);
            for (IMarker iMarker : marker) {
                iMarker.delete();
            }
        } catch (CoreException e) {
            // something went wrong
        }
    }

    private IMarker createMarker(IResource iResource, String type, Map<String, Object> attributes) {
        try {
            IMarker marker = iResource.createMarker(type);
            marker.setAttributes(attributes);
            return marker;
        } catch (CoreException e) {
            return null;
        }
    }

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

    private String getProjectName(Mutation mutation) {
        return mutation.getClassMutations().getPackageMutations().getProjectMutations().getProjectName();
    }

    private String getClassName(Mutation mutation) {
        return mutation.getClassMutations().getClassName();
    }

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
