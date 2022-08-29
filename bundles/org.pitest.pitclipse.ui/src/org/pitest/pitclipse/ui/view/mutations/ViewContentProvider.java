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

package org.pitest.pitclipse.ui.view.mutations;

import org.eclipse.jface.viewers.ITreeContentProvider;
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
    public Object[] getElements(Object element) {
        return handleElementsOrChildren(element);
    }

    @Override
    public Object[] getChildren(Object element) {
        return handleElementsOrChildren(element);
    }

    private Object[] handleElementsOrChildren(Object element) {
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
