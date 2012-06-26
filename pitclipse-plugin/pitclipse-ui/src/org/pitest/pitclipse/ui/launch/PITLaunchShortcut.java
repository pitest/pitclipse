package org.pitest.pitclipse.ui.launch;

import static org.pitest.pitclipse.ui.launch.PITClipseConstants.PIT_CONFIGURATION_TYPE;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchShortcut;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.operation.IRunnableContext;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class PITLaunchShortcut extends JavaLaunchShortcut {
	
	private static final class ShortcutCreationException extends RuntimeException {
		private static final long serialVersionUID = 5417603423928953693L;
		public ShortcutCreationException(CoreException exception) {
			super(exception);
		}
	}

	private static final String EMPTY_SELECTION_MSG = "Empty Selection";
	private static final String EMPTY_EDITOR_MSG = "Empty Editor";
	private static final String TYPE_SELECTION_TITLE = "Type Selection wtf??";

	@Override
	protected ILaunchConfiguration createConfiguration(IType type) {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = getConfigurationType();
		String fullyQualifiedName = type.getFullyQualifiedName();
		String projectName = type.getJavaProject().getElementName();
		try {
			wc = configType.newInstance(null, launchManager.generateLaunchConfigurationName(type.getTypeQualifiedName('.')));
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, fullyQualifiedName);
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
			wc.setAttribute(PITClipseConstants.PIT_TEST_CLASS, fullyQualifiedName);
			wc.setAttribute(PITClipseConstants.PIT_PROJECT, projectName);
			wc.setMappedResources(new IResource[] {type.getUnderlyingResource()});
			config = wc.doSave();
		} catch (CoreException exception) {
			throw new ShortcutCreationException(exception);	
		} 
		return config;
	}

	/**
	 * Returns the Java elements corresponding to the given objects. Members are translated
	 * to corresponding declaring types where possible.
	 * 
	 * @param objects selected objects
	 * @return corresponding Java elements
	 * @since 3.5
	 */
	protected List<IType> getJavaElements(Object[] objects) {
		Builder<IType> builder = ImmutableList.builder();
		for (Object object : objects) {
			if (object instanceof IAdaptable) {
				Object element = ((IAdaptable)object).getAdapter(ICompilationUnit.class);
				if (element != null) {
					ICompilationUnit compilationElement = (ICompilationUnit)element;
					builder.add(compilationElement.findPrimaryType());
				}
			}
		}
		return builder.build();
	}
	
	@Override
	protected IType[] findTypes(Object[] elements, IRunnableContext context) throws InterruptedException, CoreException {
	//	try {
/*			if(elements.length == 1) {
				IType type = isMainMethod(elements[0]);
				if(type != null) {
					return new IType[] {type};
				}
			}
*/			List<IType> javaElements = getJavaElements(elements);
						
			return javaElements.toArray(new IType[javaElements.size()]);
	//	} catch (InvocationTargetException e) {
//			throw (CoreException)e.getTargetException(); 
//		}
	}

	@Override
	protected ILaunchConfigurationType getConfigurationType() {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		return launchManager.getLaunchConfigurationType(PIT_CONFIGURATION_TYPE);
	}

	@Override
	protected String getEditorEmptyMessage() {
		return EMPTY_EDITOR_MSG;
	}

	@Override
	protected String getSelectionEmptyMessage() {
		return EMPTY_SELECTION_MSG;
	}

	@Override
	protected String getTypeSelectionTitle() {
		return TYPE_SELECTION_TITLE;
	}
}
