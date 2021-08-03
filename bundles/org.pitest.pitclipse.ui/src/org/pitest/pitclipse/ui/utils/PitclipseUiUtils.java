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

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;

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
}
