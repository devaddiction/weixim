package glueweb.ocl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * parser an OCL file
 */
public class ParserOCL {
	private Map<String,String> temp; 
	private Map<String,Method> correspondences;
	private ArrayList<String> reservedWords;
	private String context, body, pre, post, inv;
	private boolean contextBefore = false;

	
	public ParserOCL(){
		correspondences = new HashMap<String,Method>();
		reservedWords = new ArrayList<String>();
		initReservedWordsArray();
		temp = new HashMap<String,String>();
		initTemp();
		context=null;
		body=null;
		pre=""; post=""; inv="";
	}
	
	/**
	 * @param fileName
	 */
	public void loadOclFile(String fileName){
		String reserved="";
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String buffer;
			buffer = br.readLine();
			while(buffer != null){
				StringTokenizer st = new StringTokenizer(buffer," \t:.(,)[]{}",true); //token and delimiters
				while(st.hasMoreTokens()){
					String token = st.nextToken();
					if(reservedWords.contains(token)){ //if reserved
						reserved = token;
						saveReserved(token);
					}
					else{
						addToReserved(reserved,token); //if not reserved
					}
				}
				buffer = br.readLine();
			}
			saveTemp();
		} catch (FileNotFoundException e) {
			System.out.println("\t-OCL ERROR- File not found");
		} catch (IOException e) {
			System.out.println("\t-OCL ERROR- I/O error");
		}
	}
	
	/**
	 * @return associations list with correspondences
	 */
	public ArrayList<String> associationsNameList(){
		ArrayList<String> res = new ArrayList<String>();
		
		for(String str : correspondences.keySet())
			res.add(str);
		return res;
	}
	
	public ArrayList<String> associationsFunctionList() throws IOException{
		ArrayList<String> res = new ArrayList<String>();
		
		for(String str : correspondences.keySet()){
			StringTokenizer st = new StringTokenizer(correspondences.get(str).getMethodContext()," \t.(,)[]{}",true); //token and delimiters
			
			while(st.hasMoreTokens()){
				String token = st.nextToken();			
				if(token.contains("::")){
					token = st.nextToken();
					if(token.contains(" ")){
						token = st.nextToken();						
						res.add(token);
					}
				}
			}
		}
		return res;
	}
	
	
	
	public ArrayList<String> associationsDependencesList() throws IOException{
		ArrayList<String> res = new ArrayList<String>();
		
		for(String str : correspondences.keySet()){
			StringTokenizer st = new StringTokenizer(correspondences.get(str).getMethodBody()," \t.(,)[]{}",true); //token and delimiters

			while(st.hasMoreTokens()){
				String token = st.nextToken();
				
				if(token.contains("body:")){
					token = st.nextToken();
					if(token.contains(" ")){
						token = st.nextToken();	
						res.add(token);
					}
				}
			}
		}
		return res;
	}
	
	
	/**
	 * save a reserved word and if other context begins, saves previous and 
	 * initializes next
	 * @param reserved word
	 */
	public void saveReserved(String res){
		if(res.equalsIgnoreCase("context")){//if context
			if(contextBefore){
				saveTemp();
				temp.clear();
				initTemp();
			}
			else{
				setContextBefore(true);
			}
		}//in any case, saves the reserved word
		temp.put(res, res);
	}
	
	/**
	 * modify previous context
	 * @param bool
	 */
	public void setContextBefore(boolean bool){
		contextBefore = bool;
	}
	
	/**
	 * save the temporal correspondence in correspondences
	 */
	public void saveTemp(){
		for(String str : temp.keySet()){
			if(str.equalsIgnoreCase("context")){
				context = temp.get(str);
			}else if(str.equalsIgnoreCase("body"))
				body = temp.get(str);
			else if(str.equalsIgnoreCase("pre"))
				pre = temp.get(str);
			else if(str.equalsIgnoreCase("post"))
				post = temp.get(str);
			else 
				inv = temp.get(str);
		}
		correspondences.put(getContextName(context), new Method(context,body,pre,post,inv));
	}
	
	/**
	 * @param context with parameters
	 * @return get association name that is the context name without parameters
	 */
	public String getContextName(String context){
		StringTokenizer st = new StringTokenizer(context," \t:.(,)[]{}");
		st.nextToken();
		return st.nextToken();
	}
	
	
	/**
	 * add token to a reserved word
	 * @param reserved word
	 * @param token
	 */
	public void addToReserved(String res, String token){
		temp.put(res, temp.get(res).concat(token));
	}
	
	/**
	 * @return reserved words array
	 */
	public ArrayList<String> getReservedWords(){
		return reservedWords;
	}
	
	/**
	 * initialize the temporal map structure
	 */
	public void initTemp(){
		Iterator<String> ite = reservedWords.iterator();
		
		while(ite.hasNext()){
			temp.put(ite.next(), "");
		}
	}
		
	/**
	 * initialize reserved words array
	 */
	public void initReservedWordsArray(){
		reservedWords.add("context");
		reservedWords.add("body");
		reservedWords.add("pre");
		reservedWords.add("post");
		reservedWords.add("inv");
	}

	/* devuelve palabra reservada que contiene o null en otro caso */
	/**
	 * @param str string 
	 * @return null or the reserved word that contains this string
	 */
	public String containsReservedWord(String str){
		String res = null;
		String temp= null;
		
		Iterator<String> ite = getReservedWords().iterator();
		while(ite.hasNext()){
			temp = ite.next();
			if(str.contains(temp))
				res = temp;
		}
		return res;
	}
	

	/**
	 * print correspondences
	 */
	public void correspondencesView(){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		for(String str : correspondences.keySet()){
			pw.println(correspondences.get(str).toString());
		}
	}
	
	/**
	 * verify correspondences
	 */
	public void correspondencesVerify(){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		for(String str : correspondences.keySet()){
			if(correspondences.get(str).parametersOk())
				pw.println("["+str+"] OK!");
			else
				pw.println("["+str+"] fail!");
		}
		System.out.println(sw.getBuffer().toString());
	}
	
	/**
	 * @return correspondences structure
	 */
	public Map<String,Method> getCorrespondences(){
		return correspondences;
	}
}
