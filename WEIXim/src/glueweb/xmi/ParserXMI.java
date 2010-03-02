package glueweb.xmi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * parser an XMI file only to obtain the name of the A side and the B side of an
 * arrow.
 */
public class ParserXMI {

	private static Map<String, Association> associations;

	public ParserXMI() {
		associations = new HashMap<String, Association>();
	}

	/**
	 * load associations name list from "filename" file
	 * 
	 * @param fileName
	 * @param associationsNameList
	 * @throws Exception 
	 */
	public static void loadXmiFile(String fileName,
			ArrayList<String> associationsNameList,
			ArrayList<String> associationsDependencesList,
			ArrayList<String> functionNameList) throws Exception {

		for (int i = 0; i < associationsNameList.size(); i++) {
			loadAssociation(fileName, associationsNameList.get(i),
					associationsDependencesList.get(i), functionNameList.get(i));
		}

	}

	/**
	 * load an association
	 * 
	 * @param fileName
	 * @param assocName
	 * @throws Exception 
	 */
	private static void loadAssociation(String fileName, String contextName,
			String dependencyName, String funcName) throws Exception {
		String buffer;
		Association assoc;

		String idAssociationClass = null;
		String source = null;
		String target = null;

		String estereotipoOrigen = null;
		String estereotipoSource = null;
		String estereotipoTarget = null;

		String metodo = null;
		String evento = null;
		String origen = null;

		String idSource = null;
		String idTarget = null;

		String idTargetMethod = null;
		String targetMethod = null;

		String sourceClassId = null;
		String realizingClassifier = null;

		String idOrigenClass = null;

		String memberEnd1 = null;
		String memberEnd2 = null;
		ArrayList<String> estereotipos;

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			buffer = br.readLine();
			while (buffer != null) {
				if (buffer
						.contains("<packagedElement xmi:type='uml:AssociationClass'")
						&& buffer.contains("name='" + contextName + "'")) {
					idAssociationClass = getIdFromLine(buffer);
					while (!buffer.contains("</packagedElement>")) {
						buffer = br.readLine();
						if (buffer.contains("<memberEnd xmi:idref='"))
							memberEnd1 = getIdRefFromLine(buffer);
						buffer = br.readLine();
						if (buffer.contains("<memberEnd xmi:idref='"))
							memberEnd2 = getIdRefFromLine(buffer);
						buffer = br.readLine();
						if (buffer
								.contains("<ownedAttribute xmi:type='uml:Property'")
								&& buffer.contains("name='metodo'")) {
							buffer = br.readLine();
							metodo = getValueFromLine(buffer);
							if (buffer.contains(funcName)) {
								while (!buffer.contains("</packagedElement>")) {
									if (buffer.contains("name='origen'")) {
										buffer = br.readLine();
										origen = getValueFromLine(buffer);
									}
									if (buffer.contains("name='evento'")) {
										buffer = br.readLine();
										evento = getValueFromLine(buffer);
									}
									buffer = br.readLine();
									if (buffer
											.contains("<ownedOperation xmi:type='uml:Operation'")
											&& buffer
													.contains("name='"
															+ metodo
																	.substring(
																			0,
																			metodo
																					.length() - 2)
																	.toString()
															+ "'")) {
										metodo = metodo.substring(0, metodo
												.length() - 1);
										buffer = br.readLine();
										if (buffer.contains("<ownedParameter")) {
											while (!buffer
													.contains("</ownedOperation>")) {
												if (buffer
														.contains("<ownedParameter")) {
													metodo += getParamFromLine(buffer)
															+ ", ";
												}
												buffer = br.readLine();
											}
											if (metodo.endsWith(", "))
												metodo = metodo.substring(0,
														metodo.length() - 2);
										}
										metodo += ")";
									}
								}
							}
						}
					}
				}
				buffer = br.readLine();
			}
			br.close();

			// Con la transición y el nombre del source,
			// busco la dependencia para averiguar el componente
			br = new BufferedReader(new FileReader(fileName));
			buffer = br.readLine();
			while (buffer != null) {
				if (buffer
						.contains("<packagedElement xmi:type='uml:Dependency'")
						&& buffer.contains("name='" + dependencyName + "'")) {
					buffer = br.readLine();
					if (buffer.contains("supplier"))
						idTargetMethod = getIdRefFromLine(buffer);
				}
				buffer = br.readLine();
			}
			br.close();

			br = new BufferedReader(new FileReader(fileName));
			buffer = br.readLine();
			while (buffer != null) {
				if (buffer.contains("<mdElement elementClass='Component'")) {
					String tmpIdComponent = getIdRefFromLine(buffer = br
							.readLine());
					buffer = br.readLine();
					while (!buffer.contains("</mdOwnedViews>")) {
						if (buffer.contains("<elementID xmi:idref='"
								+ idTargetMethod + "'"))
							idTarget = tmpIdComponent;
						buffer = br.readLine();
					}
				}
				buffer = br.readLine();
			}
			br.close();

			estereotipos = getStereotypesFromFile(fileName);

			// Ya tenemos la clase Source
			idSource = getSourceIdFromAssociation(fileName, idAssociationClass,
					origen, memberEnd1, memberEnd2);
			sourceClassId = getIdClassNameFromFile(fileName, idSource);
			estereotipoSource = getStereotypeFromID(fileName,
					getElementIdFromFile(fileName, idSource), estereotipos);
			source = getNameFromFile(fileName, sourceClassId);
			if (source != null && source.contains("#")) {
				StringTokenizer st = new StringTokenizer(source, "#");
				source = st.nextToken().toString();
			}

			// Component Target
			realizingClassifier = getRealizingClassifierFile(fileName, idTarget);
			target = getNameFromFile(fileName, idTarget);
			idOrigenClass = getOrigenFromFile(fileName, sourceClassId, origen);

			estereotipoTarget = getStereotypeFromID(fileName,
					realizingClassifier, estereotipos);

			estereotipoOrigen = getStereotypeFromID(fileName, idOrigenClass,
					estereotipos);

			// Corresponden
			targetMethod = getMethodFromID(fileName, idTargetMethod);

			if (sameMethods(targetMethod, metodo)) {
				assoc = new Association(contextName, idAssociationClass,
						source, estereotipoSource, target, estereotipoTarget,
						metodo, origen, estereotipoOrigen, evento);
				if (!associations.containsKey(contextName))
					associations.put(contextName, assoc);
			} else {
				System.out.println("Métodos no concuerdan");
			}

		} catch (FileNotFoundException e) {
			System.out.println("\t-XMI ERROR- File not found");
		} catch (IOException e) {
			System.out.println("\t-XMI ERROR- I/O error");
		}

	}

	private static boolean sameMethods(String targetMethod, String metodo) {
		String methodName1 = null, methodName2 = null;
		StringTokenizer stName1 = new StringTokenizer(targetMethod, "(");
		methodName1 = stName1.nextToken();

		StringTokenizer stName2 = new StringTokenizer(metodo, "(");
		methodName2 = stName2.nextToken();

		if (!methodName1.equals(methodName2))
			return false;

		int numArgs1 = getNumArgs(targetMethod);
		int numArgs2 = getNumArgs(metodo);
		if (numArgs1 != numArgs2)
			return false;
		return true;
	}

	private static int getNumArgs(String line) {
		int res = 0;

		StringTokenizer st = new StringTokenizer(line, "(),");
		while (st.hasMoreTokens()) {
			st.nextToken();
			res++;
		}
		return res;
	}

	private static String getMethodFromID(String fileName, String idTargetMethod)
			throws IOException {
		String metodo = null;

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String buffer = br.readLine();

		buffer = br.readLine();
		while (buffer != null) {
			if (buffer
					.contains("<ownedOperation xmi:type='uml:Operation' xmi:id='"
							+ idTargetMethod + "'")) {
				metodo = getNameFromLine(buffer);
				metodo += "(";
				buffer = br.readLine();
				if (buffer.contains("<ownedParameter")) {
					while (!buffer.contains("</ownedOperation>")) {
						if (buffer.contains("<ownedParameter")) {
							metodo += getParamFromLine(buffer) + ", ";
						}
						buffer = br.readLine();
					}
					if (metodo.endsWith(", "))
						metodo = metodo.substring(0, metodo.length() - 2);
					metodo += ")";

				}
			}
			buffer = br.readLine();
		}
		return metodo;
	}

	private static String getOrigenFromFile(String fileName, String idClass,
			String origen) throws IOException {
		String buffer;
		String result = null;
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		buffer = br.readLine();
		while (buffer != null) {
			if (buffer
					.contains("<packagedElement xmi:type='uml:Class' xmi:id='"
							+ idClass + "'")) {
				buffer = br.readLine();
				while (!buffer.contains("</packagedElement>")) {
					if (buffer.contains("name='" + origen + "'")) {
						result = getIdFromLine(buffer);
						br.close();
						return result;
					}
					buffer = br.readLine();

				}
			}
			buffer = br.readLine();
		}
		br.close();
		return null;
	};

	private static String getRealizingClassifierFile(String fileName, String id)
			throws IOException {
		String buffer;
		String result = null;
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		buffer = br.readLine();
		while (buffer != null) {
			if (buffer
					.contains("<packagedElement xmi:type='uml:Component' xmi:id='"
							+ id + "'")) {
				buffer = br.readLine();
				while (!buffer.contains("</packagedElement>")) {
					if (buffer
							.contains("<realization xmi:type='uml:ComponentRealization'")
							&& buffer.contains("realizingClassifier")) {
						result = getRealizingClassifierFromLine(buffer);
						br.close();
						return result;
					}
					buffer = br.readLine();

				}
			}
			buffer = br.readLine();
		}
		br.close();
		return null;
	};

	private static String getRealizingClassifierFromLine(String line) {
		String res = null;

		StringTokenizer st = new StringTokenizer(line, "< />");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.contains("realizingClassifier='")) {
				StringTokenizer st2 = new StringTokenizer(token, "'");
				st2.nextToken();
				res = st2.nextToken();
			}
		}
		return res;
	}

	private static String getNameFromFile(String fileName, String id)
			throws IOException {
		String res = null;
		String buffer;
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		buffer = br.readLine();
		while (buffer != null) {
			if (buffer.contains("xmi:id='" + id + "'")) {
				return getNameFromLine(buffer);
			}
			buffer = br.readLine();
		}
		return res;
	}

	private static String getSourceIdFromAssociation(String fileName,
			String id, String origen, String memberEnd1, String memberEnd2)
			throws Exception {
		String buffer;
		boolean linkFirst = false, linkSecond = false;
		linkFirst = containsOrigen(fileName, memberEnd1, origen);
		linkSecond = containsOrigen(fileName, memberEnd2, origen);
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		buffer = br.readLine();
		while (buffer != null) {
			if (buffer.contains("<mdElement elementClass='AssociationClass'")) {
				buffer = br.readLine();
				while (!buffer.contains("<mdOwnedViews>")) {
					if (buffer.contains("<elementID xmi:idref='" + id + "'")) {
						buffer = br.readLine();
						while (!buffer.contains("<mdOwnedViews>")) {
							if (linkFirst && linkSecond) {
								if (buffer
										.contains("<linkFirstEndID xmi:idref='")) {
									br.close();
									return getIdRefFromLine(buffer);
								}
							} else if (linkFirst && !linkSecond) {
								if (buffer
										.contains("<linkFirstEndID xmi:idref='")) {
									br.close();
									return getIdRefFromLine(buffer);
								}
							} else if (!linkFirst && linkSecond) {
								if (buffer
										.contains("<linkSecondEndID xmi:idref='")) {
									br.close();
									return getIdRefFromLine(buffer);
								}
							} else
								throw new Exception("No se encuentra origen: "+ origen);
							buffer = br.readLine();
						}
					}
					buffer = br.readLine();
				}
			}
			buffer = br.readLine();
		}
		br.close();
		return null;
	};

	private static boolean containsOrigen(String fileName, String id,
			String origen) throws IOException {
		String tipo = null;
		String buffer = null;
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		buffer = br.readLine();
		while (buffer != null) {
			if (buffer.contains("xmi:id='" + id + "'"))
				tipo = getTypeFromLine(buffer);
			buffer = br.readLine();
		}
		br.close();
		if (tipo != null) {
			br = new BufferedReader(new FileReader(fileName));
			buffer = br.readLine();
			while (buffer != null) {
				if (buffer
						.contains("<packagedElement xmi:type='uml:Class' xmi:id='"
								+ tipo)) {
					buffer = br.readLine();
					while (!buffer.contains("</packagedElement>")) {
						if (buffer.contains(origen))
							return true;
						buffer = br.readLine();
					}
				}
				buffer = br.readLine();
			}
		}
		return false;
	}

	private static String getTypeFromLine(String buffer) {
		String res = null;

		StringTokenizer st = new StringTokenizer(buffer, "< />");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.contains("type='")) {
				StringTokenizer st2 = new StringTokenizer(token, "'");
				st2.nextToken();
				res = st2.nextToken();
			}
		}
		return res;
	}

	private static String getStereotypeFromID(String fileName, String id,
			ArrayList<String> estereotipos) throws IOException {
		String res = null;
		String buffer;
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		buffer = br.readLine();
		while (buffer != null) {
			for (int i = 0; i < estereotipos.size(); i++) {
				if (buffer.contains(estereotipos.get(i))
						&& buffer.contains("'" + id + "'")
						&& !buffer.contains("context")
						&& !buffer.contains("HyperlinkOwner")) {
					return getStereotypeFromLine(buffer, estereotipos);

				}
			}
			buffer = br.readLine();
		}
		return res;
	}

	private static ArrayList<String> getStereotypesFromFile(String fileName)
			throws IOException {
		ArrayList<String> resultado = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String buffer, stereotypeName = null;
		buffer = br.readLine();
		while (buffer != null) {
			if (buffer.contains("<stereotypesIDS>")) {
				buffer = br.readLine();
				while (!buffer.contains("</stereotypesIDS>")) {
					if (buffer.contains("<stereotype")) {
						stereotypeName = getNameFromLine(buffer);
						StringTokenizer st = new StringTokenizer(
								stereotypeName, ":");
						while (st.countTokens() > 1) {
							st.nextToken();
						}
						resultado.add(st.nextToken());
					}

					buffer = br.readLine();
				}
			}
			buffer = br.readLine();
		}
		return resultado;
	}

	private static String getStereotypeFromLine(String line,
			ArrayList<String> estereotipos) {
		String res = null;

		StringTokenizer st = new StringTokenizer(line, "< />");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			for (int i = 0; i < estereotipos.size(); i++) {
				if (token.contains(estereotipos.get(i))
						&& !line.contains("context")) {
					return estereotipos.get(i);
				}
			}
		}
		return res;
	}

	private static String getIdClassNameFromFile(String fileName, String id)
			throws IOException {
		String res = null;
		String buffer;
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		buffer = br.readLine();
		while (buffer != null) {
			if (buffer.contains("xmi:id='" + id + "'")) {
				buffer = br.readLine();
				if (buffer.contains("<elementID xmi:idref='")) {
					return getIdRefFromLine(buffer);
				}
			}
			buffer = br.readLine();
		}
		return res;
	}

	private static String getParamFromLine(String line) {
		String res = "";

		StringTokenizer st = new StringTokenizer(line);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.startsWith("name='")) {
				res = token.substring(6);
				if (res.endsWith("'"))
					res = res.substring(0, res.length() - 1);
				String tmp = st.nextToken();
				String tmp2;
				if (tmp.endsWith(";'")) {
					if (tmp.contains("&quot;")) {
						tmp2 = tmp.replaceAll("&quot;", "'");
						res += " " + tmp2;
					} else
						res += " " + tmp;
				}
			}
		}
		if (res.endsWith("''"))
			res = res.substring(0, res.length() - 1);
		return res;
	}

	private static String getNameFromLine(String line) {
		String res = null;

		StringTokenizer st = new StringTokenizer(line, "< />");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.contains("name")) {
				StringTokenizer st2 = new StringTokenizer(token, "'");
				st2.nextToken(); // xmi:id ignored
				res = st2.nextToken();
			}
		}
		return res;
	}

	private static String getIdRefFromLine(String line) {
		String res = null;

		StringTokenizer st = new StringTokenizer(line, "< />");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.contains("xmi:idref='")) {
				StringTokenizer st2 = new StringTokenizer(token, "'");
				st2.nextToken(); // xmi:id ignored
				res = st2.nextToken();
			}
		}
		return res;
	}

	/**
	 * @param line
	 *            contains "xmi:id="
	 * @return value of "xmi:id="
	 */
	private static String getIdFromLine(String line) {
		String res = null;

		StringTokenizer st = new StringTokenizer(line, "< />");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.contains("xmi:id='")) {
				StringTokenizer st2 = new StringTokenizer(token, "'");
				st2.nextToken(); // xmi:id ignored
				res = st2.nextToken();
			}
		}
		return res;
	}

	private static String getValueFromLine(String line) {
		String res = null;

		StringTokenizer st = new StringTokenizer(line, "< />");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.contains("value='")) {
				StringTokenizer st2 = new StringTokenizer(token, "'");
				st2.nextToken(); // xmi:id ignored
				res = st2.nextToken();
			}
		}
		return res;
	}

	public static String getElementIdFromFile(String fileName, String id)
			throws IOException {
		String res = null;
		String buffer;

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		buffer = br.readLine();
		while (buffer != null) {
			if (buffer.contains("<mdElement elementClass='")
					&& buffer.contains("xmi:id='" + id + "'")) {
				buffer = br.readLine();
				while (!buffer.contains("<mdElement")) {
					if (buffer.contains("<elementID")) {
						return getIdRefFromLine(buffer);
					}
					buffer = br.readLine();
				}

			}
			buffer = br.readLine();
		}
		return res;
	}

	public static ArrayList<String> getPagesFromFile(String fileName)
			throws IOException {
		String buffer;
		ArrayList<String> estereotipos = getStereotypePageFromFile(fileName);
		ArrayList<String> res = new ArrayList<String>();

		for (int i = 0; i < estereotipos.size(); i++) {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			buffer = br.readLine();
			while (buffer != null) {
				if (buffer.contains("context='" + estereotipos.get(i))) {
					res.add(getNameFromLine(buffer));
				}
				buffer = br.readLine();
			}
			br.close();
		}
		return res;
	}

	static ArrayList<String> getStereotypePageFromFile(String fileName)
			throws IOException {
		ArrayList<String> resultado = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String buffer = br.readLine();
		while (buffer != null) {
			if (buffer.contains("</xmi:Extension>")) {
				buffer = br.readLine();
				while (buffer != null) {
					if (!buffer.contains("name")
							&& (buffer.contains("page") || buffer
									.contains("Page"))) {
						resultado.add(getBaseFromLine(buffer));
					}
					buffer = br.readLine();
				}
			}
			buffer = br.readLine();
		}
		return resultado;
	}

	private static String getBaseFromLine(String line) {
		String res = null;

		StringTokenizer st = new StringTokenizer(line, "< />");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.contains("base_Element='")) {
				StringTokenizer st2 = new StringTokenizer(token, "'");
				st2.nextToken(); // xmi:id ignored
				res = st2.nextToken().toString();
			}
		}
		return res;
	}

	/**
	 * print associations
	 */
	public void associationsView() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		for (String str : associations.keySet()) {
			glueweb.pages.LogPanel.printLine(associations.get(str).toString());
			pw.println(associations.get(str).toString());
		}
		glueweb.pages.LogPanel.printLine("--------------------\n");

	}

	/**
	 * remove &#x[AB]; from Strings
	 * 
	 * @param str
	 *            dirty
	 * @return str clean
	 */
	@SuppressWarnings("unused")
	private String clean(String str) {
		int end = str.indexOf('&');

		return str.substring(0, end);
	}

	/**
	 * @return associations
	 */
	public Map<String, Association> getAssociations() {
		return associations;
	}
}
