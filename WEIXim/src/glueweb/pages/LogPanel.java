package glueweb.pages;

import glueweb.controllers.ClearController;
import glueweb.controllers.SaveAsController;
import glueweb.editors.IActivePage;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

public class LogPanel extends Composite {

	private static Text text;
	@SuppressWarnings("unused")
	private IActivePage multiEditor;

	/**
	 * Create the composite
	 * 
	 * @param parent
	 * @param style
	 */
	public LogPanel(Composite parent, IActivePage multiEditor, int style) {
		super(parent, style);

		this.multiEditor = multiEditor;

		GridLayout layout = new GridLayout();
		layout.marginBottom = 10;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.marginTop = 10;
		this.setLayout(layout);

		this.multiEditor = multiEditor;
		Group logConsoleGroup = new Group(this, SWT.FILL);
		logConsoleGroup.setText("Log Console");
		logConsoleGroup.setLayout(layout);
		logConsoleGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		text = new Text(logConsoleGroup, SWT.V_SCROLL | SWT.READ_ONLY
				| SWT.H_SCROLL | SWT.BORDER);
		text
				.setBackground(Display.getCurrent().getSystemColor(
						SWT.COLOR_WHITE));
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 2;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 1;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		logConsoleGroup.setLayoutData(gridData);
		logConsoleGroup.setLayout(gridLayout);
		text.setLayoutData(gridData1);

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.horizontalSpacing = 2;
		gridLayout1.marginWidth = 2;
		gridLayout1.marginHeight = 2;
		gridLayout1.numColumns = 2;
		gridLayout1.verticalSpacing = 1;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.END;
		gridData2.grabExcessVerticalSpace = false;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.CENTER;
		
		Composite buttonConsoleGroup = new Composite(this, SWT.NONE);
		buttonConsoleGroup.setLayoutData(gridData2);
		buttonConsoleGroup.setLayout(gridLayout1);
		

		Button saveAsButton = new Button(buttonConsoleGroup, SWT.END);
		Button clearButton = new Button(buttonConsoleGroup, SWT.END);


		clearButton.setLayoutData(gridData2);
		clearButton.setText("Clear");
		clearButton.addSelectionListener(new ClearController(text));

		saveAsButton.setText("Save as");
		saveAsButton.addSelectionListener(new SaveAsController(text));
		saveAsButton.setLayoutData(gridData2);
	}

	/**
	 * @see org.eclipse.swt.widgets.Composite#checkSubclass()
	 */
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public static void printLine(String sms) {
		text.append(sms);
		text.append("\n");
	}

}
