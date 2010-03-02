package glueweb.controllers;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * enable or disable Local or URL because are opposite
 */
public class RadioButtonServerController extends SelectionAdapter {
	private Button uploadYes, uploadNo;
	private Label serverPortLabel, serverPassLabel,
			serverUserLabel, locationLabel; 
	private Text serverPortText, serverUserText, serverText, serverPassText;

	public RadioButtonServerController(Button uploadYes, Button uploadNo, Label locationLabel,
			Text serverText, Label serverPortLabel, Text serverPortText,
			Label serverUserLabel, Text serverUserText, Label serverPassLabel,
			Text serverPassText) {
		this.uploadYes = uploadYes;
		this.uploadNo = uploadNo;
		this.locationLabel = locationLabel;
		this.serverText = serverText;
		this.serverPortLabel = serverPortLabel;
		this.serverPortText = serverPortText;
		this.serverUserLabel = serverUserLabel;
		this.serverUserText = serverUserText;
		this.serverPassLabel = serverPassLabel;
		this.serverPassText = serverPassText;		
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
		if (item.getText().equalsIgnoreCase("Yes")) {
			this.locationLabel.setVisible(true);
			this.serverText.setVisible(true);
			this.serverPortLabel.setVisible(true);
			this.serverPortText.setVisible(true);
			this.serverUserLabel.setVisible(true);
			this.serverUserText.setVisible(true);
			this.serverPassLabel.setVisible(true);
			this.serverPassText.setVisible(true);
			
			this.locationLabel.setEnabled(true);
			this.serverText.setEnabled(true);
			this.serverPortLabel.setEnabled(true);
			this.serverPortText.setEnabled(true);
			this.serverUserLabel.setEnabled(true);
			this.serverUserText.setEnabled(true);
			this.serverPassLabel.setEnabled(true);
			this.serverPassText.setEnabled(true);
		} else if (item.getText().equalsIgnoreCase("No")){
			this.locationLabel.setVisible(false);
			this.serverText.setVisible(false);
			this.serverPortLabel.setVisible(false);
			this.serverPortText.setVisible(false);
			this.serverUserLabel.setVisible(false);
			this.serverUserText.setVisible(false);
			this.serverPassLabel.setVisible(false);
			this.serverPassText.setVisible(false);

			this.locationLabel.setEnabled(false);
			this.serverText.setEnabled(false);
			this.serverPortLabel.setEnabled(false);
			this.serverPortText.setEnabled(false);
			this.serverUserLabel.setEnabled(false);
			this.serverUserText.setEnabled(false);
			this.serverPassLabel.setEnabled(false);
			this.serverPassText.setEnabled(false);
			
		}
	}
}
