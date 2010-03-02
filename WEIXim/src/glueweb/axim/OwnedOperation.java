package glueweb.axim;

import java.util.ArrayList;
import java.util.List;

public class OwnedOperation {
	private String idOwMember; 
	private String xmiType;
	private String id;
	private String name;
	private List<OwnedParameter> parametros;
	
	
	public OwnedOperation() {
		this.parametros=new ArrayList<OwnedParameter>();
	}
	
	
	public OwnedOperation(String idOwMember, String xmiType, String id, String name) {
		this.idOwMember=idOwMember;
		this.xmiType=xmiType;
		this.id=id;
		this.name=name;
		this.parametros=new ArrayList<OwnedParameter>();
	}
	
	public String toString() {
		return "<ownedOperation>\n\t xmiType: "+xmiType+"\n\t id: "+id+"\n\t name: "+name+
		"\n\t idOwMember: "+idOwMember+"\n\t\t parametros: "+this.imprimeParametros()+"\n";
	}
	
	private String imprimeParametros() {
		String res="";
		for (OwnedParameter o:parametros){
			res=res+"\t"+o.toString()+"\n";
		}
		return res;
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
	public List<OwnedParameter> getParametros() {
		return parametros;
	}
	public void setParametros(List<OwnedParameter> parametros) {
		this.parametros = parametros;
	}
	
	
	

}
