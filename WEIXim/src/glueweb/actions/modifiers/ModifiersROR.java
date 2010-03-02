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
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.core.resources.ResourcesPlugin;

public class ModifiersROR {

	public static void modifyBD(String direccion, Info BD,
			String conector, String pool) throws IOException {

		String direFinal = direccion.substring(0, direccion.indexOf("Controllers"));
		direFinal+="Configuration/database.yml";
		
		BufferedReader bf = new BufferedReader(new FileReader(direFinal));
		String linea = null;

		String nomFich1 = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/temp";
		FileWriter fichero1 = new FileWriter(nomFich1);
		PrintWriter tmp = new PrintWriter(fichero1);

		String conexion = BD.getWebServerLocation();
		StringTokenizer tk = new StringTokenizer(conexion, "/");
		String host = tk.nextToken();
		String bd = tk.nextToken();

		linea = bf.readLine();
		while (linea != null) {
			if (linea.contains("adapter:") || linea.contains("driver:")
					|| linea.contains("url:") || linea.contains("username:")
					|| linea.contains("password:")) {
				tmp.print(linea);
				tmp.print(" ");
				if (linea.contains("adapter:"))
					tmp.print("jdbc");
				else if (linea.contains("driver:"))
					tmp.print(conector);
				else if (linea.contains("url:"))
					tmp.print(pool + "://" + host + ":" + BD.getWebServerPort()
							+ "/" + bd);
				else if (linea.contains("username:"))
					tmp.print(BD.getWebServerUser());
				else if (linea.contains("password:"))
					tmp.print(BD.getWebServerPass());
				tmp.print("\n");
			} else
				tmp.println(linea);
			linea = bf.readLine();
		}
		fichero1.close();
		bf.close();

		glueweb.util.Copy copia = new glueweb.util.Copy(nomFich1, direccion);
		File eliminar = new File(nomFich1);
		eliminar.delete();
	}

	public static void modifyBL(String direccion, String index)
			throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(direccion));
		String linea = null;

		String nombre = (new File(direccion).getName()).toString();
		int pos = nombre.indexOf('_');
		nombre = nombre.substring(0, pos).concat(
				nombre.substring(pos + 1, nombre.length() - 3));

		String nomFich1 = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/temp";
		FileWriter fichero1 = new FileWriter(nomFich1);
		PrintWriter tmp = new PrintWriter(fichero1);

		linea = bf.readLine();
		System.out.println(nombre);
		while (linea != null) {
			if (linea.toUpperCase().contains(nombre.toUpperCase())) {
				tmp.println(linea);
				tmp.println("  view :list, \"" + index + "\"");
				tmp.println("  def modelo");
				tmp.println("    Post");
				tmp.println("  end ");
				tmp.print("\n");
			} else
				tmp.println(linea);
			linea = bf.readLine();
		}
		fichero1.close();
		bf.close();
		
		glueweb.util.Copiar copiar = new glueweb.util.Copiar();
		File src = new File(nomFich1);
		File dst = new File(direccion);
		copiar.copy(src, dst);
	}
}
