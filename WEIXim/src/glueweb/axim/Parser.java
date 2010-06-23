package glueweb.axim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Resource;
import org.osgi.framework.Bundle;

public class Parser {

	public String getDirectorioTrabajo() {
		return directorioTrabajo;
	}

	public void setDirectorioTrabajo(String directorioTrabajo) {
		this.directorioTrabajo = directorioTrabajo;
	}

	/** fichero xmi que se procesa */
	private File fichero;
	/** almacenara los OwnedMember del fichero XMI */
	private List<OwnedMember> listaMembers;
	/** almacenara los estereotipos del fichero XMI */
	private List<Stereotype> listaStypes;
	/** almacenara los MdElement que hay en las etiquetas mdOwnedViews */
	private List<OwnedViewsMdElement> listaOvMdElement;
	/** almacena los AssociationClass del fichero XMI */
	private List<AssociationClass> listaAssociationClass;
	/**
	 * referencia al elementId de un elemento estereotipado como Page. Nos sirve
	 * a la hora de pintar los elementos, para calcular las coordenadas
	 * correctas de donde pintar el mismo. Para ello hay que restar sus
	 * coordenadas con las de la página en la que se encuentra.
	 * */
	private String paginaActual;
	private List<String> nombresPaginas;
	private String imageMapActual;
	private String tabActual;
	/**
	 * lista en donde habra una casilla nueva ocupada por cada TabArea que
	 * hayamos definido. Una casilla de estas llevará un objeto "TabArea", el
	 * cual contiene el elementId de dicha TabArea y el contador del número de
	 * Tabs actual que contiene esa TabArea, para así poder usar esto por
	 * ejemplo en pintaTab, donde necesitamos para cada tab un div con un
	 * id=T1X, donde X será el nº de Tabs que hay en una TabArea
	 */
	private List<TabArea> listaTabArea;
	/**
	 * Conjunto de elementos ElemStereotyped. Un ElemStereotyped representa un
	 * elemento que está estereotipado y los tags de estereotipo y sus valores
	 * que se le han aplicado.
	 * */
	private Set<ElemStereotyped> conjElemStereotyped;

	/**
	 * Almacenará la versión de MagicDraw que se usó para generar el fichero XML
	 * */
	private int versionMagicDraw;
	/**
	 * Directorio actual donde el usuario tiene su fichero XML que ha
	 * seleccionado a través de la GUI
	 **/
	private String directorioTrabajo;
	/**
	 * Almacenará el nombre de la página de inicio, es decir, aquella a la que
	 * se definio su tag home
	 * **/
	private String paginaHome;

	public Parser() {
		listaMembers = new ArrayList<OwnedMember>();
		listaStypes = new ArrayList<Stereotype>();
		listaOvMdElement = new ArrayList<OwnedViewsMdElement>();
		listaAssociationClass = new ArrayList<AssociationClass>();
		listaTabArea = new ArrayList<TabArea>();
		conjElemStereotyped = new HashSet<ElemStereotyped>();
		nombresPaginas = new ArrayList<String>();
		fichero = null;
		directorioTrabajo = "";
		paginaHome = null;
	}

	/**
	 * Imprime todos los objetos OwnedMember almacenados en listaMembers
	 * */
	public void imprimelistaMembers() {
		System.out.println("-----OWNEDMEMBERS-----");
		for (OwnedMember om : listaMembers) {
			System.out.println(om);
		}
	}

	/**
	 * Imprime todos los objetos Stereotype almacenados en listaStypes
	 * */
	public void imprimelistaStypes() {
		System.out.println("-----STEREOTYPES-----");
		for (Stereotype st : listaStypes) {
			System.out.println(st);
		}
	}

	/**
	 * Imprime todos los objetos ElemStereotyped almacenados en
	 * conjElemStereotyped
	 * */
	public void imprimeconjElemStereotyped() {
		System.out.println("-----CONJ STEREOTYPES-----");
		for (ElemStereotyped es : conjElemStereotyped) {
			System.out.println(es);
		}
	}

	/**
	 * Imprime todos los objetos OwnedViewsMdElement almacenados en
	 * listaOvMdElement
	 * */
	public void imprimelistaOvMdElement() {
		System.out.println("-----OWNED VIEWS-----");
		for (OwnedViewsMdElement ov : listaOvMdElement) {
			System.out.println(ov);
		}
	}

	/**
	 * Imprime todos los objetos AssociationClass almacenados en
	 * listaAssociationClass
	 * */
	public void imprimelistaAssociationClass() {
		System.out.println("-----ASSOCIATION CLASS-----");
		for (AssociationClass ac : listaAssociationClass) {
			System.out.println(ac);
		}
	}

	/**
	 * Busca un OwnedMember pasandole su id. Si no lo encuentra devuelve null
	 */
	public OwnedMember buscaOwnedMemberbyId(String id) {
		OwnedMember res = null;
		for (OwnedMember om : listaMembers) {
			if (om.getId().equals(id)) {
				res = om;
				return res;
			}
		}
		return res;
	}

	/**
	 * Busca un OwnedMember pasandole su name. Si no lo encuentra devuelve null
	 */
	public OwnedMember buscaOwnedMemberbyName(String name) {
		OwnedMember res = null;
		for (OwnedMember om : listaMembers) {
			if (om.getName().equals(name)) {
				res = om;
				return res;
			}
		}
		return res;
	}

	/**
	 * Busca un AssociationClass pasandole su id. Si no lo encuentra devuelve
	 * null
	 */
	public AssociationClass buscaAssociationClassbyId(String id) {
		AssociationClass res = null;
		for (AssociationClass ac : listaAssociationClass) {
			if (ac.getId().equals(id)) {
				res = ac;
				return res;
			}
		}
		return res;
	}

	/**
	 * Busca un OwnedMember pasandole su name. Si no lo encuentra devuelve null
	 */
	public AssociationClass buscaAssociationClassbyName(String name) {
		AssociationClass res = null;
		for (AssociationClass ac : listaAssociationClass) {
			if (ac.getName().equals(name)) {
				res = ac;
				return res;
			}
		}
		return res;
	}

