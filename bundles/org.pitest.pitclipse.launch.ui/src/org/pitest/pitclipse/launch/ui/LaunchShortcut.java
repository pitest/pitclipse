package org.pitest.pitclipse.launch.ui;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorInput;

import java.util.List;

import static org.eclipse.jdt.ui.JavaUI.getEditorInputTypeRoot;

final class LaunchShortcut {

    static <T> Optional<T> forEditorInputDo(IEditorInput i, Function<ITypeRoot, Optional<T>> onFound, Function<Void, Optional<T>> notFound) {
        Optional<ITypeRoot> element = Optional.fromNullable(getEditorInputTypeRoot(i));
        if (element.isPresent()) {
            return element.transform(onFound).get();
        } else {
            return notFound.apply(null);
        }
    }

    static <T> Function<Void, Optional<IResource>> nothing() {
        return new Function<Void, Optional<IResource>>() {
            public Optional<IResource> apply(Void v) {
                return Optional.absent();
            }
        };
    }

    static Function<ITypeRoot, Optional<IResource>> getCorrespondingResource() {
        return new Function<ITypeRoot, Optional<IResource>>() {
            public Optional<IResource> apply(ITypeRoot t) {
                try {
                    return Optional.fromNullable(t.getCorrespondingResource());
                } catch (JavaModelException e) {
                    return Optional.absent();
                }
            }
        };
    }

    static Optional<IJavaElement> asJavaElement(Object o) {
        if (o instanceof IJavaElement) {
            IJavaElement element = (IJavaElement) o;
            return Optional.of(element);
        } else if (o instanceof IAdaptable) {
            Object adapted = ((IAdaptable) o).getAdapter(IJavaElement.class);
            return Optional.fromNullable((IJavaElement) adapted);
        } else {
            return Optional.absent();
        }
    }

    static ImmutableList<ILaunchConfiguration> emptyLaunchConfiguration() {
        return ImmutableList.<ILaunchConfiguration>of();
    }

    static ILaunchConfiguration[] emptyList() {
        return new ILaunchConfiguration[0];
    }

    static ILaunchConfiguration[] toArrayOfILaunchConfiguration(List<ILaunchConfiguration> l) {
        return l.toArray(new ILaunchConfiguration[l.size()]);
    }

    private LaunchShortcut() {
        // utility class should not be instantiated
    }
}