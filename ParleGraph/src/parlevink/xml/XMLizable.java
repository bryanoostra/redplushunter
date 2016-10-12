/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:25 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLizable.java,v $
// Revision 1.1  2006/05/24 09:00:25  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:10  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1.1.1  2002/09/11 09:36:13  zwiers
// initial import
//
// Revision 1.1.1.1  2002/08/07 15:09:09  zwiers
// Startup parlevink repository
//
// Revision 1.8  2002/06/19 16:02:53  zwiers
// update comment
//
// Revision 1.7  2002/01/16 10:16:22  zwiers
// added comment
//
// Revision 1.6  2002/01/15 09:19:34  reidsma
// added exception
//

package parlevink.xml;

import java.io.*;

/*
 * XMLizable defines objects that can be represented as an XML text string, and
 * that can be reconstructed from such strings.
 * The <b> minimal </b> requirement is that readXML will reconstruct an object
 * from a stream that is constructed by means of writeXML.
 * The methods required by this interface are:
 *
 * public void readXML(Reader) throws IOException
 * public void readXML(String)
 * public void readXML(XMLTokenizer) throws IOException
 * public void writeXML(PrintWriter)
 * public String toXMLString()
 *
 * The readXML(XMLTokenizer) implementation is optional; the method must be
 * available, but is allowed to throw an UnsupportedOperationException.
 * The other methods must be properly implemented, and must be consistent.
 * That is, XML formatted Strings, as written by writeXML, or produced by toXMLSTring()
 * must be accepted by the readXML() methods and must lead to reconstruction of
 * the original XMLizable object.
 */

public interface XMLizable
{

   /**
    * reconstructs this XMLizable object by reading and parsing XML encoded text from a Reader.
    * This method can throw an (unchecked) ScanException in case of incorrectly
    * formatted XML. 
    */
   public void readXML(Reader in) throws IOException; 
   
   /**
    * reconstructs this XMLizable object by parsing an XML encoded String s.
    * This method can throw an (unchecked) ScanException in case of incorrectly
    * formatted XML. 
    */
   public void readXML(String s); 
   
   /**
    * reconstructs this XMLizable object by parsing a stream of XML tokens,
    * that are delivered by a XMLTokenizer.
    * This method need not be supported, in which case the method should throw
    * a java.lang.UnsupportedOperationException.
    * This method can throw an (unchecked) ScanException in case of incorrectly
    * formatted XML. 
    */
   public void readXML(XMLTokenizer tokenizer) throws IOException; 


   /**
    * writes an XML encoded String to "out".
    * This String should equal the result of toXMLString().
    */
   public void writeXML(PrintWriter out); 

   /**
    * yields an XML encoded String of this XMLIzable object. 
    * The readXML() methods should be able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
   public String toXMLString(); 

}
