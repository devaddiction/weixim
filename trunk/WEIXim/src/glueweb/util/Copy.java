package glueweb.util;

import java.io.*;
import java.util.zip.*;

/**
 * create a tree folder with subfolders and files mirror
 * from source to target
 */
public class Copy{
    String source = "";
    String target = "";
	int numFiles = 0;
    
	/**
	 * @param source
	 * @param target
	 */
	public Copy(final String source, final String target){
		this.source = source;
		this.target = target;

        try {
			copyDirectory(new File(source),new File(target));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * copy directory
	 * @param srcDir
	 * @param dstDir
	 * @throws IOException
	 */
	public void copyDirectory(File srcDir, File dstDir) throws IOException {
        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdir();
            }
            
            String[] children = srcDir.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(srcDir, children[i]),new File(dstDir, children[i]));
            }
        } else {
            copy(srcDir, dstDir);
        }
    }
	
    /**
     * copy file
     * @param src
     * @param dst
     * @throws IOException
     */
    public void copy(File src, File dst) throws IOException {
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
    
    
    
    
	private final static int BUFFER_SIZE = 4096;

	@SuppressWarnings("deprecation")
	public static void unZipFileToDirectory(String sFileToUnzip,
			String sDirUnzip) throws IOException {
		unZipURLToDirectory(new File(sFileToUnzip).toURL(), sDirUnzip);
	}

	public static void unZipURLToDirectory(java.net.URL url, String sDirUnzip)
			throws IOException {
		FileOutputStream fw = null;

		File dir = new File(sDirUnzip);

		// if the unzip to dir does not exist create it.
		if (!dir.exists()) {
			dir.mkdir();
		}

		java.net.URLConnection connection = url.openConnection();
		ZipInputStream input = new ZipInputStream(connection.getInputStream());

		// while there are more entries, unzip them
		ZipEntry zipEntry = input.getNextEntry();
		while (zipEntry != null) {
			File file = new File(sDirUnzip + "/" + zipEntry.toString());

			// if the zip entry is a dir, make a new one.
			if (zipEntry.isDirectory()) {
				file.mkdir();
			}

			// else it is a file, unzip it.
			else {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				while (true) {
					byte[] bytes = new byte[4096];
					int read = input.read(bytes);
					if (read == -1) {
						break;
					}
					bout.write(bytes, 0, read);
				}
				CheckedInputStream cis = new CheckedInputStream(
						new ByteArrayInputStream(bout.toByteArray()),
						new CRC32());

				fw = new FileOutputStream(file);

				while (true) {
					byte[] bytes = new byte[4096];
					int read = cis.read(bytes);
					if (read < 0) {
						break;
					}
					fw.write(bytes, 0, read);
				}
				fw.close();
			}
			zipEntry = input.getNextEntry();
		}
		input.close();
	}

	public static void zipDirectory(String directorio, String output)
			throws IOException {
		File directory = new File(directorio);
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output));
		zipDirectory(directory, directory.getName(), zos);
		zos.close();
	}

	private static void zipDirectory(File directory, String name,
			ZipOutputStream zos) throws IOException {
		name += "/";

		zos.putNextEntry(new ZipEntry(name));
		zos.closeEntry();

		String[] entryList = directory.list();

		for (int i = 0; i < entryList.length; ++i) {
			File f = new File(directory, entryList[i]);

			if (f.isDirectory()) {
				zipDirectory(f, name + f.getName(), zos);
			} else {
				FileInputStream fis = new FileInputStream(f);
				ZipEntry entry = new ZipEntry(name + f.getName());
				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesIn = 0;

				zos.putNextEntry(entry);

				while ((bytesIn = fis.read(buffer)) != -1) {
					zos.write(buffer, 0, bytesIn);
				}

				fis.close();
				zos.closeEntry();
			}
		}
	}

} 