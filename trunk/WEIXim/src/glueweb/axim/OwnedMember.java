package glueweb.axim;

import java.util.List;
import java.util.ArrayList;

/**Habra un OwnedMember en el fichero .xmi por cada elemento definido en MagicDraw en la columna de la izquierda
 * */
public class OwnedMember {
	private String xmiType;
	private String id;
	private String name;
	private String visibility;
	private List<OwnedAttribute> listOwnedAttributes;
	private List<OwnedOperation> listOwnedOperations;
	private List<String> listIdsMemberEnds;
	private boolean isAssociationClass=false;
	
	public OwnedMember(){
		this.listOwnedAttributes=new ArrayList<OwnedAttribute>();
		this.listOwnedOperations=new ArrayList<OwnedOperation>();
		this.listIdsMemberEnds=new ArrayList<String>();
	}
	
	public OwnedMember(String xmiType,String id,String name,String visibility) {
		this.xmiType=xmiType;
		this.id=id;
		this.name=name;
		this.visibility=visibility;
		this.listOwnedAttributes=new ArrayList<OwnedAttribute>();
		this.listOwnedOperations=new ArrayList<OwnedOperation>();
		this.listIdsMemberEnds=new ArrayList<String>();
	}
	
	public String toString() {
		return "ownedMember:\n\t xmiType: "+xmiType+"\n\t id: "+id+"\n\t name: "+name+"\n\t ¿asociacion?:"+isAssociationClass+
		"\n\t [listOwnedAttrib]:\n\t "+this.imprimelistOwnedAttributes()+"\n"+
		"\n\t [listOwnedOperat]:\n\t "+this.imprimelistOwnedOperations()+"\n"+
		"\n\t [listMemberEnds]:\n\t "+listIdsMemberEnds+"\n";
	}

	private String imprimelistOwnedOperations() {
		String res="";
		for (OwnedOperation o:listOwnedOperations){
			res=res+"\t"+o.toString()+"\n";
		}
		return res;
	}

	public String imprimelistOwnedAttributes(){
		String res="";
		for (OwnedAttribute o:listOwnedAttributes){
			res=res+"\t"+o.toString()+"\n";
		}
		return res;
	}
	
	public String imprimelistMemberEnd(){
		String res="";
		for (String st :listIdsMemberEnds){
			res=res+","+st+"\n";
		}
		return res;
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

	public List<OwnedAttribute> getListOwnedAttributes() {
		return listOwnedAttributes;
	}

	public void setListOwnedAttributes(List<OwnedAttribute> listOwnedAttributes) {
		this.listOwnedAttributes = listOwnedAttributes;
	}

	public List<String> getListIdsMemberEnds() {
		return listIdsMemberEnds;
	}

	public void setListIdsMemberEnds(List<String> listIdsMemberEnds) {
		this.listIdsMemberEnds = listIdsMemberEnds;
	}

	public boolean isAssociationClass() {
		return isAssociationClass;
	}

	public void setAssociationClass(boolean isAssociationClass) {
		this.isAssociationClass = isAssociationClass;
	}

	public List<OwnedOperation> getListOwnedOperations() {
		return listOwnedOperations;
	}

	public void setListOwnedOperations(List<OwnedOperation> listOwnedOperations) {
		this.listOwnedOperations = listOwnedOperations;
	}


	
}