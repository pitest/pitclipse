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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;
import org.pitest.pitclipse.ui.core.PitUiActivator;

/**
 * Simple image provider for the mutation marker
 * @author Jonas Kutscha
 */
public class AnnotationImageProvider implements IAnnotationImageProvider {
    @Override
    public String getImageDescriptorId(Annotation annotation) {
        return annotation.getType();
    }

    @Override
    public ImageDescriptor getImageDescriptor(String imageDescritporId) {
        return PitUiActivator.getDefault().getImageDescriptor(imageDescritporId);
    }

    @Override
    public Image getManagedImage(Annotation annotation) {
        return PitUiActivator.getDefault().getImage(getImageDescriptorId(annotation));
    }
}
