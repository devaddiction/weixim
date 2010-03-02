package glueweb.pages;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import glueweb.editors.IActivePage;
import glueweb.editors.MultiPageEditor;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class ExplorerPanel extends Composite {

	private Composite composite = null;
	public static Browser browser = null;
	private Group group = null;
	public static TabFolder tabFolder;
	public static TabItem tabItem;

	public ExplorerPanel(Composite parent, IActivePage multiEditor, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		createGroup();
		setLayout(new GridLayout());
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		group = new Group(this, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(gridData);
		createBrowser();
		group.setText("Preview");
	}

	/**
	 * This method initializes browser
	 * 
	 */
	private void createBrowser() {
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;

		tabFolder = new TabFolder(group, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		tabItem = new TabItem(tabFolder, SWT.NULL);
		tabItem.setText("404");

		browser = new Browser(tabFolder, SWT.NONE);
		browser.setLayoutData(gridData1);
		browser.setUrl(ResourcesPlugin.getWorkspace().getRoot().getLocation()
				.toString()
				+ "/" + "404.html");
		tabItem.setControl(browser);

	}

	public static void addTabExplorer(String text, String url) {
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		TabItem tabItem = new TabItem(tabFolder, SWT.NULL);
		tabItem.setText(text);

		browser = new Browser(tabFolder, SWT.NONE);
		browser.setLayoutData(gridData1);
		browser.setUrl(url);
		tabItem.setControl(browser);

	}

}
