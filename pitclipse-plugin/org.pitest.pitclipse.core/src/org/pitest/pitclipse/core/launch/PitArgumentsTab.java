package org.pitest.pitclipse.core.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public final class PitArgumentsTab extends JavaLaunchTab {
	private static final int NUMBER_OF_COLUMNS = 3;

	private final class UpdateOnModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent evt) {
			updateLaunchConfigurationDialog();
		}
	}

	private Text testClassText;
	private Text projectText;

	@Override
	public void initializeFrom(ILaunchConfiguration config) {
		super.initializeFrom(config);
		testClassText.setText(getAttributeFromConfig(config,
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, ""));
		projectText.setText(getAttributeFromConfig(config,
				IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""));
	}

	public void createControl(Composite parent) {
		Font font = parent.getFont();
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		topLayout.numColumns = NUMBER_OF_COLUMNS;
		comp.setLayout(topLayout);
		comp.setFont(font);

		createVerticalSpacer(comp, 1);
		createTestClassWidgets(font, comp);
		createVerticalSpacer(comp, 1);
		createProjectWidgets(font, comp);
	}

	private void createProjectWidgets(Font font, Composite comp) {
		Label projectLabel = new Label(comp, SWT.NONE);
		projectLabel.setText("Project to mutate:");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		projectLabel.setLayoutData(gd);
		projectLabel.setFont(font);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = NUMBER_OF_COLUMNS;
		projectText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		projectText.setLayoutData(gd);
		projectText.addModifyListener(new UpdateOnModifyListener());
	}

	private void createTestClassWidgets(Font font, Composite comp) {
		Label testClassLabel = new Label(comp, SWT.NONE);
		testClassLabel.setText("Test Class:");
		GridData labelGrid = new GridData(GridData.FILL_HORIZONTAL);
		labelGrid.horizontalSpan = 1;
		testClassLabel.setLayoutData(labelGrid);
		testClassLabel.setFont(font);

		GridData textGrid = new GridData(GridData.FILL_HORIZONTAL);
		textGrid.horizontalSpan = NUMBER_OF_COLUMNS;
		testClassText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		testClassText.setLayoutData(textGrid);
		testClassText.addModifyListener(new UpdateOnModifyListener());
	}

	public String getName() {
		return "PIT";
	}

	public void performApply(ILaunchConfigurationWorkingCopy workingCopy) {
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
				projectText.getText().trim());
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				testClassText.getText().trim());
		try {
			PitMigrationDelegate.mapResources(workingCopy);
		} catch (CoreException ce) {
			setErrorMessage(ce.getStatus().getMessage());
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy workingCopy) {
		IJavaElement javaElement = getContext();
		if (javaElement != null) {
			initializeJavaProject(javaElement, workingCopy);
		} else {
			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
		}
	}

	public String getAttributeFromConfig(ILaunchConfiguration config,
			String attribute, String defaultValue) {
		String result = defaultValue;
		try {
			result = config.getAttribute(attribute, defaultValue);
		} catch (CoreException e) {
			// Swallowed
		}
		return result;
	}

}
