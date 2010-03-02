package glueweb.controllers;

import java.io.IOException;
import java.util.StringTokenizer;

import glueweb.editors.IActivePage;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
* is the action when you push browse button
* and select a file using SWT
*/
public class BrowseAXIMController extends SelectionAdapter{
	private Text text;
	
	public BrowseAXIMController(Text text){
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
	 * use *.mdzip, *.xmi and *.uml2 filter only to show this file type
	 * @param item
	 */
	private void handleBrowse(Button item){
		FileDialog sourceDialog = new FileDialog(new Shell(),SWT.OPEN);
		sourceDialog.setFilterExtensions(new String[]{"*.xml","*.xmi"});
		sourceDialog.setFilterNames(new String[]{"Extensible Markup Language (*.xml)","XMI files (*.xmi)"});
		
		String fileName = null;
		
		fileName = sourceDialog.open();		
		//cancel not pressed
		if(fileName != null){	
			text.setText(fileName);
//			glueweb.pages.AXIMPanel.statusOk("File loaded succesfully");
			//go to properties panel
		}
	}
	private String fileName(String path){
		String result = null;
		
		StringTokenizer st = new StringTokenizer(path,":\\/");
		while(st.hasMoreTokens()){
			String token = st.nextToken();
			result = token;
		}
		return result;
	}
	
}
