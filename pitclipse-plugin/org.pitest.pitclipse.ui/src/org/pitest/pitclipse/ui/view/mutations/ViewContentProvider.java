package org.pitest.pitclipse.ui.view.mutations;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.pitest.pitclipse.pitrunner.model.ClassMutations;
import org.pitest.pitclipse.pitrunner.model.Mutation;
import org.pitest.pitclipse.pitrunner.model.MutationsModel;
import org.pitest.pitclipse.pitrunner.model.MutationsModelVisitor;
import org.pitest.pitclipse.pitrunner.model.PackageMutations;
import org.pitest.pitclipse.pitrunner.model.ProjectMutations;
import org.pitest.pitclipse.pitrunner.model.Status;
import org.pitest.pitclipse.pitrunner.model.Visitable;

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
			List<Status> statuses = mutationsModel.getStatuses();
			return statuses.toArray();

		}

		@Override
		public Object[] visitProject(ProjectMutations projectMutations) {
			List<PackageMutations> packageMutations = projectMutations.getPackageMutations();
			return packageMutations.toArray();
		}

		@Override
		public Object[] visitPackage(PackageMutations packageMutations) {
			List<ClassMutations> classMutations = packageMutations.getClassMutations();
			return classMutations.toArray();
		}

		@Override
		public Object[] visitClass(ClassMutations classMutations) {
			List<Mutation> mutations = classMutations.getMutations();
			return mutations.toArray();
		}

		@Override
		public Object[] visitMutation(Mutation mutation) {
			return nothing();
		}

		@Override
		public Object[] visitStatus(Status status) {
			List<ProjectMutations> projectMutations = status.getProjectMutations();
			return projectMutations.toArray();
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