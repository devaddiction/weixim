package glueweb.actions;

import glueweb.actions.modifiers.Global;
import glueweb.editors.IActivePage;
import glueweb.ocl.*;
import glueweb.xmi.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;

/**
 * the main class of the project. This class will do all the functionality: read
 * XMI diagram, read OCL constraints and generate the source code. Is not
 * implemented completely
 */
public class Glue {
	private IActivePage multiEditor;

	private String xmiUI, xmiBL, xmiD, ocl;
	private Info infoUI, infoBL, infoD;

	private Map<String, Association> associations;

	private ArrayList<String> associationsNameList;
	private ArrayList<String> associationsFunctionList;
	private ArrayList<String> associationsDependencesList;
	private static ArrayList<String> downloaderFiles = new ArrayList<String>();
	private static ArrayList<String> downloaderFilesIU = new ArrayList<String>();
	private static ArrayList<String> downloaderFilesBL = new ArrayList<String>();

	public static boolean deleteAfterGlue = false;

	public Glue(String xmiUI, String xmiBL, String xmiD, String ocl,
			IActivePage multiEditor) throws IOException {
		this.xmiUI = xmiUI;
		this.xmiBL = xmiBL;
		this.xmiD = xmiD;
		this.ocl = ocl;
		this.multiEditor = multiEditor;

		glueAction();
	}

	/**
	 * load the *.info file but glue functionality is not implemented yet
	 * 
	 * @throws IOException
	 */
	private void glueAction() throws IOException {
		// load Info
		String filename = null;
		glueweb.pages.MainPanel.loading
				.setSelection(glueweb.pages.MainPanel.loading.getMinimum());
		IFile file = (IFile) ((IEditorPart) multiEditor).getEditorInput()
				.getAdapter(IFile.class);
		String path = System.getProperty("osgi.instance.area").substring(5)
				.toString()
				+ fileName(file.getProject().toString());

		if (!glueweb.pages.MainPanel.uiText.getText().isEmpty())
			infoUI = loadInfo(path + "/User Interface/ui.info");
		if (!glueweb.pages.MainPanel.blText.getText().isEmpty())
			infoBL = loadInfo(path + "/Bussiness Logic/bl.info");
		if (!glueweb.pages.MainPanel.dText.getText().isEmpty())
			infoD = loadInfo(path + "/Data/d.info");

		oclAction(ocl);

		xmiAction(associationsNameList, associationsFunctionList,
				associationsDependencesList);

		try {
			modifyFiles();
		} catch (Exception e) {
			this.multiEditor.showErrorGlue("Glue Web", "Error in Glue action");
			glueweb.pages.LogPanel
					.printLine("\t-GLUEWEB ERROR- Error in Glue action");

		}
		this.multiEditor.showDelete();
		if (deleteAfterGlue)
			removeDownloader();
	}

