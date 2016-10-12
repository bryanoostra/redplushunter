package vs.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: State
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class State extends FabulaElement{ 

   /**
   * The contents of a fabula element, represented as a list of RDF triples
* Protege name: contentTriple
   */
   private List contentTriple = new ArrayList();
   public void addContentTriple(RDFtriple elem) { 
     List oldList = this.contentTriple;
     contentTriple.add(elem);
   }
   public boolean removeContentTriple(RDFtriple elem) {
     List oldList = this.contentTriple;
     boolean result = contentTriple.remove(elem);
     return result;
   }
   public void clearAllContentTriple() {
     List oldList = this.contentTriple;
     contentTriple.clear();
   }
   public Iterator getAllContentTriple() {return contentTriple.iterator(); }
   public List getContentTriple() {return contentTriple; }
   public void setContentTriple(List l) {contentTriple = l; }

   /**
* Protege name: contentFabula
   */
   private List contentFabula = new ArrayList();
   public void addContentFabula(Object elem) { 
     List oldList = this.contentFabula;
     contentFabula.add(elem);
   }
   public boolean removeContentFabula(Object elem) {
     List oldList = this.contentFabula;
     boolean result = contentFabula.remove(elem);
     return result;
   }
   public void clearAllContentFabula() {
     List oldList = this.contentFabula;
     contentFabula.clear();
   }
   public Iterator getAllContentFabula() {return contentFabula.iterator(); }
   public List getContentFabula() {return contentFabula; }
   public void setContentFabula(List l) {contentFabula = l; }

}
