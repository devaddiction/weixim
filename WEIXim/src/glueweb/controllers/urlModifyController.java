package glueweb.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;


/**
 * show in status label an error when URL is not correct
 */
public class urlModifyController implements ModifyListener{
	
	public urlModifyController(){
	}
	
	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent event) {
		if(event.widget instanceof Text)
			handleModifyText((Text)event.widget);	
	}

	/**
	 * @param URL text
	 */
	private void handleModifyText(Text widget) {
		/*glueweb.pages.PropertyPanel.statusError("");
		try{
			new URL(widget.getText());
		} catch(MalformedURLException urle){
			glueweb.pages.PropertyPanel.statusError("URL is not correct");
		}*/
	}
}