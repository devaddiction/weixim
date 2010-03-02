package glueweb.controllers;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

/**
 * enable or disable Local or URL because are opposite
 */
public class RadioButtonController extends SelectionAdapter {
	private Text localText, urlText, portText, userText, passText;
	private Button browseButton;

	public RadioButtonController(Button sourceButton, Text localText,
			Button browseButton, Button urlButton, Text urlText,
			Text portTextURL, Text userText, Text passText) {
		this.localText = localText;
		this.browseButton = browseButton;
		this.urlText = urlText;
		this.portText = portTextURL;
		this.passText = passText;
		this.userText = userText;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event) {
		if (event.widget instanceof Button)
			handleRadio((Button) event.widget);
	}

	/**
	 * do the action
	 * 
	 * @param item
	 */
	private void handleRadio(Button item) {
		if (item.getText().equalsIgnoreCase("Local Directory")) {
			this.localText.setEnabled(true);
			this.browseButton.setEnabled(true);
			this.urlText.setEnabled(false);
			this.portText.setEnabled(false);
			this.userText.setEnabled(false);
			this.passText.setEnabled(false);
		} else if (item.getText().equalsIgnoreCase("Remote Directory")) {
			this.localText.setEnabled(false);
			this.browseButton.setEnabled(false);
			this.urlText.setEnabled(true);
			this.portText.setEnabled(true);
			this.userText.setEnabled(true);
			this.passText.setEnabled(true);
		}
	}
}
