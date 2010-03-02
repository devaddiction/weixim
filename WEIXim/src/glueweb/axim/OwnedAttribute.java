package glueweb.axim;


public class OwnedAttribute {
	private String idOwMember; //id del OwnedMember del que es atributo este OwnedAttribute
	private String xmiType;
	private String id;
	private String name;
	private String visibility;
	private String type; //si el elemento es un objeto de una clase, type referenciara al id del OwnedMember de dicha clase
	private String defaultValue;
	
	
	public String getDefaultValue() {
		return defaultValue;
	}


	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}


	public OwnedAttribute(String idOwMember,String xmiType,String id,String name,String visibility, String type){
		this.idOwMember=idOwMember;
		this.xmiType=xmiType;
		this.id=id;
		this.name=name;
		this.visibility=visibility;
		this.type=type;
	}

	
	public String toString() {
		return "<ownedAttribute>\n\t xmiType: "+xmiType+"\n\t id: "+id+"\n\t name: "+name+"\n\t defaultValue: "+defaultValue+
		"\n\t idOwMember: "+idOwMember+"\n\t type: "+type+"\n";
	}
	
	public String getIdOwMember() {
		return idOwMember;
	}


	public void setIdOwMember(String idOwMember) {
		this.idOwMember = idOwMember;
	}


	public String getXmiType() {
		return xmiType;
	}


	public void setXmiType(String xmiType) {
		this.xmiType = xmiType;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getVisibility() {
		return visibility;
	}


	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


}