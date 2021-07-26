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

package org.pitest.pitclipse.launch.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorInput;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.eclipse.jdt.ui.JavaUI.getEditorInputTypeRoot;

final class LaunchShortcut {

    static <T> Optional<T> forEditorInputDo(IEditorInput i, Function<ITypeRoot, Optional<T>> onFound, Supplier<Optional<T>> notFound) {
        Optional<ITypeRoot> element = Optional.ofNullable(getEditorInputTypeRoot(i));
        return element.map(onFound).orElseGet(notFound);
    }

    static Function<ITypeRoot, Optional<IResource>> getCorrespondingResource() {
        return t -> {
            try {
                return Optional.ofNullable(t.getCorrespondingResource());
            } catch (JavaModelException e) {
                return Optional.empty();
            }
        };
    }

    static Optional<IJavaElement> asJavaElement(Object o) {
        if (o instanceof IJavaElement) {
            IJavaElement element = (IJavaElement) o;
            return Optional.of(element);
        } else if (o instanceof IAdaptable) {
            Object adapted = ((IAdaptable) o).getAdapter(IJavaElement.class);
            return Optional.ofNullable((IJavaElement) adapted);
        } else {
            return Optional.empty();
        }
    }

    static List<ILaunchConfiguration> emptyLaunchConfiguration() {
        return Collections.emptyList();
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
