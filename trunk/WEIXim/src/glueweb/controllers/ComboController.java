package glueweb.controllers;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

/**
 * complete correctly the technology combo field based on viewpoint combo
 * selected
 */
public class ComboController extends SelectionAdapter {
	private Combo technologyCombo;
	private Button localRadioButton, urlRadioButton, localButton;
	private Text localText, urlText, portTextURL, userText, passText;

	public ComboController(Combo technologyCombo, Button localRadioButton,
			Text localText, Button localButton, Button urlRadioButton,
			Text urlText, Text portTextURL, Text userText, Text passText) {
		this.technologyCombo = technologyCombo;
		this.localRadioButton = localRadioButton;
		this.localText = localText;
		this.localButton = localButton;
		this.urlRadioButton = urlRadioButton;
		this.urlText = urlText;
		this.portTextURL = portTextURL;
		this.userText = userText;
		this.passText = passText;
	}

	public ComboController(Combo technologyCombo) {
		this.technologyCombo = technologyCombo;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event) {
		if (event.widget instanceof Combo)
			handleCombo((Combo) event.widget);
	}

	/**
	 * @param item
	 */
	private void handleCombo(Combo item) {
		if (item.getText().equalsIgnoreCase("User Interface")) {
			technologyCombo.setItems(new String[] { "HTML", "JSP",
					"ColdFusion", "HTML/JAVASCRIPT/XSLT", "PHP", "Ruby",
					"Perl", "ASP.NET", "XSLT", "Python", "C++ Server Pages" });
			this.localRadioButton.setVisible(true);
			this.localText.setVisible(true);
			this.localButton.setVisible(true);
			this.localRadioButton.setEnabled(true);
			this.localText.setEnabled(true);
			this.localButton.setEnabled(true);
			this.urlRadioButton.setEnabled(true);
			this.urlText.setEnabled(false);
			this.portTextURL.setEnabled(false);
			this.userText.setEnabled(false);
			this.passText.setEnabled(false);
		} else if (item.getText().equalsIgnoreCase("Business Logic")) {
			technologyCombo.setItems(new String[] { "Java(Servlet)",
					"Java(EJB)", "JSP(Java)", "ASP", "ColdFusion",
					"ColdFusion(Java)", "Java(applet)", "PHP", "Ruby", "Perl",
					"C#", "Visual Basic", "XSLT(Java)", "Python",
					"C++ Server Pages", "JavaScript", "Web Services",
					"Corba Components" });
			this.localRadioButton.setVisible(true);
			this.localText.setVisible(true);
			this.localButton.setVisible(true);
			this.localRadioButton.setEnabled(true);
			this.localText.setEnabled(true);
			this.localButton.setEnabled(true);
			this.urlRadioButton.setEnabled(true);
			this.urlText.setEnabled(false);
			this.portTextURL.setEnabled(false);
			this.userText.setEnabled(false);
			this.passText.setEnabled(false);
			this.urlText.setText("");
		} else if (item.getText().equalsIgnoreCase("DataBase")) {
			technologyCombo
					.setItems(new String[] { "Access", "Oracle", "MySQL" });
			this.localRadioButton.setVisible(false);
			this.localButton.setEnabled(false);
			this.localText.setVisible(false);
			this.localText.setEnabled(false);
			this.localButton.setVisible(false);
			this.localButton.setEnabled(false);
			this.urlRadioButton.setSelection(true);
			this.urlText.setEnabled(true);
			this.portTextURL.setEnabled(true);
			this.userText.setEnabled(true);
			this.passText.setEnabled(true);
		} else
		technologyCombo.setEnabled(true);
	}
}
