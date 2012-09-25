package org.pitest.pitclipse.core.launch;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.pitest.pitclipse.core.launch.PitclipseConstants.ATTR_TEST_CONTAINER;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public final class PitArgumentsTab extends AbstractLaunchConfigurationTab {
	private static final int NUMBER_OF_COLUMNS = 3;

	private final class UpdateOnModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent evt) {
			updateLaunchConfigurationDialog();
		}
	}

	private Text testClassText;
	private Text projectText;
	private Button testClassRadioButton;
	private Button testDirectoryRadioButton;
	private Text testDirText;
	private String containerId;

	public void initializeFrom(ILaunchConfiguration config) {
		projectText.setText(getAttributeFromConfig(config, ATTR_PROJECT_NAME,
				""));
		String testClass = getAttributeFromConfig(config, ATTR_MAIN_TYPE_NAME,
				"");
		containerId = getAttributeFromConfig(config, ATTR_TEST_CONTAINER, "");
		testClassText.setText(testClass);
		if (testClass.length() == 0 && containerId.length() > 0) {
			testClassText.setText("");
			IJavaElement containerElement = JavaCore.create(containerId);
			testDirText.setText(new JavaElementLabelProvider()
					.getText(containerElement));
			testClassRadioButton.setSelection(false);
			testDirectoryRadioButton.setSelection(true);

		} else {
			testClassText.setText(testClass);
			testDirText.setText("");
			testClassRadioButton.setSelection(true);
			testDirectoryRadioButton.setSelection(false);
		}
		testModeChanged();
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

		createSpacer(comp);
		createProjectWidgets(font, comp);
		createSpacer(comp);
		createTestClassWidgets(font, comp);
		createSpacer(comp);
		createTestDirWidgets(font, comp);
	}

	private void createProjectWidgets(Font font, Composite comp) {
		Label projectLabel = new Label(comp, SWT.NONE);
		projectLabel.setText("Project to mutate:");
		GridData gd = new GridData(FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		projectLabel.setLayoutData(gd);
		projectLabel.setFont(font);

		gd = new GridData(FILL_HORIZONTAL);
		gd.horizontalSpan = NUMBER_OF_COLUMNS;
		projectText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		projectText.setLayoutData(gd);
		projectText.addModifyListener(new UpdateOnModifyListener());
	}

	private void createTestClassWidgets(Font font, Composite comp) {
		testClassRadioButton = createTestRadioButton(comp,
				"Run mutations from a unit test");

		Label testClassLabel = new Label(comp, SWT.NONE);
		testClassLabel.setText("Test Class:");
		GridData labelGrid = new GridData(FILL_HORIZONTAL);
		labelGrid.horizontalIndent = 25;
		labelGrid.horizontalSpan = NUMBER_OF_COLUMNS;
		testClassLabel.setLayoutData(labelGrid);
		testClassLabel.setFont(font);

		GridData textGrid = new GridData(FILL_HORIZONTAL);
		textGrid.horizontalSpan = NUMBER_OF_COLUMNS;
		textGrid.horizontalIndent = 25;
		testClassText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		testClassText.setLayoutData(textGrid);
		testClassText.addModifyListener(new UpdateOnModifyListener());
	}

	private Button createTestRadioButton(Composite comp, String label) {
		final Button button = new Button(comp, SWT.RADIO);
		button.setText(label);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (button.getSelection()) {
					testModeChanged();
				}
			}
		});
		return button;
	}

	private void createTestDirWidgets(Font font, Composite comp) {
		testDirectoryRadioButton = createTestRadioButton(comp,
				"Run mutations against a package or directory");

		GridData labelGrid = new GridData(FILL_HORIZONTAL);
		labelGrid.horizontalIndent = 25;
		labelGrid.horizontalSpan = NUMBER_OF_COLUMNS;
		Label testDirectoryLabel = new Label(comp, SWT.NONE);
		testDirectoryLabel.setText("Directory:");
		testDirectoryLabel.setLayoutData(labelGrid);
		testDirectoryLabel.setFont(font);

		GridData textGrid = new GridData(FILL_HORIZONTAL);
		textGrid.horizontalSpan = NUMBER_OF_COLUMNS;
		textGrid.horizontalIndent = 25;
		testDirText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		testDirText.setLayoutData(textGrid);
		testDirText.addModifyListener(new UpdateOnModifyListener());
	}

	public String getName() {
		return "PIT";
	}

	public void performApply(ILaunchConfigurationWorkingCopy workingCopy) {
		workingCopy.setAttribute(ATTR_PROJECT_NAME, projectText.getText()
				.trim());
		if (testClassRadioButton.getSelection()) {
			workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, testClassText
					.getText().trim());
			workingCopy.setAttribute(ATTR_TEST_CONTAINER, "");
		} else {
			workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, "");
			workingCopy.setAttribute(ATTR_TEST_CONTAINER, containerId);
		}
		try {
			PitMigrationDelegate.mapResources(workingCopy);
		} catch (CoreException ce) {
			setErrorMessage(ce.getStatus().getMessage());
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy workingCopy) {
		/*
		 * IJavaElement javaElement = getContext(); if (javaElement != null) {
		 * initializeJavaProject(javaElement, workingCopy); } else {
		 * workingCopy.setAttribute(
		 * IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); }
		 */
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

	private void createSpacer(Composite comp) {
		Label label = new Label(comp, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);
	}

	private void testModeChanged() {
		boolean isSingleTestMode = testClassRadioButton.getSelection();
		testClassText.setEnabled(isSingleTestMode);
		testDirText.setEnabled(!isSingleTestMode);
		updateLaunchConfigurationDialog();
	}

}
