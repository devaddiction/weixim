package glueweb.axim;

public class OwnedParameter {
	private String idOwOperation; 
	private String xmiType;
	private String id;
	private String name;
	private String direction;
	private String defaultValue;
	private String type;
	
	public OwnedParameter(String idOwOperation, String xmiType, String id,String name, String direction, String type) {
		this.idOwOperation=idOwOperation;
		this.xmiType=xmiType;
		this.id=id;
		this.name=name;
		this.direction=direction;
		this.type=type;
	}

	public String toString() {
		return "\t<ownedParameter>\n\t xmiType: "+xmiType+"\n\t id: "+id+"\n\t name: "+name+"\n\t defaultValue: "+defaultValue+
		"\n\t idOwOperation: "+idOwOperation+"\n\t direction: "+direction+"\n\t tipo: "+type+"\n";
	}
	
	
	public String getIdOwOperation() {
		return idOwOperation;
	}

	public void setIdOwOperation(String idOwOperation) {
		this.idOwOperation = idOwOperation;
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
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}