package glueweb.axim;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que recoge la información necesaria para un Association Class. Cuando en MagicDraw se crea
 * una asociacion de este tipo entre dos clases, en cada una de esas clases se crea una propiedad nueva 
 * que representan los extremos de la asociación. Cada una de esa propiedades apuntará al elemento del extremo
 * opuesto.(no a la propiedad opuesta, sino a la clase que contiene dicha propiedad opuesta)
 * **/
public class AssociationClass {
	private String id; //el id de la association Class reflejeado en OwnedMember
	private String name; //nombre de la association Class
	private String idOwnedAttributeA; //id del extremo A de la Association class. Dicho id corresponderá al id de un onwedAttribute
	private String idOwnedAttributeB; //id del extremo B de la Association class. Dicho id corresponderá al id de un onwedAttribute
	private String idOwnedMemberA;
	private String idOwnedMemberB;
	private String tipo; //A o B
	
	private String wsdl;
	private String puerto;
	private String metodo;
	private String evento;
	private String origen;
	private List<OwnedOperation> listadoMetodos;
	
	

	public List<OwnedOperation> getListadoMetodos() {
		return listadoMetodos;
	}

	public void setListadoMetodos(List<OwnedOperation> listadoMetodos) {
		this.listadoMetodos = listadoMetodos;
	}

	public String getWsdl() {
		return wsdl;
	}

	public void setWsdl(String wsdl) {
		this.wsdl = wsdl;
	}

	public String getPuerto() {
		return puerto;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}

	public String getMetodo() {
		return metodo;
	}

	public void setMetodo(String metodo) {
		this.metodo = metodo;
	}

	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public AssociationClass(){
		
	}
	
	public AssociationClass(String id,String name, String tipo){
		this.id=id;
		this.name=name;
		this.tipo=tipo;
	}
	
	public AssociationClass(String id,String name, String idOwnedAttributeA,String idOwnedAttributeB, 
						String idOwnedMemberA,String idOwnedMemberB){
		this.id=id;
		this.name=name;
		this.idOwnedAttributeA=idOwnedAttributeA;
		this.idOwnedAttributeB=idOwnedAttributeB;
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
	public String getIdOwnedAttributeA() {
		return idOwnedAttributeA;
	}
	public void setIdOwnedAttributeA(String idOwnedAttributeA) {
		this.idOwnedAttributeA = idOwnedAttributeA;
	}
	public String getIdOwnedAttributeB() {
		return idOwnedAttributeB;
	}
	public void setIdOwnedAttributeB(String idOwnedAttributeB) {
		this.idOwnedAttributeB = idOwnedAttributeB;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	
	@Override
	public String toString() {
		return "AssociationClass:\n\t id: "+id+"\n\t name: "+name+"\n\t tipo: "+tipo+"\n\t idA: "+idOwnedAttributeA+"\n\t idB: "
		+idOwnedAttributeB+"\n\t idOwnedMemberA: "+idOwnedMemberA+"\n\t idOwnedMemberB: "+idOwnedMemberB
		+"\n\t evento: "+evento +"\n\t origen: "+origen;
	}

	public String getIdOwnedMemberA() {
		return idOwnedMemberA;
	}

	public void setIdOwnedMemberA(String idOwnedMemberA) {
		this.idOwnedMemberA = idOwnedMemberA;
	}

	public String getIdOwnedMemberB() {
		return idOwnedMemberB;
	}

	public void setIdOwnedMemberB(String idOwnedMemberB) {
		this.idOwnedMemberB = idOwnedMemberB;
	}

	


	
}
