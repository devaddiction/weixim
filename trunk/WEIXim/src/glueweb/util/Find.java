package glueweb.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Find {
	public static List<File> findFile(String fileName)
	{
	  List<File> foundFiles = new ArrayList<File>();
	  for(File file:File.listRoots())
	  {
	     findFile(fileName,file,foundFiles);
	  }	  return foundFiles;
	}

	private static void findFile(String fileName,File directory, List<? super File> list)
	{
	  for(File file:directory.listFiles())
	  {
	     if(file.getName().equals(fileName))
	        list.add(file);
	     if(file.isDirectory())
	        findFile(fileName,file,list);
	  }
	} 
}
