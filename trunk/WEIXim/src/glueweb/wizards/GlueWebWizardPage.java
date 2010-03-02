/*******************************************************************************
 * Copyright (c) ,  IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v.
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package glueweb.wizards;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;

/**
 * Standard main page for a wizard that is creates a project resource.
 * <p>
 * This page may be used by clients as-is; it may be also be subclassed to suit.
 * </p>
 * <p>
 * Example useage:
 * 
 * <pre>
 * mainPage = new WizardNewProjectCreationPage(&quot;basicNewProjectPage&quot;);
 * mainPage.setTitle(&quot;Project&quot;);
 * mainPage.setDescription(&quot;Create a new project resource.&quot;);
 * </pre>
 * 
 * </p>
 */
public class GlueWebWizardPage extends WizardPage {

	boolean useDefaults = true;

	// initial value stores
	private String initialProjectFieldValue;

	private String initialLocationFieldValue;

	Button radioButton1;
	Button radioButton2;

	// the value the user has entered
	String customLocationFieldValue;

	// widgets
	Text projectNameField;

	public static Text locationPathField;

	Label locationLabel;

	Button browseButton;

	private Listener nameModifyListener = new Listener() {
		public void handleEvent(Event e) {
			boolean valid = validatePage();
			setPageComplete(valid);
			if (valid)
				setLocationForSelection();
		}
	};

	private Listener locationModifyListener = new Listener() {
		public void handleEvent(Event e) {
			setPageComplete(validatePage());
		}
	};

	// constants
	private static final int SIZING_TEXT_FIELD_WIDTH = 250;

	/**
	 * Creates a new project creation wizard page.
	 * 
	 * @param pageName
	 *            the name of this page
	 */
	public GlueWebWizardPage(String pageName) {
		super(pageName);
		setPageComplete(false);
		customLocationFieldValue = ""; //$NON-NLS-$
	}

