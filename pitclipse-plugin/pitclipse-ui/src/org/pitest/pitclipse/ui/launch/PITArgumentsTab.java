package org.pitest.pitclipse.ui.launch;

import static org.pitest.pitclipse.ui.launch.PITClipseConstants.PIT_PROJECT;
import static org.pitest.pitclipse.ui.launch.PITClipseConstants.PIT_TEST_CLASS;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PITArgumentsTab extends JavaLaunchTab {
	private final class UpdateOnModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent evt) {
			updateLaunchConfigurationDialog();
		}
	}

	private Label testClassLabel;
	private Text testClassText;
	private Label projectLabel;
	private Text projectText;

	@Override
	public void initializeFrom(ILaunchConfiguration config) {
		super.initializeFrom(config);
		testClassText
				.setText(getAttributeFromConfig(config, PIT_TEST_CLASS, ""));
		projectText.setText(getAttributeFromConfig(config, PIT_PROJECT, ""));
	}

	public void createControl(Composite parent) {
		Font font = parent.getFont();

		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
		// IJavaDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		topLayout.numColumns = 3;
		comp.setLayout(topLayout);
		comp.setFont(font);

		createVerticalSpacer(comp, 1);
		createTestClassWidgets(font, comp);
		createVerticalSpacer(comp, 1);
		createProjectWidgets(font, comp);
	}

	private void createProjectWidgets(Font font, Composite comp) {
		GridData gd;
		projectLabel = new Label(comp, SWT.NONE);
		projectLabel.setText("Project:");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		projectLabel.setLayoutData(gd);
		projectLabel.setFont(font);
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		projectText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		projectText.setLayoutData(gd);
		projectText.addModifyListener(new UpdateOnModifyListener());
	}

	private void createTestClassWidgets(Font font, Composite comp) {
		testClassLabel = new Label(comp, SWT.NONE);
		testClassLabel.setText("Project to mutate:");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		testClassLabel.setLayoutData(gd);
		testClassLabel.setFont(font);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		testClassText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		testClassText.setLayoutData(gd);
		testClassText.addModifyListener(new UpdateOnModifyListener());
	}

	public String getName() {
		return "PIT";
	}

	public void performApply(ILaunchConfigurationWorkingCopy workingCopy) {
		workingCopy.setAttribute(PIT_TEST_CLASS, testClassText.getText());
		workingCopy.setAttribute(PIT_PROJECT, projectText.getText());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy workingCopy) {
		workingCopy.setAttribute(PIT_TEST_CLASS, "");
		workingCopy.setAttribute(PIT_PROJECT, "");
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
