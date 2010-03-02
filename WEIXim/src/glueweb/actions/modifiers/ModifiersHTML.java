package glueweb.actions.modifiers;

import glueweb.util.Copiar;
import glueweb.xmi.Info;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import org.eclipse.core.resources.ResourcesPlugin;

public class ModifiersHTML {

	public static void modify_FORM(String direccion, String action,
			String event, String modificador) throws IOException {
		boolean encontrado = false;
		BufferedReader bf = new BufferedReader(new FileReader(direccion));
		String linea = null;
		String nomFich1 = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/temp";
		String nomFich2 = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/temp_form";

		FileWriter fichero1 = new FileWriter(nomFich1);
		PrintWriter tmp = new PrintWriter(fichero1);

		String actionPost = null;

		FileWriter fichero2 = new FileWriter(nomFich2);
		PrintWriter aux = new PrintWriter(fichero2);

		while (((linea = bf.readLine()) != null) && (!encontrado)) {
			if (!linea.contains("<form>"))
				tmp.println(linea);
			else {
				Global.clearContent(nomFich2);
				String formLine = Global.printToElement(linea, tmp, "<form>");
				while (!((linea = bf.readLine()).contains("</form>"))) {
					aux.println(linea);
					if (linea.contains(event))
						encontrado = true;
				}
				aux.println(linea);
				aux.close();
				if (encontrado) {
					tmp.println(formLine);
					actionPost = Global.isActionOrPost(modificador);
					tmp.println("<form method="
							+ actionPost
							+ " action=\""
							+ Global.getRelativePath(new File(direccion),
									new File(action))
							+ "\" target=\"_parent\">");
					Global.copyFileStringPW(nomFich2, tmp);
					tmp.close();
					Global.clearContent(nomFich2);
					Global.refreshOriginalFile(direccion, nomFich1, nomFich2);
				} else {
					tmp.println("<form>");
					Global.copyFileStringPW(nomFich2, tmp);
				}
			}
		}
		aux.close();
		tmp.close();
		bf.close();
	}

	public static void modify(String direccion, String action, String event,
			String modificador, String estereotipo, String evento, String metodo)
			throws IOException {

		boolean encontrado = false;
		BufferedReader bf = new BufferedReader(new FileReader(direccion));

		String linea = null;
		String nomFich1 = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/temp";

		String metodoTMP = metodo.replaceAll("cte ", "");
		FileWriter fichero1 = new FileWriter(nomFich1);
		PrintWriter tmp = new PrintWriter(fichero1);

		linea = bf.readLine();
		while (linea != null) {
			if (linea.toUpperCase().contains(
					"ID=\"" + event.toUpperCase() + "\"")
					&& linea.toUpperCase().contains(
							"TYPE=\"" + estereotipo.toUpperCase())) {
				Global.fillContent(tmp, linea);
				tmp.println(" " + evento + "=\"" + metodoTMP + "\" />");
			} else
				tmp.println(linea);
			linea = bf.readLine();
		}
		fichero1.close();
		tmp.close();
		bf.close();

		Global.refreshOriginalFile(direccion, nomFich1);
	}

	private static void pintaFichero(String direccion) throws IOException{
		System.out.println("\n---------------------");
		BufferedReader bf = new BufferedReader(new FileReader(direccion));
		String linea = null;
		linea = bf.readLine();
		while(linea!=null){
			System.out.println(linea);
			linea = bf.readLine();
		}
		System.out.println("\n---------------------");
		bf.close();
		
	}
	public static void modify_HEAD(String direccion, String javaScript, String modifyIU, String modifyBL)
			throws FileNotFoundException, IOException {
		if(!direccion.equals(modifyIU))
			modifyIU = glueweb.util.FTP.getPath(modifyIU);
		if(!javaScript.equals(modifyBL))
			modifyBL = glueweb.util.FTP.getPath(modifyBL);
		
		if (!Global.inHead(direccion, Global.getRelativePath(
				new File(modifyIU), new File(modifyBL)))) {
			String nomFich1 = ResourcesPlugin.getWorkspace().getRoot()
					.getLocation().toString()
					+ "/temp";


			BufferedReader bf = new BufferedReader(new FileReader(direccion));
			String linea = null;

			FileWriter fichero1 = new FileWriter(nomFich1);
			PrintWriter tmp = new PrintWriter(fichero1);

			int i = 0;
			linea = bf.readLine();
			tmp.write(linea);
			tmp.write("\n");

			while (!linea.contains("</html>")) {
				if (linea.contains("<head>")) {
					tmp
							.write("<script language=\"Javascript\" type=\"text/javascript\" src=\""
									+ Global.getRelativePath(
											new File(modifyIU), new File(
													modifyBL))
									+ "\"></script>");
					tmp.write("\n");

				}
				linea = bf.readLine();
				tmp.write(linea);
				tmp.write("\n");
			}

			bf.close();
			tmp.close();

			Copiar cp = new Copiar();
			cp.copy(new File(nomFich1), new File(direccion));
			File fichero = new File(nomFich1);
			fichero.delete();
		}
	}

	public static void modify_BD_MySQL(String direccion, Info BD)
			throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(direccion));
		String linea = null;

		String nomFich1 = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/temp";
		FileWriter fichero1 = new FileWriter(nomFich1);

		String conexion = BD.getWebServerLocation();
		StringTokenizer tk = new StringTokenizer(conexion, "/");
		String host = tk.nextToken();
		String bd = tk.nextToken();

		linea = bf.readLine();
		while (linea != null) {
			if (linea.contains("mysql_connect")) {
				StringTokenizer st = new StringTokenizer(linea, "(");
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (!token.contains("mysql_connect")) {
						fichero1.write(token);
						if (st.countTokens() > 0)
							fichero1.write("(");

					} else {
						fichero1.write(token);
						fichero1.write("(\"" + host + "\",\"" + BD.getUser()
								+ "\",\"" + BD.getPass() + "\"");
					}

				}
				fichero1.write("\n");
			} else if (linea.contains("mysql_select_db")) {
				StringTokenizer st = new StringTokenizer(linea, "(");
				while (st.hasMoreTokens()) {
					String token = st.nextToken();

					if (!token.contains("mysql_select_db")) {
						fichero1.write(token);
						if (st.countTokens() > 0)
							fichero1.write("(");

					} else {
						fichero1.write(token);
						fichero1.write("(\"" + bd + "\",");
					}

				}
				fichero1.write("\n");
			} else {
				fichero1.write(linea);
				fichero1.write("\n");
			}
			linea = bf.readLine();
		}
		fichero1.close();
		bf.close();

	}
}
