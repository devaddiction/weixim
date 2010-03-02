package glueweb.pages;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import glueweb.controllers.ApplyController;
import glueweb.controllers.BrowseDirectoryController;
import glueweb.controllers.BrowseOCLController;
import glueweb.controllers.BrowseXMIController;
import glueweb.controllers.ComboController;
import glueweb.controllers.GlueController;
import glueweb.controllers.RadioButtonController;
import glueweb.controllers.RadioButtonServerController;
import glueweb.controllers.TabSelectionListener;
import glueweb.controllers.pathModifyController;
import glueweb.controllers.urlModifyController;
import glueweb.editors.IActivePage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;

public class MainPanel extends Composite {
	private static IActivePage multiEditor;
	private static Label statusLabel;
	private Text oclText;
	public static Text dText;
	public static Text blText;
	public static Text uiText;
	public static TabFolder tabFolder;
	public static TabItem tabGlue;
	public static TabItem tabPropIU;
	public static TabItem tabPropLN;
	public static TabItem tabPropBD;
	private Label filler;
	private static int faltaTab;

	static Text remoteUserText;
	static Text remotePassText;
	static Text remotePortText;
	static Text remoteText;

	static Text serverUserText;
	static Text serverPassText;
	static Text serverPortText;
	static Text serverText;

	static Text localText;

	static Combo technologyCombo;
	static Combo viewPointLanguageCombo;
	static Combo viewPointCombo;
	static Button remoteRadioButton;
	static Button localRadioButton;
	static Button localButton;
	static Button uploadYes;
	static Button uploadNo;
	static Button applyButton;

	public static ProgressBar loading;

	public static Group modelInformationGroup;
	public static Group webServerLocationGroup;
	public static Group implementationLocationGroup;

	Label serverPassLabel;
	Label serverUserLabel;
	Label serverPortLabel;
	Label locationLabel;

	/**
	 * Create the composite
	 * 
	 * @param parent
	 * @param style
	 */
	public MainPanel(Composite parent, IActivePage multiEditor, int style) {
		super(parent, style);

		this.multiEditor = multiEditor;

		GridLayout layout = new GridLayout();
		layout.marginBottom = 10;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.marginTop = 10;
		this.setLayout(layout);

		tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayout(layout);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		tabGlue = new TabItem(tabFolder, SWT.NONE, 0);
		tabGlue.setText("GlueWeb");

		tabPropIU = new TabItem(tabFolder, SWT.NONE, 1);
		tabPropIU.setText("User Interface");

		tabPropLN = new TabItem(tabFolder, SWT.NONE, 2);
		tabPropLN.setText("Bussiness Logic");

		tabPropBD = new TabItem(tabFolder, SWT.NONE, 3);
		tabPropBD.setText("DataBase");

		fillTabGlue(tabGlue, tabFolder);
		fillTabProperties(tabPropIU, tabFolder);
		fillTabProperties(tabPropLN, tabFolder);
		fillTabProperties(tabPropBD, tabFolder);
	}

