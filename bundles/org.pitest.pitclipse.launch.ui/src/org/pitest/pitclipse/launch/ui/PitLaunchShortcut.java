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

import static org.eclipse.jdt.core.IJavaElement.CLASS_FILE;
import static org.eclipse.jdt.core.IJavaElement.COMPILATION_UNIT;
import static org.eclipse.jdt.core.IJavaElement.JAVA_PROJECT;
import static org.eclipse.jdt.core.IJavaElement.METHOD;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT_ROOT;
import static org.eclipse.jdt.core.IJavaElement.TYPE;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.jdt.ui.JavaElementLabels.ALL_FULLY_QUALIFIED;
import static org.eclipse.jdt.ui.JavaElementLabels.getTextLabel;
import static org.eclipse.jdt.ui.JavaUI.getEditorInputTypeRoot;
import static org.pitest.pitclipse.launch.PitLaunchArgumentsConstants.ATTR_TEST_CONTAINER;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_AVOID_CALLS_TO;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_EXCLUDE_CLASSES;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_EXCLUDE_METHODS;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_TEST_INCREMENTALLY;
import static org.pitest.pitclipse.launch.config.LaunchConfigurationWrapper.ATTR_TEST_IN_PARALLEL;
import static org.pitest.pitclipse.launch.ui.LaunchShortcut.asJavaElement;
import static org.pitest.pitclipse.launch.ui.LaunchShortcut.emptyLaunchConfiguration;
import static org.pitest.pitclipse.launch.ui.LaunchShortcut.emptyList;
import static org.pitest.pitclipse.launch.ui.LaunchShortcut.forEditorInputDo;
import static org.pitest.pitclipse.launch.ui.LaunchShortcut.getCorrespondingResource;
import static org.pitest.pitclipse.launch.ui.LaunchShortcut.toArrayOfILaunchConfiguration;
import static org.pitest.pitclipse.launch.ui.PitLaunchUiActivator.getActiveWorkbenchShell;
import static org.pitest.pitclipse.launch.ui.PitMigrationDelegate.mapResources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOrdinaryClassFile;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.pitest.pitclipse.core.PitCoreActivator;
import org.pitest.pitclipse.runner.config.PitConfiguration;
import org.pitest.pitclipse.ui.utils.PitclipseUiUtils;

/**
 * Allows to launch a PIT analyze from a contextual menu.
 */
public class PitLaunchShortcut implements ILaunchShortcut2 {

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$
    public static final String TEST_CONFIGURATION = "Select a Test Configuration";
    public static final String TEST_RUN_CONFIGURATION = "Select JUnit configuration to run";
    public static final String PIT_CONFIGURATION_TYPE = "org.pitest.pitclipse.launch.mutationTest";

    @Override
    public void launch(IEditorPart editor, String mode) {
        ITypeRoot element = getEditorInputTypeRoot(editor.getEditorInput());
        if (element == null) {
            showNoTestsFoundDialog();
        } else {
            launch(new Object[] { element }, mode);
        }
    }

    @Override
    public void launch(ISelection selection, String mode) {
        launch(((IStructuredSelection) selection).toArray(), mode);
    }

    private void launch(Object[] elements, String mode) {
        PitclipseUiUtils.executeSafely(() -> {
            if (elements.length == 1) {
                Optional<IJavaElement> selected = asJavaElement(elements[0]);
                Optional<IJavaElement> launchElement =
                        selected.flatMap(this::getLaunchElementFor);
                
                if (launchElement.isPresent()) {
                    performLaunch(launchElement.get(), mode);
                    return;
                }
            }
            showNoTestsFoundDialog();
        });
    }

    private void showNoTestsFoundDialog() {
        MessageDialog.openInformation(getShell(), "Pitclipse", "No tests found");
    }

    private void performLaunch(IJavaElement element, String mode) throws InterruptedException, CoreException {
        ILaunchConfigurationWorkingCopy tmp = createLaunchConfiguration(element);
        Optional<ILaunchConfiguration> existingConfig = findExistingLaunchConfiguration(tmp);
        ILaunchConfiguration config;
        if (existingConfig.isPresent()) {
            config = existingConfig.get();
        } else {
            config = tmp.doSave();
        }
        DebugUITools.launch(config, mode);
    }

    private Shell getShell() {
        return getActiveWorkbenchShell();
    }

    private ILaunchManager getLaunchManager() {
        return DebugPlugin.getDefault().getLaunchManager();
    }

