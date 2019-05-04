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

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.pitest.pitclipse.runner.model.ClassMutations;
import org.pitest.pitclipse.runner.model.Countable;
import org.pitest.pitclipse.runner.model.Mutation;
import org.pitest.pitclipse.runner.model.MutationsModel;
import org.pitest.pitclipse.runner.model.MutationsModelVisitor;
import org.pitest.pitclipse.runner.model.PackageMutations;
import org.pitest.pitclipse.runner.model.ProjectMutations;
import org.pitest.pitclipse.runner.model.Status;
import org.pitest.pitclipse.runner.model.Visitable;
import org.pitest.pitclipse.runner.results.DetectionStatus;

import java.net.URL;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Sets.immutableEnumSet;
import static org.pitest.pitclipse.runner.results.DetectionStatus.KILLED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.MEMORY_ERROR;
import static org.pitest.pitclipse.runner.results.DetectionStatus.NON_VIABLE;
import static org.pitest.pitclipse.runner.results.DetectionStatus.NOT_STARTED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.RUN_ERROR;
import static org.pitest.pitclipse.runner.results.DetectionStatus.STARTED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.TIMED_OUT;

public class ViewLabelProvider extends LabelProvider {

    private static final Image MUTATION_DETECTED = getBundleImage("detected.gif");
    private static final Image MUTATION_NOT_DETECTED = getBundleImage("not_detected.gif");
    private static final Set<DetectionStatus> DETECTED_STATUSES = immutableEnumSet(KILLED, TIMED_OUT, NON_VIABLE,
            MEMORY_ERROR, NOT_STARTED, STARTED, RUN_ERROR);

    @Override
    public String getText(Object element) {
        if (element instanceof Visitable) {
            Visitable visitable = (Visitable) element;
            return visitable.accept(LabelVisitor.INSTANCE);
        }
        return "";
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof Visitable) {
            Visitable visitable = (Visitable) element;
            return visitable.accept(ImageVisitor.INSTANCE);
        }
        return null;
    }

    private static Image getBundleImage(String file) {
        Bundle bundle = FrameworkUtil.getBundle(ViewLabelProvider.class);
        URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
        ImageDescriptor image = ImageDescriptor.createFromURL(url);
        return image.createImage();
    }

    private enum LabelVisitor implements MutationsModelVisitor<String> {
        INSTANCE;

        @Override
        public String visitModel(MutationsModel mutationsModel) {
            return "Mutations";
        }

        @Override
        public String visitProject(ProjectMutations projectMutations) {
            return projectMutations.getProjectName() + countString(projectMutations);
        }

        @Override
        public String visitPackage(PackageMutations packageMutations) {
            String label = packageMutations.getPackageName();
            if (isNullOrEmpty(label)) {
                label = "(default package)";
            }
            return label + countString(packageMutations);
        }

        @Override
        public String visitClass(ClassMutations classMutations) {
            return classMutations.getClassName() + countString(classMutations);
        }

        @Override
        public String visitMutation(Mutation mutation) {
            return Integer.toString(mutation.getLineNumber()) + ": " + mutation.getDescription();
        }

        @Override
        public String visitStatus(Status status) {
            return status.getDetectionStatus().toString() + countString(status);
        }

        private String countString(Countable countable) {
            return " (" + countable.count() + ")";
        }
    }

    private enum ImageVisitor implements MutationsModelVisitor<Image> {
        INSTANCE;

        @Override
        public Image visitModel(MutationsModel mutationsModel) {
            return getPlatformIcon(ISharedImages.IMG_OBJ_ELEMENT);
        }

        @Override
        public Image visitProject(ProjectMutations projectMutations) {
            return getPlatformIcon(SharedImages.IMG_OBJ_PROJECT);
        }

        @Override
        public Image visitPackage(PackageMutations packageMutations) {
            return getJavaIcon(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKAGE);
        }

        @Override
        public Image visitClass(ClassMutations classMutations) {
            return getJavaIcon(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS);
        }

        private Image getPlatformIcon(String icon) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(icon);
        }

        private Image getJavaIcon(String icon) {
            return JavaUI.getSharedImages().getImage(icon);
        }

        @Override
        public Image visitMutation(Mutation mutation) {
            if (mutationWasDetected(mutation.getStatus())) {
                return MUTATION_DETECTED;
            }
            return MUTATION_NOT_DETECTED;
        }

        private boolean mutationWasDetected(DetectionStatus status) {
            return DETECTED_STATUSES.contains(status);
        }

        @Override
        public Image visitStatus(Status status) {
            if (mutationWasDetected(status.getDetectionStatus())) {
                return MUTATION_DETECTED;
            }
            return MUTATION_NOT_DETECTED;
        }
    }
}
