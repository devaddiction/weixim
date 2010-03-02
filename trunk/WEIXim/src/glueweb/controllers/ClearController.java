package glueweb.controllers;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

/**
* is the action to erase all content in a text field
*/
public class ClearController extends SelectionAdapter{
	private Text text;
	
	public ClearController(Text text){
		this.text = text;
	}
	
	/**
	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event){
		if(event.widget instanceof Button)
			handleClear((Button)event.widget);	
	}
	
	/**
	 * erase all content in a text field
	 * @param item
	 */
	public void handleClear(Button item){
		text.setText("");
	}
}
