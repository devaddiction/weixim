package glueweb.xmi;

/**
 * represent information about arrows in UML diagram
 */
public class Association {
	private String name, id;
	private String sourceCode, targetCode;
	private String metodo;
	private String origen;
	private String evento;
	private String estereotipoSource;
	private String estereotipoTarget;
	private String estereotipoOrigen;

	public Association(String name, String id, String sourceCode,
			String estereotipoSource, String targetCode,
			String estereotipoTarget, String metodo, String origen,
			String estereotipoOrigen, String evento) {
		this.name = name;
		this.id = id;
		this.sourceCode = sourceCode;
		this.estereotipoSource = estereotipoSource;
		this.targetCode = targetCode;
		this.estereotipoTarget = estereotipoTarget;
		this.metodo = metodo;
		this.origen = origen;
		this.evento = evento;
		this.estereotipoOrigen = estereotipoOrigen;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public String getEstereotipoSource() {
		return estereotipoSource;
	}

	public String getTargetCode() {
		return targetCode;
	}

	public String getEstereotipoTarget() {
		return estereotipoTarget;
	}

	public String getMetodo() {
		return metodo;
	}

	public String getOrigen() {
		return origen;
	}

	public String getEvento() {
		return evento;
	}
	
	public String getEstereotipoOrigen(){
		return estereotipoOrigen;
	}

	public String toString() {
		return getName() + " [" + getId() + "] codigo: " + sourceCode + " << "
				+ estereotipoSource + ">> -> " + targetCode + " << "
				+ estereotipoTarget + " >>" + "\n\t\t- Metodo: " + getMetodo()
				+ "\n\t\t- Origen: " + getOrigen() + " << " + estereotipoOrigen
				+ " >>" + "\n\t\t- Evento: " + getEvento();
	}
}
