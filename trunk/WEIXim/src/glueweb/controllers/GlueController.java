package glueweb.controllers;

import java.io.IOException;

import glueweb.actions.Glue;
import glueweb.editors.IActivePage;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

/**
 * When GlueButton is pushed, this class validates all text fields and shows an
 * error when some of them are null or the file which represent does not exits
 * **/
public class GlueController extends SelectionAdapter {
	private IActivePage multiEditor;
	private Text uiText, blText, dText, oclText;

	public GlueController(Text uiText, Text blText, Text dText, Text oclText,
			IActivePage multiEditor) {
		this.uiText = uiText;
		this.blText = blText;
		this.dText = dText;
		this.oclText = oclText;
		this.multiEditor = multiEditor;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event) {
		if (event.widget instanceof Button)
			try {
				handleGlue();
			} catch (IOException e) {
				// TODO Bloque catch generado autom�ticamente
				e.printStackTrace();
			}
	}

	private void handleGlue() throws IOException {
		if (uiText.getText() == null || uiText.getText().equals(""))
			this.multiEditor.showErrorGlue("Glue Web",
					"The field User Interface Model can not be empty");
		else if (blText.getText() == null || blText.getText().equals(""))
			this.multiEditor.showErrorGlue("Glue Web",
					"The field Bussiness Logic Model can not be empty");
		else if (oclText.getText() == null || oclText.getText().equals(""))
			this.multiEditor.showErrorGlue("Glue Web", "The field Correspondences can not be empty");
		else {
			if (glueweb.pages.MainPanel.emptyText()) {
				this.multiEditor.showErrorProperties("Glue Web",
						"Missing parameters set", glueweb.pages.MainPanel
								.whatEmptyText());
			} else
				new Glue(uiText.getText(), blText.getText(), dText.getText(),
						oclText.getText(), multiEditor);

		}

	}

}
