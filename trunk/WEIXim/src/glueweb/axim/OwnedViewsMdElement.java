package glueweb.axim;


import java.util.List;
import java.util.ArrayList;

public class OwnedViewsMdElement {
	private String elementClass; //tipo del elemento referenciado por elementID (class, diagram,...)
	private String elementId; //id del elemento al que se hace referencia (sera un OwnedMember o un OwnedAttribute)
	private String xmiid; //id que representa univocamente a este OwnedViewsMdElement
	private int[] geometry=new int[4]; //coordenadas (x,y,dx,dy) del elemento referenciado por elementId en el diagrama principal
	private String elementIdPadre; //id del elemento en el que está contenido este elemento (ej:un boton en una Pagina)
	private String xmiidPadre;
	private List<String> parts; //si el elemento referenciado tiene componentes dentro de él, se almacenaran en una lista los xmiIds de sus OwnedViewsMdElement
	private boolean pintado;
	//cuando pintamos una clase asociación, al hacer click en la primera clase (origen), la referencia se guarda en linkSecondEndID (sí, en esa), y
	//al hacer click en la segunda clase (destino), la refeencia se guarda en linkFirstEndID
	private String linkFirstEndID; 
	private String linkSecondEndID;
	
	public OwnedViewsMdElement(String elementClass,String xmiid,String elementId,int[] geometry,String linkFirstEndID,String linkSecondEndID){
		this.elementClass=elementClass;
		this.xmiid=xmiid;
		this.elementId=elementId;
		this.geometry=geometry;
		this.elementIdPadre=null;
		parts=new ArrayList<String>();
		this.pintado=false;
		this.linkFirstEndID=linkFirstEndID;
		this.linkSecondEndID=linkSecondEndID;
	}

	public String toString() {
		return "<OWV-mdElement>\n\t elementClass: "+elementClass+"\n\t elementId: "+elementId+"\n\t xmi-id: "+xmiid+"\n\t geometry: "+
				this.imprimeGeometry()+"\n\t elementIdPadre: "+elementIdPadre+"\n\t xmi-idPadre: "+
				xmiidPadre+"\n\t parts: "+parts.toString()+"\n\t linkFirstEndID="+linkFirstEndID +"\n\t linkSecondEndID="+linkFirstEndID;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof OwnedViewsMdElement)&&
			   ((OwnedViewsMdElement)obj).elementId.equals(elementId);
	}

	/**Devuelve un String con el contenido del array geometry
	 * */
	public String imprimeGeometry(){
		String res="";
		for (int i=0;i<4;i++){
			res=res+geometry[i]+",";
		}
		return res.substring(0,res.length()-1);
	}
	
	public String getElementClass() {
		return elementClass;
	}

	public void setElementClass(String elementClass) {
		this.elementClass = elementClass;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public int[] getGeometry() {
		return geometry;
	}

	public void setGeometry(int[] geometry) {
		this.geometry = geometry;
	}

	public List<String> getParts() {
		return parts;
	}

	public void setParts(List<String> parts) {
		this.parts = parts;
	}

	public String getElementIdPadre() {
		return elementIdPadre;
	}

	public void setElementIdPadre(String elementIdPadre) {
		this.elementIdPadre = elementIdPadre;
	}

	public String getXmiid() {
		return xmiid;
	}

	public void setXmiid(String xmiid) {
		this.xmiid = xmiid;
	}

	public boolean isPintado() {
		return pintado;
	}

	public void setPintado(boolean pintado) {
		this.pintado = pintado;
	}

	public String getXmiidPadre() {
		return xmiidPadre;
	}

	public void setXmiidPadre(String xmiidPadre) {
		this.xmiidPadre = xmiidPadre;
	}
	
	public String getLinkFirstEndID() {
		return linkFirstEndID;
	}

	public void setLinkFirstEndID(String linkFirstEndID) {
		this.linkFirstEndID = linkFirstEndID;
	}

	public String getLinkSecondEndID() {
		return linkSecondEndID;
	}

	public void setLinkSecondEndID(String linkSecondEndID) {
		this.linkSecondEndID = linkSecondEndID;
	}

	
	


}
