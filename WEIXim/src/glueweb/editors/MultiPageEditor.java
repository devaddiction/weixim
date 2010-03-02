package glueweb.editors;

import java.io.IOException;

import glueweb.pages.AXIMPanel;
import glueweb.pages.ExplorerPanel;
import glueweb.pages.LogPanel;
import glueweb.pages.MainPanel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IShowEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * user interface with three tag
 */
public class MultiPageEditor extends MultiPageEditorPart implements
		IActivePage, IShowEditorInput {

	private MainPanel mainPanel;

	private LogPanel logPanel;

	private AXIMPanel aximPanel;

	private ExplorerPanel explorerPanel;

	/**
	 * Creates a multi-page editor.
	 */
	public MultiPageEditor() {
		super();
	}

	/**
	 * Creates page 0 of the multi-page editor, which shows the visual panel
	 */
	void createPage0() {
		aximPanel = new AXIMPanel(getContainer(), this, 0);
		int index = addPage(aximPanel);
		setPageText(index, "AXIM");
	}

	/**
	 * Creates page 0 of the multi-page editor, which shows the visual panel
	 */
	void createPage1() {
		mainPanel = new MainPanel(getContainer(), this, 0);
		int index = addPage(mainPanel);
		setPageText(index, "Glue Web");
	}

	/**
	 * Creates page 2 of the multi-page editor, which contains a console.
	 */
	void createPage2() {
		logPanel = new LogPanel(getContainer(), this, 0);
		int index = addPage(logPanel);
		setPageText(index, "Console");
	}

	void createPage3() {
		explorerPanel = new ExplorerPanel(getContainer(), this, 0);
		int index = addPage(explorerPanel);
		setPageText(index, "Explorer");
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage1();
		createPage2();
		createPage3();
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		// ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * The <code>MultiPageEditor</code> implementation of this method checks
	 * that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}

	/**
	 * Set pageIndex as active page in multi-editor.
	 */
	public void setActivePage(int pageIndex) {
		if (pageIndex == 1)
			glueweb.pages.MainPanel.tabFolder.setSelection(0);
		super.setActivePage(pageIndex);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void showGlue() {
		getContainer().getShell().setText("AXIM Web");
		boolean continueGlue = MessageDialog.openQuestion(getContainer()
				.getShell(), "AXIM Web", "Do you want to open GlueWeb main?");
		if (continueGlue)
			this.setActivePage(1);
	}

	public void showDelete() {
		getContainer().getShell().setText("GlueWeb");
		boolean delete = MessageDialog.openQuestion(getContainer().getShell(),
				"GlueWeb", "Do you want to delete temporary files?");
		if (delete)
			glueweb.actions.Glue.deleteAfterGlue = true;
	}

	public void showErrorAxim() {
		getContainer().getShell().setText("AXIM Web");
		boolean showConsole = MessageDialog.openQuestion(getContainer()
				.getShell(), "AXIM Web",
				"AXIM produced errors. Do you want to open the log? ");
		if (showConsole)
			this.setActivePage(2);
	}

	public void showErrorGlue(String title, String cadena) {
		getContainer().getShell().setText(title);
		MessageDialog.openError(getContainer().getShell(), title, cadena);
		glueweb.pages.MainPanel.loading.setState(SWT.ERROR);

	}
	
	public void showErrorProperties(String title, String cadena, int tab) {
		glueweb.pages.MainPanel.tabFolder.setSelection(tab);
		getContainer().getShell().setText(title);
		MessageDialog.openError(getContainer().getShell(), title, cadena);
	}
	

	public void showEditorInput(IEditorInput editorInput) {
		if (editorInput.getName().endsWith(".info")) {
			setActivePage(1);
			if (editorInput.getName().equals("ui.info")) {
				glueweb.pages.MainPanel.tabFolder.setSelection(1);
				try {
					glueweb.pages.MainPanel.loadFile("User Interface/ui.info");
				} catch (IOException e) {
				}
			} else if (editorInput.getName().equals("bl.info")) {
				glueweb.pages.MainPanel.tabFolder.setSelection(2);
				try {
					glueweb.pages.MainPanel.loadFile("Bussiness Logic/bl.info");
				} catch (IOException e) {
				}
			} else if (editorInput.getName().equals("d.info")) {
				glueweb.pages.MainPanel.tabFolder.setSelection(3);
				try {
					glueweb.pages.MainPanel.loadFile("Data/d.info");
				} catch (IOException e) {
				}
			}
		}

	}


}
