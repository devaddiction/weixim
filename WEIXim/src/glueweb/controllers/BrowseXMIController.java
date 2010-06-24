package glueweb.controllers;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.StringTokenizer;

import glueweb.editors.IActivePage;
import glueweb.pages.MainPanel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;

/**
 * is the action when you push browse button and select a file using SWT
 */
public class BrowseXMIController extends SelectionAdapter {
	private Text text;
	private IActivePage multiEditor;
	private String id;

	public BrowseXMIController(Text text, IActivePage multiEditor, String string) {
		this.text = text;
		this.multiEditor = multiEditor;
		this.id = string;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event) {
		if (event.widget instanceof Button)
			handleBrowse((Button) event.widget);
	}

	/**
	 * use *.mdzip, *.xmi and *.uml2 filter only to show this file type
	 * 
	 * @param item
	 */
	private void handleBrowse(Button item) {
		FileDialog sourceDialog = new FileDialog(new Shell(), SWT.OPEN);
		sourceDialog.setFilterExtensions(new String[] { "*.mdxml", 
//				"*.mdzip",
				"*.xml", "*.xmi" });
		sourceDialog.setFilterNames(new String[] {
				"Magic Draw File Format (*.mdxml)",
//				"Packed Magic Draw File Format (*.mdzip)",
				"Extensible Markup Language (*.xml)", "XMI files (*.xmi)" });

		String fileName = null;
		IFile file = (IFile) ((IEditorPart) multiEditor).getEditorInput()
				.getAdapter(IFile.class);
		String path = System.getProperty("osgi.instance.area").substring(5)
				.toString()
				+ fileName(file.getProject().toString());

		fileName = sourceDialog.open();
		// cancel not pressed
		if (fileName != null) {
			if (fileName.endsWith(".mdzip")) {
				try {
					text.setText(fileName);
					if (this.id.equals("uiText")) {
						glueweb.util.Copy.unZipFileToDirectory(fileName, path
								.toString()
								+ "/User Interface/");
						fileName = path.toString() + "/User Interface/" + fileName(fileName);
					}
					if (this.id.equals("blText")){
						glueweb.util.Copy.unZipFileToDirectory(fileName, path
								.toString()
								+ "/Bussiness Logic/");
						fileName = path.toString() + "/Bussiness Logic/" + fileName(fileName);
					}if (this.id.equals("dText")){
						glueweb.util.Copy.unZipFileToDirectory(fileName, path
								.toString()
								+ "/Data/");
						fileName = path.toString() + "/Data/" + fileName(fileName);
					}		
				} catch (IOException e) {
					this.multiEditor.showErrorGlue("Glue Web",
							"ERROR: Can't open MDZIP");
				}
			} else {
				text.setText(fileName);
				// go to properties panel
				if (this.id.equals("uiText")) {
					glueweb.util.Copy copia = new glueweb.util.Copy(fileName,
							path.toString() + "/User Interface/"
									+ fileName(fileName));

				}
				if (this.id.equals("blText")) {
					glueweb.util.Copy copia = new glueweb.util.Copy(fileName,
							path.toString() + "/Bussiness Logic/"
									+ fileName(fileName));

				}
				if (this.id.equals("dText")) {
					glueweb.util.Copy copia = new glueweb.util.Copy(fileName,
							path.toString() + "/Data/" + fileName(fileName));

				}
			}
			try {
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(
						IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				e.toString();
			}

		}
	}

	private String fileName(String path) {
		String result = null;

		StringTokenizer st = new StringTokenizer(path, ":\\/");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			result = token;
		}
		return result;
	}

}