    /**
     * Show a selection dialog that allows the user to choose one of the
     * specified launch configurations. Return the chosen config, or
     * <code>null</code> if the user cancelled the dialog.
     * 
     * @param configList
     *            list of {@link ILaunchConfiguration}s
     * @return ILaunchConfiguration
     * @throws InterruptedException
     *             if cancelled by the user
     */
    private ILaunchConfiguration chooseConfiguration(List<ILaunchConfiguration> configList) throws InterruptedException {
        IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
        ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
        dialog.setElements(configList.toArray());
        dialog.setTitle(PitLaunchShortcut.TEST_CONFIGURATION);
        dialog.setMessage(PitLaunchShortcut.TEST_RUN_CONFIGURATION);

        dialog.setMultipleSelection(false);
        int result = dialog.open();
        if (result == Window.OK) {
            return (ILaunchConfiguration) dialog.getFirstResult();
        }
        throw new InterruptedException(); // cancelled by user
    }

    /**
     * Returns the launch configuration type id of the launch configuration this
     * shortcut will create. Clients can override this method to return the id
     * of their launch configuration.
     * 
     * @return the launch configuration type id of the launch configuration this
     *         shortcut will create
     */
    protected String getLaunchConfigurationTypeId() {
        return PIT_CONFIGURATION_TYPE;
    }

    protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(IJavaElement element) throws CoreException {
        final String testName;
        final String mainTypeQualifiedName;
        final String containerHandleId;

        switch (element.getElementType()) {
            case TYPE:
                containerHandleId = EMPTY_STRING;
                // don't replace, fix for binary inner types
                mainTypeQualifiedName = ((IType) element).getFullyQualifiedName('.');
                testName = element.getElementName();
                break;
            case METHOD:
                IMethod method = (IMethod) element;
                containerHandleId = EMPTY_STRING;
                mainTypeQualifiedName = method.getDeclaringType().getFullyQualifiedName('.');
                testName = method.getDeclaringType().getElementName() + '.' + method.getElementName();
                break;
            default: // JAVA_PROJECT, PACKAGE_FRAGMENT_ROOT, PACKAGE_FRAGMENT
                String name = getTextLabel(element, ALL_FULLY_QUALIFIED);
                containerHandleId = element.getHandleIdentifier();
                mainTypeQualifiedName = EMPTY_STRING;
                testName = name.substring(name.lastIndexOf(IPath.SEPARATOR) + 1);
        }

        ILaunchConfigurationType configType = getLaunchManager().getLaunchConfigurationType(getLaunchConfigurationTypeId());
        ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(testName));

        wc.setAttribute(ATTR_MAIN_TYPE_NAME, mainTypeQualifiedName);
        wc.setAttribute(ATTR_PROJECT_NAME, element.getJavaProject().getElementName());
        wc.setAttribute(ATTR_TEST_CONTAINER, containerHandleId);

