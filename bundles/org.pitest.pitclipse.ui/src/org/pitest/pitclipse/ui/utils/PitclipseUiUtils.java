/*******************************************************************************
 * Copyright 2021 Lorenzo Bettini and contributors
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
package org.pitest.pitclipse.ui.utils;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.pitest.pitclipse.ui.view.mutations.PitMutationsView;

/**
 * A few utilities for the UI.
 * 
 * @author Lorenzo Bettini
 *
 */
public class PitclipseUiUtils {

    private PitclipseUiUtils() {
        // Only static utility methods
    }

    @FunctionalInterface
    public static interface RunnableWithCoreException {
        void run() throws CoreException;
    }

    @FunctionalInterface
    public static interface ProviderWithCoreException<T> {
        T get() throws CoreException;
    }

    @FunctionalInterface
    public static interface PredicateWithCoreException {
        boolean test() throws CoreException;
    }

    @FunctionalInterface
    public static interface RunnableWithCoreExceptionInterruptable {
        void run() throws CoreException, InterruptedException;
    }

    /**
     * Executes the passed predicate and in case of {@link CoreException} returns
     * false.
     * 
     * @param predicate
     */
    public static boolean executeSafely(PredicateWithCoreException predicate) {
        try {
            return predicate.test();
        } catch (CoreException ce) {
            return false;
        }
    }

    /**
     * Returns the result of the passed provider and in case of {@link CoreException} returns
     * the orElse argument.
     * 
     * @param predicate
     */
    public static <T> T executeSafelyOrElse(ProviderWithCoreException<T> provider, T orElse) {
        try {
            return provider.get();
        } catch (CoreException ce) {
            return orElse;
        }
    }

    /**
     * Executes the passed lambda and ignores exceptions.
     * 
     * @param runnable
     */
    public static void executeSafely(RunnableWithCoreExceptionInterruptable runnable) {
        try {
            runnable.run();
        } catch (InterruptedException e) { // NOSONAR
            // OK, silently move on
        } catch (CoreException e) { // NOSONAR
            // OK, silently move on
        }
    }

    /**
     * Try to adapt the object to the {@link IAdaptable} type, or returns null
     * if it's not an {@link IAdaptable}.
     * 
     * @param <T>
     * @param o
     * @param type
     * @return
     */
    public static <T extends IAdaptable> T tryToAdapt(Object o, Class<T> type) {
        if (o instanceof IAdaptable) {
            return ((IAdaptable) o).getAdapter(type);
        }
        return null;
    }

    /**
     * Only performs the operation if the {@link Composite} is not null and not
     * disposed.
     * 
     * @param c
     */
    public static void setFocusSafely(Composite c) {
        if (c != null && !c.isDisposed()) {
            c.setFocus();
        }
    }

    /**
     * Only performs the operation if the {@link Composite} is not null and not
     * disposed.
     * 
     * @param c
     */
    public static void disposeSafely(Composite c) {
        if (c != null && !c.isDisposed()) {
            c.dispose();
        }
    }

    /**
     * Executes the {@link Runnable} and in case of {@link SWTError} shows a dialog
     * with the specified title and error message, using the specified parent.
     * 
     * @param runnable
     * @param parent
     * @param title
     * @param errorMessage
     */
    public static void executeSafely(Runnable runnable, Composite parent,
            String title, String errorMessage) {
        try {
            runnable.run();
        } catch (SWTError e) {
            if (e.getMessage() != null) {
                errorMessage += "\n" + e.getMessage();
            }
            MessageBox messageBox = new MessageBox(parent.getShell(),
                    SWT.ICON_ERROR | SWT.OK);
            messageBox.setText(title);
            messageBox.setMessage(errorMessage);
            messageBox.open();
        }
    }

    /**
     * Creates an image descriptor from the image file located in the "images"
     * folder.
     * 
     * @param file
     * @return
     */
    public static ImageDescriptor getBundleImage(String file) {
        Bundle bundle = FrameworkUtil.getBundle(PitMutationsView.class);
        URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
        return ImageDescriptor.createFromURL(url);
    }
}
