package glueweb.ocl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;


public class Method {
	private String context;
	private String body;
	private String pre;
	private String post;
	private String inv;
	
	/**
	 * @param context: line of context
	 * @param body: line of body
	 * @param pre: line of preconditions
	 * @param post: line of postconditions
	 * @param inv: line of invariants
	 */
	public Method(String context, String body, String pre, String post, String inv){
		this.context = context;
		this.body = body;
		this.pre = pre;
		this.post = post;
		this.inv = inv;
	}
	
	/**
	 * @return context
	 */
	public String getMethodContext(){
		return context;
	}
	
	/**
	 * @return body
	 */
	public String getMethodBody(){
		return body;
	}
	
	/**
	 * @return preconditions
	 */
	public String getMethodPre(){
		return pre;
	}
	
	/**
	 * @return postconditions
	 */
	public String getMethodPost(){
		return post;
	}
	
	/**
	 * @return invariants
	 */
	public String getMethodInv(){
		return inv;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return getMethodContext()+"\n"+
			getMethodBody()+"\n"+
			getMethodPre()+"\n"+
			getMethodPost()+"\n"+
			getMethodInv();
	}
	

	/**
	 * @param line: context or body
	 * @return ArrayList with parameters
	 */
	public ArrayList<String> listOfParameters(String line){
		ArrayList<String> res = new ArrayList<String>();
		
		ArrayList<String> temp = mySplit(line,true);
		Iterator<String> ite = temp.iterator();
		while(ite.hasNext()){
			String token = ite.next();
			if(token.equalsIgnoreCase("(") || token.equalsIgnoreCase(",")){
				res.add(ite.next());
			}
		}
		
		return res;
	}
	

	/**
	 * @param str: String to split in tokens
	 * @param delim: true->include delimiters
	 * 				 false->not include delimiters
	 * @return ArrayList with str splitted in tokens
	 */
	private ArrayList<String> mySplit(String str, boolean delim) {
		ArrayList<String> res = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(str," \t:.(,)[]{}",delim);
		while(st.hasMoreTokens()){
			res.add(st.nextToken());
		}
		return res;
	}
	

	/**
	 * @return true->numbers and name of parameters are OK
	 * 		   false->parameters do not agree 
	 */
	public boolean parametersOk(){
		ArrayList<String> contextArray = listOfParameters(context);
		ArrayList<String> bodyArray = listOfParameters(body);
		boolean res = true;
		
		if(contextArray.size() != bodyArray.size())
			res = false;
		else{
			Iterator<String> iter = contextArray.iterator();
			while(iter.hasNext()){
				if(!bodyArray.contains(iter.next()))
					res = false;
			}	
		}
		
		return res;
	}
}
