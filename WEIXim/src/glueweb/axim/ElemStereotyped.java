package glueweb.axim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

/**Clase que representa un elemento estereotipado. Contendrá el id de dicho elemento
 *, el id del elemento Stereotype con el que está estereotipado, y un Map con los nombres y valores
 *de los tags de dicho estereotipo para ese elemento */
public class ElemStereotyped {
	private String id;
	private String idStereotype;
	private Map<String,String> tags;
	
	public ElemStereotyped(String id, String idStereotype){
		this.id=id;
		this.idStereotype=idStereotype;
		tags=new TreeMap<String,String>();
	}

	public void addTag(String nombre, String valor){
		tags.put(nombre, valor);
	}
	
	/**Comprueba si un determinado tag de estereotipo existe para este ElemStereotyped*/
	public boolean existeTag(String t){
		boolean res=false;
		Set<String> nombretags=tags.keySet();
		for (String n:nombretags){
			if (n.equalsIgnoreCase(t)){
				res=true;
			}
		}
		return res;
	}
	
	/**Devuelve el valor de un determinado tag. Si no encuentra el tag devuelve null*/
	public String getValorTag(String t){
		String res=null;
		if (this.existeTag(t)){
			Set<Entry<String,String>> c=tags.entrySet();
			for (Entry<String,String> e:c ){
				if (e.getKey().equals(t)){
					res=e.getValue();
				}
			}
		}
		return res;
	}
	
	/**Devuelve una lista con los nombres de los tags de este elemento*/
	public List<String> getListaNombresTags(){
		Set<String> nombretags=tags.keySet();
		return new ArrayList<String>(nombretags);
	}
	
	
	
	public String toString() {	
		return "ElemStereotype:\n\t id: "+id+"\n\t idStereotype: "+idStereotype+"\n\t tags: "+this.getListaNombresTags();	
	}
	
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ElemStereotyped) && 
		((ElemStereotyped)obj).id.equals(id) &&
		((ElemStereotyped)obj).idStereotype.equals(idStereotype);
	}
	
	
	@Override
	public int hashCode() {
		return (id.hashCode()+idStereotype.hashCode())/2;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdStereotype() {
		return idStereotype;
	}

	public void setIdStereotype(String idStereotype) {
		this.idStereotype = idStereotype;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}
	
	
}
