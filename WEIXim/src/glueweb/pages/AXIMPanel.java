package glueweb.pages;

import java.net.URL;

import glueweb.Activator;
import glueweb.controllers.AximController;
import glueweb.controllers.BrowseAXIMController;
import glueweb.controllers.ControlParserAxim;
import glueweb.controllers.pathModifyController;
import glueweb.editors.IActivePage;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

public class AXIMPanel extends Composite {
	@SuppressWarnings("unused")
	private IActivePage multiEditor;
	private Group imagePictureGroup;
	private static Label statusLabel;
	private static Text consola;
	Combo uiCombo = null;
	Combo lnCombo = null;
	Combo dCombo = null;
	Text localText = null;
	public static ProgressBar loading;

	/**
	 * Create the composite
	 * 
	 * @param parent
	 * @param style
	 */
	public AXIMPanel(Composite parent, IActivePage multiEditor, int style) {
		super(parent, style);

		this.multiEditor = multiEditor;

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginLeft = 5;
		gridLayout.marginRight = 5;
		gridLayout.marginTop = 5;
		gridLayout.marginBottom = 5;
		createGroup();
		this.setLayout(gridLayout);
		Label filler = new Label(this, SWT.NONE);
		createGroup1();
		filler = new Label(this, SWT.NONE);
		createGroup2();

	}

	@SuppressWarnings("deprecation")
	private void createGroup() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 4;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.CENTER;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.CENTER;
		gridData1.heightHint = 199;
		gridData1.widthHint = 199;
		imagePictureGroup = new Group(this, SWT.NONE);
		imagePictureGroup.setLayout(gridLayout1);
		imagePictureGroup.setLayoutData(gridData1);


		Image imagen = Activator.imageDescriptorFromPlugin("WEIXim", "icons/image.jpg").createImage();
		imagePictureGroup.setBackgroundImage(imagen);

		gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.BEGINNING;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.CENTER;
		gridData1.horizontalSpan = 1;

		Group modelInformationGroup;
		modelInformationGroup = new Group(this, SWT.BEGINNING);
		gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		modelInformationGroup.setLayout(gridLayout1);

		modelInformationGroup.setLayoutData(gridData1);
		modelInformationGroup.setText("Platform Implementations Details");

		Label uiLabel;
		uiLabel = new Label(modelInformationGroup, SWT.RIGHT);
		uiLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false,
				1, 1));
		uiLabel.setText("User Interface");

		uiCombo = new Combo(modelInformationGroup, SWT.READ_ONLY);
		uiCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true,
				false, 1, 1));
		uiCombo.setItems(new String[] { "HTML", "JSP", "ColdFusion",
				"HTML/JAVASCRIPT/XSLT", "PHP", "Ruby", "Perl", "ASP.NET",
				"XSLT", "Python", "C++ Server Pages" });

		Label lnLabel;
		lnLabel = new Label(modelInformationGroup, SWT.RIGHT);
		lnLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false,
				1, 1));
		lnLabel.setText("Bussiness Logic");

		lnCombo = new Combo(modelInformationGroup, SWT.READ_ONLY);
		lnCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true,
				false, 1, 1));
		lnCombo.setItems(new String[] { "Java(Servlet)", "Java(EJB)",
				"JSP(Java)", "ASP", "ColdFusion", "ColdFusion(Java)",
				"Java(applet)", "PHP", "Ruby", "Perl", "C#", "Visual Basic",
				"XSLT(Java)", "Python", "C++ Server Pages", "JavaScript",
				"Web Services", "Corba Components" });

		Label dLabel;
		dLabel = new Label(modelInformationGroup, SWT.RIGHT);
		dLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false,
				1, 1));
		dLabel.setText("DataBase");

		dCombo = new Combo(modelInformationGroup, SWT.READ_ONLY);
		dCombo.setItems(new String[] { "Access", "Oracle", "MySQL" });
		dCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true,
				false, 1, 1));
	}

	private void createGroup1() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 2;
		gridData1.verticalAlignment = GridData.CENTER;
		gridData1.horizontalAlignment = GridData.CENTER;
		Group modelLocationGroup;
		modelLocationGroup = new Group(this, SWT.NONE);
		modelLocationGroup.setLayout(gridLayout1);
		modelLocationGroup.setLayoutData(gridData1);
		modelLocationGroup.setText("Load WEI Model");

		localText = new Text(modelLocationGroup, SWT.BORDER);
		localText.addModifyListener(new pathModifyController("aximpanel"));
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false,
				1, 1);
		gridData.widthHint = 500;
		localText.setLayoutData(gridData);

		Button browseButton;
		browseButton = new Button(modelLocationGroup, SWT.NONE);
		browseButton.setText("Browse");
		browseButton.addSelectionListener(new BrowseAXIMController(localText));
		browseButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true,
				false, 1, 1));
	}

	private void createGroup2() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 4;
		gridData1.verticalAlignment = GridData.CENTER;
		gridData1.horizontalAlignment = GridData.CENTER;

		Group statusGroup;
		statusGroup = new Group(this, SWT.NONE);
		statusGroup.setLayout(gridLayout1);
		statusGroup.setLayoutData(gridData1);
		statusGroup.setText("Status");

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.widthHint = 250;
		loading = new ProgressBar(statusGroup, SWT.NONE);
		loading.setLayoutData(gridData);
		loading.setMinimum(0);
		loading.setMaximum(100);

		Button applyButton;
		applyButton = new Button(statusGroup, SWT.NONE);
		applyButton.setText("Apply");
		applyButton.addSelectionListener(new AximController(uiCombo, lnCombo,
				dCombo, localText, multiEditor));
	}

	/**
	 * @see org.eclipse.swt.widgets.Composite#checkSubclass()
	 */
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @param sms
	 * @return
	 */
	public static void statusOk(String sms) {// green color
		statusLabel.setText(sms);
		statusLabel
				.setForeground(new Color(statusLabel.getDisplay(), 0, 155, 0));
	}

	/**
	 * @param sms
	 */
	public static void statusError(String sms) {// red color
		statusLabel.setText(sms);
		statusLabel
				.setForeground(new Color(statusLabel.getDisplay(), 155, 0, 0));
	}

	public void agregaMensaje(String m) {
		consola.append(m);
		consola.append("\n");
		glueweb.pages.LogPanel.printLine(m);
	}

	public static void agregaTexto(String texto) {
		consola.append(texto);
		consola.append("\n");
		glueweb.pages.LogPanel.printLine(texto);
	}

	public void controlador(ControlParserAxim crt) {
		// TODO Auto-generated method stub

	}

	public void limpiarbarraestado() {
		AXIMPanel.statusNull();
	}

	public static void limpiatexto() {
		consola.redraw();
	}

	public static void statusNull() {
		statusLabel.setText("");
		statusLabel
				.setForeground(new Color(statusLabel.getDisplay(), 155, 0, 0));
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

	public static Image createImage(String imagePath) {
		final Bundle pluginBundle = Platform.getBundle("WEIXim");
		final Path imageFilePath = new Path("icons/" + imagePath);
		final URL imageFileUrl = Platform.find(pluginBundle, imageFilePath);
		return ImageDescriptor.createFromURL(imageFileUrl).createImage();
	}

}
