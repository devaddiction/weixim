package glueweb.axim;

import glueweb.controllers.ControlParserAxim;


public interface VistaParser {
	
	void agregaMensaje(String m);
	void controlador(ControlParserAxim crt);
	void agregaTexto(String texto);
	void limpiarbarraestado();
	void limpiatexto();
	void statusError(String msg);
	void statusOk(String msg);
	void statusNull();
}