	private void modifyFiles() throws IOException {
		int i = 0;
		String pathIU = null;
		String pathBL = null;
		String modifyIU = null;
		String modifyBL = null;
		boolean downloadedIU = false;
		boolean downloadedBL = false;
		int barrasCarga = 0;
		int contadorDependencias = 0;

		for (String str : associations.keySet()) {
			glueweb.pages.MainPanel.statusOk("");
			barrasCarga = barrasCarga
					+ (glueweb.pages.MainPanel.loading.getMaximum() / associations
							.size());
			glueweb.pages.MainPanel.loading.setSelection(barrasCarga);

			if (associations.get(str).getEstereotipoSource().equals("page")
					&& !(infoUI.getTechnology().equals("Ruby") && infoBL
							.getTechnology().equals("Ruby"))) {
				pathIU = infoUI.getLocation().toString() + "/"
						+ associations.get(str).getSourceCode().toString()
						+ "." + setExtension(infoUI.getTechnology());
				modifyIU = pathIU;
				pathBL = infoBL.getLocation().toString() + "/"
						+ associations.get(str).getTargetCode().toString()
						+ "." + setExtension(infoBL.getTechnology());
				modifyBL = pathBL;
				IFile file = (IFile) ((IEditorPart) multiEditor)
						.getEditorInput().getAdapter(IFile.class);
				String path = System.getProperty("osgi.instance.area")
						.substring(5).toString()
						+ fileName(file.getProject().toString());

				if (!infoUI.isLocal()) {
					pathIU = path + "/User Interface/"
							+ associations.get(str).getSourceCode().toString()
							+ "." + setExtension(infoUI.getTechnology());
					modifyIU = infoUI.getLocation() + "/"
							+ associations.get(str).getSourceCode().toString()
							+ "." + setExtension(infoUI.getTechnology());
					if (!downloadedIU) {
						glueweb.util.FTP.downloadFileByFTP(infoUI, associations
								.get(str).getSourceCode().toString()
								+ "." + setExtension(infoUI.getTechnology()),
								pathIU);
						downloadedIU = true;
						try {
							ResourcesPlugin.getWorkspace().getRoot()
									.refreshLocal(IResource.DEPTH_INFINITE,
											null);
						} catch (CoreException e) {
							e.toString();
						}
					}
				}
				if (!downloaderFilesIU.contains(pathIU)) {
					downloaderFilesIU.add(pathIU);
				}

				if (!infoBL.isLocal()) {
					pathBL = path + "/Bussiness Logic/"
							+ associations.get(str).getTargetCode().toString()
							+ "." + setExtension(infoBL.getTechnology());
					modifyBL = infoBL.getLocation() + "/"
							+ associations.get(str).getTargetCode().toString()
							+ "." + setExtension(infoBL.getTechnology());
					if (!downloadedBL) {
						glueweb.util.FTP.downloadFileByFTP(infoBL, associations
								.get(str).getTargetCode().toString()
								+ "." + setExtension(infoBL.getTechnology()),
								pathBL);
						downloadedBL = true;
						try {
							ResourcesPlugin.getWorkspace().getRoot()
									.refreshLocal(IResource.DEPTH_INFINITE,
											null);
						} catch (CoreException e) {
							e.toString();
						}
					}
				}
				if (!downloaderFilesBL.contains(pathBL)) {
					downloaderFilesBL.add(pathBL);
				}

				modifyFileIU(pathIU, infoUI.getTechnology(), infoBL
						.getTechnology(), oclFunctions(ocl).get(i), pathBL,
						associations.get(str).getOrigen(), pathBL, associations
								.get(str).getEstereotipoOrigen(), associations
								.get(str).getEvento(), associations.get(str)
								.getMetodo(), modifyIU, modifyBL);

				// Esto para Ruby porque modifica BL
			} else if (infoUI.getTechnology().equals("Ruby")
					&& infoBL.getTechnology().equals("Ruby")) {
				String direccion = infoBL.getLocation().toString() + "/"
						+ associations.get(str).getTargetCode().toString()
						+ ".rb";

				String index = infoUI.getLocation().toString() + "/"
						+ associations.get(str).getSourceCode().toString()
						+ ".rhtml";

				glueweb.actions.modifiers.ModifiersROR.modifyBL(direccion,
						index);
				if (infoD.getTechnology().equals("MySQL"))
					glueweb.actions.modifiers.ModifiersROR.modifyBD(direccion,
							infoD, "com.mysql.jdbc.Driver", "jdbc:mysql");
			} else {
				modifyFileBL(pathBL, setExtension(infoBL.getTechnology()),
						infoD);
			}
			i++;

			contadorDependencias++;
		}
		glueweb.pages.MainPanel.loading
				.setSelection(glueweb.pages.MainPanel.loading.getMaximum());

		ArrayList<String> pages = glueweb.xmi.ParserXMI.getPagesFromFile(xmiUI);
		glueweb.pages.ExplorerPanel.tabItem.setText(pages.get(0) + "."
				+ setExtension(infoUI.getTechnology()));
		glueweb.pages.ExplorerPanel.browser.setUrl(infoUI.getLocation()
				.toString()
				+ "/"
				+ pages.get(0)
				+ "."
				+ setExtension(infoUI.getTechnology()));
		for (int count = 1; count < pages.size(); count++) {
			glueweb.pages.ExplorerPanel.addTabExplorer(pages.get(count), infoUI
					.getLocation().toString()
					+ "/"
					+ pages.get(count)
					+ "."
					+ setExtension(infoUI.getTechnology()));
		}

		if (infoUI.getWebServerLocation() != null) {
			for (int j = 0; j < downloaderFilesIU.size(); j++)
				glueweb.util.FTP.uploadFileByFTP(infoUI, downloaderFilesIU.get(
						j).toString());
		}

		if (infoBL.getWebServerLocation() != null) {
			for (int j = 0; j < downloaderFilesBL.size(); j++)
				glueweb.util.FTP.uploadFileByFTP(infoBL, downloaderFilesBL.get(
						j).toString());
		}

		downloaderFiles.addAll(downloaderFilesIU);
		downloaderFiles.addAll(downloaderFilesBL);
		// downloaderFiles.add(path + "/User Interface/ui.info");
		// downloaderFiles.add(path + "/Bussiness Logic/bl.info");
		// downloaderFiles.add(path + "/Data/d.info");
		downloaderFiles.add(ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/" + "temp_form");
		downloaderFiles.add(ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/" + "temp");
		downloaderFiles.add(ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/" + "404.html");

	}

	private String setExtension(String technology) {
		if (technology.equals("PHP"))
			return "php";
		else if (technology.equals("HTML"))
			return "html";
		else if (technology.equals("JAVA"))
			return "java";
		else if (technology.equals("JavaScript"))
			return "js";
		return null;
	}

	public static void removeDownloader() {
		for (int i = 0; i < downloaderFiles.size(); i++) {
			if (downloaderFiles.subList(i + 1, downloaderFiles.size())
					.contains(downloaderFiles.get(i)))
				downloaderFiles.remove(i);
		}
		for (int i = 0; i < downloaderFiles.size(); i++) {
			File fichero = new File(downloaderFiles.get(i).toString());
			if (fichero.delete()) {
				glueweb.pages.LogPanel.printLine("Deleting... "
						+ downloaderFiles.get(i));
			}
		}
		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(
					IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.toString();
		}
	}

	private void modifyFileIU(String direccion, String typeSource,
			String typeTarget, String elemento, String action, String event,
			String modificador, String estereotipoElemento, String evento,
			String metodo, String modifyIU, String modifyBL) throws IOException {

		if (Global.inForm(direccion, event)) {
			if (typeSource.equals("HTML")) {
				if (typeTarget.equals("JavaScript")) {
					glueweb.actions.modifiers.ModifiersHTML.modify_HEAD(
							direccion, action, modifyIU, modifyBL);
				}
				glueweb.actions.modifiers.ModifiersHTML.modify_FORM(direccion,
						action, event, modificador);
			} else if (typeSource.equals("PHP"))
				glueweb.actions.modifiers.ModifiersPHP.modify_FORM(direccion,
						action, event, modificador);
			else if (typeSource.equals("JSP"))
				glueweb.actions.modifiers.ModifiersJava.modify_FORM(direccion,
						action, event, modificador);
		} else {
			if (typeSource.equals("HTML")) {
				if (typeTarget.equals("JavaScript")) {
					glueweb.actions.modifiers.ModifiersHTML.modify_HEAD(
							direccion, action, modifyIU, modifyBL);
				}
				glueweb.actions.modifiers.ModifiersHTML.modify(direccion,
						action, event, modificador, estereotipoElemento,
						evento, metodo);
			}
		}

	}

	private void modifyFileBL(String direccion, String typeSource, Info bd)
			throws IOException {

		if (bd.getTechnology().equals("MySQL")) {
			if (typeSource.equals("HTML"))
				glueweb.actions.modifiers.ModifiersHTML.modify_BD_MySQL(
						direccion, bd);
			else if (typeSource.equals("PHP"))
				glueweb.actions.modifiers.ModifiersPHP.modify_BD_MySQL(
						direccion, bd);
			else if (typeSource.equals("JSP"))
				glueweb.actions.modifiers.ModifiersJava.modify_BD_MySQL(
						direccion, bd, "com.mysql.jdbc.Driver", "jdbc:mysql");
			else if (typeSource.equals("RUBY"))
				glueweb.actions.modifiers.ModifiersROR.modifyBD(direccion, bd,
						"com.mysql.jdbc.Driver", "jdbc:mysql");
		}
	}

	private ArrayList<String> oclFunctions(String oclFile) throws IOException {
		ParserOCL parserOcl = new ParserOCL();
		parserOcl.loadOclFile(oclFile);
		return parserOcl.associationsFunctionList();
	}

	/**
	 * Create a new instance of ParserOCL to load the OCL constraint file
	 * 
	 * @param oclFile
	 * @return an ArrayList<String> with associations name
	 * @throws IOException
	 */
	private void oclAction(String oclFile) throws IOException {
		ParserOCL parserOcl = new ParserOCL();
		parserOcl.loadOclFile(oclFile);
		parserOcl.correspondencesView();
		parserOcl.correspondencesVerify();

		this.associationsFunctionList = parserOcl.associationsFunctionList();
		this.associationsNameList = parserOcl.associationsNameList();
		this.associationsDependencesList = parserOcl
				.associationsDependencesList();
	}

	/**
	 * Create a new instance of ParserXMI to load the XMI diagrams. Check if the
	 * association is contained in any diagrams.
	 * 
	 * @param adl
	 * 
	 * @param associations
	 *            name list
	 * @throws IOException
	 */
	private void xmiAction(ArrayList<String> anl, ArrayList<String> afl,
			ArrayList<String> adl) {
		ParserXMI parserXmi = new ParserXMI();

		if (anl == null) {
			this.multiEditor.showErrorGlue("Glue Web",
					"There aren't OCL constraints");
			glueweb.pages.LogPanel
					.printLine("\t-GLUEWEB ERROR- there aren't OCL constraints");
		} else {
			// try in User Interface Model
			try {
				ParserXMI.loadXmiFile(xmiUI, anl, adl, afl);
			} catch (Exception e) {
				this.multiEditor.showErrorGlue("Glue Web", e.toString());
				glueweb.pages.LogPanel.printLine("\t-GLUEWEB ERROR- "
						+ e.toString());

			}
			associations = parserXmi.getAssociations();
			if (associations == null) {
				// try in Business Logic Model

				associations = parserXmi.getAssociations();
				if (associations == null) {
					// try in Data Model
					try {
						ParserXMI.loadXmiFile(xmiD, anl, adl, afl);
					} catch (Exception e) {
						this.multiEditor
								.showErrorGlue("Glue Web", e.toString());
					}
					associations = parserXmi.getAssociations();
					if (associations == null) {
						this.multiEditor.showErrorGlue("Glue Web",
								"There aren't XMI associations");
						glueweb.pages.LogPanel
								.printLine("\t-GLUEWEB ERROR- there aren't XMI associations");
					}
				}
			}
		}
		parserXmi.associationsView();
	}

	private static String fileName(String path) {
		String result = null;

		StringTokenizer st = new StringTokenizer(path, ":\\/");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			result = token;
		}
		return result;
	}

	/**
	 * Load the *.info file with the information of XMI diagrams.
	 * 
	 * @param path
	 * @return Info file
	 * @see glueweb.xmi#Info
	 */
	private Info loadInfo(String path) {
		Info temp = new Info();
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			if (!path.contains("d.info")) {
				if (br.readLine().contains("local")) {
					temp.setLocal();
					temp.setLocation(br.readLine());
					temp.setViewpointLanguage(br.readLine());
					temp.setTechnology(br.readLine());
					temp.setWebServerLocation(br.readLine());
					temp.setWebServerPort(br.readLine());
					temp.setWebServerUser(br.readLine());
					temp.setWebServerPass(br.readLine());
					temp.setPort(null);
					temp.setUser(null);
					temp.setPass(null);
				} else {
					temp.setUrl();
					temp.setLocation(br.readLine());
					temp.setPort(br.readLine());
					temp.setUser(br.readLine());
					temp.setPass(br.readLine());
					temp.setViewpointLanguage(br.readLine());
					temp.setTechnology(br.readLine());
					temp.setWebServerLocation(br.readLine());
					temp.setWebServerPort(br.readLine());
					temp.setWebServerUser(br.readLine());
					temp.setWebServerPass(br.readLine());
				}
			} else {
				temp.setLocal();
				temp.setViewpointLanguage(br.readLine());
				temp.setTechnology(br.readLine());
				temp.setWebServerLocation(br.readLine());
				temp.setWebServerPort(br.readLine());
				temp.setWebServerUser(br.readLine());
				temp.setWebServerPass(br.readLine());
			}
		} catch (FileNotFoundException e) {
			this.multiEditor.showErrorGlue("Glue Web", "Info file not found");
			glueweb.pages.LogPanel
					.printLine("\t-GLUEWEB ERROR- info file not found");
		} catch (IOException e) {
			this.multiEditor.showErrorGlue("Glue Web", "Info file I/O error");
			glueweb.pages.LogPanel
					.printLine("\t-GLUEWEB ERROR- info file I/O error");
		}
		return temp;
	}

	String copyFile(String original, String directorio) {
		IFile file = (IFile) ((IEditorPart) multiEditor).getEditorInput()
				.getAdapter(IFile.class);
		String path = System.getProperty("osgi.instance.area").substring(5)
				.toString()
				+ fileName(file.getProject().toString());
		glueweb.util.Copy copia = new glueweb.util.Copy(original, path
				.toString()
				+ "/" + directorio + fileName(original));
		return path.toString() + "/" + directorio + fileName(original);
	}

}
