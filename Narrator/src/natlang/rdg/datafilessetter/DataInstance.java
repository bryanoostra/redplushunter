/**
 * 
 */
package natlang.rdg.datafilessetter;

import java.io.File;
import java.io.InputStream;

/**
 * @author Rene Zeeders
 *
 */
public class DataInstance {

	private InputStream inputstream;
	private String lang;
	private String baseURI = "";
	private String filename = "";
	
	public DataInstance(InputStream inputstream, String lang, String baseURI){
		this.inputstream = inputstream;
		this.lang = lang;
		this.baseURI = baseURI;
	}
	
	public DataInstance(InputStream inputstream, String lang){
		this.inputstream = inputstream;
		this.lang = lang;
	}

	public DataInstance(InputStream inputstream, String lang, File f){
		this.inputstream = inputstream;
		this.lang = lang;
		this.baseURI = "file:" + f.getName();
		this.filename = f.getAbsolutePath();
	}
	
	/**
	 * @return the baseURI
	 */
	public String getBaseURI() {
		return baseURI;
	}

	/**
	 * @param baseURI the baseURI to set
	 */
	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}

	/**
	 * @return the in
	 */
	public InputStream getReader() {
		return inputstream;
	}

	/**
	 * @param in the in to set
	 */
	public void setReader(InputStream in) {
		this.inputstream = in;
	}

	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public String getFileName(){
		return filename;
	}
	
	public String toString(){
		return filename;
	}
	
}
