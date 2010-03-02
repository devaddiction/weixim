package glueweb.actions.modifiers;

import glueweb.xmi.Info;
import java.io.BufferedReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import org.eclipse.core.resources.ResourcesPlugin;

public class ModifiersJava {

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
					tmp.println("<form method=" + actionPost + " action=\""
							+ action + "\">");
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
	
	public static void modify_BD_MySQL(String direccion, Info BD, String conector, String pool)
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
			if (linea.contains("DriverManager.getConnection")) {
				StringTokenizer st = new StringTokenizer(linea, "(");
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (!token.contains("DriverManager.getConnection")) {
						fichero1.write(token);
						if (st.countTokens() > 0)
							fichero1.write("(");

					} else {
						fichero1.write(token);
						fichero1.write("(\"" + pool + "://" + host + ":" + BD.getPort() + "/" + bd + "\",\""
								+ BD.getUser() + "\",\"" + BD.getPass() + "\"");
					}

				}
				fichero1.write("\n");
			} else if (linea.contains("Class.forName")) {
				StringTokenizer st = new StringTokenizer(linea, "(");
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (!token.contains("Class.forName")) {
						fichero1.write(token);
						if (st.countTokens() > 0)
							fichero1.write("(");

					} else {
						fichero1.write(token);
						fichero1.write("(\"" + conector + "\",");
					}

				}
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
