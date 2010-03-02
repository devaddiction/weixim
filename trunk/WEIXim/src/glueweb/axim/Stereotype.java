package glueweb.axim;

import java.util.List;
import java.util.ArrayList;

public class Stereotype {
	private String id; //el id del stereotype reflejeado en OwnedMember
	private String name; //nombre del estereotipo
	private List<String> listaid;//almacenara los id de los elementos estereotipados con este estereotipo
	
	public Stereotype(String id,String name){
		this.id=id;
		this.name=name;
		this.listaid=new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getListaid() {
		return listaid;
	}

	public void setListaid(List<String> listaid) {
		this.listaid = listaid;
	}
	
	public String toString() {	
		return "stereotype:\n\t name: "+name+"\n\t listaid: "+listaid.toString();	
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
		
}
