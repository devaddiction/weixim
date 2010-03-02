package glueweb.controllers;

import java.io.File;

import glueweb.axim.Parser;
import glueweb.editors.IActivePage;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

public class AximController implements SelectionListener {

	private Combo uiCombo, lnCombo, dCombo;
	private Text localText;
	private IActivePage multiEditor;

	public AximController(Combo uiCombo, Combo lnCombo, Combo dCombo,
			Text localText, IActivePage multiEditor) {
		this.uiCombo = uiCombo;
		this.lnCombo = lnCombo;
		this.dCombo = dCombo;
		this.localText = localText;
		this.multiEditor = multiEditor;
	}

	public void widgetSelected(SelectionEvent event) {
		if (event.widget instanceof Button)
			handleApply();
	}

	public void widgetDefaultSelected(SelectionEvent event) {
		if (event.widget instanceof Button)
			handleApply();
	}

	private void handleApply() {
		if (localText.getText() == null)
			glueweb.pages.AXIMPanel.statusError("Model UML is null");
		else if (uiCombo.getText() == null)
			glueweb.pages.AXIMPanel.statusError("User Interface is null");
		else if (lnCombo.getText() == null)
			glueweb.pages.AXIMPanel.statusError("Bussiness Logic is null");
		else if ((uiCombo.getText().toString().equals("HTML"))
				&& ((lnCombo.getText().toString().equals("JavaScript"))
						|| (lnCombo.getText().toString().equals("Web Services")) || (lnCombo
						.getText().toString().equals("Corba Components")))
				&& (localText.isEnabled())
				&& ((!dCombo.getText().toString().equals("Access"))
						&& (!dCombo.getText().toString().equals("Oracle")) && (!dCombo
						.getText().toString().equals("MySQL")))) {
			Parser p = new Parser();
			ControlParserAxim c = new ControlParserAxim(p, new File(localText
					.getText().toString()), uiCombo, lnCombo, dCombo,
					multiEditor);
			File fichero = new File(ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString() + "/" + "404.html");
			fichero.delete();
		} else {
			uiCombo.removeAll();

			uiCombo.setItems(new String[] { "HTML" });
			uiCombo.select(0);
			lnCombo.removeAll();

			lnCombo.setItems(new String[] { "JavaScript", "Web Services",
					"Corba Components" });
			lnCombo.select(0);

			dCombo.removeAll();
			dCombo.setItems(new String[] { "Access", "Oracle", "MySQL" });
			dCombo.setEnabled(false);
		}

	}

	/**
	 * checks that source location is null
	 * 
	 * @param source
	 */
	private void sourceValidate(Text source) {
		if (source.getText() == null || source.getText().equals(""))
			glueweb.pages.LogPanel.printLine("source location is null");
		else if (!(new File(source.getText())).exists()) {
			glueweb.pages.LogPanel.printLine("source location not exists");
		}
	}

}
