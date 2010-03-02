package glueweb.controllers;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class TabSelectionListener implements SelectionListener {
	TabFolder tabFolder;
	Button localRadioButton, localButton, remoteRadioButton, uploadYes, uploadNo;
	Text localText, remoteText, remotePortText, remoteUserText, remotePassText;
	Group webServerLocationGroup;
	Combo viewPointCombo, technologyCombo;
	Text serverText, serverPortText, serverUserText, serverPassText;
	Label serverPassLabel, serverUserLabel, serverPortLabel, locationLabel;

	public TabSelectionListener(TabFolder tabFolder, Button localRadioButton,
			Text localText, Button localButton, Button remoteRadioButton,
			Button uploadYes, Button uploadNo, Text remoteText, Text remotePortText, Text remoteUserText,
			Text remotePassText, Text serverText, Text serverPortText, Text serverUserText, Text serverPassText, Label serverPassLabel, Label serverUserLabel, Label serverPortLabel, Label locationLabel, Group webServerLocationGroup,
			Combo viewPointCombo, Combo technologyCombo) {
		this.tabFolder = tabFolder;
		this.localRadioButton = localRadioButton;
		this.localButton = localButton;
		this.remoteRadioButton = remoteRadioButton;
		this.localText = localText;
		this.remoteText = remoteText;
		this.remotePortText = remotePortText;
		this.remoteUserText = remoteUserText;
		this.remotePassText = remotePassText;
		this.webServerLocationGroup = webServerLocationGroup;
		this.viewPointCombo = viewPointCombo;
		this.uploadNo = uploadNo;
		this.uploadYes = uploadYes;
		this.technologyCombo = technologyCombo;
		this.serverText = serverText;
		this.serverPortText = serverPortText;
		this.serverUserText = serverUserText;
		this.serverPassText = serverPassText;
		this.serverPassLabel = serverPassLabel;
		this.serverUserLabel = serverUserLabel;
		this.serverPortLabel = serverPortLabel;
		this.locationLabel = locationLabel;
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		TabItem item = (TabItem) e.item;
		if (item == null) {
			return;
		}
		if (getSelectedItem().getText().toString().equals("DataBase")) {
			this.webServerLocationGroup.setVisible(false);
			this.webServerLocationGroup.setEnabled(false);
			this.localRadioButton.setVisible(false);
			this.localRadioButton.setSelection(false);
			this.localButton.setVisible(false);
			this.remoteRadioButton.setEnabled(true);
			this.remoteRadioButton.setSelection(true);
			this.localText.setVisible(false);
			this.remoteText.setEnabled(true);
			this.remotePortText.setEnabled(true);
			this.remoteUserText.setEnabled(true);
			this.remotePassText.setEnabled(true);
			this.viewPointCombo.removeAll();
			this.viewPointCombo.setItems(new String[] { "Data" });
			this.viewPointCombo.select(0);
			this.technologyCombo.removeAll();
			this.technologyCombo.setEnabled(true);
			this.technologyCombo.setItems(new String[] { "Access", "Oracle",
					"MySQL" });
			this.remoteText.setText("serverName/DataBase or serverName/Dataname");
		} else {
			this.webServerLocationGroup.setVisible(true);
			this.webServerLocationGroup.setEnabled(true);
			this.uploadNo.setEnabled(true);
			this.uploadYes.setEnabled(true);
			this.uploadNo.setSelection(true);
			this.uploadYes.setSelection(false);
			this.serverText.setVisible(false);
			this.serverPortText.setVisible(false);
			this.serverUserText.setVisible(false);
			this.serverPassText.setVisible(false);
			this.serverPassLabel.setVisible(false);
			this.serverUserLabel.setVisible(false);
			this.serverPortLabel.setVisible(false);
			this.locationLabel.setVisible(false);
			this.localRadioButton.setVisible(true);
			this.localRadioButton.setSelection(true);			
			this.localButton.setVisible(true);
			this.localButton.setEnabled(true);
			this.remoteRadioButton.setEnabled(true);
			this.remoteRadioButton.setSelection(false);
			this.localText.setVisible(true);
			this.localText.setEnabled(false);
			this.remoteText.setEnabled(false);
			this.remotePortText.setEnabled(false);
			this.remoteUserText.setEnabled(false);
			this.remotePassText.setEnabled(false);
			this.remoteText.setText("");
			if (getSelectedItem().getText().toString().equals("User Interface")) {
				this.localText.setEnabled(true);
				this.viewPointCombo.removeAll();
				this.viewPointCombo.setItems(new String[] { "User Interface" });
				this.viewPointCombo.select(0);
				this.technologyCombo.removeAll();
				this.technologyCombo.setEnabled(true);
				this.technologyCombo
						.setItems(new String[] { "HTML", "JSP", "ColdFusion",
								"HTML/JAVASCRIPT/XSLT", "PHP", "Ruby", "Perl",
								"ASP.NET", "XSLT", "Python", "C++ Server Pages" });
			} else if (getSelectedItem().getText().toString().equals("Bussiness Logic")) {
				this.localText.setEnabled(true);
				this.viewPointCombo.removeAll();
				this.viewPointCombo.setItems(new String[] { "Bussiness Logic" });
				this.viewPointCombo.select(0);
				this.technologyCombo.removeAll();
				this.technologyCombo.setEnabled(true);
				this.technologyCombo.setItems(new String[] { "Java(Servlet)",
						"Java(EJB)", "JSP(Java)", "ASP", "ColdFusion",
						"ColdFusion(Java)", "Java(applet)", "PHP", "Ruby", "Perl",
						"C#", "Visual Basic", "XSLT(Java)", "Python",
						"C++ Server Pages", "JavaScript", "Web Services",
						"Corba Components" });
			}
/*			viewPointCombo.addSelectionListener(new ComboController(
					technologyCombo, localRadioButton, localText, localButton,
					remoteRadioButton, remoteText, remotePortText, remoteUserText,
					remotePassText));
*/
		}
	}

	private TabItem getSelectedItem() {
		TabItem items[] = tabFolder.getSelection();
		if (items.length > 0) {
			return items[0];
		} else {
			return null;
		}
	}

}
