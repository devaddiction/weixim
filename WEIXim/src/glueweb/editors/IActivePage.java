package glueweb.editors;

/**
 * define a method to set active page
 */
public interface IActivePage {
	public void setActivePage(int i);

	public void showGlue();

	public void showDelete();

	public void showErrorAxim();

	public void showErrorGlue(String title, String cadena);
	
	public void showErrorProperties(String title, String cadena, int tab);
	
}
