package glueweb.controllers;

import java.io.File;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;


/**
 * show in status label an error when path is not correct
 */
public class pathModifyController implements ModifyListener{
	private String tab;
	
	/**
	 * @param text
	 */
	public pathModifyController(String tab){
		this.tab = tab;
	}
	
	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent event) {
		if(event.widget instanceof Text)
			handleModifyText((Text)event.widget);	
	}

	/**
	 * @param text path
	 */
	private void handleModifyText(Text widget) {
		File file = new File(widget.getText());
		boolean exists = file.exists();
		
		if(tab.equalsIgnoreCase("mainpanel")){
			glueweb.pages.MainPanel.statusOk("");
			if(!exists){
				glueweb.pages.LogPanel.printLine("path is not correct");
			}
		}
		else if(tab.equalsIgnoreCase("propertypanel")){
			if(!exists){
			}
		}
		else if(tab.equalsIgnoreCase("aximpanel")){
			glueweb.pages.AXIMPanel.statusOk("");
			if(!exists){
				glueweb.pages.AXIMPanel.statusError("path is not correct");
			}
		}
	}
}