	/**
	 * Lee todo el fichero xmi en busca de etiquetas ownedMember, procesa la
	 * informacion de cada una y va creando objetos OwnedMember y almacenandolos
	 * en listaMembers. Si un ownedMember es del tipo uml:Stereotype, crea un
	 * objeto Stereotype y lo añade a listaStypes. Busca todos las etiquetas
	 * OwnedAttribute dentro de cada OwnedMember y crea objetos OwnedAttribute y
	 * los almacena en listOwnedAtrributes del OwnedMember en cuestion. Si la
	 * version de MagicDraw es la 15, en lugar de procesar elementos
	 * "ownedMember", procesará elementos "packagedElement".
	 * 
	 * @throws Exception
	 */
	public void leeOwnedMembers() throws Exception {
		BufferedReader br = null;
		OwnedMember omActual = null; // Referencia al ultimo OwnedMember leído.
		// Asi si nos encontramos
		// OwnedAttributes, sabremos
		// que son atributos de dicho OwnedMember
		OwnedAttribute oaActual = null; // Referencia al ultimo OwnedAttribute
		// leído.
		OwnedOperation opActual = null; // Referencia al ultimo OwnedOperation
		// leído.
		OwnedParameter oparamActual = null; // Referencia al ultimo
		// OwnedParameter leído.
		boolean pintar = false; // Se pondrá a true cuando lea el primer
		// <mdOwnedViews. Lo hacemos para que si hay mas
		// <mdOwnedViews anidados (estos no nos interesan) no se procesen
		boolean leeUmlModel = false;
		boolean leyendoOwnedAttribute = false;
		boolean leyendoOwnedParameter = false;

		String etiqOwnedMember = "<ownedMember";
		if (this.versionMagicDraw == 15)
			etiqOwnedMember = "<packagedElement";

		try {
			InputStreamReader in = new InputStreamReader(new FileInputStream(
					fichero), "UTF-8");
			br = new BufferedReader(in);
			String linea = br.readLine();
			while (linea != null) {
				if (linea.contains("<uml:Model")) {
					leeUmlModel = true;
				} else if (linea.contains("</uml:Model")) {
					leeUmlModel = false;
				} else if (linea.contains(etiqOwnedMember)
						&& (leeUmlModel == true)) {
					OwnedMember om = this.extraeOwnedMember(linea);
					listaMembers.add(om);
					omActual = om;
					if (om.getXmiType().equals("uml:Stereotype")) {
						listaStypes
								.add(new Stereotype(om.getId(), om.getName()));
					} else if (om.getXmiType().equals("uml:AssociationClass")
							|| om.getXmiType().equals("uml:Association")) {
						om.setAssociationClass(true);
						AssociationClass ac = new AssociationClass(om.getId(),
								om.getName(), "A");
						listaAssociationClass.add(ac); // al principio,
						// supondremos que es de
						// tipo A

					}
				} else if (linea.contains("<ownedAttribute")
						&& (leeUmlModel == true)) {
					leyendoOwnedAttribute = true;
					leyendoOwnedParameter = false;// ///
					OwnedAttribute oa = this.extraeOwnedAttribute(linea);
					oaActual = oa;
					oa.setIdOwMember(omActual.getId()); // le asignamos al
					// OwnedAttribute el id
					// del OwnedMember al
					// que pertenece
					omActual.getListOwnedAttributes().add(oa); // añadimos el
					// OwnedAttribute
					// a la lista de
					// OwnedAttributes
					// del
					// correspond.
					// OwnedMember
				} else if (linea.contains("</ownedAttribute")
						&& (leeUmlModel == true)) {
					leyendoOwnedAttribute = false;
				} else if (linea.contains("<ownedOperation")
						&& (leeUmlModel == true)) {
					OwnedOperation op = this.extraeOwnedOperation(linea);
					opActual = op;
					op.setIdOwMember(omActual.getId());
					omActual.getListOwnedOperations().add(op);
					AssociationClass acActual = this
							.buscaAssociationClassbyId(omActual.getId());
					acActual.setTipo("B"); // una AssociationClass con una
					// operacion en su interior, es
					// engloba en Transition de tipo B
				} else if (linea.contains("<ownedParameter")
						&& (leeUmlModel == true)) {
					// debug("Parameter ON");
					leyendoOwnedParameter = true;
					leyendoOwnedAttribute = false; // ///
					OwnedParameter oparam = this.extraeOwnedParameter(linea);
					oparamActual = oparam;
					oparam.setIdOwOperation(opActual.getId());
					opActual.getParametros().add(oparam);

				} else if (linea.contains("</ownedParameter")
						&& (leeUmlModel == true)) {
					leyendoOwnedParameter = false;
				} else if (linea.contains("<defaultValue")
						&& (leeUmlModel == true)
						&& (leyendoOwnedAttribute == true)
						&& (leyendoOwnedParameter == false)) {
					this.extraeDefaultValueParaOwnedAttribute(linea, oaActual);
				} else if (linea.contains("<defaultValue")
						&& (leeUmlModel == true)
						&& (leyendoOwnedAttribute == false)
						&& (leyendoOwnedParameter == true)) {
					this.extraeDefaultValueParaOwnedParameter(linea,
							oparamActual);
				} else if (linea.contains("<type xmi:type")
						&& (leeUmlModel == true)
						&& (leyendoOwnedAttribute == false)
						&& (leyendoOwnedParameter == true)) {
					this.extraeTypeParaOwnedParameter(linea, oparamActual);
				} else if (linea.contains("<memberEnd")
						&& (leeUmlModel == true)) {
					this.leeMemberEnd(linea, omActual); // recoge los datos
					// sobre un MemberEnd
				} else if (linea.contains("<mdOwnedViews") && pintar == false) {
					this.leeOwnedViewsMdElement(br); // recolectamos la
					// informacion acerca
					// del diagrama
					// realizado en
					// MagicDraw
					pintar = true;
				}
				linea = br.readLine();
			}

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Recibe un objeto AssociationClass y busca sus atributos (wsdl, puerto,
	 * event, origen, metodo...) y rellena los valores en los vampos corectos
	 * del objeto AssociationClass.
	 * 
	 * @throws ParserXmiException
	 * */
	private void obtieneAtributosAssociationClass(AssociationClass ac)
			throws ParserXmiException {
		OwnedMember om = this.buscaOwnedMemberbyId(ac.getId());
		if (om == null)
			throw new ParserXmiException(
					"No se encontró el OwnedMember asociado a este supuesto AssociationClass");

		String wsdl = "";
		String puerto = "";
		String metodo = "";
		String evento = "";
		String origen = "";

		if (ac.getTipo().equals("B")) { // si es de tipo B es porque tiene
			// definida alguna operación dentro de
			// ella.
			for (OwnedAttribute oa : om.getListOwnedAttributes()) {
				if (oa.getName().equalsIgnoreCase("wsdl")) {
					wsdl = oa.getDefaultValue();
				} else if (oa.getName().equalsIgnoreCase("puerto")) {
					puerto = oa.getDefaultValue();
				} else if (oa.getName().equalsIgnoreCase("metodo")) {
					metodo = oa.getDefaultValue();
				} else if (oa.getName().equalsIgnoreCase("evento")) {
					evento = oa.getDefaultValue();
				} else if (oa.getName().equalsIgnoreCase("origen")) {
					origen = oa.getDefaultValue();
				}

			}

			// Si es una asociacion tipo B pero con la operacion especial
			// "cargaFichero", solo nos interesan los atributos origen,metodo y
			// evento.
			if (metodo != null && metodo.equalsIgnoreCase("cargaFichero")) {
				if (evento == null || evento == "")
					throw new ParserXmiException(
							"No se encontro el atributo evento para la asociacion "
									+ ac.getName());
				if (origen == null || origen == "")
					throw new ParserXmiException(
							"No se encontro el atributo origen para la asociacion "
									+ ac.getName());
				if (metodo == null || metodo == "")
					throw new ParserXmiException(
							"No se encontro el atributo metodo para la asociacion "
									+ ac.getName());

			} else if (metodo != null
					&& metodo.equalsIgnoreCase("addOperationWS")) {
				if (evento == null || evento == "")
					throw new ParserXmiException(
							"No se encontro el atributo evento para la asociacion "
									+ ac.getName());
				if (origen == null || origen == "")
					throw new ParserXmiException(
							"No se encontro el atributo origen para la asociacion "
									+ ac.getName());

			} else if (metodo != null
					&& metodo.equalsIgnoreCase("removeOperationWS")) {
				if (evento == null || evento == "")
					throw new ParserXmiException(
							"No se encontro el atributo evento para la asociacion "
									+ ac.getName());
				if (origen == null || origen == "")
					throw new ParserXmiException(
							"No se encontro el atributo origen para la asociacion "
									+ ac.getName());

			} else { // es una asociacion normal tipo B o con la operacion
				// especial "decideWS"
				// if (wsdl==null || wsdl=="") throw new
				// ParserXmiException("No se encontro el atributo wsdl para la asociacion "+ac.getName());
				// if (puerto==null || puerto=="") throw new
				// ParserXmiException("No se encontro el atributo puerto para la asociacion "+ac.getName());
				if (metodo == null || metodo == "")
					throw new ParserXmiException(
							"No se encontro el atributo metodo para la asociacion "
									+ ac.getName());
				if (evento == null || evento == "")
					throw new ParserXmiException(
							"No se encontro el atributo evento para la asociacion "
									+ ac.getName());
				if (origen == null || origen == "")
					throw new ParserXmiException(
							"No se encontro el atributo origen para la asociacion "
									+ ac.getName());
			}
			ac.setWsdl(wsdl);
			ac.setPuerto(puerto);
			ac.setMetodo(metodo);
			ac.setEvento(evento);
			ac.setOrigen(origen);

			if (om.getListOwnedOperations() != null)
				ac.setListadoMetodos(om.getListOwnedOperations());

		} else if (ac.getTipo().equals("A")) { // solo nos interesa los
			// atributos evento y origen. El
			// resto y las operaciones no
			// importan en este caso
			for (OwnedAttribute oa : om.getListOwnedAttributes()) {
				if (oa.getName().equalsIgnoreCase("evento")) {
					evento = oa.getDefaultValue();
				} else if (oa.getName().equalsIgnoreCase("origen")) {
					origen = oa.getDefaultValue();
				}
			}

			if (evento == null || evento == "")
				throw new ParserXmiException(
						"No se encontro el atributo evento para la asociacion "
								+ ac.getName());
			if (origen == null || origen == "")
				throw new ParserXmiException(
						"No se encontro el atributo origen para la asociacion "
								+ ac.getName());

			ac.setEvento(evento);
			ac.setOrigen(origen);
		}

		// El atributo origen debe solamente referirse a elementos:
		// <<Buton>>,<<LinkLabel>>,<<Text>>,<<TextArea>>,<<Combo>>,<<RadioButton>>
		// o <<CheckBox>>
		if (origen.equals("") == false) {
			OwnedAttribute oa = this.buscaOwnedAttributebyName(origen);
			if (oa == null)
				throw new ParserXmiException(
						"No se encontró un OwnedAttribute "
								+ "para el elemento con nombre "
								+ origen
								+ ".Posible referencia de atributo origen en clase"
								+ " de asociación a un elemento que no existe");
			// System.out.println(oa.getId());
			OwnedViewsMdElement ov = this.buscaOwnedViewsMdElementbyId(oa
					.getId());
			if (ov != null) {
				String st = this.averiguaStereotype(ov.getElementId());
				if (st.equalsIgnoreCase("Button") == false
						&& st.equalsIgnoreCase("LinkLabel") == false
						&& st.equalsIgnoreCase("Text") == false
						&& st.equalsIgnoreCase("TextArea") == false
						&& st.equalsIgnoreCase("ComboBox") == false
						&& st.equalsIgnoreCase("CheckBox") == false
						&& st.equalsIgnoreCase("RadioButton") == false) {
					throw new ParserXmiException(
							"El atributo origen de una clase de asociacion debe "
									+ "referirse a un elemento Button,LinkLabel,Text,TextArea,ComboBox,RadioButton o CheckBox. \n Se encontro un "
									+ st);
				}
			}
		}

	}

	/**
	 * Lee de nuevo todo el fichero xmi en busca de etiquetas "Data:estereotipo"
	 * (ej: Data:Page) y almacenara dentro de cada elemento de listaStypes los
	 * ids de los elementos estereotipados con el estereoripo procesado con la
	 * ayuda del metodo extraeBase_Element
	 * 
	 * @throws Exception
	 */
	public void leeDataStereotypes() throws Exception {
		boolean procesar = false;
		BufferedReader br = null;
		try {
			InputStreamReader in = new InputStreamReader(new FileInputStream(
					fichero), "UTF-8");
			br = new BufferedReader(in);
			String linea = br.readLine();
			while (linea != null) {
				if (linea.contains("</stereotypeApplicationForProxies")) { // no
					// leemos
					// Data:xxx
					// hasta
					// saltarme
					// lo
					// que
					// no
					// me
					// interesa
					procesar = true;
				}
				for (Stereotype st : listaStypes) {
					if (linea.contains("<Data:" + st.getName() + " xmi")
							&& procesar == true) {
						this.extraeBase_Element(linea, st);
					}
				}
				linea = br.readLine();
			}

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Se le pasa una linea con un "Data:estereotipo" (ej:Data:Page) y obtiene
	 * de ella el campo base_element y añade dicho id a la listaid del objeto
	 * Stereotype que tambien se le pasa como parametro. Además, si se han
	 * especificado tags de estereotipo para el elemento tratato, se recogerán
	 * los nombres y valores de dichos tags y se almacenarán convenientemente en
	 * los objetos ElemStereotyped.
	 * 
	 * @throws ParserXmiException
	 */
	public void extraeBase_Element(String linea, Stereotype stype)
			throws ParserXmiException {
		String id = ""; // este id no nos interesa
		String base_element = "";
		ElemStereotyped es = null;

		StringTokenizer st = new StringTokenizer(linea, "'");

		boolean tieneTags = false;
		// System.out.println("tam temp conj="+this.conjElemStereotyped.size());
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.contains("xmi:id")) {
				token = st.nextToken();
				id = token;
				/*
				 * }else if (token.contains("base_Element") ||
				 * token.contains("base_Class")||
				 * token.contains("base_StructuralFeature") ||
				 * token.contains("base_AssociationClass") ||
				 * token.contains("base_BehavioralFeature")){
				 */
			} else if (token.contains("base_")) {
				token = st.nextToken();
				base_element = token;
				stype.getListaid().add(base_element);
				es = new ElemStereotyped(base_element, stype.getId());
				// System.out.println("base element actual="+base_element);

				// comprobamos que el elemento procesado no tiene mas de un
				// estereotipo aplicado
				if (this.conjElemStereotyped != null) {
					for (ElemStereotyped elemSt : this.conjElemStereotyped) {
						if (elemSt.getId().equals(base_element)) {
							// System.out.println("A-->"+base_element);
							// System.out.println("A1-->"+es.toString());
							// System.out.println("A2-->"+elemSt.toString());
							throw new ParserXmiException(
									"Un elemento solo debe tener un estereotipo aplicado.");
						}
					}
				}

				this.conjElemStereotyped.add(es);

			} else { // hay definidos tags de estereotipo para este elemento.
				// Hay que recogerlos
				if (st.hasMoreTokens()) {
					tieneTags = true;
					if (!base_element.equals("")) {
						// token=st.nextToken();
						String nombre = token.trim();
						nombre = nombre.substring(0, nombre.length() - 1);
						token = st.nextToken();
						String valor = token.trim();
						es.addTag(nombre, valor);
						// debug("nombre: "+nombre+",valor: "+valor);

					}
				}
			}
		}

	}

	/*
	 * public void extraeBase_Element(String linea, Stereotype stype) { String
	 * id=""; //este id no nos interesa String base_element=""; ElemStereotyped
	 * es=null;
	 * 
	 * StringTokenizer st=new StringTokenizer(linea,"'");
	 * 
	 * boolean tieneTags=false;
	 * 
	 * while (st.hasMoreTokens()){ String token=st.nextToken(); if
	 * (token.contains("xmi:id")){ token=st.nextToken(); id=token; }else if
	 * (token.contains("base_Element")){ token=st.nextToken();
	 * base_element=token; stype.getListaid().add(base_element); es=new
	 * ElemStereotyped(base_element,stype.getId()); }else{ //hay definidos tags
	 * de estereotipo para este elemento. Hay que recogerlos if
	 * (st.hasMoreTokens()){ tieneTags=true; if (!base_element.equals("")){
	 * //token=st.nextToken(); String nombre=token.trim();
	 * nombre=nombre.substring(0,nombre.length()-1); token=st.nextToken();
	 * String valor=token.trim(); es.addTag(nombre, valor);
	 * //debug("nombre: "+nombre+",valor: "+valor);
	 * this.conjElemStereotyped.add(es); } } } } if (tieneTags==false)
	 * this.conjElemStereotyped.add(es); }
	 */

	/**
	 * Obtiene los elementos OwnedViewsMdElement que contienen principalmente la
	 * id del elemento al que referencian y un campo geometry, que indica las
	 * coordenadas (x,y,dx,dy) respecto del diagrama global
	 * 
	 * @throws Exception
	 * */
	private void leeOwnedViewsMdElement(BufferedReader br) throws Exception {

		String elementClass = "";
		String elementId = "";
		String xmiid = "";
		int[] geometry = null;
		String xmiidActual = null;
		String elementIdActual = null;
		String elementIdPadre = null;
		String xmiidPadre = null;
		String padreAnt = null;
		String xmiidAnt = null;
		String linkFirstEndID = "";
		String linkFirstEndIDActual = null;
		String linkSecondEndID = "";
		String linkSecondEndIDActual = null;

		try {
			String linea = br.readLine();
			while (!linea.contains("</mdOwnedDiagrams")) {
				if (linea.contains("<mdElement")) { // cada <mdElement> tendra
					// dentro una <geometry>
					StringTokenizer st = new StringTokenizer(linea, " ");
					st.nextToken(); // despreciamos el primero porque es
					// "<mdElement"
					while (st.hasMoreTokens()) {
						String token = st.nextToken();
						if (token.contains("elementClass")) {
							elementClass = token.substring(13);
							elementClass = elementClass.substring(1,
									elementClass.length() - 1);
						}
						if (token.contains("xmi:id")) {
							xmiid = token.substring(7);
							xmiid = xmiid.substring(1, xmiid.length() - 2);
							xmiidActual = xmiid;
						}

					}
				} else if (linea.contains("<elementID")) {
					StringTokenizer st = new StringTokenizer(linea, " ");
					st.nextToken(); // despreciamos el primero porque es
					// "<elementID"
					while (st.hasMoreTokens()) {
						String token = st.nextToken();
						if (token.contains("xmi:idref")) {
							elementId = token.substring(10);
							elementId = elementId.substring(1, elementId
									.length() - 3);
							// Actualizamos la referencia del elementIdActual
							elementIdActual = elementId;
						}
					}

				} else if (linea.contains("<linkFirstEndID")) {
					StringTokenizer st = new StringTokenizer(linea, " ");
					st.nextToken(); // despreciamos el primero porque es
					// "<linkFirstEndID"
					while (st.hasMoreTokens()) {
						String token = st.nextToken();
						if (token.contains("xmi:idref")) {
							linkFirstEndID = token.substring(10);
							linkFirstEndID = linkFirstEndID.substring(1,
									linkFirstEndID.length() - 3);
							// Actualizamos la referencia del
							// linkFirstEndIDActual
							linkFirstEndIDActual = linkFirstEndID;
							// System.out.println("link1="+linkFirstEndIDActual);
						}
					}

				} else if (linea.contains("<linkSecondEndID")) {
					StringTokenizer st = new StringTokenizer(linea, " ");
					st.nextToken(); // despreciamos el primero porque es
					// "<linkFirstEndID"
					while (st.hasMoreTokens()) {
						String token = st.nextToken();
						if (token.contains("xmi:idref")) {
							linkSecondEndID = token.substring(10);
							linkSecondEndID = linkSecondEndID.substring(1,
									linkSecondEndID.length() - 3);
							// Actualizamos la referencia del
							// linkFirstEndIDActual
							linkSecondEndIDActual = linkSecondEndID;
							// System.out.println("link2="+linkSecondEndIDActual);
						}
					}

				} else if (linea.contains("<geometry")) {
					int indice = linea.indexOf("<geometry>");
					linea = linea.substring(indice + 10, linea.length() - 10);
					StringTokenizer st2 = new StringTokenizer(linea, " ");
					geometry = new int[4];
					for (int i = 0; i < 4; i++) {
						String token = st2.nextToken();
						token = token.substring(0, token.length() - 1);
						geometry[i] = (int) Integer.parseInt(token);
					}
					/*
					 * Cuando llego a la etiqueta <geometry>, ya puedo almacenar
					 * el MdElement que la contiene al tener todos sus datos. Si
					 * elementClass es Class, hay que crear y añadir un nuevo
					 * ArbolG a listArbolOvMdElement.
					 */
					// if (elementIdActual!=null) {
					OwnedViewsMdElement ovmde = new OwnedViewsMdElement(
							elementClass, xmiidActual, elementIdActual,
							geometry, linkFirstEndIDActual,
							linkSecondEndIDActual);
					ovmde.setElementIdPadre(elementIdPadre); // elementIdPadre
					// sera null o
					// el id del
					// MdElement
					// padre
					ovmde.setXmiidPadre(xmiidPadre);
					listaOvMdElement.add(ovmde);
					// }

					if (elementClass.equals("Class")) {

					} else if (elementClass.equals("DiagramFrame")) {

					} else if (elementClass.equals("Part")) {

					}

				} else if (linea.contains("<parts")) {
					// Actualizamos la referencia del elementIdPadre
					padreAnt = elementIdPadre;
					xmiidAnt = xmiidPadre;
					elementIdPadre = elementIdActual;
					xmiidPadre = xmiidActual;

				} else if (linea.contains("</parts")) {
					// Actualizamos la referencia del elementIdPadre
					elementIdPadre = padreAnt;
					xmiidPadre = xmiidAnt;
					// padre anterior debe apuntar ahora al padre del elemento
					// con id==elementIdPadre
					if (elementIdPadre != null) {
						padreAnt = this.buscaOwnedViewsMdElementbyId(
								xmiidPadre, elementIdPadre).getElementIdPadre();
						xmiidAnt = this.buscaOwnedViewsMdElementbyId(
								xmiidPadre, elementIdPadre).getXmiidPadre();
					}

					/*
					 * hay que ignorar lo que haya entre <properties> y
					 * </properties> ya que no nos aporta nada y puede machacar
					 * informacion que ya tengamos recolectada al coincidir
					 * algunos campos (como elementId)
					 */
				} else if (linea.contains("<properties")) {
					while (!linea.contains("</properties")) {
						linea = br.readLine();
					}
				}
				linea = br.readLine();

			} // cierre del while principal
			this.rellenaPartsOwnedViewsMdElement(); // asignacion de los valores
			// correctos al campo
			// "parts" de los
			// OwnedViewsMdElement

		} finally {
			br = null;
		}

	}

	/*
	 * Una vez conseguida la lista listaOvMdElement, sus elementos no tienen el
	 * campo "parts" relleno. Este método asigna los valores correctos a dicho
	 * campo para cada objeto de esa lista
	 */
	private void rellenaPartsOwnedViewsMdElement() {
		for (OwnedViewsMdElement o : listaOvMdElement) {
			if (o.getElementClass().equals("Part")) {
				if (o.getElementIdPadre() != null) { // Este elemento tiene
					// padre
					OwnedViewsMdElement ov = this
							.buscaOwnedViewsMdElementbyXmiId(o.getXmiidPadre());// buscamos
					// al
					// padre
					ov.getParts().add(o.getXmiid()); // añadimos a la lista
					// parts del padre el
					// xmiid del elemento
					// procesado
				}
			}
		}

	}

	/**
	 * Obtiene la informaion de los elementos MemberEnd que contienen
	 * principalmente la id del ownedAttribute que es un extremos de la
	 * asociacion. Almacena la informacion necesaria sobre estos tanto en el
	 * OwnedMember correspondiente como en el objeto AssociationClass asociado.
	 * */
	private void leeMemberEnd(String linea, OwnedMember om) {
		/*
		 * System.out.println("linea="+linea); String id="";
		 * 
		 * StringTokenizer st=new StringTokenizer(linea," "); st.nextToken();
		 * //despreciamos el primero porque es "<memberEnd" while
		 * (st.hasMoreTokens()){ String token=st.nextToken();
		 * 
		 * if (token.contains("xmi:idref")){ id=token.substring(10);
		 * id=id.substring(1,id.length()-3); } }
		 * 
		 * om.getListIdsMemberEnds().add(id); if
		 * (om.getXmiType().equals("uml:AssociationClass") ||
		 * om.getXmiType().equals("uml:Association")){ AssociationClass
		 * ac=this.buscaAssociationClassbyId(om.getId()); if
		 * (om.getListIdsMemberEnds().size()==1){ //el primer memberEnd
		 * corresponderá con el extremo B de la asociacion
		 * ac.setIdOwnedAttributeB(id);
		 * ac.setIdOwnedMemberB(this.buscaOwnedAttributebyId
		 * (id).getIdOwMember()); }else if
		 * (om.getListIdsMemberEnds().size()==2){ //el segundo memberEnd
		 * corresponderá con el extremo A de la asociacion
		 * ac.setIdOwnedAttributeA(id);
		 * ac.setIdOwnedMemberA(this.buscaOwnedAttributebyId
		 * (id).getIdOwMember()); } }
		 */
	}

	/**
	 * Se le pasa una linea del fichero XML que contiene <defaultValue...> y un
	 * OwnedAttribute, y extrae el valor de la etiqueta defaultValue y se lo
	 * asigna a la propiedad defaultValue de OwnedAttribute.
	 * **/
	private void extraeDefaultValueParaOwnedAttribute(String linea,
			OwnedAttribute oaActual) {
		StringTokenizer st = new StringTokenizer(linea, " ");
		st.nextToken(); // despreciamos el primero porque es "<defaultValue"
		while (st.hasMoreTokens()) {
			String token = st.nextToken();

			if (token.contains("value")) {
				String value = token.substring(6);
				value = value.substring(1, value.length() - 3);
				oaActual.setDefaultValue(value);
			}
		}

	}

	/**
	 * Se le pasa una linea del fichero XML que contiene <defaultValue...> y un
	 * OwnedParameter, y extrae el valor de la etiqueta defaultValue y se lo
	 * asigna a la propiedad defaultValue de OwnedParameter.
	 */
	private void extraeDefaultValueParaOwnedParameter(String linea,
			OwnedParameter oparamActual) {
		StringTokenizer st = new StringTokenizer(linea, " ");
		st.nextToken(); // despreciamos el primero porque es "<defaultValue"
		while (st.hasMoreTokens()) {
			String token = st.nextToken();

			if (token.contains("value")) {
				String value = token.substring(6);
				value = value.substring(1, value.length() - 3);
				oparamActual.setDefaultValue(value);
			}
		}

	}

	/**
	 * Extrae de la linea que se le pasa como parametro el tipo para el
	 * OwnedParameter. Ejemplo: <ownedParameter ....> <type
	 * xmi:type='uml:PrimitiveType'
	 * href='http://schema.omg.org/spec/UML/2.0/uml.xml#Integer'> ......... <
	 * /type>
	 * 
	 * */
	private void extraeTypeParaOwnedParameter(String linea,
			OwnedParameter oparamActual) {
		StringTokenizer st = new StringTokenizer(linea, "#");
		st.nextToken(); // despreciamos el primero porque es "<type"
		while (st.hasMoreTokens()) {
			String token = st.nextToken();

			if (!token.contains("href")) {
				String value = token.substring(0);
				if (token.contains(">"))
					value = value.substring(0, value.length() - 2);
				else
					value = value.substring(0, value.length() - 1);
				oparamActual.setType(value);
			}
		}

	}

	/**
	 * Busca un OwnedViewsMdElement pasandole su xmiid. Si no lo encuentra
	 * devuelve null
	 */
	public OwnedViewsMdElement buscaOwnedViewsMdElementbyXmiId(String xmiid) {
		OwnedViewsMdElement res = null;
		for (OwnedViewsMdElement o : listaOvMdElement) {
			if (o.getXmiid().equals(xmiid)) {
				res = o;
				return res;
			}
		}
		return res;
	}

	/**
	 * Busca un OwnedViewsMdElement pasandole su elementId. Si no lo encuentra
	 * devuelve null
	 */
	public OwnedViewsMdElement buscaOwnedViewsMdElementbyId(String eId) {
		OwnedViewsMdElement res = null;
		for (OwnedViewsMdElement o : listaOvMdElement) {
			if (o.getElementId() != null && o.getElementId().equals(eId)) {
				res = o;
				return res;
			}
		}
		return res;
	}

	/**
	 * Busca un OwnedViewsMdElement pasandole su xmiid y elementId. Si no lo
	 * encuentra devuelve null
	 */
	public OwnedViewsMdElement buscaOwnedViewsMdElementbyId(String xmiid,
			String eId) {
		OwnedViewsMdElement res = null;
		for (OwnedViewsMdElement o : listaOvMdElement) {
			if (o.getElementId().equals(eId) && o.getXmiid().equals(xmiid)) {
				res = o;
				return res;
			}
		}
		return res;
	}

	/**
	 * A partir de una linea con la etiqueta <ownedMember....> obtiene un
	 * OwnedMember analizando dicha linea y extrayendo los campos
	 */
	public OwnedMember extraeOwnedMember(String linea) {
		String type = "";
		String id = "";
		String name = "";
		String visibility = "";

		StringTokenizer st = new StringTokenizer(linea, " ");
		st.nextToken(); // despreciamos el primero porque es "<ownedMember"
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.contains("xmi:type")) {
				type = token.substring(9);
				if (token.contains(">"))
					type = type.substring(1, type.length() - 2);
				else
					type = type.substring(1, type.length() - 1); // 'type'-->type
			} else if (token.contains("xmi:id")) {
				id = token.substring(7);
				if (token.contains(">"))
					id = id.substring(1, id.length() - 2);
				else
					id = id.substring(1, id.length() - 1);
			} else if (token.contains("name")) {
				name = token.substring(5);
				if (token.contains(">"))
					name = name.substring(1, name.length() - 2);
				else
					name = name.substring(1, name.length() - 1);
			} else if (token.contains("visibility")) {
				visibility = token.substring(11);
				if (token.contains(">"))
					visibility = visibility.substring(1,
							visibility.length() - 2);
				else
					visibility = visibility.substring(1,
							visibility.length() - 1);
			}
		}
		return new OwnedMember(type, id, name, visibility);
	}

