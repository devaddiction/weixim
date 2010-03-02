package glueweb.axim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

public class Utils {

	/**
	 * Copia un directorio con todo y su contendido
	 * 
	 * @param srcDir
	 * @param dstDir
	 * @throws IOException
	 */
	public static void copyDirectory(File srcDir, File dstDir)
			throws IOException {
		if (srcDir.isDirectory()) {
			if (!dstDir.exists()) {
				dstDir.mkdir();
			}

			String[] children = srcDir.list();
			for (int i = 0; i < children.length; i++) {
				// los ficheros acabados en .db los ignoramos. Son los clasicos
				// Thumbs.db
				if (!children[i].endsWith(".db")) {
					// System.out.println(children[i]);
					copyDirectory(new File(srcDir, children[i]), new File(
							dstDir, children[i]));
				}
			}
		} else {
			copy(srcDir, dstDir);
		}
	}

	/**
	 * Copia un solo archivo
	 * 
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}


	/*
	 * public static void main(String arg[]) { Utils cp = new Utils(); try {
	 * System.out.println(System.getProperty("user.dir")); cp .copyDirectory(new
	 * File(".\\javascript"), new File( "C:\\ABREGO2"));
	 * 
	 * System.out.print("Copiado con exito"); } catch (Exception e) {
	 * System.out.println(e); } }
	 */

	/*
	 * public static void main(String[] args) {
	 * System.out.println(WorkingDirectory.get());
	 * System.out.println(System.getProperty("user.dir"));
	 * 
	 * String cad="&quot; Hola mundo &quot;"; System.out.println(cad); //if
	 * (cad.matches("^\".*\"")) System.out.println("Encaja"); if
	 * (cad.matches("^&quot;.*&quot;"))
	 * System.out.println("Encaja,quitando comillas="
	 * +cad.substring(1,cad.length()));
	 * 
	 * else System.out.println("No encaja"); }
	 */

}

class WorkingDirectory {

	private static File WORKING_DIRECTORY;

	public static File get() {
		if (WORKING_DIRECTORY == null) {
			try {
				URL url = WorkingDirectory.class.getResource("ejemplo.txt");
				// System.out.println(url);
				if (url.getProtocol().equals("file")) {
					File f = new File(url.toURI());
					f = f.getParentFile().getParentFile().getParentFile();
					WORKING_DIRECTORY = f;
				} else if (url.getProtocol().equals("jar")) {
					String expected = "!/util/ejemplo.txt";
					String s = url.toString();
					s = s.substring(4);
					s = s.substring(0, s.length() - expected.length());
					File f = new File(new URL(s).toURI());
					f = f.getParentFile();
					WORKING_DIRECTORY = f;
				}
			} catch (Exception e) {
				WORKING_DIRECTORY = new File(".");
			}
		}
		return WORKING_DIRECTORY.getParentFile();
	}

	public static String getDirectorioProyecto() {
		return System.getProperty("user.dir");
	}

}
