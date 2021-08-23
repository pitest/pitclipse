package org.pitest.pitclipse.ui.highlighting;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;
import org.pitest.pitclipse.ui.core.PitUiActivator;

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