	/**
	 * (non-Javadoc) Method declared on IDialogPage.
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());

		initializeDialogUnits(parent);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
				IIDEHelpContextIds.NEW_PROJECT_WIZARD_PAGE);

		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createProjectNameGroup(composite);
		createProjectLocationGroup(composite);
		createAximOrGlue(composite);
		setPageComplete(validatePage());
		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
	}

	/**
	 * Creates the project location specification controls.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private final void createProjectLocationGroup(Composite parent) {

		Font font = parent.getFont();
		// project specification group
		Group projectGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		projectGroup.setLayout(layout);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectGroup.setFont(font);
		projectGroup.setText("Set Location");

		final Button useDefaultsButton = new Button(projectGroup, SWT.CHECK
				| SWT.RIGHT);
		useDefaultsButton.setText("Use default location");
		useDefaultsButton.setSelection(useDefaults);
		useDefaultsButton.setFont(font);

		GridData buttonData = new GridData();
		buttonData.horizontalSpan = 3;
		useDefaultsButton.setLayoutData(buttonData);

		createUserSpecifiedProjectLocationGroup(projectGroup, !useDefaults);

		SelectionListener listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				useDefaults = useDefaultsButton.getSelection();
				browseButton.setEnabled(!useDefaults);
				locationPathField.setEnabled(!useDefaults);
				locationLabel.setEnabled(!useDefaults);
				if (useDefaults) {
					customLocationFieldValue = locationPathField.getText();
					setLocationForSelection();
				} else {
					locationPathField.setText(customLocationFieldValue);
				}
			}
		};
		useDefaultsButton.addSelectionListener(listener);
	}

	/**
	 * Creates the project name specification controls.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private final void createProjectNameGroup(Composite parent) {
		// project specification group
		Composite projectGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		projectGroup.setLayout(layout);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// new project label
		Label projectLabel = new Label(projectGroup, SWT.NONE);
		projectLabel.setText("WEIXim Project Name");
		projectLabel.setFont(parent.getFont());

		// new project name entry field
		projectNameField = new Text(projectGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		projectNameField.setLayoutData(data);
		projectNameField.setFont(parent.getFont());

		// Set the initial value first before listener
		// to avoid handling an event during the creation.
		if (initialProjectFieldValue != null)
			projectNameField.setText(initialProjectFieldValue);
		projectNameField.addListener(SWT.Modify, nameModifyListener);
	}

	private final void createAximOrGlue(Composite parent) {
		Group actionGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		actionGroup.setLayout(layout);
		actionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		actionGroup.setText("Action");
		Label label = new Label(actionGroup, SWT.NULL);
		label = new Label(actionGroup, SWT.NULL);
		label.setText("&Select Create or Glue:");
		
		label = new Label(actionGroup, SWT.NULL);
		label.setText("");
		
		label = new Label(actionGroup, SWT.NULL);
		label.setText("");
		
		label = new Label(actionGroup, SWT.NULL);
		label.setText("");
		
		radioButton1 = new Button(actionGroup, SWT.RADIO);
		radioButton1.setAlignment(SWT.LEFT);
		radioButton1.setText("Generate implementations from WEI models");
		radioButton1.setSelection(true);
		
		label = new Label(actionGroup, SWT.NULL);
		label.setText("");
		
		label = new Label(actionGroup, SWT.NULL);
		label.setText("");
		
		radioButton2 = new Button(actionGroup, SWT.RADIO);
		radioButton2.setAlignment(SWT.LEFT);
		radioButton2.setText("Glue implementations from WEI models");
		radioButton2.setSelection(false);
	}


	/**
	 * Creates the project location specification controls.
	 * 
	 * @param projectGroup
	 *            the parent composite
	 * @param enabled
	 *            the initial enabled state of the widgets created
	 */
	private void createUserSpecifiedProjectLocationGroup(
			Composite projectGroup, boolean enabled) {

		Font font = projectGroup.getFont();

		// location label
		locationLabel = new Label(projectGroup, SWT.NONE);
		locationLabel.setText("Location");
		locationLabel.setEnabled(enabled);
		locationLabel.setFont(font);

		// project location entry field
		locationPathField = new Text(projectGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		locationPathField.setLayoutData(data);
		locationPathField.setEnabled(enabled);
		locationPathField.setFont(font);

		// browse button
		browseButton = new Button(projectGroup, SWT.PUSH);
		browseButton.setText("Browse");
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleLocationBrowseButtonPressed();
			}
		});

		browseButton.setEnabled(enabled);
		browseButton.setFont(font);
		setButtonLayoutData(browseButton);

		// Set the initial value first before listener
		// to avoid handling an event during the creation.
		if (initialLocationFieldValue == null)
			locationPathField.setText(Platform.getLocation().toOSString());
		else
			locationPathField.setText(initialLocationFieldValue);
		locationPathField.addListener(SWT.Modify, locationModifyListener);
	}

	/**
	 * Returns the current project location path as entered by the user, or its
	 * anticipated initial value. Note that if the default has been returned the
	 * path in a project description used to create a project should not be set.
	 * 
	 * @return the project location path or its anticipated initial value.
	 */
	public IPath getLocationPath() {
		if (useDefaults)
			return Platform.getLocation();

		return new Path(getProjectLocationFieldValue());
	}
	
	public boolean getCreateButton(){
		return radioButton1.getSelection();
	}
	
	public boolean getGlueButton(){
		return radioButton2.getSelection();
	}

	/**
	 * Creates a project resource handle for the current project name field
	 * value.
	 * <p>
	 * This method does not create the project resource; this is the
	 * responsibility of <code>IProject::create</code> invoked by the new
	 * project resource wizard.
	 * </p>
	 * 
	 * @return the new project resource handle
	 */
	public IProject getProjectHandle() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(
				getProjectName());
	}

	/**
	 * Returns the current project name as entered by the user, or its
	 * anticipated initial value.
	 * 
	 * @return the project name, its anticipated initial value, or
	 *         <code>null</code> if no project name is known
	 */
	public String getProjectName() {
		if (projectNameField == null)
			return initialProjectFieldValue;

		return getProjectNameFieldValue();
	}

	/**
	 * Returns the value of the project name field with leading and trailing
	 * spaces removed.
	 * 
	 * @return the project name in the field
	 */
	private String getProjectNameFieldValue() {
		if (projectNameField == null)
			return ""; //$NON-NLS-$

		return projectNameField.getText().trim();
	}

	/**
	 * Returns the value of the project location field with leading and trailing
	 * spaces removed.
	 * 
	 * @return the project location directory in the field
	 */
	private String getProjectLocationFieldValue() {
		if (locationPathField == null)
			return ""; //$NON-NLS-$

		return locationPathField.getText().trim();
	}

	/**
	 * Open an appropriate directory browser
	 */
	void handleLocationBrowseButtonPressed() {
		DirectoryDialog dialog = new DirectoryDialog(locationPathField
				.getShell());
		dialog.setMessage("Directory");

		String dirName = getProjectLocationFieldValue();
		if (!dirName.equals("")) { //$NON-NLS-$
			File path = new File(dirName);
			if (path.exists())
				dialog.setFilterPath(new Path(dirName).toOSString());
		}

		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			customLocationFieldValue = selectedDirectory;
			locationPathField.setText(customLocationFieldValue);
		}
	}

	/**
	 * Sets the initial project name that this page will use when created. The
	 * name is ignored if the createControl(Composite) method has already been
	 * called. Leading and trailing spaces in the name are ignored.
	 * 
	 * @param name
	 *            initial project name for this page
	 */
	public void setInitialProjectName(String name) {
		if (name == null)
			initialProjectFieldValue = null;
		else {
			initialProjectFieldValue = name.trim();
			initialLocationFieldValue = getDefaultLocationForName(initialProjectFieldValue);
		}
	}

	/**
	 * Set the location to the default location if we are set to useDefaults.
	 */
	void setLocationForSelection() {
		if (useDefaults)
			locationPathField
					.setText(getDefaultLocationForName(getProjectNameFieldValue()));
	}

	/**
	 * Get the defualt location for the provided name.
	 * 
	 * @param nameValue
	 *            the name
	 * @return the location
	 */
	private String getDefaultLocationForName(String nameValue) {
		IPath defaultPath = Platform.getLocation().append(nameValue);
		return defaultPath.toOSString();
	}

	/**
	 * Returns whether this page's controls currently all contain valid values.
	 * 
	 * @return <code>true</code> if all controls are valid, and
	 *         <code>false</code> if at least one is invalid
	 */
	protected boolean validatePage() {
		IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();

		String projectFieldContents = getProjectNameFieldValue();
		if (projectFieldContents.equals("")) { //$NON-NLS-$
			setErrorMessage(null);
			setMessage("Project Name Empty");
			return false;
		}

		IStatus nameStatus = workspace.validateName(projectFieldContents,
				IResource.PROJECT);
		if (!nameStatus.isOK()) {
			setErrorMessage(nameStatus.getMessage());
			return false;
		}

		String locationFieldContents = getProjectLocationFieldValue();

		if (locationFieldContents.equals("")) { //$NON-NLS-$
			setErrorMessage(null);
			setMessage("Project Location Empty");
			return false;
		}

		IPath path = new Path(""); //$NON-NLS-$
		if (!path.isValidPath(locationFieldContents)) {
			setErrorMessage("Location Error");
			return false;
		}

		IPath projectPath = new Path(locationFieldContents);
		if (!useDefaults && Platform.getLocation().isPrefixOf(projectPath)) {
			setErrorMessage("Default Location Error");
			return false;
		}

		IProject handle = getProjectHandle();
		if (handle.exists()) {
			setErrorMessage("Error Loading Project Name ");
			return false;
		}

		/*
		 * If not using the default value validate the location.
		 */
		if (!useDefaults()) {
			IStatus locationStatus = workspace.validateProjectLocation(handle,
					projectPath);
			if (!locationStatus.isOK()) {
				setErrorMessage(locationStatus.getMessage()); //$NON-NLS-$
				return false;
			}
		}

		setErrorMessage(null);
		setMessage(null);
		return true;
	}

	/*
	 * see @DialogPage.setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			projectNameField.setFocus();
	}

	/**
	 * Returns the useDefaults.
	 * 
	 * @return boolean
	 */
	public boolean useDefaults() {
		return useDefaults;
	}

}