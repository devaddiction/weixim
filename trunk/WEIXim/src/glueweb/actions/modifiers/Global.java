package glueweb.actions.modifiers;

import glueweb.util.Copiar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Global {

	public static boolean inForm(String direccion, String event)
			throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(direccion));

		String linea = null;
		while ((linea = bf.readLine()) != null) {
			if (linea.contains("<FORM>".toUpperCase())) {
				linea = bf.readLine();
				while (linea != null && !linea.contains("</FORM>".toUpperCase())) {
					linea = bf.readLine();
					if (linea.toUpperCase().contains(event.toUpperCase()))
						return true;
				}
			}
		}
		return false;
	}

	public static boolean inHead(String direccion, String event)
			throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(direccion));
		boolean resultado = false;
		String linea = null;
		while ((linea = bf.readLine()) != null) {
			if (linea.contains("<head>")) {
				while (!(linea = bf.readLine()).contains("</head>")) {
					if (linea.contains(event))
						resultado = true;
				}
			}
		}
		return resultado;
	}

	public static String isActionOrPost(String direccion) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(direccion));
		String buffer;

		buffer = br.readLine();
		while (buffer != null) {
			if (buffer.contains("$_GET") || buffer.contains("getParameter"))
				return "GET";
			buffer = br.readLine();
		}
		return "POST";
	}

	public static String printToElement(String line, PrintWriter tmp,
			String separator) {
		String result = "";
		StringTokenizer st = new StringTokenizer(line, " \n\t\r");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (!token.contains(separator)) {
				result = result + " " + token.toString();
			}
		}
		return result;
	}

	public static void clearContent(String filename) throws IOException {
		File fichero = new File(filename);
		fichero.delete();

		FileWriter crearNuevo = new FileWriter(filename);
		PrintWriter aux = new PrintWriter(crearNuevo);
		aux.close();
	}

	static void copyFileStringPW(String original, PrintWriter copy)
			throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(original));
		String linea = null;
		while ((linea = bf.readLine()) != null) {
			copy.println(linea);
		}
		bf.close();
		clearContent(original);
	}

	public static void refreshOriginalFile(String original, String copy,
			String aux) throws IOException {
		Copiar cp = new Copiar();
		cp.copy(new File(original), new File(aux));

		FileWriter fichero = new FileWriter(original);
		PrintWriter pw = new PrintWriter(fichero);
		BufferedReader bf1 = new BufferedReader(new FileReader(copy));
		BufferedReader bf2 = new BufferedReader(new FileReader(aux));

		String linea1 = bf1.readLine();
		String linea2 = null;

		while (linea1.equals(linea2)) {
			pw.println(linea1);
			linea1 = bf1.readLine();
			linea2 = bf2.readLine();
		}
		while (linea1 != null) {
			pw.println(linea1);
			linea1 = bf1.readLine();
			linea2 = bf2.readLine();
		}
		while (linea2 != null) {
			pw.println(linea2);
			linea2 = bf2.readLine();
		}

		bf1.close();
		bf2.close();
		pw.close();
	}

	public static void refreshOriginalFile(String original, String copy) throws IOException {
		Global.clearContent(original);
		
		FileWriter fichero = new FileWriter(original);
		PrintWriter pw = new PrintWriter(fichero);
		BufferedReader bf1 = new BufferedReader(new FileReader(copy));

		String linea1 = bf1.readLine();

		while (linea1 != null) {
			pw.println(linea1);
			linea1 = bf1.readLine();
		}

		bf1.close();
		pw.close();
		
		Global.clearContent(copy);
	}
	
	public static void fillContent(PrintWriter tmp, String line) {
		StringTokenizer st = new StringTokenizer(line);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (!token.contains("/>")) {
				tmp.print(token);
				tmp.print(" ");
			} else {
				String tmp2 = token.replaceAll("/>", "");
				tmp.print(tmp2);
			}
		}
	}

	private static ArrayList<String> getPath(String path) {
		boolean firstIteration = false;
		ArrayList<String> res = null;

		StringTokenizer st = new StringTokenizer(path, "/");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			res.add(token.toString());

		}
		return res;
	}

	private static List getPathList(File f) {
		List l = new ArrayList();
		File r;
		try {
			r = f.getCanonicalFile();
			while (r != null) {
				l.add(r.getName());
				r = r.getParentFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
			l = null;
		}
		return l;
	}

	/**
	 * figure out a string representing the relative path of 'f' with respect to
	 * 'r'
	 * 
	 * @param r
	 *            home path
	 * @param f
	 *            path of file
	 */
	private static String matchPathLists(List r, List f) {
		int i, j;
		String s;

		s = "";
		i = r.size() - 1;
		j = f.size() - 1;

		// first eliminate common root
		while ((i >= 0) && (j >= 0) && (r.get(i).equals(f.get(j)))) {
			i--;
			j--;
		}

		// for each remaining level in the home path, add a ..
		for (; i >= 1; i--) {
			s += ".." + File.separator;
		}

		// for each level in the file path, add the path
		for (; j >= 1; j--) {
			s += f.get(j) + File.separator;
		}

		// file name
		s += f.get(j);
		return s;
	}

	/**
	 * get relative path of File 'f' with respect to 'home' directory example :
	 * home = /a/b/c f = /a/d/e/x.txt s = getRelativePath(home,f) =
	 * ../../d/e/x.txt
	 * 
	 * @param home
	 *            base path, should be a directory, not a file, or it doesn't
	 *            make sense
	 * @param f
	 *            file to generate path for
	 * @return path from home to f as a string
	 */
	public static String getRelativePath(File home, File f) {
		File r;
		List homelist;
		List filelist;
		String s;

		homelist = getPathList(home);
		filelist = getPathList(f);

		s = matchPathLists(homelist, filelist);

		return s;
	}

}
