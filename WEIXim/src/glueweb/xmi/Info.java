package glueweb.xmi;

/**
 * represent information about UML diagrams
 */
public class Info {
	private String viewpoint;
	private String viewpointLanguage;
	private String technology;
	private String location;
	private String port;
	private String user;
	private String pass;
	private String webServerPort;
	private String webServerLocation;
	private String webServerUser;
	private String webServerPass;
	private Boolean isLocal;
	
	public Info(){
		this.isLocal = false;
		this.location = "";
		this.port = "";
		this.user = "";
		this.pass = "";
		this.technology = "";
		this.viewpoint = "";
		this.viewpointLanguage = "";
		this.webServerLocation = "";
		this.webServerPort = "";
		this.webServerUser = "";
		this.webServerPass = "";
	}
	
	public void setLocation(String str){
		this.location = str;
	}
	
	public void setPort(String str){
		this.port = str;
	}
		
	public void setUser(String str){
		this.user = str;
	}
	
	public void setPass(String str){
		this.pass = str;
	}
	
	public void setTechnology(String str){
		this.technology = str;
	}
	
	public void setViewpoint(String str){
		this.viewpoint = str;
	}
	
	public void setViewpointLanguage(String str){
		this.viewpointLanguage = str;
	}
	
	public void setWebServerLocation(String str){
		this.webServerLocation = str;
	}
	
	public void setWebServerPort(String str){
		this.webServerPort = str;
	}
	
	public void setWebServerUser(String str){
		this.webServerUser = str;
	}
	
	public void setWebServerPass(String str){
		this.webServerPass = str;
	}
	
	public void setLocal(){
		this.isLocal = true;
	}
	
	public void setUrl(){
		this.isLocal = false;
	}
	
	public String getLocation(){
		return location;
	}
	
	public String getPort(){
		return port;
	}
	
	public String getUser(){
		return user;
	}
	
	public String getPass(){
		return pass;
	}	
	
	public String getViewpoint(){
		return viewpoint;
	}
	
	public String getViewpointLanguage(){
		return viewpointLanguage;
	}
	
	public String getTechnology(){
		return technology;
	}
	
	public String getWebServerLocation(){
		return webServerLocation;
	}
	
	public String getWebServerPort(){
		return webServerPort;
	}
	
	public String getWebServerUser(){
		return webServerUser;
	}	
	
	public String getWebServerPass(){
		return webServerPass;
	}
	
	//true: is local, false: is URL
	public Boolean isLocal(){
		return isLocal;
	}
	
	public String toString(){
		return viewpoint+":"+
				viewpointLanguage+":"+
				technology+":"+
				location+":"+
				webServerPort+":"+
				webServerLocation+":"+
				isLocal;
	}
}
