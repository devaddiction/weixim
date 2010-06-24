package glueweb.util;

import glueweb.xmi.Info;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.net.ftp.FTPClient;


public class FTP {

	private static String getHost(String server, ArrayList<String> path) {
		String host = null;
		boolean firstIteration = false;

		StringTokenizer st = new StringTokenizer(server, "/");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (!firstIteration) {
				host = token.toString();
				firstIteration = true;
			} else {
				path.add(token.toString());
			}
		}
		return host;
	}
	
	public static String getPath(String server) {
		ArrayList<String> remotePath = new ArrayList<String>();
		boolean resultado = false;
		String host = getHost(server, remotePath);
		String remotePathString = "";
		for (int i = 0; i < remotePath.size(); i++) {
			remotePathString = remotePathString + "/" + remotePath.get(i);
		}
		return remotePathString;
	}

	public static boolean connectionFTP(Info infoFile)
			throws NumberFormatException, SocketException, IOException {
		ArrayList<String> remotePath = new ArrayList<String>();
		boolean resultado = false;
		String host = getHost(infoFile.getLocation(), remotePath);
		String remotePathString = "";
		for (int i = 0; i < remotePath.size(); i++) {
			remotePathString = remotePathString + "/" + remotePath.get(i);
		}

		FTPClient ftp = new FTPClient();
		ftp.connect(host, Integer.parseInt(infoFile.getPort()));

		return ftp.login(infoFile.getUser().toString(), infoFile.getPass()
				.toString());

	}

	public static void downloadFileByFTP(Info infoFile, String target,
			String file) throws NumberFormatException, SocketException,
			IOException {
		ArrayList<String> remotePath = new ArrayList<String>();

		String host = getHost(infoFile.getLocation(), remotePath);
		String remotePathString = "";
		for (int i = 0; i < remotePath.size(); i++) {
			remotePathString = remotePathString + "/" + remotePath.get(i);
		}
		
		
		FTPClient ftp = new FTPClient();
		ftp.connect(host, Integer.parseInt(infoFile.getPort()));
		ftp.login(infoFile.getUser().toString(), infoFile.getPass().toString());
		ftp.enterRemotePassiveMode();
		ftp.enterLocalPassiveMode();
		for (int i = 0; i < remotePath.size(); i++) {
			ftp.cwd(remotePath.get(i));
		}		    
	    OutputStream oS = new FileOutputStream(file); 
	    ftp.retrieveFile(target, oS);
	    
		ftp.disconnect();
	}

	public static void uploadFileByFTP(Info infoFile, String source)
			throws NumberFormatException, SocketException, IOException {
		ArrayList<String> remotePath = new ArrayList<String>();
		String host = getHost(infoFile.getWebServerLocation(), remotePath);
		String remotePathString = "";
		for (int i = 0; i < remotePath.size(); i++) {
			remotePathString = remotePathString + "/" + remotePath.get(i);
		}

		FTPClient ftp = new FTPClient();

		ftp.connect(host, Integer.parseInt(infoFile.getPort()));
		ftp.login(infoFile.getUser().toString(), infoFile.getPass().toString());
		ftp.enterRemotePassiveMode();
		ftp.enterLocalPassiveMode();
		for (int i = 0; i < remotePath.size(); i++) {
			ftp.cwd(remotePath.get(i));
		}
	    File f = new File(source);

        FileInputStream fis = new FileInputStream(f); 
	    ftp.storeFile(new File(source).getName(), fis);
	    
		ftp.disconnect();
	}
}
