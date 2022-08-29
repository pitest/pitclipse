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

import static org.eclipse.jdt.ui.JavaUI.getEditorInputTypeRoot;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.ui.IEditorInput;
import org.pitest.pitclipse.ui.utils.PitclipseUiUtils;

import com.google.common.collect.ImmutableList;

final class LaunchShortcut {

    static <T> Optional<T> forEditorInputDo(IEditorInput i, Function<ITypeRoot, Optional<T>> onFound, Supplier<Optional<T>> notFound) {
        Optional<ITypeRoot> element = Optional.ofNullable(getEditorInputTypeRoot(i));
        return element.map(onFound).orElseGet(notFound);
    }

    static Function<ITypeRoot, Optional<IResource>> getCorrespondingResource() {
        return t -> 
            Optional.ofNullable(PitclipseUiUtils.executeSafelyOrElse(
                t::getCorrespondingResource, null));
    }

    static Optional<IJavaElement> asJavaElement(Object o) {
        if (o instanceof IJavaElement) {
            return Optional.of((IJavaElement) o);
        }
        return Optional.ofNullable(PitclipseUiUtils.tryToAdapt(o, IJavaElement.class));
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