        PitConfiguration preferences = PitCoreActivator.getDefault().getConfiguration();
        wc.setAttribute(ATTR_TEST_IN_PARALLEL, preferences.isParallelExecution());
        wc.setAttribute(ATTR_TEST_INCREMENTALLY, preferences.isIncrementalAnalysis());
        wc.setAttribute(ATTR_EXCLUDE_CLASSES, preferences.getExcludedClasses());
        wc.setAttribute(ATTR_EXCLUDE_METHODS, preferences.getExcludedMethods());
        wc.setAttribute(ATTR_AVOID_CALLS_TO, preferences.getAvoidCallsTo());
        mapResources(wc);
        return wc;
    }

    /**
     * Returns the attribute names of the attributes that are compared when
     * looking for an existing similar launch configuration. Clients can
     * override and replace to customize.
     * 
     * @return the attribute names of the attributes that are compared
     */
    protected String[] getAttributeNamesToCompare() {
        return new String[] { ATTR_PROJECT_NAME, ATTR_TEST_CONTAINER, ATTR_MAIN_TYPE_NAME };
    }

    private static boolean hasSameAttributes(ILaunchConfiguration config1, ILaunchConfiguration config2, String[] attributeToCompare) {
        return PitclipseUiUtils.executeSafely(() -> {
            for (String element : attributeToCompare) {
                String val1 = config1.getAttribute(element, EMPTY_STRING);
                String val2 = config2.getAttribute(element, EMPTY_STRING);
                if (!val1.equals(val2)) {
                    return false;
                }
            }
            return true;
        });
    }

    private Optional<ILaunchConfiguration> findExistingLaunchConfiguration(ILaunchConfigurationWorkingCopy temporary) throws InterruptedException,
            CoreException {
        List<ILaunchConfiguration> candidateConfigs = findExistingLaunchConfigurations(temporary);

        // If there are no existing configs associated with the IType, create one.
        // If there is exactly one config associated with the IType, return it.
        // Otherwise, if there is more than one config associated with the
        // IType, prompt the user to choose one.
        int candidateCount = candidateConfigs.size();
        if (candidateCount == 0) {
            return Optional.empty();
        } else if (candidateCount == 1) {
            return Optional.ofNullable(candidateConfigs.get(0));
        }

        // Prompt the user to choose a config.
        // if the user does not presses OK we have already interrupted
        // the launch with an InterruptedException
        ILaunchConfiguration config = chooseConfiguration(candidateConfigs);
        return Optional.ofNullable(config);
    }

    private List<ILaunchConfiguration> findExistingLaunchConfigurations(ILaunchConfigurationWorkingCopy temporary) throws CoreException {
        ILaunchConfigurationType configType = temporary.getType();

        ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(configType);
        String[] attributeToCompare = getAttributeNamesToCompare();

        List<ILaunchConfiguration> candidateConfigs = new ArrayList<>();
        for (ILaunchConfiguration config : configs) {
            if (hasSameAttributes(config, temporary, attributeToCompare)) {
                candidateConfigs.add(config);
            }
        }
        return candidateConfigs;
    }

    private List<ILaunchConfiguration> findExistingLaunchConfigurations(Object candidate) {
        Optional<IJavaElement> element = asJavaElement(candidate);
        return element.map(findLaunchConfigurations()).orElse(emptyLaunchConfiguration());
    }

    /**
     * {@inheritDoc}
     * 
     * @since 3.4
     */
    @Override
    public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
        IStructuredSelection ss = (IStructuredSelection) selection;
        if (ss.size() == 1) {
            List<ILaunchConfiguration> configs = findExistingLaunchConfigurations(ss.getFirstElement());
            return toArrayOfILaunchConfiguration(configs);
        }
        return null; // NOSONAR Eclipse code relies on returned null array
    }

    /**
     * {@inheritDoc}
     * 
     * @since 3.4
     */
    @Override
    public ILaunchConfiguration[] getLaunchConfigurations(final IEditorPart editor) {
        ITypeRoot element = getEditorInputTypeRoot(editor.getEditorInput());
        return Optional.ofNullable(element).map(toLaunchConfigurations()).orElse(emptyList());
    }

    /**
     * {@inheritDoc}
     * 
     * @since 3.4
     */
    @Override
    public IResource getLaunchableResource(ISelection selection) {
        IStructuredSelection ss = (IStructuredSelection) selection;
        if (ss.size() == 1) {
            Object selected = ss.getFirstElement();
            return asJavaElement(selected).map(IJavaElement::getResource).orElse(null);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 3.4
     */
    @Override
    public IResource getLaunchableResource(IEditorPart editor) {
        return forEditorInputDo(
                editor.getEditorInput(),
                getCorrespondingResource(),
                Optional::empty)
            .orElse(null);
    }

    private Function<IJavaElement, List<ILaunchConfiguration>> findLaunchConfigurations() {
        return element -> {
            Optional<IJavaElement> launchElement = getLaunchElementFor(element);
            return launchElement
                .map(javaElement -> 
                    PitclipseUiUtils.executeSafelyOrElse(
                        () -> findExistingLaunchConfigurations
                            (createLaunchConfiguration(javaElement)),
                        emptyLaunchConfiguration()))
                .orElse(emptyLaunchConfiguration());
        };
    }

    private Optional<IJavaElement> getLaunchElementFor(IJavaElement element) {
        switch (element.getElementType()) {
            case JAVA_PROJECT:
            case PACKAGE_FRAGMENT_ROOT:
            case PACKAGE_FRAGMENT:
            case TYPE:
            case METHOD:
                return Optional.of(element);
            case CLASS_FILE:
				IClassFile classFile = (IClassFile) element;
				if (classFile instanceof IOrdinaryClassFile) {
					return Optional.ofNullable(((IOrdinaryClassFile)classFile).getType());
				}
            case COMPILATION_UNIT:
                return Optional.ofNullable(((ICompilationUnit) element).findPrimaryType());
            default:
                return Optional.empty();
        }
    }

    private Function<ITypeRoot, ILaunchConfiguration[]> toLaunchConfigurations() {
        return typeRoot -> {
            List<ILaunchConfiguration> configs = findExistingLaunchConfigurations(typeRoot);
            return toArrayOfILaunchConfiguration(configs);
        };
    }
}
