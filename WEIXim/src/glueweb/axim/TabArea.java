package glueweb.axim;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**Clase que almacena el xmiId de una TabArea,el número de Tabs que alberga actualmente
 * dicha TabArea, y el elementId de cada una de dichas Tabs.
 * Una TabArea simplemente es una Section que contiene alguna Tab dentro de ella.
 * */
public class TabArea {
	private String xmiid; //xmiid de este TabArea
	private int numTabs; //numero de Tabs de esta TabArea
	private List<String> listTabs; //contiene una lista con los xmiIds de las Tabs que contiene
	private Map<String,String> mapXmiIdsyNombres; //contendra un map con pares (xmiId,nombre_deTab)
	private Map<String,Integer> mapXmiIdsyIndices; //contendra un map con pares (xmiId,indice_deTab)
	

	

	public TabArea(String xmiid){
		this.xmiid=xmiid;
		this.numTabs=0;
		this.listTabs=new ArrayList<String>();
		mapXmiIdsyNombres=new LinkedHashMap<String,String>();
		mapXmiIdsyIndices=new LinkedHashMap<String,Integer>();
	}

	/**Método que añade un xmiid a listTabs*/
	public void addTab(String xmiid){
		this.listTabs.add(xmiid);
		this.numTabs=listTabs.size();
	}
	
	/**Dado el xmiid de una Tab que se encuentra en esta TabArea, devuelve su nombre.
	 * Si por algún caso no la encuentra, devuelve null
	 * */
	public String getNombreByXmiIdTab(String xmiId){
		String res=null;
		for (Map.Entry<String,String> tupla:this.mapXmiIdsyNombres.entrySet()){
			if (tupla.getKey().equals(xmiId)){
				res=tupla.getValue();
			}
		}
		return res;
	}
	
	/**Dado el xmiid de una Tab que se encuentra en esta TabArea, devuelve el indice de dicho Tab.
	 * Si por algún caso no la encuentra, devuelve null
	 * */
	public Integer getIndiceByXmiIdTab(String xmiId){
		Integer res=null;
		for (Map.Entry<String,Integer> tupla:this.mapXmiIdsyIndices.entrySet()){
			if (tupla.getKey().equals(xmiId)){
				res=tupla.getValue();
			}
		}
		return res;
	}
	
	public Map<String, Integer> getMapXmiIdsyIndices() {
		return mapXmiIdsyIndices;
	}

	public void setMapXmiIdsyIndices(Map<String, Integer> mapXmiIdsyIndices) {
		this.mapXmiIdsyIndices = mapXmiIdsyIndices;
	}
	
	
	public List<String> getListTabs() {
		return listTabs;
	}

	public void setListTabs(List<String> listTabs) {
		this.listTabs = listTabs;
	}

	public String getXmiid() {
		return xmiid;
	}

	public void setXmiid(String xmiid) {
		this.xmiid = xmiid;
	}

	public int getNumTabs() {
		return numTabs;
	}

	public void setNumTabs(int numTabs) {
		this.numTabs = numTabs;
	}
	
	public Map<String, String> getMapXmiIdsyNombres() {
		return mapXmiIdsyNombres;
	}

	public void setMapXmiIdsyNombres(Map<String, String> mapXmiIdsyNombres) {
		this.mapXmiIdsyNombres = mapXmiIdsyNombres;
	}
	
}
