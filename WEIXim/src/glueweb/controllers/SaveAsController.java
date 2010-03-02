package glueweb.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * save the log file
 */
public class SaveAsController extends SelectionAdapter{
	private Text text;
	
	public SaveAsController(Text text){
		this.text = text;
	}
	
	/**
	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event){
		if(event.widget instanceof Button)
			handleSaveAs((Button)event.widget);	
	}
	
	/**
	 * @param button
	 */
	public void handleSaveAs(Button item){
		FileDialog dlg = new FileDialog(new Shell(), SWT.SAVE);
		String filename = null;
		boolean done = false;
		
		while(!done){
			filename = dlg.open();
			//cancel pressed
			if(filename == null){
				done = true;
			}
			else{
				File file = new File(filename);
				//Exists? yes 
				if(file.exists()){
					MessageBox mb = new MessageBox(dlg.getParent(), SWT.ICON_WARNING|SWT.YES|SWT.NO);
					mb.setMessage(filename+" already exists. Do you want to replace it?");
					//replace? yes
					if(mb.open() == SWT.YES){
						save(text,filename);
						done = true;
					}
					//replace? no
				}
				//Exists? no
				else{
					save(text,filename);
					done = true;
				}
			}
		}
	}

	/**
	 * @param log file content
	 * @param filename
	 */
	private void save(Text text2, String filename) {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(filename));
			pw.println(text2.getText());
			pw.close();
		} catch (IOException e) {
			glueweb.pages.LogPanel.printLine("\t-CONSOLE ERROR- saving log file");
		}
	}
}
