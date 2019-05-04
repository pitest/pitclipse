package org.pitest.pitclipse.ui.view.mutations;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.pitest.pitclipse.runner.model.ClassMutations;
import org.pitest.pitclipse.runner.model.Mutation;
import org.pitest.pitclipse.runner.model.MutationsModel;
import org.pitest.pitclipse.runner.model.MutationsModelVisitor;
import org.pitest.pitclipse.runner.model.PackageMutations;
import org.pitest.pitclipse.runner.model.ProjectMutations;
import org.pitest.pitclipse.runner.model.Status;
import org.pitest.pitclipse.runner.model.Visitable;

public class ViewContentProvider implements ITreeContentProvider {

    @Override
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object[] getElements(Object element) {
        if (element instanceof Visitable) {
            Visitable visitable = (Visitable) element;
            return visitable.accept(Structure.VISITOR);
        }
        return nothing();
    }

    @Override
    public Object[] getChildren(Object element) {
        if (element instanceof Visitable) {
            Visitable visitable = (Visitable) element;
            return visitable.accept(Structure.VISITOR);
        }
        return nothing();
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof Visitable) {
            Visitable visitable = (Visitable) element;
            return visitable.accept(Parent.VISITOR);
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof Visitable) {
            Visitable visitable = (Visitable) element;
            Object[] children = visitable.accept(Structure.VISITOR);
            return children.length > 0;
        }
        return false;
    }

    private enum Structure implements MutationsModelVisitor<Object[]> {
        VISITOR;

        @Override
        public Object[] visitModel(MutationsModel mutationsModel) {
            return mutationsModel.getStatuses().toArray();

        }

        @Override
        public Object[] visitProject(ProjectMutations projectMutations) {
            return projectMutations.getPackageMutations().toArray();
        }

        @Override
        public Object[] visitPackage(PackageMutations packageMutations) {
            return packageMutations.getClassMutations().toArray();
        }

        @Override
        public Object[] visitClass(ClassMutations classMutations) {
            return classMutations.getMutations().toArray();
        }

        @Override
        public Object[] visitMutation(Mutation mutation) {
            return nothing();
        }

        @Override
        public Object[] visitStatus(Status status) {
            return status.getProjectMutations().toArray();
        }
    }

    private enum Parent implements MutationsModelVisitor<Object> {
        VISITOR;

        @Override
        public Object visitModel(MutationsModel mutationsModel) {
            return null;
        }

        @Override
        public Object visitStatus(Status status) {
            return status.getMutationsModel();
        }

        @Override
        public Object visitProject(ProjectMutations projectMutations) {
            return projectMutations.getStatus();
        }

        @Override
        public Object visitPackage(PackageMutations packageMutations) {
            return packageMutations.getProjectMutations();
        }

        @Override
        public Object visitClass(ClassMutations classMutations) {
            return classMutations.getPackageMutations();
        }

        @Override
        public Object visitMutation(Mutation mutation) {
            return mutation.getClassMutations();
        }
    }

    private static final Object[] nothing() {
        return new Object[0];
    }
}