	private void fillTabGlue(TabItem item, TabFolder tabFolder) {
		Composite composite = new Composite(tabFolder, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true,
				false));
		item.setControl(composite);
		Label space = new Label(composite, 1);
		space.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1,
				1));
		space.setText("");
		fillFirstGlue(composite, layout);
		space = new Label(composite, 1);
		space.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1,
				1));
		space.setText("");
		space = new Label(composite, 1);
		space.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1,
				1));
		space.setText("");
		fillSecondGlue(composite, layout);
		space = new Label(composite, 1);
		space.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1,
				1));
		space.setText("");
		space = new Label(composite, 1);
		space.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1,
				1));
		space.setText("");
		fillThirdGlue(composite, layout);
	}

	private void fillTabProperties(TabItem item, TabFolder tabFolder) {
		Composite composite = new Composite(tabFolder, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true,
				false));
		item.setControl(composite);
		Label space = new Label(composite, 1);
		space.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1,
				1));
		fillFirstProperties(composite, layout);
		fillSecondProperties(composite, layout);
		if (!item.getText().equals("DataBase"))
			fillThirdProperties(composite, layout);
		space = new Label(composite, 1);
		fillFourthProperties(composite, layout, item.getText());
	}

	private void fillFirstGlue(Composite composite, GridLayout layout) {
		int withText = 500;

		Group xmiModelsGroup = new Group(composite, SWT.CENTER);
		xmiModelsGroup.setText("XMI Models");
		xmiModelsGroup.setLayout(new GridLayout(3, false));
		xmiModelsGroup.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true,
				false, 1, 1));

		final Label uiLabel = new Label(xmiModelsGroup, SWT.CENTER);
		uiLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false,
				1, 1));
		uiLabel.setText("User Interface");

		uiText = new Text(xmiModelsGroup, SWT.BORDER);
		uiText.addModifyListener(new pathModifyController("mainpanel"));
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1);
		gridData.widthHint = withText;
		uiText.setLayoutData(gridData);

		final Button uiButton = new Button(xmiModelsGroup, SWT.NONE);
		uiButton.setText("Browse");
		uiButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		uiButton.addSelectionListener(new BrowseXMIController(uiText,
				multiEditor, "uiText"));

		final Label blLabel = new Label(xmiModelsGroup, SWT.LEFT);
		blLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false,
				1, 1));
		blLabel.setText("Business Logic");

		blText = new Text(xmiModelsGroup, SWT.BORDER);
		blText.addModifyListener(new pathModifyController("mainpanel"));
		gridData.widthHint = withText;
		blText.setLayoutData(gridData);

		final Button blButton = new Button(xmiModelsGroup, SWT.NONE);
		blButton.setText("Browse");
		blButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		blButton.addSelectionListener(new BrowseXMIController(blText,
				multiEditor, "blText"));

		final Label dLabel = new Label(xmiModelsGroup, SWT.LEFT);
		dLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false,
				1, 1));
		dLabel.setText("Data");

		dText = new Text(xmiModelsGroup, SWT.BORDER);
		dText.addModifyListener(new pathModifyController("mainpanel"));
		gridData.widthHint = withText;
		dText.setLayoutData(gridData);

		final Button dButton = new Button(xmiModelsGroup, SWT.NONE);
		dButton.setText("Browse");
		dButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		dButton.addSelectionListener(new BrowseXMIController(dText,
				multiEditor, "dText"));
	}

	private void fillSecondGlue(Composite composite, GridLayout layout) {
		int withText = 500;

		Group correspondencesGroup = new Group(composite, SWT.CENTER);
		correspondencesGroup.setText("Correspondences");
		correspondencesGroup.setLayout(new GridLayout(3, false));
		correspondencesGroup.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				true, false, 1, 1));

		final Label oclLabel = new Label(correspondencesGroup, SWT.RIGHT);
		oclLabel.setText("Constraints");
		oclLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false,
				1, 1));

		oclText = new Text(correspondencesGroup, SWT.BORDER);
		oclText.addModifyListener(new pathModifyController("mainpanel"));
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1);
		gridData.widthHint = withText;
		oclText.setLayoutData(gridData);

		final Button oclButton = new Button(correspondencesGroup, SWT.NONE);
		oclButton.setText("Browse");
		oclButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		oclButton.addSelectionListener(new BrowseOCLController(oclText,
				multiEditor));

	}

	private void fillThirdGlue(Composite composite, GridLayout layout) {
		Group statusGroup = new Group(composite, SWT.CENTER);
		statusGroup.setText("Status");
		statusGroup.setLayout(new GridLayout(2, false));
		statusGroup.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true,
				false, 1, 1));

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.widthHint = 250;
		loading = new ProgressBar(statusGroup, SWT.NONE);
		loading.setLayoutData(gridData);
		loading.setMinimum(0);
		loading.setMaximum(100);

		final Button glueButton = new Button(statusGroup, SWT.NONE);
		glueButton.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false,
				1, 1));
		glueButton.setText("Glue!");
		glueButton.addSelectionListener(new GlueController(uiText, blText,
				dText, oclText, multiEditor));
	}

	private void fillFirstProperties(Composite composite, GridLayout layout) {
		modelInformationGroup = new Group(composite, SWT.CENTER);
		modelInformationGroup.setText("Model Information");
		modelInformationGroup.setLayout(new GridLayout(2, false));
		modelInformationGroup.setLayoutData(new GridData(SWT.CENTER,
				SWT.CENTER, true, false, 1, 1));

		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false,
				1, 1);

		Label viewpointLabel = new Label(modelInformationGroup, SWT.CENTER);
		viewpointLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,
				false, 1, 1));
		viewpointLabel.setText("Viewpoint");

		viewPointCombo = new Combo(modelInformationGroup, SWT.READ_ONLY);
		viewPointCombo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,
				false, 1, 1));
		viewPointCombo.setItems(new String[] { "User Interface",
				"Bussiness Logic", "Data" });

		Label viewPointLanguageLabel;
		viewPointLanguageLabel = new Label(modelInformationGroup, SWT.RIGHT);
		viewPointLanguageLabel.setLayoutData(new GridData(SWT.RIGHT,
				SWT.CENTER, true, false, 1, 1));
		viewPointLanguageLabel.setText("Viewpoint Language");

		viewPointLanguageCombo = new Combo(modelInformationGroup, SWT.READ_ONLY);
		viewPointLanguageCombo.setLayoutData(gridData);
		viewPointLanguageCombo.setItems(new String[] { "UML", "WebML", "W2000",
				"OOHDM", "UWE", "OO-H" });
	}

	private void fillSecondProperties(Composite composite, GridLayout layout) {
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false,
				1, 1);
		implementationLocationGroup = new Group(composite, SWT.CENTER);
		implementationLocationGroup.setText("Implementation Details");
		implementationLocationGroup.setLayout(new GridLayout(8, false));
		implementationLocationGroup.setLayoutData(new GridData(SWT.CENTER,
				SWT.CENTER, true, false, 1, 1));

		filler = new Label(implementationLocationGroup, SWT.NONE);

		Label viewPointLanguageLabel;
		viewPointLanguageLabel = new Label(implementationLocationGroup,
				SWT.RIGHT);
		viewPointLanguageLabel.setLayoutData(new GridData(SWT.RIGHT,
				SWT.CENTER, true, false, 1, 1));
		viewPointLanguageLabel.setText("Technology");

		technologyCombo = new Combo(implementationLocationGroup, SWT.LEFT);
		technologyCombo.setItems(new String[] { "viewpoint first" });
		technologyCombo.setText("viewpoint first");
		technologyCombo.setLayoutData(gridData);
		technologyCombo.setEnabled(false);

		filler = new Label(implementationLocationGroup, SWT.NONE);

		filler = new Label(implementationLocationGroup, SWT.NONE);

		filler = new Label(implementationLocationGroup, SWT.NONE);

		filler = new Label(implementationLocationGroup, SWT.NONE);

		filler = new Label(implementationLocationGroup, SWT.NONE);

		localRadioButton = new Button(implementationLocationGroup, SWT.RADIO);
		localRadioButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				true, false, 1, 1));
		localRadioButton.setSelection(true);
		localRadioButton.setAlignment(SWT.LEFT);
		localRadioButton.setText("Local Directory");

		localText = new Text(implementationLocationGroup, SWT.BORDER);
		localText.addModifyListener(new pathModifyController("propertypanel"));
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.horizontalSpan = 4;
		gridData2.verticalAlignment = GridData.CENTER;
		localText.setLayoutData(gridData2);

		localButton = new Button(implementationLocationGroup, SWT.NONE);
		localButton.setText("Browse");
		localButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false, 1, 1));

		filler = new Label(implementationLocationGroup, SWT.NONE);

		filler = new Label(implementationLocationGroup, SWT.NONE);

		remoteRadioButton = new Button(implementationLocationGroup, SWT.RADIO);
		remoteRadioButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				true, false, 1, 1));
		remoteRadioButton.setAlignment(SWT.LEFT);
		remoteRadioButton.setText("Remote Directory");

		remoteText = new Text(implementationLocationGroup, SWT.BORDER);
		remoteText.addModifyListener(new pathModifyController("propertypanel"));
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.horizontalSpan = 3;
		gridData3.verticalAlignment = GridData.CENTER;
		remoteText.setLayoutData(gridData3);
		remoteText.setEnabled(false);

		filler = new Label(implementationLocationGroup, SWT.NONE);

		filler = new Label(implementationLocationGroup, SWT.NONE);

		Label portLabel;
		portLabel = new Label(implementationLocationGroup, SWT.END);
		portLabel.setAlignment(SWT.END);
		portLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false,
				1, 1));
		portLabel.setText("Port");

		remotePortText = new Text(implementationLocationGroup, SWT.BORDER);
		remotePortText.setEnabled(false);

		Label userLabel;
		userLabel = new Label(implementationLocationGroup, SWT.NONE);
		userLabel.setAlignment(SWT.RIGHT);
		userLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false,
				1, 1));
		userLabel.setText("Username");

		remoteUserText = new Text(implementationLocationGroup, SWT.BORDER);
		remoteUserText.setEnabled(false);

		Label passLabel;
		passLabel = new Label(implementationLocationGroup, SWT.NONE);
		passLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false,
				1, 1));
		passLabel.setText("Password");

		remotePassText = new Text(implementationLocationGroup, SWT.BORDER
				| SWT.PASSWORD);
		remotePassText.setEnabled(false);

		// Listeners
		remoteRadioButton.addSelectionListener(new RadioButtonController(
				localRadioButton, localText, localButton, remoteRadioButton,
				remoteText, remotePortText, remoteUserText, remotePassText));
		localButton.addSelectionListener(new BrowseDirectoryController(
				localText));
		localRadioButton.addSelectionListener(new RadioButtonController(
				localRadioButton, localText, localButton, remoteRadioButton,
				remoteText, remotePortText, remoteUserText, remotePassText));
		viewPointCombo.addSelectionListener(new ComboController(
				technologyCombo, localRadioButton, localText, localButton,
				remoteRadioButton, remoteText, remotePortText, remoteUserText,
				remotePassText));
	}

	private void fillThirdProperties(Composite composite, GridLayout layout) {
		webServerLocationGroup = new Group(composite, SWT.CENTER);
		webServerLocationGroup.setText("Web Server");
		webServerLocationGroup.setLayout(new GridLayout(8, false));
		webServerLocationGroup.setLayoutData(new GridData(SWT.CENTER,
				SWT.CENTER, true, false, 1, 1));

		filler = new Label(webServerLocationGroup, SWT.NONE);

		filler = new Label(webServerLocationGroup, SWT.NONE);

		Label selectUpload;
		selectUpload = new Label(webServerLocationGroup, SWT.RIGHT);
		selectUpload.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,
				false, 1, 1));
		selectUpload.setText("Upload results?");

		uploadYes = new Button(webServerLocationGroup, SWT.RADIO);
		uploadYes.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false,
				1, 1));
		uploadYes.setAlignment(SWT.LEFT);
		uploadYes.setText("Yes");

		uploadNo = new Button(webServerLocationGroup, SWT.RADIO);
		uploadNo.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false,
				1, 1));
		uploadNo.setAlignment(SWT.LEFT);
		uploadNo.setText("No");
		uploadNo.setSelection(true);

		filler = new Label(webServerLocationGroup, SWT.NONE);

		filler = new Label(webServerLocationGroup, SWT.NONE);

		filler = new Label(webServerLocationGroup, SWT.NONE);

		locationLabel = new Label(webServerLocationGroup, SWT.NONE);
		locationLabel.setAlignment(SWT.RIGHT);
		locationLabel.setText("URL");
		locationLabel.setVisible(false);

		serverText = new Text(webServerLocationGroup, SWT.BORDER);
		serverText.setBounds(76, 24, 349, 19);
		serverText.addModifyListener(new urlModifyController());
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.horizontalSpan = 4;
		gridData1.verticalAlignment = GridData.CENTER;
		serverText.setLayoutData(gridData1);
		serverText.setVisible(false);

		filler = new Label(webServerLocationGroup, SWT.NONE);

		serverPortLabel = new Label(webServerLocationGroup, SWT.END);
		serverPortLabel.setAlignment(SWT.END);
		serverPortLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, true,
				false, 1, 1));
		serverPortLabel.setText("Port");
		serverPortLabel.setVisible(false);

		serverPortText = new Text(webServerLocationGroup, SWT.BORDER);
		serverPortText.setVisible(false);

		serverUserLabel = new Label(webServerLocationGroup, SWT.NONE);
		serverUserLabel.setAlignment(SWT.RIGHT);
		serverUserLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, true,
				false, 1, 1));
		serverUserLabel.setText("Username");
		serverUserLabel.setVisible(false);

		serverUserText = new Text(webServerLocationGroup, SWT.BORDER);
		serverUserText.setVisible(false);

		serverPassLabel = new Label(webServerLocationGroup, SWT.NONE);
		serverPassLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, true,
				false, 1, 1));
		serverPassLabel.setText("Password");
		serverPassLabel.setVisible(false);

		serverPassText = new Text(webServerLocationGroup, SWT.BORDER
				| SWT.PASSWORD);

		serverPassText.setVisible(false);

		uploadYes.addSelectionListener(new RadioButtonServerController(
				uploadYes, uploadNo, locationLabel, serverText,
				serverPortLabel, serverPortText, serverUserLabel,
				serverUserText, serverPassLabel, serverPassText));

		uploadNo.addSelectionListener(new RadioButtonServerController(
				uploadYes, uploadNo, locationLabel, serverText,
				serverPortLabel, serverPortText, serverUserLabel,
				serverUserText, serverPassLabel, serverPassText));

	}

	private void fillFourthProperties(Composite composite, GridLayout layout,
			String tabText) {
		Group statusGroup;
		statusGroup = new Group(composite, SWT.CENTER);
		statusGroup.setText("Status");
		statusGroup.setLayout(new GridLayout(2, false));
		statusGroup.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true,
				false, 1, 1));

		statusLabel = new Label(statusGroup, SWT.NONE);
		GridData gridData2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1);
		gridData2.widthHint = 500;
		statusLabel.setLayoutData(gridData2);

		applyButton = new Button(statusGroup, SWT.CENTER);
		applyButton.setBounds(460, 13, 72, 23);
		applyButton.setText("Apply");
		applyButton.addSelectionListener(new ApplyController(viewPointCombo,
				viewPointLanguageCombo, technologyCombo, localText, remoteText,
				remotePortText, remoteUserText, remotePassText, serverText,
				serverPortText, serverUserText, serverPassText, multiEditor));

		tabFolder.addSelectionListener(new TabSelectionListener(tabFolder,
				localRadioButton, localText, localButton, remoteRadioButton,
				uploadYes, uploadNo, remoteText, remotePortText,
				remoteUserText, remotePassText, serverText, serverPortText,
				serverUserText, serverPassText, serverPassLabel,
				serverUserLabel, serverPortLabel, locationLabel,
				webServerLocationGroup, viewPointCombo, technologyCombo));
		/*
		 * if (tabText.equals("User Interface")) tabFolder.addFocusListener(new
		 * TabIUFocusListener(tabFolder, localRadioButton, localText,
		 * localButton, remoteRadioButton, remoteText, remotePortText,
		 * remoteUserText, remotePassText, webServerLocationGroup,
		 * viewPointCombo, technologyCombo, viewPointLanguageCombo, serverText,
		 * serverPortText, serverUserText, serverPassText, multiEditor)); else
		 * if (tabText.equals("Bussiness Logic")) tabFolder.addFocusListener(new
		 * TabBLFocusListener(tabFolder, localRadioButton, localText,
		 * localButton, remoteRadioButton, remoteText, remotePortText,
		 * remoteUserText, remotePassText, webServerLocationGroup,
		 * viewPointCombo, technologyCombo, viewPointLanguageCombo, serverText,
		 * serverPortText, serverUserText, serverPassText, multiEditor)); else
		 * if (tabText.equals("Data")) tabFolder.addFocusListener(new
		 * TabDFocusListener(tabFolder, localRadioButton, localText,
		 * localButton, remoteRadioButton, remoteText, remotePortText,
		 * remoteUserText, remotePassText, webServerLocationGroup,
		 * viewPointCombo, technologyCombo, viewPointLanguageCombo, serverText,
		 * serverPortText, serverUserText, serverPassText, multiEditor));
		 */}

	/**
	 * @see org.eclipse.swt.widgets.Composite#checkSubclass()
	 */
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @param sms
	 */
	public static void statusOk(String sms) {// green color
		glueweb.pages.LogPanel.printLine(sms);
	}

	/**
	 * @param sms
	 */
	public static void statusError(String sms) {// red color
		glueweb.pages.LogPanel.printLine(sms);
	}

	protected ProgressBar createProgressBar(Composite parent, int style,
			int min, int current, int max) {
		ProgressBar pb = new ProgressBar(parent, style);
		if (min >= 0) {
			pb.setMinimum(min);
		}
		if (max >= 0) {
			pb.setMaximum(max);
		}
		if (current >= 0) {
			pb.setSelection(current);
		}
		return pb;
	}

	protected ProgressBar createVProgressBar(Composite parent, int min,
			int current, int max) {
		return createProgressBar(parent, SWT.VERTICAL, min, current, max);
	}

	protected ProgressBar createHProgressBar(Composite parent, int min,
			int current, int max) {
		return createProgressBar(parent, SWT.HORIZONTAL, min, current, max);
	}

	public static void activeGlue() {
		tabFolder.setSelection(0);
	}

	public static void loadFile(String file) throws IOException {
		String filename = null;
		IFile fileIFile = (IFile) ((IEditorPart) multiEditor).getEditorInput()
				.getAdapter(IFile.class);
		String path = System.getProperty("osgi.instance.area").substring(5)
				.toString()
				+ fileName(fileIFile.getProject().toString());
		BufferedReader br = new BufferedReader(
				new FileReader(path + "/" + file));
		if (file.endsWith("ui.info")) {
			glueweb.pages.MainPanel.tabFolder.setSelection(1);
			viewPointCombo.select(0);
		} else if (file.endsWith("bl.info")) {
			glueweb.pages.MainPanel.tabFolder.setSelection(2);
			viewPointCombo.select(1);
		}
		if (br.readLine().equals("local")) {
			localRadioButton.setSelection(true);
			localRadioButton.setEnabled(true);
			localText.setEnabled(true);
			remoteRadioButton.setSelection(false);
			remoteRadioButton.setEnabled(true);
			remoteText.setEnabled(false);
			remoteText.setText("");
			remotePortText.setEnabled(false);
			remotePortText.setText("");
			remoteUserText.setEnabled(false);
			remoteUserText.setText("");
			remoteUserText.setEnabled(false);
			remoteUserText.setText("");
			localText.setText(br.readLine().toString());
		} else {
			localRadioButton.setSelection(false);
			localRadioButton.setEnabled(false);
			localText.setEnabled(false);
			localText.setText("");
			remoteRadioButton.setSelection(true);
			remoteRadioButton.setEnabled(true);
			remoteText.setEnabled(true);
			remotePortText.setEnabled(true);
			remoteUserText.setEnabled(true);
			remotePassText.setEnabled(true);
			remoteText.setText(br.readLine().toString());
			remotePortText.setText(br.readLine().toString());
			remoteUserText.setText(br.readLine().toString());
			remotePassText.setText(br.readLine().toString());
		}
		if (file.endsWith("d.info")) {
			glueweb.pages.MainPanel.tabFolder.setSelection(3);
			viewPointCombo.select(2);
			localRadioButton.setSelection(false);
			localRadioButton.setEnabled(false);
			localText.setEnabled(false);
			localText.setText("");
			remoteRadioButton.setSelection(false);
			remoteRadioButton.setEnabled(false);
			remoteText.setEnabled(false);
			remoteText.setText("");
			remotePortText.setEnabled(false);
			remotePortText.setText("");
			remoteUserText.setEnabled(false);
			remoteUserText.setText("");
			remotePassText.setEnabled(false);
			remotePassText.setText("");
			remoteText.setEnabled(true);
			remotePortText.setEnabled(true);
			remoteUserText.setEnabled(true);
			remotePassText.setEnabled(true);
		}
		if (br.readLine().equals("UML")) {
			viewPointLanguageCombo.select(0); // Siempre es UML (de momento)
		}
		if (file.endsWith("ui.info")) {
			String categoria = br.readLine().toString();
			viewPointCombo.removeAll();
			viewPointCombo.setItems(new String[] { "User Interface",
					"Business Logic", "Data" });
			viewPointCombo.select(0);
			technologyCombo.removeAll();
			technologyCombo.setItems(new String[] { "HTML", "JSP",
					"ColdFusion", "HTML/JAVASCRIPT/XSLT", "PHP", "Ruby",
					"Perl", "ASP.NET", "XSLT", "Python", "C++ Server Pages" });
			viewPointCombo.addSelectionListener(new ComboController(
					technologyCombo));
			if (categoria.equals("HTML"))
				technologyCombo.select(0);
			else if (categoria.equals("JSP"))
				technologyCombo.select(1);
			else if (categoria.equals("ColdFusion"))
				technologyCombo.select(2);
			else if (categoria.equals("HTML/JAVASCRIPT/XSLT"))
				technologyCombo.select(3);
			else if (categoria.equals("PHP"))
				technologyCombo.select(4);
			else if (categoria.equals("Ruby"))
				technologyCombo.select(5);
			else if (categoria.equals("Perl"))
				technologyCombo.select(6);
			else if (categoria.equals("ASP.NET"))
				technologyCombo.select(7);
			else if (categoria.equals("XSLT"))
				technologyCombo.select(8);
			else if (categoria.equals("Python"))
				technologyCombo.select(9);
			else if (categoria.equals("C++ Server Pages"))
				technologyCombo.select(10);
		} else if (file.endsWith("bl.info")) {
			String categoria = br.readLine().toString();
			viewPointCombo.removeAll();
			viewPointCombo.setItems(new String[] { "User Interface",
					"Business Logic", "Data" });
			viewPointCombo.select(1);
			technologyCombo.removeAll();
			technologyCombo.setItems(new String[] { "Java(Servlet)",
					"Java(EJB)", "JSP(Java)", "ASP", "ColdFusion",
					"ColdFusion(Java)", "Java(applet)", "PHP", "Ruby", "Perl",
					"C#", "Visual Basic", "XSLT(Java)", "Python",
					"C++ Server Pages" });
			viewPointCombo.addSelectionListener(new ComboController(
					technologyCombo));
			if (categoria.equals("Java(Servlet)"))
				technologyCombo.select(0);
			else if (categoria.equals("Java(EJB)"))
				technologyCombo.select(1);
			else if (categoria.equals("JSP(Java)"))
				technologyCombo.select(2);
			else if (categoria.equals("ASP"))
				technologyCombo.select(3);
			else if (categoria.equals("ColdFusion"))
				technologyCombo.select(4);
			else if (categoria.equals("ColdFusion(Java)"))
				technologyCombo.select(5);
			else if (categoria.equals("Java(applet)"))
				technologyCombo.select(6);
			else if (categoria.equals("PHP"))
				technologyCombo.select(7);
			else if (categoria.equals("Ruby"))
				technologyCombo.select(8);
			else if (categoria.equals("Perl"))
				technologyCombo.select(9);
			else if (categoria.equals("C#"))
				technologyCombo.select(10);
			else if (categoria.equals("Visual Basic"))
				technologyCombo.select(11);
			else if (categoria.equals("XSLT(Java)"))
				technologyCombo.select(12);
			else if (categoria.equals("Python"))
				technologyCombo.select(13);
			else if (categoria.equals("C++ Server Pages"))
				technologyCombo.select(14);
		} else if (file.endsWith("d.info")) {
			String categoria = br.readLine().toString();
			viewPointCombo.removeAll();
			viewPointCombo.setItems(new String[] { "User Interface",
					"Business Logic", "Data" });
			viewPointCombo.select(2);
			technologyCombo.removeAll();
			technologyCombo
					.setItems(new String[] { "Access", "Oracle", "MySQL" });
			viewPointCombo.addSelectionListener(new ComboController(
					technologyCombo));
			if (categoria.equals("Access"))
				technologyCombo.select(0);
			else if (categoria.equals("Oracle"))
				technologyCombo.select(1);
			else if (categoria.equals("MySQL"))
				technologyCombo.select(2);
		}
		if (!br.readLine().equals("\n")) {
			uploadYes.setEnabled(true);
			uploadYes.setSelection(true);
			webServerLocationGroup.setEnabled(true);
			serverText.setText(br.readLine().toString());
			serverPortText.setText(br.readLine().toString());
			serverUserText.setText(br.readLine().toString());
			serverPassText.setText(br.readLine().toString());
		} else {

		}

	}

	static String fileName(String path) {
		String result = null;

		StringTokenizer st = new StringTokenizer(path, ":\\/");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			result = token;
		}
		return result;
	}

	public static boolean emptyText() {
		IFile file = (IFile) ((IEditorPart) multiEditor).getEditorInput()
				.getAdapter(IFile.class);
		String path = System.getProperty("osgi.instance.area").substring(5)
				.toString()
				+ fileName(file.getProject().toString());
		try {
			ArrayList<String> viewpointLanguages = new ArrayList<String>();
			viewpointLanguages.add("UML");
			viewpointLanguages.add("WebML");
			viewpointLanguages.add("W2000");
			viewpointLanguages.add("OOHDM");
			viewpointLanguages.add("UWE");
			viewpointLanguages.add("OO-H");

			ArrayList<String> arrayUI = new ArrayList<String>();
			arrayUI.add("HTML");
			arrayUI.add("JSP");
			arrayUI.add("ColdFusion");
			arrayUI.add("HTML/JAVASCRIPT/XSLT");
			arrayUI.add("PHP");
			arrayUI.add("Ruby");
			arrayUI.add("Perl");
			arrayUI.add("ASP.NET");
			arrayUI.add("XSLT");
			arrayUI.add("Python");
			arrayUI.add("C++ Server Pages");

			ArrayList<String> arrayBL = new ArrayList<String>();
			arrayBL.add("Java(Servlet)");
			arrayBL.add("Java(EJB)");
			arrayBL.add("JSP(Java)");
			arrayBL.add("ASP");
			arrayBL.add("ColdFusion");
			arrayBL.add("ColdFusion(Java)");
			arrayBL.add("Java(applet)");
			arrayBL.add("PHP");
			arrayBL.add("Ruby");
			arrayBL.add("Perl");
			arrayBL.add("C#");
			arrayBL.add("Visual Basic");
			arrayBL.add("XSLT(Java)");
			arrayBL.add("Python");
			arrayBL.add("C++ Server Pages");
			arrayBL.add("JavaScript");
			arrayBL.add("Web Services");
			arrayBL.add("Corba Components");
			arrayBL.add("Visual Basic");
			arrayBL.add("Visual Basic");
			arrayBL.add("Visual Basic");

			ArrayList<String> arrayD = new ArrayList<String>();
			arrayD.add("Access");
			arrayD.add("Oracle");
			arrayD.add("MySQL");

			if (!glueweb.pages.MainPanel.uiText.getText().isEmpty()) {
				BufferedReader br = new BufferedReader(new FileReader(path
						+ "/User Interface/ui.info"));
				String buffer = br.readLine();
				if (buffer.equals("local")) {
					if (!br.readLine().isEmpty()) {
						if (viewpointLanguages.contains(br.readLine())) {
							if (arrayUI.contains(br.readLine())) {
							} else {
								faltaTab = 1;
								return true;
							}
						} else {
							faltaTab = 1;
							return true;
						}
					} else {
						faltaTab = 1;
						return true;
					}
				} else if (buffer.equals("url")) {
					buffer = br.readLine();
					while (!viewpointLanguages.contains(buffer))
						buffer = br.readLine();
					if (viewpointLanguages.contains(buffer)) {
						if (arrayUI.contains(br.readLine())) {
						} else {
							faltaTab = 1;
							return true;
						}
					} else {
						faltaTab = 1;
						return true;
					}
				} else {
					faltaTab = 1;
					return true;
				}
			}
			if (!glueweb.pages.MainPanel.blText.getText().isEmpty()) {
				BufferedReader br = new BufferedReader(new FileReader(path
						+ "/Bussiness Logic/bl.info"));
				String buffer = br.readLine();
				if (buffer.equals("local")) {
					if (!br.readLine().isEmpty()) {
						if (viewpointLanguages.contains(br.readLine())) {
							if (arrayBL.contains(br.readLine())) {
							} else {
								faltaTab = 2;
								return true;
							}
						} else {
							faltaTab = 2;
							return true;
						}
					} else {
						faltaTab = 2;
						return true;
					}
				} else if (buffer.equals("url")) {
					buffer = br.readLine();
					while (!viewpointLanguages.contains(buffer))
						buffer = br.readLine();
					if (viewpointLanguages.contains(buffer)) {
						if (arrayBL.contains(br.readLine())) {
						} else {
							faltaTab = 2;
							return true;
						}
					} else {
						faltaTab = 2;
						return true;
					}
				} else {
					faltaTab = 2;
					return true;
				}
			}
			if (!glueweb.pages.MainPanel.dText.getText().isEmpty()) {
				BufferedReader br = new BufferedReader(new FileReader(path
						+ "/Data/d.info"));
				String buffer = br.readLine();
				if (buffer.equals("local")) {
					buffer = br.readLine();
					if(buffer.contains("serverName/DataBase or serverName/Dataname")){
						faltaTab = 3;
						return true;
					}
					while (!viewpointLanguages.contains(buffer))
						buffer = br.readLine();
					if (viewpointLanguages.contains(buffer)) {
						if (arrayD.contains(br.readLine())) {
						} else {
							faltaTab = 3;
							return true;
						}
					} else {
						faltaTab = 3;
						return true;
					}
				} else {
					faltaTab = 3;
					return true;
				}
			}

		} catch (Exception e) {
			return true;
		}
		return false;
	}

	public static int whatEmptyText() {
		return faltaTab;
	}

}
