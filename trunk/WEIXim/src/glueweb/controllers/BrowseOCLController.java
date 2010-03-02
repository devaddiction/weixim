package glueweb.controllers;

import java.util.StringTokenizer;

import glueweb.editors.IActivePage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
public class BrowseOCLController extends SelectionAdapter {
	private Text text;
	private IActivePage multiEditor;

	public BrowseOCLController(Text text, IActivePage multiEditor) {
		this.text = text;
		this.multiEditor = multiEditor;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event) {
		if (event.widget instanceof Button)
			handleBrowse((Button) event.widget);
	}

	/**
	 * use *.ocl filter only to show this file type
	 * 
	 * @param item
	 */
	private void handleBrowse(Button item) {
		FileDialog sourceDialog = new FileDialog(new Shell(), SWT.OPEN);
		sourceDialog.setFilterExtensions(new String[] { "*.ocl" });
		sourceDialog.setFilterNames(new String[] { "OCL files (*.ocl)" });
		String fileName = null;

		fileName = sourceDialog.open();
		// cancel not pressed
		if (fileName != null) {
			text.setText(fileName);
//			glueweb.pages.MainPanel.statusOk("File loaded succesfully");

			IFile file = (IFile) ((IEditorPart) multiEditor).getEditorInput()
					.getAdapter(IFile.class);
			String path = System.getProperty("osgi.instance.area").substring(5)
					.toString()
					+ fileName(file.getProject().toString());
			glueweb.util.Copy copia = new glueweb.util.Copy(fileName, path
					.toString()
					+ "/Correspondences/" + fileName(fileName));
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