	/**
	 * A partir de una linea con la etiqueta <ownedMember....> obtiene un
	 * OwnedMember analizando dicha linea y extrayendo los campos
	 */
	public OwnedAttribute extraeOwnedAttribute(String linea) {
		String idOwMember = "";
		String xmiType = "";
		String id = "";
		String name = "";
		String visibility = "";
		String type = "";
		String association = "";

		StringTokenizer st = new StringTokenizer(linea, " ");
		st.nextToken(); // despreciamos el primero porque es "<ownedAttribute"
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.contains("xmi:type")) {
				xmiType = token.substring(9);
				xmiType = xmiType.substring(1, xmiType.length() - 1); // 'xmiType'
				// -->
				// xmiType
			} else if (token.contains("xmi:id")) {
				id = token.substring(7);
				if (token.contains("/>"))
					id = id.substring(1, id.length() - 3);
				else
					id = id.substring(1, id.length() - 1);
			} else if (token.contains("name")) {
				name = token.substring(5);
				if (token.contains("/>"))
					name = name.substring(1, name.length() - 3);
				else
					name = name.substring(1, name.length() - 1);
			} else if (token.contains("visibility")) {
				visibility = token.substring(11);
				if (token.contains("/>"))
					visibility = visibility.substring(1,
							visibility.length() - 3);
				else
					visibility = visibility.substring(1,
							visibility.length() - 1);
			} else if (token.contains("type")) {
				type = token.substring(5);
				if (token.contains("/>"))
					type = type.substring(1, type.length() - 3);
				else
					type = type.substring(1, type.length() - 1);
			} else if (token.contains("association")) {
				association = token.substring(12);
				if (token.contains("/>"))
					association = association.substring(1, type.length() - 3);
				else
					association = association.substring(1,
							association.length() - 1);
			}
		}
		// vamos a extraer el contenido del atributo "name"
		if (linea.contains("' name='")) {
			StringTokenizer st2 = new StringTokenizer(linea, "'");
			boolean ok = false;
			while (st2.hasMoreTokens() && ok == false) {
				String token2 = st2.nextToken();
				if (token2.contains("name=")) {
					// el siguiente token es el contenido de name=...
					ok = true;
					name = st2.nextToken();
				}
			}
		}
		return new OwnedAttribute(idOwMember, xmiType, id, name, visibility,
				type);
	}

	/**
	 * A partir de una linea con la etiqueta <ownedOperation....> obtiene un
	 * OwnedOperation analizando dicha linea y extrayendo los campos
	 */
	public OwnedOperation extraeOwnedOperation(String linea) {
		String idOwMember = "";
		String xmiType = "";
		String id = "";
		String name = "";

		StringTokenizer st = new StringTokenizer(linea, " ");
		st.nextToken(); // despreciamos el primero porque es "<ownedOperation"
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.contains("xmi:type")) {
				xmiType = token.substring(9);
				xmiType = xmiType.substring(1, xmiType.length() - 1); // 'xmiType'
				// -->
				// xmiType
			} else if (token.contains("xmi:id")) {
				id = token.substring(7);
				if (token.contains("/>"))
					id = id.substring(1, id.length() - 3);
				else
					id = id.substring(1, id.length() - 1);
			} else if (token.contains("name")) {
				name = token.substring(5);
				if (token.contains("/>"))
					name = name.substring(1, name.length() - 3);
				else
					name = name.substring(1, name.length() - 1);
			}
		}
		// vamos a extraer el contenido del atributo "name"
		if (linea.contains("' name='")) {
			StringTokenizer st2 = new StringTokenizer(linea, "'");
			boolean ok = false;
			while (st2.hasMoreTokens() && ok == false) {
				String token2 = st2.nextToken();
				if (token2.contains("name=")) {
					// el siguiente token es el contenido de name=...
					ok = true;
					name = st2.nextToken();
				}
			}
		}
		return new OwnedOperation(idOwMember, xmiType, id, name);
	}

	/**
	 * A partir de una linea con la etiqueta <ownedParameter....> obtiene un
	 * OwnedParameter analizando dicha linea y extrayendo los campos
	 */
	public OwnedParameter extraeOwnedParameter(String linea) {
		String idOwOperation = "";
		String xmiType = "";
		String id = "";
		String name = "";
		String direction = "";
		String type = "";

		StringTokenizer st = new StringTokenizer(linea, " ");
		st.nextToken(); // despreciamos el primero porque es "<ownedParameter"
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.contains("xmi:type")) {
				xmiType = token.substring(9);
				xmiType = xmiType.substring(1, xmiType.length() - 1); // 'xmiType'
				// -->
				// xmiType
			} else if (token.contains("xmi:id")) {
				id = token.substring(7);
				if (token.contains("/>"))
					id = id.substring(1, id.length() - 3);
				else
					id = id.substring(1, id.length() - 1);
			} else if (token.contains("name")) {
				name = token.substring(5);
				if (token.contains("/>"))
					name = name.substring(1, name.length() - 3);
				else
					name = name.substring(1, name.length() - 1);
			} else if (token.contains("direction")) {
				direction = token.substring(10);
				if (token.contains("/>"))
					direction = direction.substring(1, direction.length() - 3);
				else
					direction = direction.substring(1, direction.length() - 2);
			} else if (token.contains("type")) {
				type = token.substring(5);
				if (token.contains("/>"))
					type = type.substring(1, type.length() - 3);
				else
					type = type.substring(1, type.length() - 1);
			}
		}
		// vamos a extraer el contenido del atributo "name"
		if (linea.contains("' name='")) {
			StringTokenizer st2 = new StringTokenizer(linea, "'");
			boolean ok = false;
			while (st2.hasMoreTokens() && ok == false) {
				String token2 = st2.nextToken();
				if (token2.contains("name=")) {
					// el siguiente token es el contenido de name=...
					ok = true;
					name = st2.nextToken();
				}
			}
		}
		return new OwnedParameter(idOwOperation, xmiType, id, name, direction,
				type);
	}

	/**
	 * Una vez leidos los miembros, generamos el fichero .html. Se genera la
	 * página html principal, la cual contendrá el código html del resto de
	 * páginas, cada una de ellas dentro de un div diferente. Esta página
	 * principal dispondrá de una barra, que contendrá enlaces. Al pulsar sobre
	 * un determinado enlace, la capa (página) correspondiente, se hará visible,
	 * ocultándose el resto.
	 * 
	 * @throws Exception
	 */
	public void generaPaginasHTML() throws Exception {
		glueweb.pages.AXIMPanel.loading.setSelection(glueweb.pages.AXIMPanel.loading.getMinimum());
		generaEsqueletoHTML(this.fichero.getName()); // generamos el .html
		// esqueleto de la
		// pagina contenedora de
		// divs.
		pintaTituloYrelojArena(); // genera la barra con los enlaces a las
		// distintas páginas (divs)
		int barrasCarga = 0;
		for (Stereotype st : listaStypes) {
			if (st.getName().equalsIgnoreCase("Page")) { // para el estereotipo
				// Page...

				if (st.getListaid().size() == 0) {
					throw new ParserXmiException(
							"No hay ningún elemento Page definido en el modelo UML.");
				}

				for (String id : st.getListaid()) { // para cada elemento
					// estereotipado como
					// Page...
					OwnedMember om = this.buscaOwnedMemberbyId(id); // obtenemos
					// el
					// OwnedMember
					// asociado
					// a partir
					// del id
					// debug("PAGINA "+om.getName());
					OwnedViewsMdElement ov = this
							.buscaOwnedViewsMdElementbyId(id); // comprobamos
					// que dicho
					// elemento este
					// en el dibujo
					/*
					 * si el elemento está sobre el dibujo, se genera su código
					 * html; en otro caso no. Puede pasar que tengamos una clase
					 * definida en OwnedMember y estereotipada como Page pero en
					 * MagicDraw no la hayamos pintado, por lo que no habra
					 * referencia alguna a este elemento en los
					 * OwnedViewsMdElement
					 */
					if (ov != null) {
						// Comprobamos si existe el tag home para esta Page.
						ElemStereotyped es = this
								.buscaElementStereotypedbyId(id);
						if (es == null)
							throw new PintaException(
									"Error en generaPaginasHTML, ElemStereotyped no encontrado");
						boolean home = false;
						if (es.existeTag("home")) {
							String b = es.getValorTag("home");
							if (b.equalsIgnoreCase("true")) { // LA PAGINA TIENE
								// DEFINIDO EL
								// TAG HOME A
								// TRUE
								home = true;
								if (this.paginaHome == null || paginaHome == "") { // ok,
									// esta
									// es
									// por
									// ahora
									// la
									// unica
									// pagina
									// con
									// tag
									// home
									// definido
									this.paginaHome = om.getName(); // guardamos
									// el nombre
									// de la
									// pagina
									// Home
								} else {
									glueweb.pages.AXIMPanel.loading
											.setState(SWT.ERROR);
									throw new PintaException(
											"Sólo debe haber un elemento Page con el tag \"home\" definido a true");

								}
							}
						}

						// Si la pagina tiene el tag home a true, será la página
						// de inicio(su capa sera visible cuando se cargue la
						// pagina
						// Generamos el código de cada Page dentro de su div
						// cpcXXXX
						generaEsqueletoDiv(om.getName(), home);
						this.pintaPagina(ov.getXmiid(), id);
						this.generaCierreDiv();
					} else {
						// debug("ownedview null para omember con id="+id);
					}
				}
			}
			barrasCarga = barrasCarga
					+ (glueweb.pages.AXIMPanel.loading.getMaximum() / (listaStypes
							.size() + 1));
			glueweb.pages.AXIMPanel.loading.setSelection(barrasCarga);
		}

		glueweb.pages.AXIMPanel.loading
				.setSelection(glueweb.pages.AXIMPanel.loading.getMaximum());
		if (this.paginaHome == null || this.paginaHome == "") {
			throw new PintaException(
					"Debe definir en el modelo un elemento Page con el tag home");
		}

		this.generaCierreHTML(this.fichero.getName()); // generamos el cierre de
		// la página html
		// principal contenedora
		// de divs.

	}

	/**
	 * Este método copia los ficheros y directorios necesarios en el directorio
	 * donde el usuario tiene su XML exportado desde MagicDraw. Así el codigo
	 * html resultante del proceso de análisis funcionará.
	 * 
	 * @throws ParserXmiException
	 * @throws IOException
	 * **/
	private void copiaFicherosNecesarios() throws ParserXmiException,
			IOException {
		String path = System.getProperty("osgi.syspath");

		glueweb.util.Copy.unZipFileToDirectory(path.toString()
				+ "/WEIXim_1.0.0.jar", ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/tmp");
		glueweb.util.Copy.unZipFileToDirectory(ResourcesPlugin.getWorkspace()
				.getRoot().getLocation().toString()
				+ "/tmp/tmpAxim.zip", this.directorioTrabajo);

		this.deleteDir(new File(ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ "/tmp/"));

	}

	private void pintaTituloYrelojArena() throws ParserXmiException,
			IOException {

		// Recogemos los nombres de las paginas presentes en el dibujo
		for (Stereotype st : listaStypes) {

			if (st.getName().equals("Page")) { // para el estereotipo Page...
				if (st.getListaid().size() == 0) {
					throw new ParserXmiException(
							"No hay ningún elemento Page definido.");
				}
				for (String id : st.getListaid()) { // para cada elemento
					// estereotipado como
					// Page...
					OwnedMember om = this.buscaOwnedMemberbyId(id); // obtenemos
					// el
					// OwnedMember
					// asociado
					// a partir
					// del id
					OwnedViewsMdElement ov = this
							.buscaOwnedViewsMdElementbyId(id); // comprobamos
					// que dicho
					// elemento este
					// en el dibujo
					if (ov != null) {
						nombresPaginas.add(om.getName()); // almacenamos los
						// nombres de cada
						// pagina en una
						// lista
						// debug("nombre pagina="+om.getName());
					} else {
						// debug("ownedview null para omember con id="+id);
					}
				}
			}
		}

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(this.directorioTrabajo
					+ File.separator + this.fichero.getName() + ".html", true)); // apertura
			// en
			// modo
			// append

			// pintamos la capa de titulo
			pw.println("\n <!--CAPA DE TÍTULO-->");
			pw
					.println(" <div title=\"tituloPaginaInterna\" id=\"tituloPaginaInterna\" style=\"display:block;\">");
			// pw.println("	<h4 align=\"center\">Aplicación "+
			// this.fichero.getName()+"</h4>");
			pw.println("</div>");
			// pintamos la capa del reloj de arena
			pw.println("\n <!--CAPA DE RELOJ DE ARENA-->");
			pw
					.println("<div title=\"tiempoEsperaInterna\" id=\"tiempoEsperaInterna\" style=\"display:none;\">");
			pw
					.println("	<center><font color=\"red\" size=\"5\" align=\"center\">Por favor, espere</font></center>");
			pw
					.println("	<img src=\"./imagenes/relojArena.gif\" align=\"right\"/>");
			pw.println("</div> \n");

			// el siguiente elemento se pinta para darle el foco desde
			// javascript tras ciertas operaciones. Para
			// que asi algunos eventos onchange salten.
			// pw.println("\t <input type=\"hidden\" name=\"cpcchivato\" id=\"cpcchivato\" value=\" \" /> \n");
			pw.flush();
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}

	}

	public void generaEsqueletoDiv(String pagina, boolean home)
			throws IOException {
		debug("Generando pagina " + pagina + ".html en div");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(this.directorioTrabajo
					+ File.separator + this.fichero.getName() + ".html", true)); // el
			// nombre
			// del
			// html
			// principal
			pw.println("\n <!-- CAPA PARA PÁGINA: " + pagina.toUpperCase()
					+ " --> \n");
			if (home == false) {
				pw.println("<div  id=\"cpc" + pagina
						+ "\"  name=\"capa_cpc\" style=\"display:none;\">"); // los
				// nombres
				// de
				// las
				// capas
				// son
				// cpcxxxx
			} else {
				pw.println("<div  id=\"cpc" + pagina
						+ "\"  name=\"capa_cpc\" style=\"display:block;\">"); // los
				// nombres
				// de
				// las
				// capas
				// son
				// cpcxxxx
			}
			pw.flush();
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}

	}

	private void generaCierreDiv() throws IOException {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(this.directorioTrabajo
					+ File.separator + this.fichero.getName() + ".html", true)); // apertura
			// en
			// modo
			// append
			// pw=new PrintWriter(new
			// FileWriter(this.fichero.getName()+".html",true)); //el nombre del
			// html principal
			pw.println("</div>");
			pw.flush();
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}

	}

	/**
	 * Genera un .html con el nombre del fichero que se le pasa como
	 * argumento.En dicho html solo incluirá el título y un mensaje en el body.
	 * El pintado de los componentes de la web se realizará en otro método
	 * 
	 * @throws IOException
	 * */
	public void generaEsqueletoHTML(String fichero) throws IOException {
		debug("Generando  esqueleto pagina principal: " + fichero + ".html...");
		PrintWriter pw = null;
		try {
			// el nombre del html se corresponde con el de la clase
			pw = new PrintWriter(new FileWriter(this.directorioTrabajo
					+ File.separator + this.fichero.getName() + ".html"));
			// pw=new PrintWriter(new FileWriter(fichero+".html")); //el nombre
			// del html se corresponde con el de la clase
			// pw.println("<!-- saved from url=(0013)about:internet -->");
			// //para evitar el mensaje de IE: ""para ayudar a proteger su
			// seguridad..."
			pw.println("<html>");
			pw.println("<head>");
			pw.println("<title>" + fichero + "</title>");
			// inclusion de las hojas de estilo y ficheros javascript necesarios
			pw
					.println("<link rel=\"stylesheet\" href=\"./css/menu.css\" type=\"text/css\" />");
			pw
					.println("<link rel=\"stylesheet\" href=\"./css/tabs.css\" type=\"text/css\" />");
			pw
					.println("<link rel=\"stylesheet\" href=\"./css/jctabs.css\" type=\"text/css\" />");
			pw
					.println("<link rel=\"stylesheet\" href=\"./css/rfnet.css\" type=\"text/css\" />");
			pw
					.println("<script type=\"text/javascript\" src=\"./javascript/menu.js\"></script>");
			pw
					.println("<script type=\"text/javascript\" src=\"./javascript/tabs.js\"></script>");
			pw
					.println("<script type=\"text/javascript\" src=\"./javascript/jctabs.js\"></script>");
			pw
					.println("<script type=\"text/javascript\" src=\"./javascript/codigojavascript.js\"></script>");
			pw
					.println("<script type=\"text/javascript\" src=\"./javascript/misfunciones.js\"></script>");
			pw
					.println("<script type=\"text/javascript\" src=\"./javascript/datetimepicker.js\"></script>");
			pw
					.println("<script type=\"text/javascript\" src=\"./javascript/prototype.js\"></script>");
			pw
					.println("<script type=\"text/javascript\" src=\"./javascript/ws.js\"></script>");
			pw
					.println("<script type=\"text/javascript\" src=\"./javascript/sessvars.js\"></script>");
			pw.println("</head> \n");

			pw.println("<!--CUERPO DE LA PÁGINA HTML PRINCIPAL-->");
			pw.println("<body>");

			pw.flush();
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}
	}

	/**
	 * Metodo que simplemente añade el las etiquetas de cierre de html y body al
	 * final del fichero html correspondiente
	 * 
	 * @throws IOException
	 */
	private void generaCierreHTML(String fichero) throws IOException {
		PrintWriter pw = null;
		try {
			// pw=new PrintWriter(new FileWriter(fichero+".html",true));
			// //apertura en modo append
			pw = new PrintWriter(new FileWriter(this.directorioTrabajo
					+ File.separator + this.fichero.getName() + ".html", true)); // apertura
			// en
			// modo
			// append
			pw.println("\n <!--CIERRE PAGINA HTML PRINCIPAL-->");
			pw.println("</body>");
			pw.println("</html>");
			pw.flush();
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}

	}

	/**
	 * Este método pinta todos los componentes de la página que se corresponde
	 * con el xmiid y elementId pasado como argumento.
	 * 
	 * @throws IOException
	 * @throws PintaException
	 * */
	private void pintaPagina(String xmiid, String eId) throws IOException,
			PintaException {
		OwnedMember om = this.buscaOwnedMemberbyId(eId);
		debug("Pintando pagina " + fichero + ".html...");
		this.paginaActual = eId; // actualizamos la referencia a la página
		// actual
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(this.directorioTrabajo
					+ File.separator + this.fichero.getName() + ".html", true)); // el
			// nombre
			// del
			// html
			// principal
			// pw=new PrintWriter(new
			// FileWriter(this.fichero.getName()+".html",true)); //apertura en
			// modo append
			this.pintaComponentes(xmiid, eId, pw); // pintamos los componentes
			// de este elemento dentro
			// de él
			pw.flush();
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}

	}

	/**
	 * Se le pasa como argumento un xmiId y un elementId y pintará todos los
	 * componentes que vayan dentro del elemento que se corresponda con dicho
	 * xmiId y elementId.
	 * 
	 * @throws PintaException
	 * */
	private void pintaComponentes(String xmiid, String eId, PrintWriter pw)
			throws PintaException {

		OwnedViewsMdElement ovmde = this.buscaOwnedViewsMdElementbyXmiId(xmiid);
		if (ovmde == null)
			throw new PintaException("Error al pintar elemento " + xmiid
					+ ",OwnedViewMdElement no encontrado");
		// Iteramos sobre la lista de componentes del elemento a pintar
		for (String xid : ovmde.getParts()) { // xid se refiere a un xmiid
			try {
				OwnedViewsMdElement ov = this
						.buscaOwnedViewsMdElementbyXmiId(xid);
				if (ov == null)
					throw new PintaException("OwnedViewMdElement no encontrado");
				String id = ov.getElementId();
				String estereoName = this.averiguaStereotype(id); // Devuelve el
				// nombre
				// del
				// estereotipo
				// asociado
				// a este
				// elemento
				if (estereoName == null)
					throw new PintaException(
							"Estereotipo para elemento con elementId= " + id
									+ " no encontrado");
				// debug(id+" ->"+estereoName);
				if (estereoName.equals("Button")) {
					pintaButton(xid, id, pw);
				} else if (estereoName.equals("Label")) {
					pintaLabel(xid, id, pw);
				} else if (estereoName.equals("LinkLabel")) {
					pintaLinkLabel(xid, id, pw);
				} else if (estereoName.equals("Section")) {
					pintaSection(xid, id, pw);
				} else if (estereoName.equals("TextArea")) {
					pintaTextArea(xid, id, pw);
				} else if (estereoName.equals("Text")) {
					pintaText(xid, id, pw);
				} else if (estereoName.equals("CheckBox")) {
					pintaCheckBox(xid, id, pw);
				} else if (estereoName.equals("RadioButton")) {
					pintaRadioButton(xid, id, pw);
				} else if (estereoName.equals("ComboBox")) {
					pintaComboBox(xid, id, pw);
				} else if (estereoName.equals("Form")) {
					pintaForm(xid, id, pw);
				} else if (estereoName.equals("Menu")) {
					pintaMenu(xid, id, pw);
				} else if (estereoName.equals("MenuItem")) {
					pintaMenuItem(xid, id, pw);
				} else if (estereoName.equals("Tab")) {
					pintaTab2(xid, id, pw);
				} else if (estereoName.equals("PictureBox")) {
					pintaPictureBox(xid, id, pw);
				} else if (estereoName.equals("AudioBox")) {
					pintaAudioBox(xid, id, pw);
				} else if (estereoName.equals("ImageMap")) {
					pintaImageMap(xid, id, pw);
				} else if (estereoName.equals("DateTimePicker")) {
					pintaDateTimePicker(xid, id, pw);
				} else {
					pintaNoReconocido(xid, id, pw);
				}
				pw.flush();
				/*
				 * Si se produce PintaException dentro del for, se capturara
				 * aqui y no se interrumpira el pintado del resto de componentes
				 */
			} finally {
				pw.flush();
			}
		}

	}

	/**
	 * Pinta un DateTimePicker pasandole como argumento el xmiId y el elementId
	 * del OwnedViewsMdElement de dicho DateTimePicker
	 * 
	 * @throws PintaException
	 */
	private void pintaDateTimePicker(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaDateTimePicker, OwnedAttribute no encontrado");
		ElemStereotyped es = this.buscaElementStereotypedbyId(eid);
		if (es == null)
			throw new PintaException(
					"Error en pintaDateTimePicker, ElemStereotyped no encontrado");
		String date = "";
		boolean isClickable = true;
		if (es.existeTag("date"))
			date = es.getValorTag("date");
		if (es.existeTag("isClickable"))
			isClickable = Boolean.valueOf(es.getValorTag("isClickable"));
		if (isClickable == true) {
			pw.println("<input type=\"text\" id=\"" + oa.getName()
					+ "\" value=\"" + date + "\" name=\"" + oa.getName()
					+ "\" maxlength=\"25\" size=\"25\" "
					+ this.generaStyleSinAltoNiAncho(xmiid, eid) + "/>");
			pw.println("<a href=\"javascript:NewCal('" + oa.getName()
					+ "','ddmmyyyy')\" "
					+ this.generaStyleIconoCalendario(xmiid, eid) + ">");
			pw
					.println("\t <img src=\"./imagenes/cal.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"Elige una fecha\">");
			pw.println("</a>");
		} else {
			pw.println("<input type=\"text\" id=\"" + oa.getName()
					+ "\" value=\"" + date + "\" name=\"" + oa.getName()
					+ "\" disabled=\"true\" maxlength=\"25\" size=\"25\" "
					+ this.generaStyleSinAltoNiAncho(xmiid, eid) + "/>");
			pw.println("<a href=\"javascript:;\"  disabled=\"true\" "
					+ this.generaStyleIconoCalendario(xmiid, eid) + ">");
			pw
					.println("\t <img src=\"./imagenes/cal.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"Elige una fecha\">");
			pw.println("</a>");
		}
	}

	/**
	 * Pinta un ImageMap pasandole como argumento el xmiId y el elementId del
	 * OwnedViewsMdElement de dicho ImageMap
	 * 
	 * @throws PintaException
	 */
	private void pintaImageMap(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		this.imageMapActual = xmiid;
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaImageMap, OwnedAttribute no encontrado");
		ElemStereotyped es = this.buscaElementStereotypedbyId(eid);
		if (es == null)
			throw new PintaException(
					"Error en pintaImageMap, ElemStereotyped no encontrado");
		String src = "./imagenes/imagenvacia.gif";
		if (es.existeTag("src"))
			src = es.getValorTag("src");
		// pw.println("<div "+this.generaStyleConBorder(xmiid,eid,"dashed")+">"+oa.getName());
		pw.println("<img src=\"" + src + "\" usemap=\"#" + oa.getName() + "\""
				+ this.generaStyle(xmiid, eid) + "/>");
		pw.println("<map name=\"" + oa.getName() + "\">");
		OwnedViewsMdElement ovmde = this.buscaOwnedViewsMdElementbyXmiId(xmiid);
		for (String xid : ovmde.getParts()) {
			String id = this.buscaOwnedViewsMdElementbyXmiId(xid)
					.getElementId();
			OwnedAttribute oaAux = this.buscaOwnedAttributebyId(id);
			int[] geometry = this.adaptaCoordenadasAImageMap(xid);
			ElemStereotyped esAux = this.buscaElementStereotypedbyId(id);
			String url = "./paginas/linkroto.html";
			if (esAux.existeTag("url"))
				url = esAux.getValorTag("url");
			/*
			 * pw.println("<area shape=\"rect\" title=\""+oaAux.getName()+
			 * "\" coords="
			 * +geometry[0]+","+geometry[1]+","+geometry[2]+","+geometry[3]+
			 * " href='"+url+"'/>");
			 */
			pw.println("<area shape=\"rect\" title=\"" + url + "\" coords="
					+ geometry[0] + "," + geometry[1] + "," + geometry[2] + ","
					+ geometry[3] + " href='" + url + "'/>");
		}
		pw.println("</map>");
		// pw.println("</div>");

	}

	/**
	 * Metodo auxiliar que usa pintaImageMap para adaptar las coordenadas de un
	 * linklabel a las fronteras del ImageMap. Como argumento al metodo se le
	 * pasa el xmi-id de dicho linklabel
	 * 
	 * @throws PintaException
	 **/
	private int[] adaptaCoordenadasAImageMap(String xmiid)
			throws PintaException {
		int[] res = new int[4];
		OwnedViewsMdElement ovmde = this.buscaOwnedViewsMdElementbyXmiId(xmiid);
		if (ovmde == null)
			throw new PintaException(
					"Error en adaptaCoordenadasAImageMap, OwnedViewsMdElement no encontrado");
		int[] geometryLinkLabel = ovmde.getGeometry();
		int[] geometryImageMap = this.buscaOwnedViewsMdElementbyXmiId(
				ovmde.getXmiidPadre()).getGeometry();
		res[0] = geometryLinkLabel[0] - geometryImageMap[0];
		res[1] = geometryLinkLabel[1] - geometryImageMap[1];
		res[2] = res[0] + geometryLinkLabel[2];
		res[3] = res[1] + geometryLinkLabel[3];
		return res;
	}

	/**
	 * Pinta un PictureBox(una imagen) pasandole como argumento el xmiId y el
	 * elementId del OwnedViewsMdElement de dicho PictureBox
	 * 
	 * @throws PintaException
	 */
	private void pintaPictureBox(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaPictureBox, OwnedAttribute no encontrado");
		ElemStereotyped es = this.buscaElementStereotypedbyId(eid);
		if (es == null)
			throw new PintaException(
					"Error en pintaPictureBox, ElemStereotyped no encontrado");
		String file = "imagenvacia.jpeg";
		String src = "./imagenes/imagenvacia.gif";
		boolean isClickable = true;
		if (es.existeTag("file"))
			file = es.getValorTag("file");
		if (es.existeTag("src"))
			src = es.getValorTag("src");
		if (es.existeTag("isClickable"))
			isClickable = Boolean.valueOf(es.getValorTag("isClickable"));
		if (isClickable == false)
			pw.println("<img name=\"" + file + "\" id=\"" + file + "\" src=\""
					+ src + "\" onclick='notisclickable();' "
					+ this.generaStyle(xmiid, eid) + " />");
		else
			pw.println("<img name=\"" + file + "\" id=\"" + file + "\" src=\""
					+ src + "\" " + this.generaStyle(xmiid, eid) + " />");
		pw.flush();
	}

	/**
	 * Pinta un AudioBox pasandole como argumento el xmiId elementId del
	 * OwnedViewsMdElement de dicho AudioBox
	 * 
	 * @throws PintaException
	 */
	private void pintaAudioBox(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaAudioBox, OwnedAttribute no encontrado");
		ElemStereotyped es = this.buscaElementStereotypedbyId(eid);
		if (es == null)
			throw new PintaException(
					"Error en pintaAudioBox, ElemStereotyped no encontrado");
		String file = "error.wav";
		String src = "./sonidos/error.wav";
		boolean isClickable = true;
		if (es.existeTag("file"))
			file = es.getValorTag("file");
		if (es.existeTag("src"))
			src = es.getValorTag("src");
		if (es.existeTag("isClickable"))
			isClickable = Boolean.valueOf(es.getValorTag("isClickable"));
		if (isClickable == false)
			pw
					.println("<embed name=\""
							+ file
							+ "\" id=\""
							+ file
							+ "\" src=\""
							+ src
							+ "\" type=\"audio/mpeg\" autostart=\"false\" onclick='notisclickable();' "
							+ this.generaStyle(xmiid, eid) + "> </embed>");
		else
			pw.println("<embed name=\"" + file + "\" id=\"" + file
					+ "\" src=\"" + src
					+ "\" type=\"audio/mpeg\" autostart=\"false\" "
					+ this.generaStyle(xmiid, eid) + "> </embed>");

		pw.flush();
	}

	/**
	 * Pinta un Tab pasandole como argumento el xmiId y el elementId del
	 * OwnedViewsMdElement de dicho Tab
	 * 
	 * @throws PintaException
	 * @throws IOException
	 **/
	private void pintaTab(String xmiid, String eid, PrintWriter pw)
			throws PintaException, IOException {
		/*
		 * Buscamos el TabArea al que pertenece esta Tab, incrementamos el
		 * numero de tabs de esa TabArea, y pintamos dicha Tab
		 */
		TabArea ta = this.buscaTabArea(this.buscaOwnedViewsMdElementbyXmiId(
				xmiid).getXmiidPadre());
		if (ta == null)
			throw new PintaException(
					"Error en pintaTab, TabArea padre no encontrado");
		ta.addTab(xmiid); // Añadimos el xmiid de la tab a su TabArea
		// correspondiente
		/* Actualizamos la referencia de la Tab Actual procesada */
		this.tabActual = xmiid;
		/* Generamos el fichero .html que contendrá el cuerpo de la tab */
		this.generaCuerpoTab(xmiid, eid);
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		OwnedAttribute oapadre = this.buscaOwnedAttributebyId(this
				.buscaOwnedViewsMdElementbyXmiId(xmiid).getElementIdPadre());
		if (oa == null || oapadre == null)
			throw new PintaException(
					"Error en pintaTab, OwnedAttribute no encontrado");
		pw.println("<a class=\"tab\" href=\"" + oa.getName()
				+ ".html\" target=\"" + oapadre.getName() + "\">"
				+ oa.getName() + "</a>");

		pw.flush();
	}

	/**
	 * Pinta un Tab pasandole como argumento el xmiId y el elementId del
	 * OwnedViewsMdElement de dicho Tab
	 * 
	 * @throws PintaException
	 **/
	private void pintaTab2(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		/*
		 * Buscamos el TabArea al que pertenece esta Tab, incrementamos el
		 * numero de tabs de esa TabArea, y pintamos dicha Tab
		 */
		TabArea ta = this.buscaTabArea(this.buscaOwnedViewsMdElementbyXmiId(
				xmiid).getXmiidPadre());
		if (ta == null)
			throw new PintaException(
					"Error en pintaTab2, TabArea padre no encontrado");
		// si esta tab está dentro de otra tab: ERROR, no se permite el
		// anidamiento de tabs
		if (anidadaEnOtraTab(xmiid))
			throw new PintaException(
					"No se permite el anidamiento de elementos Tab");

		ta.addTab(xmiid); // Añadimos el xmiid de la tab a su TabArea
		// correspondiente
		/* Actualizamos la referencia de la Tab Actual procesada */
		this.tabActual = xmiid;
		// Obtenemos el numero de tabs que hay en el TabArea donde se encuentra
		// la Tab que se esta procesando
		int numTabs = this.getNumTabsEnTabArea(ta.getXmiid());
		// Obtenemos el numero de TabArea que es
		int indice = listaTabArea.size();
		// Obtenemos el numero de Tab que es
		Integer i = ta.getIndiceByXmiIdTab(xmiid);
		if (i == null)
			throw new PintaException(
					"Error en pintaTab, indice de la Tab no encontrado");
		if (i == 1) {
			pw.println("<div title=\"" + i + "\" id=\"tab" + indice + "-" + i
					+ "\" style=\"display:block\">");
		} else {
			pw.println("<div title=\"" + i + "\" id=\"tab" + indice + "-" + i
					+ "\" style=\"display:none\">");
		}

		this.pintaComponentes(xmiid, eid, pw);
		pw.println("</div>");

		pw.flush();
	}

	/**
	 * Metodo que genera el fichero .html que contiene el contenido de la Tab
	 * cuyo xmiid y elementid se pasan como argumento. Si la Tab se llama por
	 * ejemplo "t3", el html se llamará "t3.html"
	 * 
	 * @throws PintaException
	 * @throws IOException
	 * **/
	private void generaCuerpoTab(String xmiid, String eid)
			throws PintaException, IOException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en generaCuerpoTag, OwnedAttribute no encontrado");
		String fich = oa.getName();
		PrintWriter pwtab = null;
		try {
			this.generaEsqueletoHTML(fich);
			pwtab = new PrintWriter(new FileWriter(fich + ".html", true));
			this.pintaComponentes(xmiid, eid, pwtab);
			this.generaCierreHTML(fich);
			pwtab.flush();
		} finally {
			if (pwtab != null) {
				pwtab.flush();
				pwtab.close();
			}
		}
	}

	/**
	 * Metodo que recorre listaTabArea en busca del TabArea que se corresponda
	 * con el xmiid pasado como argumento. Si no lo encuentra devuelve null.
	 * */
	private TabArea buscaTabArea(String xmiid) {
		TabArea res = null;
		for (TabArea ta : this.listaTabArea) {
			if (ta.getXmiid().equals(xmiid)) {
				res = ta;
			}
		}
		return res;
	}

	/**
	 * Pinta un MenuItem pasandole como argumento el xmiid y el elementId del
	 * OwnedViewsMdElement de dicho MenuItem.
	 * 
	 * @throws PintaException
	 **/
	private void pintaMenuItem(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaMenuItem, OwnedAttribute no encontrado");
		pw
				.println("<a class=\"menuButton\" onclick=\"return buttonClick(event, '"
						+ oa.getName()
						+ "');\" onmouseover=\"buttonMouseover(event, '"
						+ oa.getName() + "');\">" + oa.getName() + "</a>");
		pw.println("<div id=\"" + oa.getName()
				+ "\" class=\"menu\" onmouseover=\"menuMouseover(event)\">");
		this.pintaComponentesMenuItem(xmiid, eid, pw);
		pw.flush();
		pw.println("</div>");
		pw.flush();
	}

	/**
	 * Método auxiliar de pintaMenuItem que genera el código html necesario para
	 * pintar los elementos de la lista desplegable de cada MenuItem (Ej: Para
	 * un tipico menú File : open,save,close,...). Solo permitimos definir
	 * dentro de un MenuItem elementos Button
	 * 
	 * @throws PintaException
	 * */
	private void pintaComponentesMenuItem(String xmiid, String eid,
			PrintWriter pw) throws PintaException {
		OwnedViewsMdElement ovmde = this.buscaOwnedViewsMdElementbyXmiId(xmiid);
		if (ovmde == null)
			throw new PintaException(
					"Error en pintaComponentesMenuItem del menuItem " + xmiid
							+ ",OwnedViewMdElement no encontrado");

		// Iteramos sobre los elementos del MenuItem
		for (String xid : ovmde.getParts()) {
			OwnedViewsMdElement ov = buscaOwnedViewsMdElementbyXmiId(xid);
			if (ov == null)
				throw new PintaException("OwnedViewMdElement no encontrado");
			OwnedAttribute oa = this.buscaOwnedAttributebyId(ov.getElementId());
			if (oa == null)
				throw new PintaException(
						"OwnedAttribute para opcion de menuItem no encontrado");
			// Solo permitimos definir dentro de un MenuItem elementos Button
			String est = this.averiguaStereotype(ov.getElementId());
			if (est.equals("Button") == false) {
				throw new PintaException(
						"En un MenuItem solo puede haber elementos Button. Se encontró un "
								+ est);
			}

			// Compruebo si hay alguna asociación de tipo A o tipo B en la que
			// esté implicado este componente de menuItem.
			// Si es así, se refleja como el correspondiente evento que se
			// especificó en el diagrama UML.
			AssociationClass acA = this
					.buscaElementoEnAlgunaAssociationTipoA(oa);
			List<AssociationClass> listacB = this
					.buscaElementoEnAlgunaAssociationTipoB(oa);

			if (acA == null && (listacB == null || listacB.size() == 0)) {
				pw.println("<a class=\"menuItem\" href=\"javascript:;\">"
						+ oa.getName() + "</a>");
			} else if (acA != null && listacB == null) {
				pw.println("<a href=\"javascript:;\" class=\"menuItem\"  id=\""
						+ oa.getName() + "\" name=\"" + oa.getName() + "\"  "
						+ this.construyeCadenaEventosTipoA(acA) + ">"
						+ oa.getName() + "</a>");
			} else if (acA == null && listacB != null) {
				// Hay que construir la cadena de eventos para este elemento(
				// ej: onclick="..." onchange=" "...)
				pw.println("<a href=\"javascript:;\" class=\"menuItem\"  id=\""
						+ oa.getName() + "\" name=\"" + oa.getName() + "\"  "
						+ this.construyeCadenaEventosTipoB(listacB) + ">"
						+ oa.getName() + "</a>");
			} else if (acA != null && listacB != null) {
				pw.println("<a href=\"javascript:;\" class=\"menuItem\"  id=\""
						+ oa.getName() + "\" name=\"" + oa.getName() + "\"  "
						+ this.construyeCadenaEventosTipoAyB(listacB, acA)
						+ ">" + oa.getName() + "</a>");
			}

			pw.flush();
		}

	}

	/**
	 * Pinta un Menu pasandole como argumento el xmiid y el elementId del
	 * OwnedViewsMdElement de dicho Menu.
	 * 
	 * @throws PintaException
	 **/
	private void pintaMenu(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaMenu, OwnedAttribute no encontrado");
		pw.println("<div class=\"menuBar\" style=\"width:80%;\">");
		this.pintaComponentes(xmiid, eid, pw);
		pw.println("</div>");
		pw.flush();
	}

	/**
	 * Pinta un Form pasandole como argumento el xmiid y el elementId del
	 * OwnedViewsMdElement de dicho Form.
	 **/
	private void pintaForm(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		ElemStereotyped es = this.buscaElementStereotypedbyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaForm, OwnedAttribute no encontrado");
		if (es == null)
			throw new PintaException(
					"Error en pintaForm, ElemStereotyped no encontrado");
		String action = "";
		String method = "post";
		if (es.existeTag("action"))
			action = es.getValorTag("action");
		if (es.existeTag("method"))
			method = es.getValorTag("method");

		pw.println("<div " + this.generaStyleConBorder(xmiid, eid, "dashed")
				+ ">" + oa.getName() + "</div>");
		pw.println("<form name=\"" + oa.getName() + "\" id=\"" + oa.getName()
				+ "\" action=\"" + action + "\" method=\"" + method + "\">");
		this.pintaComponentes(xmiid, eid, pw);
		pw.println("</form>");
		pw.flush();
	}

	/**
	 * Pinta un ComboBox pasandole como argumento el xmiid y el elementId del
	 * OwnedViewsMdElement de dicho ComboBox.
	 **/
	private void pintaComboBox(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaComboBox, OwnedAttribute no encontrado");

		// Compruebo si hay alguna asociación de tipo A o tipo B en la que está
		// implicado este combo. Si es asi, se refleja como
		// el correspondiente evento que se especificó en el diagrama UML.
		AssociationClass acA = this.buscaElementoEnAlgunaAssociationTipoA(oa);
		List<AssociationClass> listacB = this
				.buscaElementoEnAlgunaAssociationTipoB(oa);

		pw.println("<div " + this.generaStyleConBorder(xmiid, eid, "dashed")
				+ ">" + oa.getName() + "</div>");
		if (acA == null && (listacB == null || listacB.size() == 0)) {
			pw.println("<select  id=\"" + oa.getName() + "\"  name=\""
					+ oa.getName() + "\" "
					+ this.generaStyleSinAltoNiAncho(xmiid, eid) + ">");
		} else if (acA != null && listacB == null) {
			pw.println("<select  id=\"" + oa.getName() + "\"  name=\""
					+ oa.getName() + "\" "
					+ this.construyeCadenaEventosTipoA(acA) + " "
					+ this.generaStyleSinAltoNiAncho(xmiid, eid) + ">");
		} else if (acA == null && listacB != null) {
			// Hay que construir la cadena de eventos para este elemento( ej:
			// onclick="..." onchange=" "...)
			pw.println("<select  id=\"" + oa.getName() + "\"  name=\""
					+ oa.getName() + "\" "
					+ this.construyeCadenaEventosTipoB(listacB) + " "
					+ this.generaStyleSinAltoNiAncho(xmiid, eid) + ">");
		} else if (acA != null && listacB != null) {
			pw.println("<select  id=\"" + oa.getName() + "\"  name=\""
					+ oa.getName() + "\" "
					+ this.construyeCadenaEventosTipoAyB(listacB, acA) + " "
					+ this.generaStyleSinAltoNiAncho(xmiid, eid) + ">");
		}

		OwnedViewsMdElement ovmde = this.buscaOwnedViewsMdElementbyXmiId(xmiid);
		for (String xid : ovmde.getParts()) {
			String id = this.buscaOwnedViewsMdElementbyXmiId(xid)
					.getElementId();
			OwnedAttribute oaAux = this.buscaOwnedAttributebyId(id);
			ElemStereotyped es = this.buscaElementStereotypedbyId(id);
			String value = oaAux.getName();
			if (es != null && es.existeTag("value")) {
				value = es.getValorTag("value");
			}
			pw.println("<option value=\"" + value + "\">" + oaAux.getName()
					+ "</option>");
		}
		pw.println("</select>");
		pw.flush();
	}

	/**
	 * Pinta un Button pasandole como argumento el xmiId y elementId del
	 * OwnedViewsMdElement de dicho button
	 **/
	private void pintaButton(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		ElemStereotyped es = this.buscaElementStereotypedbyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaButton, OwnedAttribute no encontrado");
		if (es == null)
			throw new PintaException(
					"Error en pintaButton, ElemStereotyped no encontrado");
		String type = "button";
		boolean isClickable = true;
		if (es.existeTag("type"))
			type = es.getValorTag("type");
		if (es.existeTag("isClickable"))
			isClickable = Boolean.valueOf(es.getValorTag("isClickable"));

		// Compruebo si hay alguna asociación de tipo A o tipo B en la que está
		// implicado este botón. Si es así, se refleja como
		// el correspondiente evento que se especificó en el diagrama UML.
		AssociationClass acA = this.buscaElementoEnAlgunaAssociationTipoA(oa);
		List<AssociationClass> listacB = this
				.buscaElementoEnAlgunaAssociationTipoB(oa);
		String disabled = "";
		if (isClickable == false)
			disabled = "disabled";
		if (acA == null && (listacB == null || listacB.size() == 0)) {
			pw.println("<input type=\"" + type + "\" id=\"" + oa.getName()
					+ "\"  name=\"" + oa.getName() + "\" " + disabled
					+ " value=\"" + oa.getName() + "\" "
					+ this.generaStyle(xmiid, eid) + " />");
		} else if (acA != null && listacB == null) {
			pw.println("<input type=\"" + type + "\" id=\"" + oa.getName()
					+ "\"  name=\"" + oa.getName() + "\" " + disabled
					+ " value=\"" + oa.getName() + "\" "
					+ this.construyeCadenaEventosTipoA(acA) + " "
					+ this.generaStyle(xmiid, eid) + " />");
		} else if (acA == null && listacB != null) {
			// Hay que construir la cadena de eventos para este elemento( ej:
			// onclick="..." onchange=" "...)
			pw.println("<input type=\"" + type + "\" id=\"" + oa.getName()
					+ "\" name=\"" + oa.getName() + "\" " + disabled
					+ " value=\"" + oa.getName() + "\" "
					+ this.construyeCadenaEventosTipoB(listacB) + " "
					+ this.generaStyle(xmiid, eid) + " />");
		} else if (acA != null && listacB != null) {
			pw.println("<input type=\"" + type + "\" id=\"" + oa.getName()
					+ "\" name=\"" + oa.getName() + "\" " + disabled
					+ " value=\"" + oa.getName() + "\" "
					+ this.construyeCadenaEventosTipoAyB(listacB, acA) + " "
					+ this.generaStyle(xmiid, eid) + " />");
		}

		pw.flush();
	}

	/**
	 * Devuelve un String con el contenido del evento onclick necesario para
	 * ejecutar el correspondiente Web Service.
	 * */
	/*
	 * private String construyeOnClickAosiacionTipoB(AssociationClass acB)
	 * throws PintaException { String res="onClick=\"\"";
	 * 
	 * 
	 * OwnedMember om=this.buscaOwnedMemberbyId(acB.getId()); if (om==null)
	 * throw newPintaException(
	 * "No se encontro el OwnedMember para la associatonClass con id="
	 * +acB.getId());
	 * 
	 * 
	 * //Recogemos los parametros del metodo del WS a ejecutar List<String>
	 * params=this.recogeParametrosMetodoAsociacionTipoB(acB,acB.getMetodo());
	 * if (params==null) throw new
	 * PintaException("No se encontraron los parametros para el metodo "
	 * +acB.getMetodo()); String parameters="'"+params.get(0)+"'"; for (int
	 * i=1;i<params.size();i++){
	 * parameters=parameters+","+"'"+params.get(i)+"'"; }
	 * 
	 * //Obtenemos la pagina destino de la transición String paginaDestino="";
	 * if (this.buscaOwnedMemberbyId(acB.getIdOwnedMemberA()).getName().
	 * equals(this.buscaOwnedMemberbyId(acB.getIdOwnedMemberB()).getName())){
	 * //El origen y el destino son la misma pagina paginaDestino="this"; }else{
	 * paginaDestino
	 * ="./"+this.buscaOwnedMemberbyId(acB.getIdOwnedMemberB()).getName
	 * ()+".html"; }
	 * 
	 * 
	 * 
	 * 
	 * res="\" onClick=\"ejecutaWSbyIBM('"+paginaDestino+"','"+acB.getWsdl()+"','"
	 * +acB.getPuerto()+"','"+acB.getMetodo()+"',"+parameters+")\" "; return
	 * res; }
	 */

	/**
	 * Devuelve un String con el contenido de los eventos de tipo A, que son
	 * transiciones simples, mas los enventos de tipo B, los cuales ejecutan un
	 * metodo de un Web Service.
	 * 
	 * @throws PintaException
	 * */
	private String construyeCadenaEventosTipoAyB(
			List<AssociationClass> listacB, AssociationClass acA)
			throws PintaException {
		if (this.compruebaRepeticionEventos(listacB, acA))
			throw new PintaException(
					"No puede haber dos asociaciones con el mismo origen y el mismo evento");
		String cadA = this.construyeCadenaEventosTipoA(acA);
		String cadB = this.construyeCadenaEventosTipoB(listacB);
		return cadA + " " + cadB;
	}

	/**
	 * Devuelve un String con el contenido de los enventos de tipo B, los cuales
	 * ejecutan un metodo de un Web Service.(ej: <input type="miboton"
	 * onclick="...." onchange="..." ..../>
	 * 
	 * @throws ParserXmiException
	 * */
	private String construyeCadenaEventosTipoB(List<AssociationClass> lista)
			throws PintaException {
		String cadenaEventos = "";
		String parameters = "";

		if (compruebaRepeticionEventos(lista))
			throw new PintaException(
					"No puede haber dos asociaciones con el mismo origen y el mismo evento");

		for (AssociationClass acB : lista) {

			OwnedMember om = this.buscaOwnedMemberbyId(acB.getId());
			if (om == null)
				throw new PintaException(
						"No se encontro el OwnedMember para la associatonClass con id="
								+ acB.getId());

			// Recogemos los parametros del metodo a ejecutar
			List<String> params = this.recogeParametrosMetodoAsociacionTipoB(
					acB, acB.getMetodo());
			if (params == null)
				throw new PintaException(
						"No se encontraron los parametros para el metodo "
								+ acB.getMetodo());

			// Comprobamos si es una asociacion tipo B especial : "decideWS"
			// La cadena de eventos para una asociacion así tiene que quedar por
			// ejemplo como:
			// ....onclick="decideWS('comboOp','suma|campoA|campoB|salida', 'resta|campoA|campoB|salida')"
			// decideWS sera una funcion interna nuestra de JavaScript, no un
			// WS.
			if (esAsociacionDecideWS(acB)) {

				if (params.size() > 1)
					throw new PintaException(
							"La funcion especial decideWS solo acepta un argumento");
				parameters = "'" + params.get(0); // Ej: decideWS(comboOp)
				// <<---Esto en el UML
				for (OwnedOperation op : acB.getListadoMetodos()) { // para cada
					// operacion
					// definida
					// tras
					// decideWS
					// en el
					// UML...
					if (!op.getName().equalsIgnoreCase("decideWS")) {
						int cont = 0;
						parameters = parameters + "," + "'" + op.getName();
						for (OwnedParameter opa : op.getParametros()) {
							opa.setName(opa.getName()
									.replaceAll("&quot;", "\""));
							if (cont == 0 && opa.getName().matches("^\".*\"")) { // les
								// quitamos
								// las
								// ""
								// a
								// la
								// url
								// de
								// la
								// wsdl
								parameters = parameters
										+ "|"
										+ opa.getName().substring(1,
												opa.getName().length() - 1);
								cont++;
							} else {
								// las ctes en el UML vienen entre "". Las
								// cambiamos por cte_xxx para que lo entienda en
								// jasvascript
								if (cont > 0
										&& opa.getName().matches("^\".*\"")) {
									parameters = parameters
											+ "|"
											+ "cte_"
											+ opa.getName().substring(1,
													opa.getName().length() - 1);
								} else { // el nombre del parametro no necesita
									// tratamiento especial
									parameters = parameters + "|"
											+ opa.getName();
								}
							}

						}
					}
					parameters = parameters + "'";
				}
				// debug("cadena parameters para decideWS="+parameters);

				// Obtenemos la pagina destino de la transicion
				String paginaDestino = "";
				if (this.buscaOwnedMemberbyId(acB.getIdOwnedMemberA())
						.getName().equals(
								this.buscaOwnedMemberbyId(
										acB.getIdOwnedMemberB()).getName())) {
					// la pagina origen y destino son la misma pagina
					paginaDestino = "this";
				} else {
					paginaDestino = this.buscaOwnedMemberbyId(
							acB.getIdOwnedMemberA()).getName();
				}

				cadenaEventos = cadenaEventos + acB.getEvento()
						+ "=\"decideWS('" + paginaDestino + "'," + parameters
						+ ")\" ";

				// Comprobamos si la asociacion tiene como operacion a ejecutar
				// la funcion especial
				// cargaFichero. La cual coge el valor de un input type file,
				// lee el fichero, y vuelca
				// el resultado sobre un determinado elemento HTML. Se debe de
				// construir una cadena
				// de eventos como p.e.:
				// onchange="cargaFichero('buscador','this', 'area')"
			} else if (esAsociacionCargaFichero(acB)) {

				// Ej: cargaFichero('browser','areaTexto') <<---Esto en el UML
				if (params.size() != 2)
					throw new PintaException(
							"La función especial cargaFichero necesita 2 argumentos");

				// Obtenemos id del elemento fuente (normalmente un input
				// type="file"
				String idElemFuente = params.get(0);
				// Obtenemos la pagina destino de la transicion
				String paginaDestino = "";
				if (this.buscaOwnedMemberbyId(acB.getIdOwnedMemberA())
						.getName().equals(
								this.buscaOwnedMemberbyId(
										acB.getIdOwnedMemberB()).getName())) {
					// la pagina origen y destino son la misma pagina
					paginaDestino = "this";
				} else {
					paginaDestino = this.buscaOwnedMemberbyId(
							acB.getIdOwnedMemberA()).getName();
				}
				// Obtenemos id del elemento destino
				String idElemDestino = params.get(1);

				cadenaEventos = cadenaEventos + acB.getEvento()
						+ "=\"cargaFichero('" + idElemFuente + "','"
						+ paginaDestino + "','" + idElemDestino + "')\" ";

				// Comprueba si la AssociationClass pasada como parametro tiene
				// como metodo a ejecutar la funcion especial
				// "addOperationWS",la cual añadir un metodo mas a ejecutar a un
				// elemento HTML "juez", de los que deciden
				// que metodo ejecutar.
			} else if (esAsociacionAddOperation(acB)) {
				// Ej:
				// addOperationWS('juez','mete_wsdl','label_operacion','mete_operacion','mete_argumentos','dire_crecimiento')
				// <<---Esto en el UML
				if (params.size() != 6)
					throw new PintaException(
							"La función especial addOperationWS necesita 6 argumentos");

				// Obtenemos id del elemento juez (normalmente un combo o un
				// grupo de radiobuttons)
				String idElemJuez = params.get(0);
				// Obtenemos id del elemento disparador
				// String idElemDisparador=params.get(1);
				// Resto de elementos necesarios
				String wsdl = params.get(1);
				wsdl = calculaSiArgumentoEsCte(wsdl);
				String labeloperacion = params.get(2);
				labeloperacion = calculaSiArgumentoEsCte(labeloperacion);
				String operacion = params.get(3);
				operacion = calculaSiArgumentoEsCte(operacion);
				String argumentos = params.get(4);
				argumentos = calculaSiArgumentoEsCte(argumentos);
				String direccionCrecimiento = params.get(5);
				direccionCrecimiento = calculaSiArgumentoEsCte(direccionCrecimiento);

				// Obtenemos la pagina destino de la transicion
				String paginaDestino = "";
				if (this.buscaOwnedMemberbyId(acB.getIdOwnedMemberA())
						.getName().equals(
								this.buscaOwnedMemberbyId(
										acB.getIdOwnedMemberB()).getName())) {
					// la pagina origen y destino son la misma pagina
					paginaDestino = "this";
				} else {
					paginaDestino = this.buscaOwnedMemberbyId(
							acB.getIdOwnedMemberA()).getName();
				}

				cadenaEventos = cadenaEventos + acB.getEvento()
						+ "=\"addOperationWS('" + paginaDestino + "','" + wsdl
						+ "','" + idElemJuez + "','" + labeloperacion + "','"
						+ operacion + "','" + argumentos + "','"
						+ direccionCrecimiento + "')\" ";

				// Comprueba si la AssociationClass pasada como parametro tiene
				// como metodo a ejecutar la funcion especial
				// "removeOperationWS",la cual eliminara un elemento HTML que
				// representa a una operacion existente.
			} else if (esAsociacionRemoveOperation(acB)) {
				// Ej: addOperationWS('juez','mete_operacion') <<---Esto en el
				// UML
				if (params.size() != 2)
					throw new PintaException(
							"La función especial removeOperationWS necesita 2 argumentos");

				// Obtenemos id del elemento juez (normalmente un combo o un
				// grupo de radiobuttons)
				String idElemJuez = params.get(0);
				String operacion = params.get(1);
				operacion = calculaSiArgumentoEsCte(operacion);

				// Obtenemos la pagina destino de la transicion
				String paginaDestino = "";
				if (this.buscaOwnedMemberbyId(acB.getIdOwnedMemberA())
						.getName().equals(
								this.buscaOwnedMemberbyId(
										acB.getIdOwnedMemberB()).getName())) {
					// la pagina origen y destino son la misma pagina
					paginaDestino = "this";
				} else {
					paginaDestino = this.buscaOwnedMemberbyId(
							acB.getIdOwnedMemberA()).getName();
				}

				cadenaEventos = cadenaEventos + acB.getEvento()
						+ "=\"removeOperationWS('" + paginaDestino + "','"
						+ idElemJuez + "','" + operacion + "')\" ";

			} else if (esAsociacionUserOperation(acB)) {
				String nombre = acB.getMetodo();
				nombre = nombre.substring(7);
				// System.out.println("nombre="+nombre);
				parameters = ""; // Ej: decideWS(comboOp) <<---Esto en el UML
				for (OwnedOperation op : acB.getListadoMetodos()) { // para cada
					// operacion
					// definida
					// ...
					if (op.getName().equalsIgnoreCase(acB.getMetodo())) {
						int cont = 0;
						if (op.getParametros() == null
								|| op.getParametros().size() == 0) {
							parameters = "";
						} else {
							for (OwnedParameter opa : op.getParametros()) {
								opa.setName(opa.getName().replaceAll("&quot;",
										"\""));
								if (cont == 0) {
									parameters = "'" + opa.getName() + "'";
								} else {
									// las ctes en el UML vienen entre "". Las
									// cambiamos por cte_xxx para que lo
									// entienda en jasvascript
									if (cont > 0
											&& opa.getName().matches("^\".*\"")) {
										parameters = parameters
												+ ","
												+ "cte_"
												+ opa
														.getName()
														.substring(
																1,
																opa
																		.getName()
																		.length() - 1);
									} else { // el nombre del parametro no
										// necesita tratamiento especial
										parameters = parameters + ",'"
												+ opa.getName() + "'";
									}
								}
								cont++;
							}
						}
					}

				}
				cadenaEventos = cadenaEventos + acB.getEvento() + "=\""
						+ nombre + "(" + parameters + ")\" ";

				// Se formara la cadena con la llamada a
				// ejecutaWSByIBM(.........)
			} else {

				parameters = "'" + this.calculaSiArgumentoEsCte(params.get(0))
						+ "'";
				for (int i = 1; i < params.size(); i++) {
					parameters = parameters + "," + "'"
							+ this.calculaSiArgumentoEsCte(params.get(i)) + "'";
				}

				// Obtenemos la pagina destino de la transicion
				String paginaDestino = "";
				if (this.buscaOwnedMemberbyId(acB.getIdOwnedMemberA())
						.getName().equals(
								this.buscaOwnedMemberbyId(
										acB.getIdOwnedMemberB()).getName())) {
					// la pagina origen y destino son la misma pagina
					paginaDestino = "this";
				} else {
					paginaDestino = this.buscaOwnedMemberbyId(
							acB.getIdOwnedMemberA()).getName();
				}

				acB.setWsdl(acB.getWsdl().replaceAll("&quot;", "")); // les
				// quitamos
				// las
				// ""
				// por
				// si la
				// wsdl
				// viene
				// entrecomillada
				cadenaEventos = cadenaEventos + acB.getEvento()
						+ "=\"ejecutaWSbyIBM('" + paginaDestino + "','"
						+ acB.getWsdl() + "','" + acB.getMetodo() + "',"
						+ parameters + ")\" ";
			}

		}
		return cadenaEventos;
	}

	/**
	 * Comprueba si la cadena que se le pasa com argumento está entrecomillada.
	 * Si es asi, la transforma a cte_xxx. Ej: &quot;Hola&quot; --->cte_hola
	 * **/
	private String calculaSiArgumentoEsCte(String cad) {
		if (!cad.contains("quot;")) {
			return cad;
		} else {
			cad = cad.replaceAll("&quot;", "");
			// System.out.println("cte_"+cad);
			return "cte_" + cad;
		}
	}

	/**
	 * Comprueba si la AssociationClass pasada como parámetro tiene como metodo
	 * a ejecutar la funcion especial "cargaFichero", la cual toma como
	 * argumento un input type="file",recoge el valor del fichero apuntado , lee
	 * dicho fichero y vuelca su contenido sobre un elemento HTML destino.
	 * */
	private boolean esAsociacionCargaFichero(AssociationClass acB) {
		if (acB.getMetodo().equalsIgnoreCase("cargaFichero")) {
			for (OwnedOperation op : acB.getListadoMetodos()) {
				if (op.getName().equalsIgnoreCase("cargaFichero")) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean esAsociacionUserOperation(AssociationClass acB) {
		if (acB.getMetodo().contains("CUSTOM:")) {
			// System.out.println("FUNCION USER");
			// for (OwnedOperation op:acB.getListadoMetodos()){
			// if (op.getName().contains("USER:")){
			// return true;
			// }
			// }
			return true;
		}
		return false;
	}

	/**
	 * Comprueba si la AssociationClass pasada como parámetro tiene como metodo
	 * a ejecutar la funcion especial "addOperationWS",la cual añadirá un metodo
	 * mas a ejecutar a un elemento HTML "juez", de los que deciden qué metodo
	 * ejecutar.
	 * */
	private boolean esAsociacionAddOperation(AssociationClass acB) {
		if (acB.getMetodo().equalsIgnoreCase("addOperationWS")) {
			for (OwnedOperation op : acB.getListadoMetodos()) {
				if (op.getName().equalsIgnoreCase("addOperationWS")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Comprueba si la AssociationClass pasada como parámetro tiene como metodo
	 * a ejecutar la funcion especial "removeOperationWS",la cual eliminaré un
	 * metodo a ejecutar de un elemento HTML "juez", de los que deciden qué
	 * metodo ejecutar.
	 * */
	private boolean esAsociacionRemoveOperation(AssociationClass acB) {
		if (acB.getMetodo().equalsIgnoreCase("removeOperationWS")) {
			for (OwnedOperation op : acB.getListadoMetodos()) {
				if (op.getName().equalsIgnoreCase("removeOperationWS")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Comprueba si la AssociationClass pasada como parámetro tiene como metodo
	 * a ejecutar la funcion especial "decideWS", la cual toma como argumento un
	 * elemento html(como un combo o grupo de radiobuttons), y segun el valor de
	 * estos ejecutará un determinado WS. Tambien comprueba que la definicion de
	 * decideWS está en el listado de operaciones.(metodos)
	 * */
	private boolean esAsociacionDecideWS(AssociationClass acB) {
		if (acB.getMetodo().equalsIgnoreCase("decideWS")) {
			for (OwnedOperation op : acB.getListadoMetodos()) {
				if (op.getName().equalsIgnoreCase("decideWS")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Comprueba que el listado de AssociationClass que se pasa como argumento
	 * no contenga mas de una AssociationClass con el mismo atributo "evento".
	 * Ya que si es asi, se podrian dar casos como este: <input type="button"
	 * onclick="suma(...) onclick="resta" .... El listado que se pasa como
	 * argumento tiene la peculiaridad de que su atributo origen es el mismo
	 * para todos los elementos de dicho listado.
	 * */
	private boolean compruebaRepeticionEventos(List<AssociationClass> lista) {
		String[] eventos = new String[lista.size()];
		int k = 0;
		for (AssociationClass ac : lista) {
			eventos[k] = ac.getEvento();
			k++;
		}

		for (int i = 0; i < eventos.length; i++) {
			for (int j = i + 1; j < eventos.length; j++) {
				if (eventos[i].equalsIgnoreCase(eventos[j])) { // con que haya
					// dos eventos
					// iguales,
					// salimos
					debug("DOS EVENTOS IGUALES");
					return true;
				}
			}
		}

		return false;

	}

	/**
	 * Comprueba que el listado de AssociationClass que se pasa como argumento
	 * no contenga mas de una AssociationClass con el mismo atributo "evento".
	 * Ya que si es asi, se podrian dar casos como este: <input type="button"
	 * onclick="suma(...) onclick="resta" .... Luego comprueba que el evento del
	 * objeto AssociationClass que se pasa como parametro(que sera de tipo A, no
	 * coincide con ninguno de los que se pasa en la lista. El listado que se
	 * pasa como argumento tiene la peculiaridad de que su atributo origen es el
	 * mismo para todos los elementos de dicho listado
	 * */
	private boolean compruebaRepeticionEventos(List<AssociationClass> lista,
			AssociationClass acA) {
		if (this.compruebaRepeticionEventos(lista)) {
			return true;
		} else {
			for (AssociationClass ac : lista) {
				if (ac.getEvento().equalsIgnoreCase(acA.getEvento())) {
					return true;
				}
			}
			return false;
		}

	}

	private String construyeCadenaEventosTipoA(AssociationClass acA) {
		// el destino de una asociacion es el extremo A de la misma.
		String paginaDestino = this.buscaOwnedMemberbyId(
				acA.getIdOwnedMemberA()).getName();
		return acA.getEvento() + "=\"transitar('" + paginaDestino + "');\" ";
	}

	/**
	 * Este metodo le llega un AssociationClass y el nombre de un metodo y
	 * devuelve un array con los nombres de los parametros de dicho metodo que
	 * está declarado en la asociacion correspondiente. Los parametros que se
	 * recogen son tanto los de entrada como el de salida. Recordemos que el
	 * nombre de dichos parametros se deben de corresponder con los ids de los
	 * elementos HTML de los que queremos recoger sus valores, para que estos
	 * sean los argumentos del metodo a ejecutar. Si no encuentra dicho metodo
	 * devuelve null.
	 * 
	 * @throws ParserXmiException
	 * */
	private List<String> recogeParametrosMetodoAsociacionTipoB(
			AssociationClass acB, String metodo) throws PintaException {
		List<String> params = new ArrayList<String>();
		boolean ok = false;

		OwnedMember om = this.buscaOwnedMemberbyId(acB.getId());
		for (OwnedOperation op : om.getListOwnedOperations()) {
			if (op.getName().equalsIgnoreCase(metodo)) {
				ok = true;
				for (OwnedParameter par : op.getParametros()) {
					params.add(par.getName());
				}
			}
		}

		if (ok == false)
			throw new PintaException("No se encontró la definicion del método "
					+ metodo);
		if (params.size() == 0)
			throw new PintaException(
					"No se encontraron parametros para el metodo " + metodo);

		return params;

	}

	/**
	 * Este metodo busca si el elemento representado por el OwnedAttribute
	 * pasado como parámetro está implicado en alguna association de tipo pasado
	 * como parametro.(su nombre esté indicado en el tag "origen" de la
	 * transición). Si es así, devuelve el objeto AssociationClass
	 * correspondiente tipo A= Una asociacion simple de Pagina a Pagina. tipo B=
	 * Una asociacion con la ejecucion de un Web Service.
	 */
	/*
	 * private AssociationClass
	 * buscaElementoEnAlgunaAssociationTipo(OwnedAttribute oa, String tipo) {
	 * AssociationClass ac=null;
	 * 
	 * if (tipo.equals("A")){ for (OwnedMember om:this.listaMembers) { if
	 * (om.isAssociationClass()==true){
	 * ac=this.buscaAssociationClassbyId(om.getId()); if
	 * (ac.getId().equals(om.getId())&&
	 * this.averiguaStereotype(ac.getId()).equalsIgnoreCase("Transition")){
	 * ElemStereotyped es=this.buscaElementStereotypedbyId(ac.getId()); if
	 * (es!=null && es.existeTag("origen") &&
	 * es.getValorTag("origen").equalsIgnoreCase(oa.getName()) &&
	 * ac.getTipo().equals("A")){ return ac; } if (ac.getTipo().equals("A") &&
	 * ac.getOrigen().equalsIgnoreCase(oa.getName())){ return ac; } } } }
	 * 
	 * }else if (tipo.equals("B")){ for (OwnedMember om:this.listaMembers) { if
	 * (om.isAssociationClass()==true){
	 * ac=this.buscaAssociationClassbyId(om.getId()); if
	 * (ac.getId().equals(om.getId())&&
	 * this.averiguaStereotype(ac.getId()).equalsIgnoreCase("Transition")){
	 * ElemStereotyped es=this.buscaElementStereotypedbyId(ac.getId()); if
	 * (es!=null && es.existeTag("origen") &&
	 * es.getValorTag("origen").equalsIgnoreCase(oa.getName()) &&
	 * ac.getTipo().equals("B")){ return ac; } if (ac.getTipo().equals("B") &&
	 * ac.getOrigen().equalsIgnoreCase(oa.getName())){ return ac; } } } } }
	 * 
	 * return null;
	 * 
	 * }
	 */

	/**
	 * Este metodo busca si el elemento representado por el OwnedAttribute
	 * pasado como parámetro está implicado en alguna association de tipo pasado
	 * como parametro.(su nombre esté indicado en el tag "origen" de la
	 * transición). Si es así, devuelve el objeto AssociationClass
	 * correspondiente tipo A= Una asociacion simple de Pagina a Pagina. tipo B=
	 * Una asociacion con la ejecucion de un Web Service.
	 */
	private AssociationClass buscaElementoEnAlgunaAssociationTipoA(
			OwnedAttribute oa) {
		AssociationClass ac = null;

		for (OwnedMember om : this.listaMembers) {
			if (om.isAssociationClass() == true) {
				ac = this.buscaAssociationClassbyId(om.getId());
				if (ac.getId().equals(om.getId())
						&& this.averiguaStereotype(ac.getId())
								.equalsIgnoreCase("Transition")) {
					if (ac.getTipo().equals("A")
							&& ac.getOrigen().equalsIgnoreCase(oa.getName())) {
						return ac;
					}
				}
			}

		}

		return null;

	}

	/**
	 * Este metodo busca si el elemento representado por el OwnedAttribute
	 * pasado como parámetro está implicado en alguna association de tipo pasado
	 * como parametro.(su nombre esté indicado en el tag "origen" de la
	 * transición). Si es así, devuelve el objeto AssociationClass
	 * correspondiente tipo A= Una asociacion simple de Pagina a Pagina. tipo B=
	 * Una asociacion con la ejecucion de un Web Service.
	 */
	private List<AssociationClass> buscaElementoEnAlgunaAssociationTipoB(
			OwnedAttribute oa) {
		AssociationClass ac = null;
		List<AssociationClass> res = new ArrayList<AssociationClass>();

		for (OwnedMember om : this.listaMembers) {
			if (om.isAssociationClass() == true) {
				ac = this.buscaAssociationClassbyId(om.getId());
				if (ac.getId().equals(om.getId())
						&& this.averiguaStereotype(ac.getId())
								.equalsIgnoreCase("Transition")) {
					if (ac.getTipo().equals("B")
							&& ac.getOrigen().equalsIgnoreCase(oa.getName())) {
						res.add(ac);
					}
				}
			}
		}

		return res;

	}

	/**
	 * Pinta un Label pasandole como argumento el xmiid y el elementId del
	 * OwnedViewsMdElement de dicho Label
	 **/
	private void pintaLabel(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		ElemStereotyped es = this.buscaElementStereotypedbyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaLabel, OwnedAttribute no encontrado");
		if (es == null)
			throw new PintaException(
					"Error en pintaLinkLabel, ElemStereotyped no encontrado");
		String color = "green";
		String texto = oa.getName();
		if (es.existeTag("color"))
			color = es.getValorTag("color");
		if (color == null || color == "")
			color = "green";
		if (es.existeTag("texto"))
			texto = es.getValorTag("texto");
		// System.out.println("texto="+texto);
		if (texto == null || texto == "")
			texto = oa.getName();
		pw.println("<font color=\"" + color + "\" "
				+ this.generaStyleSinAltoNiAncho(xmiid, eid) + "><I>" + texto
				+ "</I></font>");
		pw.flush();
	}

	/**
	 * Pinta un LinkLabel pasandole como argumento el xmiid y el elementId del
	 * OwnedViewsMdElement de dicho LinkLabelLabel.
	 **/
	private void pintaLinkLabel(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		ElemStereotyped es = this.buscaElementStereotypedbyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaLinkLabel, OwnedAttribute no encontrado");
		if (es == null)
			throw new PintaException(
					"Error en pintaLinkLabel, ElemStereotyped no encontrado");
		String url = "./paginas/linkroto.html";
		String texto = oa.getName();
		boolean isClickable = true;
		if (es.existeTag("url"))
			url = es.getValorTag("url");
		if (es.existeTag("isClickable"))
			isClickable = Boolean.valueOf(es.getValorTag("isClickable"));
		if (es.existeTag("texto"))
			texto = es.getValorTag("texto");
		// System.out.println("texto="+texto);
		if (texto == null || texto == "")
			texto = oa.getName();

		// Compruebo si hay alguna asociación de tipo A o tipo B en la que esté
		// implicado este enlace. Si es así, se refleja como
		// el correspondiente evento que se especificó en el diagrama UML.
		AssociationClass acA = this.buscaElementoEnAlgunaAssociationTipoA(oa);
		List<AssociationClass> listacB = this
				.buscaElementoEnAlgunaAssociationTipoB(oa);
		if (isClickable == false) {
			pw.println("<a href=\"javascript:;\" id=\"" + oa.getName()
					+ "\" title=\"No clickable\" name=\"" + oa.getName()
					+ "\" " + this.generaStyleSinAltoNiAncho(xmiid, eid) + ">"
					+ texto + "</a>");
		} else {

			if (acA == null && (listacB == null || listacB.size() == 0)) {
				if (!url.equals("./paginas/linkroto.html")
						&& urlConNombreDePagina(url)) { // atajo para ir a otro
					// elemento Page
					pw.println("<a href=\"javascript:;\" id=\"" + oa.getName()
							+ "\" name=\"" + oa.getName()
							+ "\" onclick=\"transitar('" + url + "');\" "
							+ this.generaStyleSinAltoNiAncho(xmiid, eid) + ">"
							+ texto + "</a>");
				} else { // enlace normal
					pw.println("<a href='" + url + "' id=\"" + oa.getName()
							+ "\" name=\"" + oa.getName() + "\" title=\"" + url
							+ "\" "
							+ this.generaStyleSinAltoNiAncho(xmiid, eid) + ">"
							+ texto + "</a>");
				}

			} else if (acA != null && listacB == null) {
				pw.println("<a href=\"javascript:;\" id=\"" + oa.getName()
						+ "\" name=\"" + oa.getName() + "\" "
						+ this.generaStyleSinAltoNiAncho(xmiid, eid) + " "
						+ this.construyeCadenaEventosTipoA(acA) + "> " + texto
						+ " </a>");

			} else if (acA == null && listacB != null) {
				// Hay que construir la cadena de eventos para este elemento(
				// ej: onclick="..." onchange=" "...)
				pw.println("<a href=\"javascript:;\" id=\"" + oa.getName()
						+ "\" name=\"" + oa.getName() + "\" "
						+ this.generaStyleSinAltoNiAncho(xmiid, eid) + " "
						+ this.construyeCadenaEventosTipoB(listacB) + "> "
						+ texto + " </a>");
			} else if (acA != null && listacB != null) {
				pw.println("<a href=\"javascript:;\" id=\"" + oa.getName()
						+ "\" name=\"" + oa.getName() + "\" "
						+ this.generaStyleSinAltoNiAncho(xmiid, eid) + " "
						+ this.construyeCadenaEventosTipoAyB(listacB, acA)
						+ "> " + texto + " </a>");
			}

		}

		pw.flush();
	}

	/**
	 * Comprueba si la url de un LinkLabel contiene el nombre de un elemento
	 * Page. Si es así, el usuario está usando un atajo para ir a otra pagina,
	 * es decir, en lugar de modelar una asociacion con la funcion "transita",
	 * está usando un simple LinkLabel cuya url es el nombre del elemento Page
	 * al que quiere ir. Así es más cómodo.
	 * **/
	private boolean urlConNombreDePagina(String url) {
		boolean res = false;
		for (String pag : this.nombresPaginas) {
			if (url.equals(pag)) {
				res = true;
				break;
			}
		}
		return res;

	}

	/**
	 * Pinta una Section pasandole como argumento el xmiid y el elementId del
	 * OwnedViewsMdElement de dicha Section.
	 * 
	 * @throws PintaException
	 */
	private void pintaSection(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		ElemStereotyped es = this.buscaElementStereotypedbyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaSection, OwnedAttribute no encontrado");
		if (es == null)
			throw new PintaException(
					"Error en pintaSection, ElemStereotyped no encontrado");
		int width = 0;
		int heigth = 0;
		if (es.existeTag("width"))
			width = Integer.parseInt(es.getValorTag("width"));
		if (es.existeTag("heigth"))
			heigth = Integer.parseInt(es.getValorTag("heigth"));
		/*
		 * if (width!=0 && heigth!=0){ //los tags width y heigth se
		 * especificaron en el diagrama uml
		 * 
		 * }else{
		 * 
		 * }
		 */
		if (this.isTabArea(xmiid)) { // Esta sección contiene tabs, por lo que
			// es una tabArea
			this.pintaTabArea2(xmiid, eid, pw);
		} else { // Es una sección normal
			pw.println("<div " + this.generaStyleConBorder(xmiid, eid, "solid")
					+ ">" + oa.getName() + "</div>");
			this.pintaComponentes(xmiid, eid, pw);
		}
		pw.flush();
	}

	/**
	 * Método que recibe un xmiid de una Section y comprueba si esa Section es
	 * una TabArea, es decir, si tiene dentro de ella elementos Tab o no.
	 * 
	 * @throws PintaException
	 * */
	private boolean isTabArea(String xmiid) throws PintaException {
		boolean res = false;
		OwnedViewsMdElement ovmde = this.buscaOwnedViewsMdElementbyXmiId(xmiid);
		if (ovmde == null)
			throw new PintaException("Error en IsTabArea con argumento:"
					+ xmiid + ",OwnedViewMdElement no encontrado");
		// Iteramos sobre la lista de componentes del elemento candidato a ser
		// TabArea para comprobar si tiene alguna Tab
		for (String xid : ovmde.getParts()) {
			String id = this.buscaOwnedViewsMdElementbyXmiId(xid)
					.getElementId();
			String estereoName = this.averiguaStereotype(id); // Devuelve el
			// nombre del
			// estereotipo
			// asociado a
			// este elemento
			if (estereoName == null)
				throw new PintaException(
						"Error en isTabArea, Estereotipo no encontrado");
			if (estereoName.equals("Tab")) {
				res = true;
			}
		}
		return res;
	}

	/**
	 * Método que recibe un xmiid de una TabArea y devuelve el numero de tabs
	 * que tiene dentro de ella
	 * 
	 * @throws PintaException
	 * */
	private int getNumTabsEnTabArea(String xmiid) throws PintaException {
		int res = 0;
		OwnedViewsMdElement ovmde = this.buscaOwnedViewsMdElementbyXmiId(xmiid);
		if (ovmde == null)
			throw new PintaException(
					"Error en getNumTabsEnTabArea con argumento:" + xmiid
							+ ",OwnedViewMdElement no encontrado");
		// Iteramos sobre la lista de componentes del elemento TabArea para
		// contar sus tabs
		for (String xid : ovmde.getParts()) {
			String id = this.buscaOwnedViewsMdElementbyXmiId(xid)
					.getElementId();
			String estereoName = this.averiguaStereotype(id); // Devuelve el
			// nombre del
			// estereotipo
			// asociado a
			// este elemento
			if (estereoName == null)
				throw new PintaException(
						"Error en getNumTabsEnTabArea, Estereotipo no encontrado");
			if (estereoName.equals("Tab")) {
				res++;
			}
		}
		return res;
	}

	/**
	 * Método que recibe un xmiid de una Tab y comprueba si esta dentro de otro
	 * Tab (no permitido anidamiento de Tabs)
	 * 
	 * @throws PintaException
	 * */
	private boolean anidadaEnOtraTab(String xmiid) throws PintaException {
		boolean res = false;
		OwnedViewsMdElement ovmde = this.buscaOwnedViewsMdElementbyXmiId(xmiid);
		if (ovmde == null)
			throw new PintaException("Error en anidadaEnOtraTab con argumento:"
					+ xmiid + ",OwnedViewMdElement no encontrado");
		String xmiidPadre = ovmde.getXmiidPadre();
		while (xmiidPadre != null && xmiidPadre != "") {
			OwnedViewsMdElement ovmdePadre = this
					.buscaOwnedViewsMdElementbyXmiId(xmiidPadre);
			if (ovmdePadre == null)
				throw new PintaException(
						"Error en anidadaEnOtraTab con argumento:" + xmiid
								+ ",OwnedViewMdElement no encontrado");
			String estereoName = this.averiguaStereotype(ovmdePadre
					.getElementId()); // Devuelve el nombre del estereotipo
			// asociado a este elemento
			if (estereoName == null)
				throw new PintaException(
						"Error en anidadaEnOtraTab, Estereotipo no encontrado");
			if (estereoName.equals("Tab")) {
				res = true;
				return res;
			} else {
				ovmde = ovmdePadre;
				xmiidPadre = ovmde.getXmiidPadre();
			}

		}

		return res;

	}

	/**
	 * Pinta un TabArea pasandole como argumento el xmiid y el elementId del
	 * OwnedViewsMdElement de dicho TabArea.
	 * 
	 * @throws PintaException
	 **/
	private void pintaTabArea(String xmiid, String eid, PrintWriter pw)
			throws PintaException {

		this.listaTabArea.add(new TabArea(xmiid)); // creamos el objeto TabArea
		// pertinente con 0 tabs
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaTabArea, OwnedAttribute no encontrado");
		OwnedViewsMdElement ovmde = this.buscaOwnedViewsMdElementbyXmiId(xmiid);
		if (ovmde == null)
			throw new PintaException("Error al pintar elemento " + xmiid
					+ ",OwnedViewMdElement no encontrado");
		String nombreTab1 = this.buscaNombrePrimeraTab(xmiid);
		pw
				.println("<div class=\"tabBox\"" + this.generaStyle(xmiid, eid)
						+ ">");
		pw.println("<div class=\"tabArea\">");

		String backupPaginaActual = this.paginaActual;
		this.paginaActual = eid;
		this.pintaComponentes(xmiid, eid, pw); // pintamos el cuerpo de las tabs
		this.paginaActual = backupPaginaActual;
		pw.println("</div>");
		pw.println("<div class=\"tabMain\">");
		pw
				.println("<div class=\"tabIframeWrapper\"><iframe class=\"tabContent\" name=\""
						+ oa.getName()
						+ "\" src=\""
						+ nombreTab1
						+ ".html\" marginheight='8' marginwidth='8' frameborder='0' style=\"height:"
						+ ovmde.getGeometry()[3] + "px;\"></iframe></div>");
		pw.println("</div>");
		pw.println("</div>");
		pw.flush();
	}

	/**
	 * Pinta un TabArea pasandole como argumento el xmiid y el elementId del
	 * OwnedViewsMdElement de dicho TabArea.
	 * 
	 * @throws PintaException
	 **/
	private void pintaTabArea2(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		TabArea ta = new TabArea(xmiid); // creamos el objeto TabArea pertinente
		// con 0 tabs
		this.listaTabArea.add(ta);
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaTabArea, OwnedAttribute no encontrado");
		OwnedViewsMdElement ovmde = this.buscaOwnedViewsMdElementbyXmiId(xmiid);
		if (ovmde == null)
			throw new PintaException("Error al pintar elemento " + xmiid
					+ ",OwnedViewMdElement no encontrado");
		// obtenemos un map con pares (xmiid_Tab, nombre_Tab)
		Map<String, String> mapTabs = buscaNombreTabsEnTabArea(xmiid);
		int numTabs = this.getNumTabsEnTabArea(xmiid);

		// pintamos el menu de pestañitas
		int indice = listaTabArea.size();
		pw.println("<div id=\"MenuTabArea" + indice + "\" "
				+ this.generaStyle(xmiid, eid) + ">");
		int i = 1;
		for (Map.Entry<String, String> tupla : mapTabs.entrySet()) {
			pw.println("	<a href=\"#\" id=\"tabDatos" + indice + "TabArea" + i
					+ "\" class=\"tab-button\" title=\"" + tupla.getValue()
					+ "\" onclick=\"javascript:setTab2('tab" + indice + "-" + i
					+ "','tabDatos" + indice + "TabArea" + i + "'," + numTabs
					+ ",'tabs" + indice + "','tabDatos" + indice
					+ "TabArea');\" >");
			pw.println("		<span>" + tupla.getValue() + "</span>");
			pw.println("	</a>");
			ta.getMapXmiIdsyIndices().put(tupla.getKey(), i);
			i++;
		}
		pw.println("</div>");
		pw.flush();

		// pintamos sus tabs
		pw.println("<div id=\"tabs" + indice + "\" "
				+ this.generaStyleCuerpoAreaTab(xmiid, eid, "solid") + ">");
		// pw.println("<div id=\"tabs"+indice+"\">");
		this.pintaComponentes(xmiid, eid, pw);
		pw.println("</div>");
		pw.flush();
	}

	/**
	 * Busca el nombre de las Tabs que se encuentra en la TabArea cuyo xmiid se
	 * pasa como parámetro. Devuelve un mapa con el xmiid de dicho Tab y su
	 * nombre.
	 * 
	 * @throws PintaException
	 * */
	private Map<String, String> buscaNombreTabsEnTabArea(String xmiid)
			throws PintaException {
		Map<String, String> res = new LinkedHashMap<String, String>();
		OwnedViewsMdElement ovmde = this.buscaOwnedViewsMdElementbyXmiId(xmiid);
		if (ovmde == null)
			throw new PintaException(
					"Error en buscaNombreTabsEnTabArea,OwnedViewMdElement no encontrado");
		for (String xid : ovmde.getParts()) {
			String id = this.buscaOwnedViewsMdElementbyXmiId(xid)
					.getElementId();
			String estereoName = this.averiguaStereotype(id); // Devuelve el
			// nombre del
			// estereotipo
			// asociado a
			// este elemento
			if (estereoName == null)
				throw new PintaException(
						"Error en isTabArea, Estereotipo no encontrado");
			if (estereoName.equals("Tab")) {
				OwnedAttribute oa = this.buscaOwnedAttributebyId(id);
				res.put(xid, oa.getName());
			}
		}

		return res;
	}

	/**
	 * Busca el nombre de la primera Tab que se encuentra en la TabArea cuyo
	 * xmiid se pasa como parámetro.
	 * 
	 * @throws PintaException
	 * */
	private String buscaNombrePrimeraTab(String xmiid) throws PintaException {
		String res = "blank";
		OwnedViewsMdElement ovmde = this.buscaOwnedViewsMdElementbyXmiId(xmiid);
		if (ovmde == null)
			throw new PintaException(
					"Error en buscaNombrePrimeraTab,OwnedViewMdElement no encontrado");
		for (String xid : ovmde.getParts()) {
			String id = this.buscaOwnedViewsMdElementbyXmiId(xid)
					.getElementId();
			String estereoName = this.averiguaStereotype(id); // Devuelve el
			// nombre del
			// estereotipo
			// asociado a
			// este elemento
			if (estereoName == null)
				throw new PintaException(
						"Error en isTabArea, Estereotipo no encontrado");
			if (estereoName.equals("Tab")) {
				OwnedAttribute oa = this.buscaOwnedAttributebyId(id);
				res = oa.getName();
				break;
			}
		}
		return res;
	}

	/**
	 * Pinta una TextArea pasandole como argumento el elementId del
	 * OwnedViewsMdElement de dicha TextArea, y el elementId del elemento padre
	 * OwnedViewsMdElement que contiene a dicha TextArea.
	 **/
	private void pintaTextArea(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		ElemStereotyped es = this.buscaElementStereotypedbyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaTextArea, OwnedAttribute no encontrado");
		if (es == null)
			throw new PintaException(
					"Error en pintaTextArea, ElemStereotyped no encontrado");
		// int rows=0;
		// int columns=0;
		// if (es.existeTag("rows"))
		// rows=Integer.parseInt(es.getValorTag("rows"));
		// if (es.existeTag("columns"))
		// columns=Integer.parseInt(es.getValorTag("columns"));
		/*
		 * if (rows!=0 && columns!=0){ //los tags rows y columns se
		 * especificaron en el diagrama uml
		 * pw.println("<textarea name=\""+oa.getName
		 * ()+"\" cols="+columns+" rows="+rows+" "+this.generaStyle(eid,
		 * pw)+"> </textarea>");
		 * debug("<textarea name=\""+oa.getName()+"\" cols="
		 * +columns+" rows="+rows+" "+this.generaStyleSinAltoNiAncho(eid,
		 * pw)+"> </textarea>"); }else{
		 * pw.println("<textarea name=\""+oa.getName
		 * ()+"\" cols=20 rows=20 "+this.generaStyle(eid, pw)+"> </textarea>");
		 * debug
		 * ("<textarea name=\""+oa.getName()+"\" cols=20 rows=20 "+this.generaStyle
		 * (eid, pw)+"> </textarea>"); }
		 */

		boolean isEditable = true;
		if (es.existeTag("isEditable"))
			isEditable = Boolean.valueOf(es.getValorTag("isEditable"));

		// Compruebo si hay alguna asociación de tipo A o tipo B en la que esté
		// implicado este textarea. Si es asi, se refleja como
		// el correspondiente evento que se especificó en el diagrama UML.
		AssociationClass acA = this.buscaElementoEnAlgunaAssociationTipoA(oa);
		List<AssociationClass> listacB = this
				.buscaElementoEnAlgunaAssociationTipoB(oa);
		String readonly = "";
		if (isEditable == false)
			readonly = "readonly";
		if (acA == null && (listacB == null || listacB.size() == 0)) {
			pw.println("<textarea id=\"" + oa.getName() + "\" name=\""
					+ oa.getName() + "\" " + readonly + " "
					+ this.generaStyle(xmiid, eid) + "></textarea>");
		} else if (acA != null && listacB == null) {
			pw.println("<textarea id=\"" + oa.getName() + "\" name=\""
					+ oa.getName() + "\" " + readonly + " "
					+ this.construyeCadenaEventosTipoA(acA) + " "
					+ this.generaStyle(xmiid, eid) + "></textarea>");
		} else if (acA == null && listacB != null) {
			// Hay que construir la cadena de eventos para este elemento( ej:
			// onclick="..." onchange=" "...)
			pw.println("<textarea id=\"" + oa.getName() + "\" name=\""
					+ oa.getName() + "\" " + readonly + " "
					+ this.construyeCadenaEventosTipoB(listacB) + " "
					+ this.generaStyle(xmiid, eid) + "></textarea>");
		} else if (acA != null && listacB != null) {
			pw.println("<textarea id=\"" + oa.getName() + "\" name=\""
					+ oa.getName() + "\" " + readonly + " "
					+ this.construyeCadenaEventosTipoAyB(listacB, acA) + " "
					+ this.generaStyle(xmiid, eid) + "></textarea>");
		}

		pw.flush();
	}

	/**
	 * Pinta una Text pasandole como argumento el xmiid y el elementId del
	 * OwnedViewsMdElement de dicha Text.
	 * 
	 * @throws PintaException
	 **/
	private void pintaText(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		ElemStereotyped es = this.buscaElementStereotypedbyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaText, OwnedAttribute no encontrado");
		if (es == null)
			throw new PintaException(
					"Error en pintaText, ElemStereotyped no encontrado");
		int size = 0;
		boolean isEditable = true;
		String lengthmax = "";
		String value = "";
		String type = "text"; // text por defecto
		if (es.existeTag("size"))
			size = Integer.parseInt(es.getValorTag("size"));
		if (es.existeTag("isEditable"))
			isEditable = Boolean.valueOf(es.getValorTag("isEditable"));
		if (es.existeTag("lengthmax"))
			lengthmax = es.getValorTag("lengthmax");
		if (es.existeTag("type"))
			type = es.getValorTag("type");
		if (es.existeTag("value"))
			value = es.getValorTag("value");
		String readonly = "";
		if (isEditable == false)
			readonly = "readonly";
		// Compruebo si hay alguna asociacion de tipo A o tipo B en la que esta
		// implicado este Text. Si es asi, se refleja como
		// el correspondiente evento que se especifico en el diagrama UML.
		AssociationClass acA = this.buscaElementoEnAlgunaAssociationTipoA(oa);
		List<AssociationClass> listacB = this
				.buscaElementoEnAlgunaAssociationTipoB(oa);

		if (acA == null && (listacB == null || listacB.size() == 0)) {
			// el tag size se ha especificado en el diagrama uml. Se tomara su
			// valor para la anchura, ignorandose la dimension en el diagrama
			// UML
			if (size != 0)
				pw.println("<input type=\"" + type + "\" id=\"" + oa.getName()
						+ "\" name=\"" + oa.getName() + "\" " + readonly
						+ " size=" + size + "  maxlength=\"" + lengthmax
						+ "\" value=\"" + value + "\" "
						+ this.generaStyleSinAltoNiAncho(xmiid, eid) + " />");
			else
				pw.println("<input type=\"" + type + "\" id=\"" + oa.getName()
						+ "\" name=\"" + oa.getName() + "\" " + readonly
						+ "  maxlength=\"" + lengthmax + "\" value=\"" + value
						+ "\" " + this.generaStyleSinAlto(xmiid, eid) + " />");

		} else if (acA != null && listacB == null) {
			// el tag size se ha especificado en el diagrama uml. Se tomara su
			// valor para la anchura, ignorandose la dimension en el diagrama
			// UML
			if (size != 0)
				pw.println("<input type=\"" + type + "\" id=\"" + oa.getName()
						+ "\" name=\"" + oa.getName() + "\" " + readonly
						+ " size=" + size + "  maxlength=\"" + lengthmax
						+ "\" value=\"" + value + "\" "
						+ this.construyeCadenaEventosTipoA(acA) + " "
						+ this.generaStyleSinAltoNiAncho(xmiid, eid) + " />");
			else
				pw.println("<input type=\"" + type + "\" id=\"" + oa.getName()
						+ "\" name=\"" + oa.getName() + "\" " + readonly
						+ "  maxlength=\"" + lengthmax + "\" value=\"" + value
						+ "\" " + this.construyeCadenaEventosTipoA(acA) + " "
						+ this.generaStyleSinAlto(xmiid, eid) + " />");

		} else if (acA == null && listacB != null) {
			// el tag size se ha especificado en el diagrama uml. Se tomara su
			// valor para la anchura, ignorandose la dimension en el diagrama
			// UML
			if (size != 0)
				pw.println("<input type=\"" + type + "\" id=\"" + oa.getName()
						+ "\" name=\"" + oa.getName() + "\" " + readonly
						+ " size=" + size + "  maxlength=\"" + lengthmax
						+ "\" value=\"" + value + "\" "
						+ this.construyeCadenaEventosTipoB(listacB) + " "
						+ this.generaStyleSinAltoNiAncho(xmiid, eid) + " />");
			else
				pw.println("<input type=\"" + type + "\" id=\"" + oa.getName()
						+ "\" name=\"" + oa.getName() + "\" " + readonly
						+ "  maxlength=\"" + lengthmax + "\" value=\"" + value
						+ "\" " + this.construyeCadenaEventosTipoB(listacB)
						+ " " + this.generaStyleSinAlto(xmiid, eid) + " />");

		} else if (acA != null && listacB != null) {
			// el tag size se ha especificado en el diagrama uml. Se tomara su
			// valor para la anchura, ignorandose la dimension en el diagrama
			// UML
			if (size != 0)
				pw.println("<input type=\"" + type + "\" id=\"" + oa.getName()
						+ "\" name=\"" + oa.getName() + "\" " + readonly
						+ " size=" + size + "  maxlength=\"" + lengthmax
						+ "\" value=\"" + value + "\" "
						+ this.construyeCadenaEventosTipoAyB(listacB, acA)
						+ " " + this.generaStyleSinAltoNiAncho(xmiid, eid)
						+ " />");
			else
				pw.println("<input type=\"" + type + "\" id=\"" + oa.getName()
						+ "\" name=\"" + oa.getName() + "\" " + readonly
						+ "  maxlength=\"" + lengthmax + "\" value=\"" + value
						+ "\" "
						+ this.construyeCadenaEventosTipoAyB(listacB, acA)
						+ " " + this.generaStyleSinAlto(xmiid, eid) + " />");
		}

		pw.flush();
	}

	/**
	 * Pinta un CheckBox pasandole como argumento el xmiid y el elementId del
	 * OwnedViewsMdElement de dicho CheckBox.
	 * 
	 * @throws PintaException
	 **/
	private void pintaCheckBox(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		ElemStereotyped es = this.buscaElementStereotypedbyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaCheckBox, OwnedAttribute no encontrado");
		if (es == null)
			throw new PintaException(
					"Error en pintaCheckBox, ElemStereotyped no encontrado");
		boolean click = false;
		String value = "";
		if (es.existeTag("click"))
			click = Boolean.valueOf(es.getValorTag("click"));
		if (es.existeTag("value"))
			value = es.getValorTag("value");

		String checked = "";
		if (click == true)
			checked = "checked";
		// Compruebo si hay alguna asociación de tipo A o tipo B en la que esta
		// implicado este checkbox. Si es asi, se refleja como
		// el correspondiente evento que se especificó en el diagrama UML.
		AssociationClass acA = this.buscaElementoEnAlgunaAssociationTipoA(oa);
		List<AssociationClass> listacB = this
				.buscaElementoEnAlgunaAssociationTipoB(oa);

		if (acA == null && (listacB == null || listacB.size() == 0)) {
			pw.println("<input type=\"checkbox\" id=\"" + oa.getName() + "\" "
					+ "name=\"" + oa.getName() + "\" value=\"" + value + "\" "
					+ checked + " "
					+ this.generaStyleSinAltoNiAncho(xmiid, eid) + " />");
		} else if (acA != null && listacB == null) {
			pw.println("<input type=\"checkbox\" id=\"" + oa.getName() + "\" "
					+ "name=\"" + oa.getName() + "\" value=\"" + value + "\" "
					+ checked + " " + this.construyeCadenaEventosTipoA(acA)
					+ " " + this.generaStyleSinAltoNiAncho(xmiid, eid) + " />");
		} else if (acA == null && listacB != null) {
			pw.println("<input type=\"checkbox\" id=\"" + oa.getName() + "\" "
					+ "name=\"" + oa.getName() + "\" value=\"" + value + "\" "
					+ checked + " " + this.construyeCadenaEventosTipoB(listacB)
					+ " " + this.generaStyleSinAltoNiAncho(xmiid, eid) + " />");
		} else if (acA != null && listacB != null) {
			pw.println("<input type=\"checkbox\" id=\"" + oa.getName() + "\" "
					+ "name=\"" + oa.getName() + "\" value=\"" + value + "\" "
					+ checked + " "
					+ this.construyeCadenaEventosTipoAyB(listacB, acA) + " "
					+ this.generaStyleSinAltoNiAncho(xmiid, eid) + " />");
		}

		pw.flush();
	}

	/**
	 * Pinta un RadioButton pasandole como argumento el elementId del
	 * OwnedViewsMdElement de dicho RadioButton, y el elementId del elemento
	 * padre OwnedViewsMdElement que contiene a dicho RadioButton.
	 **/
	private void pintaRadioButton(String xmiid, String eid, PrintWriter pw)
			throws PintaException {
		OwnedAttribute oa = this.buscaOwnedAttributebyId(eid);
		ElemStereotyped es = this.buscaElementStereotypedbyId(eid);
		if (oa == null)
			throw new PintaException(
					"Error en pintaRadioButton, OwnedAttribute no encontrado");
		if (es == null)
			throw new PintaException(
					"Error en pintaRadioButton, ElemStereotyped no encontrado");
		boolean click = false;
		String value = "";
		if (es.existeTag("click"))
			click = Boolean.valueOf(es.getValorTag("click"));
		if (es.existeTag("value"))
			value = es.getValorTag("value");

		String checked = "";
		if (click)
			checked = "checked";

		// Compruebo si hay alguna asociación de tipo A o tipo B en la que está
		// implicado este radiobutton. Si es asi, se refleja como
		// el correspondiente evento que se especifico en el diagrama UML.
		AssociationClass acA = this.buscaElementoEnAlgunaAssociationTipoA(oa);
		List<AssociationClass> listacB = this
				.buscaElementoEnAlgunaAssociationTipoB(oa);

		if (acA == null && (listacB == null || listacB.size() == 0)) {
			pw.println("<input type=\"radio\" id=\"" + oa.getName() + "\" "
					+ "name=\"" + oa.getName() + "\" value=\"" + value + "\" "
					+ checked + " "
					+ this.generaStyleSinAltoNiAncho(xmiid, eid) + " />");
		} else if (acA != null && listacB == null) {
			pw.println("<input type=\"radio\" id=\"" + oa.getName() + "\" "
					+ "name=\"" + oa.getName() + "\" value=\"" + value + "\" "
					+ checked + " " + this.construyeCadenaEventosTipoA(acA)
					+ " " + this.generaStyleSinAltoNiAncho(xmiid, eid) + " />");
		} else if (acA == null && listacB != null) {
			pw.println("<input type=\"radio\" id=\"" + oa.getName() + "\" "
					+ "name=\"" + oa.getName() + "\" value=\"" + value + "\" "
					+ checked + " " + this.construyeCadenaEventosTipoB(listacB)
					+ " " + this.generaStyleSinAltoNiAncho(xmiid, eid) + " />");
		} else if (acA != null && listacB != null) {
			pw.println("<input type=\"radio\" id=\"" + oa.getName() + "\" "
					+ "name=\"" + oa.getName() + "\" value=\"" + value + "\" "
					+ checked + " "
					+ this.construyeCadenaEventosTipoAyB(listacB, acA) + " "
					+ this.generaStyleSinAltoNiAncho(xmiid, eid) + " />");
		}

		pw.flush();
	}

	/**
	 * Mostrara un aviso en el fichero html de salida en el que se indica que no
	 * se reconoció como válido un determinado estereotipo.
	 */
	private void pintaNoReconocido(String xmiid, String eid, PrintWriter pw) {
		pw.println("<font color=red>--Elemento con xmi-id:" + xmiid
				+ " no reconocido--</font>");
		pw.flush();

	}

	/**
	 * Metodo auxiliar que usan los pintaXXX que generara el fragmento html con
	 * el atributo "style" que calculará e indicará las coordenadas finales del
	 * elemento en el documento html. Si el elemento a pintar esta dentro de una
	 * Tab, las coordenadas se pintaran respecto a las fronteras de la Tab. Si
	 * el elemento a pintar esta dentro de un ImageMap, las coordenadas se
	 * pintaran respecto a las fronteras de la ImageMap.
	 */
	private String generaStyle(String xmiid, String eid) {
		String res = "";
		int left = 0;
		int top = 0;
		int width = 0;
		int heigth = 0;
		// Recuperamos las coordenadas de la página donde se encuentra este
		// elemento y calculamos las coordenadas finales
		int[] geometryPage = this.buscaOwnedViewsMdElementbyId(
				this.paginaActual).getGeometry();
		int[] geometryElem = this.buscaOwnedViewsMdElementbyXmiId(xmiid)
				.getGeometry();
		int[] geometryTab = null;
		int[] geometryIMap = null;
		if (this.dentroDeTab(xmiid)) {
			geometryTab = this.buscaOwnedViewsMdElementbyXmiId(this.tabActual)
					.getGeometry();
			left = geometryElem[0] - geometryTab[0];
			top = geometryElem[1] - geometryTab[1];
		} else if (this.dentroDeImageMap(xmiid)) {
			geometryIMap = this.buscaOwnedViewsMdElementbyXmiId(
					this.imageMapActual).getGeometry();
			left = geometryElem[0] - geometryIMap[0];
			top = geometryElem[1] - geometryIMap[1];
		} else {
			left = geometryElem[0] - geometryPage[0];
			top = geometryElem[1] - geometryPage[1];
		}
		width = geometryElem[2]; // anchura
		heigth = geometryElem[3]; // altura
		res = res + " style=\"position:absolute; left:" + left + "px; top:"
				+ top + "px; width:" + width + "px; height:" + heigth
				+ "px;\" ";
		return res;
	}

	/**
	 * Metodo auxiliar que usan los pintaXXX que generara el fragmento html con
	 * el atributo "style" que calculará e indicará las coordenadas finales del
	 * elemento en el documento html. Lo que se diferencia este de generaStyle
	 * es que este método incluyer el parámetro "border" en el atributo style.
	 * Es llamado por ejemplo por PintaSection. Si el elemento a pintar esta
	 * dentro de una Tab, las coordenadas se pintaran respecto a las fronteras
	 * de la Tab. Si el elemento a pintar esta dentro de un ImageMap, las
	 * coordenadas se pintaran respecto a las fronteras de la ImageMap.
	 */
	private String generaStyleConBorder(String xmiid, String eid, String border) {
		String res = "";
		int left = 0;
		int top = 0;
		int width = 0;
		int heigth = 0;
		// Recuperamos las coordenadas de la página donde se encuentra este
		// elemento y calculamos las coordenadas finales
		int[] geometryPage = this.buscaOwnedViewsMdElementbyId(
				this.paginaActual).getGeometry();
		int[] geometryElem = this.buscaOwnedViewsMdElementbyXmiId(xmiid)
				.getGeometry();
		int[] geometryTab = null;
		int[] geometryIMap = null;
		if (this.dentroDeTab(xmiid)) {
			geometryTab = this.buscaOwnedViewsMdElementbyXmiId(this.tabActual)
					.getGeometry();
			left = geometryElem[0] - geometryTab[0];
			top = geometryElem[1] - geometryTab[1];
		} else if (this.dentroDeImageMap(xmiid)) {
			geometryIMap = this.buscaOwnedViewsMdElementbyXmiId(
					this.imageMapActual).getGeometry();
			left = geometryElem[0] - geometryIMap[0];
			top = geometryElem[1] - geometryIMap[1];
		} else {
			left = geometryElem[0] - geometryPage[0];
			top = geometryElem[1] - geometryPage[1];
		}
		width = geometryElem[2]; // anchura
		heigth = geometryElem[3]; // altura
		res = res + " style=\"position:absolute; left:" + left + "px; top:"
				+ top + "px; width:" + width + "px; height:" + heigth
				+ "px; border:1px " + border + ";\" ";
		return res;
	}

	/**
	 * Pintara el recuadro donde van las tabs un poco mas abajo de la linea
	 * donde se situan las pestañitas Aumenta un poco pues el parametro top de
	 * style
	 * */
	private String generaStyleCuerpoAreaTab(String xmiid, String eid,
			String border) {
		String res = "";
		int left = 0;
		int top = 0;
		int width = 0;
		int heigth = 0;
		// Recuperamos las coordenadas de la página donde se encuentra este
		// elemento y calculamos las coordenadas finales
		int[] geometryPage = this.buscaOwnedViewsMdElementbyId(
				this.paginaActual).getGeometry();
		int[] geometryElem = this.buscaOwnedViewsMdElementbyXmiId(xmiid)
				.getGeometry();
		int[] geometryTab = null;
		int[] geometryIMap = null;
		if (this.dentroDeTab(xmiid)) {
			geometryTab = this.buscaOwnedViewsMdElementbyXmiId(this.tabActual)
					.getGeometry();
			left = geometryElem[0] - geometryTab[0];
			top = geometryElem[1] - geometryTab[1];
		} else if (this.dentroDeImageMap(xmiid)) {
			geometryIMap = this.buscaOwnedViewsMdElementbyXmiId(
					this.imageMapActual).getGeometry();
			left = geometryElem[0] - geometryIMap[0];
			top = geometryElem[1] - geometryIMap[1];
		} else {
			left = geometryElem[0] - geometryPage[0];
			top = geometryElem[1] - geometryPage[1];
			top = top + 25; // Aumento
		}
		width = geometryElem[2]; // anchura
		heigth = geometryElem[3]; // altura
		res = res + " style=\"position:absolute; left:" + left + "px; top:"
				+ top + "px; width:" + width + "px; height:" + heigth
				+ "px; border:1px " + border + ";\" ";
		return res;
	}

	/**
	 * Igual que generaStyle pero sin indicar en style los atributos width y
	 * height Si el elemento a pintar esta dentro de una Tab, las coordenadas se
	 * pintaran respecto a las fronteras de la Tab. Si el elemento a pintar esta
	 * dentro de un ImageMap, las coordenadas se pintaran respecto a las
	 * fronteras de la ImageMap.
	 */
	private String generaStyleSinAltoNiAncho(String xmiid, String eid) {
		String res = "";
		int left = 0;
		int top = 0;
		// Recuperamos las coordenadas de la página donde se encuentra este
		// elemento y calculamos las coordenadas finales
		int[] geometryPage = this.buscaOwnedViewsMdElementbyId(
				this.paginaActual).getGeometry();
		int[] geometryElem = this.buscaOwnedViewsMdElementbyXmiId(xmiid)
				.getGeometry();
		int[] geometryTab = null;
		int[] geometryIMap = null;
		if (this.dentroDeTab(xmiid)) {
			geometryTab = this.buscaOwnedViewsMdElementbyXmiId(this.tabActual)
					.getGeometry();
			left = geometryElem[0] - geometryTab[0];
			top = geometryElem[1] - geometryTab[1];
		} else if (this.dentroDeImageMap(xmiid)) {
			geometryIMap = this.buscaOwnedViewsMdElementbyXmiId(
					this.imageMapActual).getGeometry();
			left = geometryElem[0] - geometryIMap[0];
			top = geometryElem[1] - geometryIMap[1];
		} else {
			left = geometryElem[0] - geometryPage[0];
			top = geometryElem[1] - geometryPage[1];
		}
		res = res + " style=\"position:absolute; left:" + left + "px; top:"
				+ top + "px;\"";
		return res;
	}

	private String generaStyleSinAlto(String xmiid, String eid) {
		String res = "";
		int left = 0;
		int top = 0;
		int width = 0;
		// Recuperamos las coordenadas de la página donde se encuentra este
		// elemento y calculamos las coordenadas finales
		int[] geometryPage = this.buscaOwnedViewsMdElementbyId(
				this.paginaActual).getGeometry();
		int[] geometryElem = this.buscaOwnedViewsMdElementbyXmiId(xmiid)
				.getGeometry();
		int[] geometryTab = null;
		int[] geometryIMap = null;
		if (this.dentroDeTab(xmiid)) {
			geometryTab = this.buscaOwnedViewsMdElementbyXmiId(this.tabActual)
					.getGeometry();
			left = geometryElem[0] - geometryTab[0];
			top = geometryElem[1] - geometryTab[1];
		} else if (this.dentroDeImageMap(xmiid)) {
			geometryIMap = this.buscaOwnedViewsMdElementbyXmiId(
					this.imageMapActual).getGeometry();
			left = geometryElem[0] - geometryIMap[0];
			top = geometryElem[1] - geometryIMap[1];
		} else {
			left = geometryElem[0] - geometryPage[0];
			top = geometryElem[1] - geometryPage[1];
		}
		width = geometryElem[2]; // anchura
		res = res + " style=\"position:absolute; left:" + left + "px; top:"
				+ top + "px; width:" + width + "px;\" ";
		return res;
	}

	/**
	 * Metodo que coloca en un sitio adecuado el iconito del calendario en el
	 * cual se se pulsa se despliega un calendario grande para elegir una
	 * determinada fecha. Se le pasa como argumenteo el xmi-id y el elementid
	 * del OwnedViewsMdElement de un elemento DateTimePicker.
	 */
	private String generaStyleIconoCalendario(String xmiid, String eid) {
		String res = "";
		int left = 0;
		int top = 0;
		// Recuperamos las coordenadas de la página donde se encuentra este
		// elemento y calculamos las coordenadas finales
		int[] geometryPage = this.buscaOwnedViewsMdElementbyId(
				this.paginaActual).getGeometry();
		int[] geometryElem = this.buscaOwnedViewsMdElementbyXmiId(xmiid)
				.getGeometry();
		int[] geometryTab = null;
		int[] geometryIMap = null;
		if (this.dentroDeTab(xmiid)) {
			geometryTab = this.buscaOwnedViewsMdElementbyXmiId(this.tabActual)
					.getGeometry();
			left = geometryElem[0] - geometryTab[0];
			top = geometryElem[1] - geometryTab[1];
		} else if (this.dentroDeImageMap(xmiid)) {
			geometryIMap = this.buscaOwnedViewsMdElementbyXmiId(
					this.imageMapActual).getGeometry();
			left = geometryElem[0] - geometryIMap[0];
			top = geometryElem[1] - geometryIMap[1];
		} else {
			left = geometryElem[0] - geometryPage[0];
			top = geometryElem[1] - geometryPage[1];
		}
		left = left - 20; // para que quede un poco a la izquierda del campo de
		// texto de la fecha
		res = res + " style=\"position:absolute; left:" + left + "px; top:"
				+ top + "px;\"";
		return res;
	}

	/**
	 * Metodo que recibe un xmiid de un elemento y determina si dicho elemento
	 * está contenido o no en una Tab.
	 * */
	private boolean dentroDeTab(String xmiid) {
		boolean res = false;
		String xmiPadre = this.buscaOwnedViewsMdElementbyXmiId(xmiid)
				.getXmiidPadre();
		while (xmiPadre != null) {
			if (this.averiguaStereotype(
					this.buscaOwnedViewsMdElementbyXmiId(xmiPadre)
							.getElementId()).equals("Tab")) {
				res = true;
			}
			xmiPadre = this.buscaOwnedViewsMdElementbyXmiId(xmiPadre)
					.getXmiidPadre();
		}
		return res;
	}

	/**
	 * Metodo que recibe un xmiid de un elemento y determina si dicho elemento
	 * está contenido o no en un ImageMap.
	 * */
	private boolean dentroDeImageMap(String xmiid) {
		boolean res = false;
		String xmiPadre = this.buscaOwnedViewsMdElementbyXmiId(xmiid)
				.getXmiidPadre();
		while (xmiPadre != null) {
			if (this.averiguaStereotype(
					this.buscaOwnedViewsMdElementbyXmiId(xmiPadre)
							.getElementId()).equals("ImageMap")) {
				res = true;
			}
			xmiPadre = this.buscaOwnedViewsMdElementbyXmiId(xmiPadre)
					.getXmiidPadre();
		}
		return res;
	}

	/**
	 * Busca un atributo de un elemento pasandole el id de dicho atributo y el
	 * id del elemento en el que está dicho atributo. Si no lo encuentra
	 * devuelve null.
	 * */
	private OwnedAttribute buscaOwnedAttributebyId(String idpadre, String id) {
		OwnedAttribute res = null;
		OwnedMember om = this.buscaOwnedMemberbyId(idpadre);
		for (OwnedAttribute oa : om.getListOwnedAttributes()) {
			if (oa.getId().equals(id)) {
				res = oa;
				return res;
			}
		}
		return res;
	}

	/**
	 * Busca un atributo de un elemento pasandole solamente el id de dicho
	 * atributo Si no lo encuentra devuelve null.
	 * */
	private OwnedAttribute buscaOwnedAttributebyId(String id) {
		OwnedAttribute res = null;
		for (OwnedMember om : this.listaMembers) {
			for (OwnedAttribute oa : om.getListOwnedAttributes()) {
				if (oa.getId().equals(id)) {
					res = oa;
					return res;
				}
			}
		}
		return res;
	}

	/**
	 * Busca un atributo de un elemento pasandole solamente el name de dicho
	 * atributo Si no lo encuentra devuelve null.
	 * */
	private OwnedAttribute buscaOwnedAttributebyName(String name) {
		OwnedAttribute res = null;
		for (OwnedMember om : this.listaMembers) {
			for (OwnedAttribute oa : om.getListOwnedAttributes()) {
				if (oa.getName().equals(name)) {
					res = oa;
					return res;
				}
			}
		}
		return res;
	}

	/**
	 * Busca un ElemStereotyped pasandole solamente el id de dicho
	 * ElemStereotyped Si no lo encuentra devuelve null.
	 * */
	private ElemStereotyped buscaElementStereotypedbyId(String id) {
		ElemStereotyped res = null;
		for (ElemStereotyped es : this.conjElemStereotyped) {
			if (es.getId().equals(id)) {
				res = es;
			}
		}
		return res;
	}

	/**
	 * Devuelve una cadena con el nombre del estereotypo asociado al elemento al
	 * que hace referencia el id pasado como argumento. Si no lo encuentra
	 * devuelve null.
	 * */
	private String averiguaStereotype(String id) {
		String res = null;
		for (Stereotype s : listaStypes) {
			for (String elem : s.getListaid()) {
				if (elem.equals(id)) {
					res = s.getName();
					return res;
				}
			}
		}
		return res;
	}

	/**
	 * Busca un Stereotype pasandole su id. Si no lo encuentra devuelve null.
	 */
	public Stereotype buscaStereotypebyId(String id) {
		Stereotype res = null;
		for (Stereotype s : listaStypes) {
			if (s.getId().equals(id)) {
				res = s;
				return res;
			}
		}
		return res;
	}

	public List<OwnedMember> getListaMembers() {
		return listaMembers;
	}

	public void setListaMembers(List<OwnedMember> listaMembers) {
		this.listaMembers = listaMembers;
	}

	public List<Stereotype> getListaStypes() {
		return listaStypes;
	}

	public void setListaStypes(List<Stereotype> listaStypes) {
		this.listaStypes = listaStypes;
	}

	/**
	 * Metodo principal que realiza toda la lógica del proceso de parseado
	 * 
	 * @throws Exception
	 **/

	public void procesa(File f) throws Exception {
		this.fichero = f;
		this.versionMagicDraw = this.averiguaVersionMagicDrawUsada();
		debug("version MagicDraw usada=" + this.versionMagicDraw);
		this.leeOwnedMembers();
		this.leeDataStereotypes();
		this.procesaAssociationClasses();
		// this.imprimeconjElemStereotyped();
		// this.imprimelistaMembers();
		this.generaPaginasHTML();
		this.copiaFicherosNecesarios();
		// this.imprimelistaAssociationClass();
		if (this.versionMagicDraw == -1) {
			throw new ParserXmiException(
					"La versión de MagicDraw usada no es compatible con este Parser. Use la 12 o 15.1");
		}
	}

	/**
	 * Para cada AsociaciationClass establecemos sus propiedades OwnedMemberA y
	 * OwnedMemberB, donde: OwnedMemberA: id del ownedmember de la clase destino
	 * de la asociación. OwnedMemberB: id del ownedmember de la clase origen de
	 * la asociación. También obtenemos los atributos de los AssociationClass
	 * 
	 * @throws PintaException
	 * **/
	private void procesaAssociationClasses() throws ParserXmiException,
			PintaException {
		for (AssociationClass ac : this.listaAssociationClass) {
			OwnedMember om = this.buscaOwnedMemberbyId(ac.getId());
			OwnedViewsMdElement ovmde = this.buscaOwnedViewsMdElementbyId(om
					.getId());
			ac.setIdOwnedMemberA(this.buscaOwnedViewsMdElementbyXmiId(
					ovmde.getLinkFirstEndID()).getElementId());
			ac.setIdOwnedMemberB(this.buscaOwnedViewsMdElementbyXmiId(
					ovmde.getLinkSecondEndID()).getElementId());
			// System.out.println("AC id="+ac.getId()+"\n OMA(dest)="+ac.getIdOwnedMemberA()+"\n OMB(orig)="+ac.getIdOwnedMemberB());
			this.obtieneAtributosAssociationClass(ac);
		}

		// Revisamos que no haya asociaciones sin estar estereotipadas como
		// Transition
		if (!compruebaAsociacionEstereotipada(this.listaAssociationClass))
			throw new PintaException(
					"Hay una clase asociación no estereotipada como <<Transition>>");

	}

	private boolean compruebaAsociacionEstereotipada(
			List<AssociationClass> lista) throws PintaException {

		for (AssociationClass ac : lista) {
			// Devuelve el nombre del estereotipo asociado a este elemento
			String estereoName = this.averiguaStereotype(ac.getId());
			if (estereoName == null
					|| estereoName.equals("Transition") == false)
				return false;
		}
		return true;
	}

	/*
	 * private boolean generaTransiciones(String fichero) { boolean exito=true;
	 * PrintWriter pw=null; try { //pw=new PrintWriter(new
	 * FileWriter(fichero+".html",true)); //apertura en modo append
	 * 
	 * }catch(Exception e){ exito=false; e.printStackTrace(); }finally{ if
	 * (pw!=null){ pw.flush(); pw.close(); } } return exito;
	 * 
	 * }
	 */

	/**
	 * Metodo que averigua la versión de Magic Draw que se usó para crear el
	 * fichero XML que se procesa. Segun sea una versión u otra, se usarán unas
	 * determinadas etiquetas u otras. Por ejemplo, en la versión 12 de
	 * MagicDraw, se usa la etiqueta ownedMember. Sin embargo, en la versión 15,
	 * dicha etiqueta se ha pasado a llamar packagedElement. Si no se pudo
	 * averiguar la versión usada, por defecto se usará la configuracíon del
	 * Parser XMI para la versión 12. Actualmente el Parser XMI solo garantiza
	 * funcionar con la versión 12 y 15. Si devuelve -1, se produjo un error.
	 * **/
	private int averiguaVersionMagicDrawUsada() {
		BufferedReader br = null;
		int version = 12;
		try {
			InputStreamReader in = new InputStreamReader(new FileInputStream(
					fichero), "UTF-8");
			br = new BufferedReader(in);
			String linea = br.readLine();
			while (linea != null) {
				if (linea.contains("<xmi:Documentation")) {
					StringTokenizer st = new StringTokenizer(linea, " ");
					while (st.hasMoreTokens()) {
						String token = st.nextToken();
						if (token.toUpperCase().contains(
								new String("ExporterVersion").toUpperCase())) {
							String[] partes = token.split("=");
							if (partes[1].contains("12")) {
								version = 12;
							} else if (partes[1].contains("13")) {
								version = 12;
							} else if (partes[1].contains("14")) {
								version = 12;
							} else if (partes[1].contains("15")) {
								version = 15;
							} else {
								version = 12;
							}
						}
					}
				}
				linea = br.readLine();
			}
		} catch (FileNotFoundException e) {
			version = -1; // indicará error
			debug("Error al abrir el fichero " + fichero);
			e.printStackTrace();
		} catch (IOException e) {
			version = -1;
			debug("Error al leer del fichero " + fichero);
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return version;

	}

	/**
	 * Deja al parser en un estado listo para volver a realizar otro
	 * procesamiento de un XML.
	 * **/
	public void limpiezaDatos() {
		listaMembers = new ArrayList<OwnedMember>();
		listaStypes = new ArrayList<Stereotype>();
		listaOvMdElement = new ArrayList<OwnedViewsMdElement>();
		listaAssociationClass = new ArrayList<AssociationClass>();
		fichero = null;
		paginaActual = null;
		tabActual = null;
		imageMapActual = null;
		listaTabArea = new ArrayList<TabArea>();
		conjElemStereotyped = new HashSet<ElemStereotyped>();
		nombresPaginas = new ArrayList<String>();
		versionMagicDraw = 12;
		directorioTrabajo = "";
		paginaHome = null;
	}

	/**
	 * Método que simplemente muestra una linea de texto pasada como argumento
	 * por pantalla. Destinado a depurar el programa
	 * */
	public void debug(String linea) {
		glueweb.pages.LogPanel.printLine(linea);
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

}
