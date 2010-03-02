package glueweb.controllers;

import glueweb.axim.FicheroNoValidoException;
import glueweb.axim.Parser;
import glueweb.editors.IActivePage;
import glueweb.xmi.Info;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;

import java.io.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

public class ControlParserAxim {
	Parser p;
	File eleccion;
	IActivePage multiEditor;
	Combo uiCombo, lnCombo, dCombo;

	public ControlParserAxim(Parser p, File eleccion, Combo uiCombo,
			Combo lnCombo, Combo dCombo, IActivePage multiEditor) {
		this.p = p;
		this.eleccion = eleccion;
		this.multiEditor = multiEditor;
		this.uiCombo = uiCombo;
		this.lnCombo = lnCombo;
		this.dCombo = dCombo;

		this.compruebaExtension();
		this.actionPerformed();

	}

	private void ComprobacionFichero() throws FicheroNoValidoException {
		if (eleccion.isFile() == false) {
			throw new FicheroNoValidoException("Tipo de fichero no válido");
		} else {
			if (!compruebaExtension()) {
				throw new FicheroNoValidoException("Tipo de fichero no válido");
			}
		}
	}

	private boolean compruebaExtension() {
		boolean res = true;
		if (eleccion.getName().endsWith(".xml")
				|| eleccion.getName().endsWith(".xmi")
				|| eleccion.getName().endsWith(".XML")
				|| eleccion.getName().endsWith(".XMI")) {
			res = true;
		} else {
			res = false;
		}
		return res;
	}

	public void actionPerformed() {
		Date d = new Date();
		int hora = d.getHours();
		int min = d.getMinutes();
		String minCad = "";
		if (min >= 0 && min <= 9)
			minCad = "0" + min;
		else
			minCad = "" + min;
		String hora_actual = hora + ":" + minCad + " h.";
		try {
			if (eleccion != null) {
				ComprobacionFichero(); // comprobara que el fichero no
				// es un directorio
				glueweb.pages.LogPanel.printLine(eleccion.toString());
				// Lanzamos el parser xmi, pasandole ademas el directorio donde
				// el usuario tiene su XML
				IFile file = (IFile) ((IEditorPart) multiEditor)
						.getEditorInput().getAdapter(IFile.class);
				String path = System.getProperty("osgi.instance.area")
						.substring(5).toString()
						+ fileName(file.getProject().toString());

				glueweb.util.Copy copia = new glueweb.util.Copy(eleccion
						.toString(), path.toString() + "/User Interface/"
						+ fileName(eleccion.toString()));
				copia = new glueweb.util.Copy(eleccion.toString(), path
						.toString()
						+ "/Bussiness Logic/" + fileName(eleccion.toString()));
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(
						IResource.DEPTH_INFINITE, null);

				p.setDirectorioTrabajo(eleccion.getParent());
				p.procesa(eleccion);
				// Si todo ha ido bien, mensaje de ok
				glueweb.pages.LogPanel
						.printLine("\n"
								+ hora_actual
								+ "-----------"
								+ eleccion.getName()
								+ "----------- \n >proceso de análisis OK. \n >Fichero HTML \""
								+ eleccion.getName()
								+ ".html\" generado con éxito\n");
				glueweb.pages.LogPanel
						.printLine("-------------------------------------------------------\n ");
				glueweb.pages.ExplorerPanel.browser.setUrl(eleccion.toString()
						+ ".html");
				glueweb.pages.ExplorerPanel.tabItem.setText(eleccion.getName()
						.toString());
				this.multiEditor.showGlue();
				this.completeProperties();
				this.fillGlue();

			}
		} catch (Exception e) {
			e.printStackTrace();
			glueweb.pages.LogPanel.printLine("\n" + hora_actual + "-----------"
					+ eleccion.getName() + "----------- \n >ERROR: "
					+ e.getMessage() + "\n");
			glueweb.pages.LogPanel.printLine(eleccion.getName()
					+ ": Se produjo un error en la aplicación\n");
			p.limpiezaDatos();
			this.multiEditor.showErrorAxim();
		}

	}

	private void completeProperties() throws CoreException {
		IFile file = (IFile) ((IEditorPart) multiEditor).getEditorInput()
				.getAdapter(IFile.class);
		String path = System.getProperty("osgi.instance.area").substring(5)
				.toString()
				+ fileName(file.getProject().toString());

		Info uiFile = new Info();
		uiFile.setLocal();
		uiFile.setTechnology(uiCombo.getText().toString());
		uiFile.setViewpoint("User Interface");
		uiFile.setViewpointLanguage("UML");

		String filename = "User Interface/ui.info";
		saveInfo(path + "/" + filename, eleccion.getParent().toString(), uiFile);
		glueweb.pages.MainPanel.uiText.setText(eleccion.toString());
		// Mierda glueweb.pages.MainPanel.uiText.set = (Text) path +
		// "/User Interface/" + eleccion.getName();
		glueweb.pages.MainPanel.blText.setText(eleccion.toString());
		Info blFile = new Info();
		blFile.setLocal();
		blFile.setTechnology(lnCombo.getText().toString());
		blFile.setViewpoint("Bussiness Logic");
		blFile.setViewpointLanguage("UML");
		filename = "Bussiness Logic/bl.info";
		saveInfo(path + "/" + filename, eleccion.getParent().toString(), blFile);

		ResourcesPlugin.getWorkspace().getRoot().refreshLocal(
				IResource.DEPTH_INFINITE, null);

	}

	private void saveInfo(String filepath, String location, Info info) {
		FileWriter myFile = null;
		try {
			myFile = new FileWriter(filepath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(myFile);

		StringTokenizer st = new StringTokenizer(filepath, "/");
		String type = null;
		while (st.hasMoreTokens()) {
			type = st.nextToken().toString();
		}

		pw.println("local");
		pw.println(location);

		pw.println(info.getViewpointLanguage().toString());
		pw.println(info.getTechnology().toString());

		pw.println();
		pw.close();

	}

	private String fileName(String path) {
		String result = null;

		StringTokenizer st = new StringTokenizer(path, ":\\/");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			result = token;
		}
		return result;
	}

	private void fillGlue() {
		glueweb.pages.MainPanel.uiText.setEnabled(false);
		glueweb.pages.MainPanel.blText.setEnabled(false);
		glueweb.pages.MainPanel.dText.setEnabled(false);
		glueweb.pages.MainPanel.tabPropBD.dispose();
		glueweb.pages.MainPanel.tabPropIU.dispose();
		glueweb.pages.MainPanel.tabPropLN.dispose();
	}
}
