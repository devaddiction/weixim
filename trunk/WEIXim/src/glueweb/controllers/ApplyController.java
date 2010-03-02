package glueweb.controllers;

import glueweb.editors.IActivePage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.ResourceUtil;

/**
 * is the action when you push Apply button
 */
public class ApplyController extends SelectionAdapter {
	private Combo viewpoint, viewpointLanguage, technology;

	private Text sourceText, urlText, portText, userText, passText,
			webServerLocationText, webServerPortText, webServerUserText,
			webServerPassText;

	private IActivePage multiEditor;

	public ApplyController(Combo viewPointCombo, Combo viewPointLanguageCombo,
			Combo technologyCombo, Text localText, Text urlText, Text portText,
			Text userText, Text passText, Text webServerLocationText,
			Text webServerPortText, Text webServerUserText,
			Text webServerPassText, IActivePage multiEditor) {
		this.viewpoint = viewPointCombo;
		this.viewpointLanguage = viewPointLanguageCombo;
		this.technology = technologyCombo;
		this.sourceText = localText;
		this.urlText = urlText;
		this.portText = portText;
		this.userText = userText;
		this.passText = passText;
		this.webServerLocationText = webServerLocationText;
		this.webServerPortText = webServerPortText;
		this.webServerUserText = webServerUserText;
		this.webServerPassText = webServerPassText;
		this.multiEditor = multiEditor;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event) {
		if (event.widget instanceof Button)
			handleApply();
	}

	/**
	 * checks that fields are not null and validates them
	 */
	public void handleApply() {
		if ((viewpoint.getText() == null)
				|| (viewpointLanguage.getText() == null)
				|| (technology.getText() == null))
			return;
		if (sourceText.isEnabled()) {
			try {
				sourceValidate(sourceText);
			} catch (Exception e) {
				glueweb.pages.LogPanel.printLine("Can't download sources");
			}
		} else if (urlText.isEnabled()) {
			try {
				URLValidate(urlText, portText, userText, passText);
			} catch (Exception e) {
				glueweb.pages.LogPanel.printLine("Can't upload sources");
			}
		}

		// add XMI info file
		String filename = null;
		IFile file = (IFile) ((IEditorPart) multiEditor).getEditorInput()
				.getAdapter(IFile.class);
		String path = System.getProperty("osgi.instance.area").substring(5)
				.toString()
				+ fileName(file.getProject().toString());
		if (viewpoint.getText().contains("User Interface")) {
			filename = "User Interface/ui.info";
		} else if (viewpoint.getText().contains("Bussiness Logic"))
			filename = "Bussiness Logic/bl.info";
		else if (viewpoint.getText().contains("Data"))
			filename = "Data/d.info";

		saveInfo(path + "/" + filename);

		glueweb.pages.MainPanel.activeGlue();

	}

	/**
	 * save *.info file
	 * 
	 * @param filepath
	 * @param string
	 */
	public void saveInfo(String filepath) {
		FileWriter myFile = null;
		try {
			myFile = new FileWriter(filepath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(myFile);

		StringTokenizer st = new StringTokenizer(filepath, "/");
		String type = null;
		while (st.hasMoreTokens()) {
			type = st.nextToken().toString();
		}

		if (filepath.endsWith("d.info")) {
			pw.println("local");
			pw.println(urlText.getText());
			pw.println(portText.getText());
			pw.println(userText.getText());
			pw.println(passText.getText());
			pw.println(viewpointLanguage.getText().toString());
			pw.println(technology.getText().toString());
		} else {
			if (sourceText.isEnabled()) {
				pw.println("local");
				// source code local
				pw.println(sourceText.getText());
			} else {
				pw.println("url");
				// source code in url
				pw.println(urlText.getText());
				pw.println(portText.getText());
				pw.println(userText.getText());
				pw.println(passText.getText());
			}
			pw.println(viewpointLanguage.getText().toString());
			pw.println(technology.getText().toString());

			if (webServerLocationText.isEnabled()) {
				pw.println(webServerLocationText.getText());
				pw.println(webServerPortText.getText());
				pw.println(webServerUserText.getText());
				pw.println(webServerPassText.getText());
			}
		}
		pw.println();
		pw.close();

	}

	/**
	 * checks web server location and port are not null, web server port is
	 * number and the web server URL is correct.
	 * 
	 * @param webServerLocationText
	 * @param webServerPortText
	 * @param webServerPassText2
	 * @param webServerUserText
	 * @return true if all ok, false in other case.
	 */
	private boolean URLValidate(Text webServerLocationText,
			Text webServerPortText, Text webServerUserText,
			Text webServerPassText) {
		boolean ok = false;
		if ((webServerLocationText.getText() == null || webServerLocationText
				.getText().equals(""))
				|| (webServerPortText.getText() == null || webServerPortText
						.getText().equals(""))
				|| (webServerUserText.getText() == null || webServerUserText
						.getText().equals(""))
				|| (webServerPassText.getText() == null || webServerPassText
						.getText().equals("")))
			return false;
		else {
			try {

				glueweb.xmi.Info server = new glueweb.xmi.Info();
				server.setLocation(webServerLocationText.getText());
				server.setPort(webServerPortText.getText());
				server.setUser(webServerUserText.getText());
				server.setPass(webServerPassText.getText());

				if (glueweb.util.FTP.connectionFTP(server))
					return true;
				return false;
			} catch (Exception e) {
				this.multiEditor.showErrorGlue("Glue Web",
						"Malformed Web Server");
			}
		}
		return false;
	}

	/**
	 * checks that source location is null
	 * 
	 * @param source
	 */
	private void sourceValidate(Text source) {
		if (source.getText() == null || source.getText().equals(""))
			this.multiEditor.showErrorGlue("Glue Web",
					"Source location is null");
		else if (!(new File(source.getText())).exists()) {
			this.multiEditor.showErrorGlue("Glue Web",
					"Source location not exists");
		}
	}

	String fileName(String path) {
		String result = null;

		StringTokenizer st = new StringTokenizer(path, ":\\/");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			result = token;
		}
		return result;
	}

}
