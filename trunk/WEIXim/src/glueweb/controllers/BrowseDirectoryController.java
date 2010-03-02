package glueweb.controllers;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
* is the action when you push browse button
* and select a directory using SWT
*/
public class BrowseDirectoryController  extends SelectionAdapter{
	private Text text;
	
	public BrowseDirectoryController(Text text){	
		this.text = text;
	}
	
	/**
	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event) {
		if(event.widget instanceof Button)
			handleBrowse((Button)event.widget);			
	}
	
	/**
	 * checks if press cancel or not
	 * @param item
	 */
	private void handleBrowse(Button item){
		DirectoryDialog sourceDialog = new DirectoryDialog(new Shell());
		String directoryName = null;
		
		directoryName = sourceDialog.open();
		//cancel not pressed
		if(directoryName != null){
			text.setText(directoryName);
		}
	}
}
