package natlang.deptree.model;

import java.util.*;


/** 
 *
 */
public class DutchDependencyStructure implements DependencyStructure {

   private String TextLine;
   private String rel;
   private String cat;
   
   DutchDependencyStructure TopNode;
   DutchDependencyStructure MotherNode;
   ArrayList SubStructures;
   HashMap edgeMap; // Maps edges to DependencyStructures on the string rel
   ArrayList edgeLabelList;
   
 /**
  * 
  */
  public DutchDependencyStructure(String ParseString){
  // Init private variables
      TextLine = ParseString;
      SubStructures = new ArrayList();
      edgeMap = new HashMap();
      edgeLabelList = new ArrayList();
   // We are the top node
      rel = "top";
      cat = "smain";
      TopNode = this;
      MotherNode = this; 
  }


   /**
    *
    * Private constructor this is used to create the daughters
    */
   protected DutchDependencyStructure(DutchDependencyStructure TopNode,DutchDependencyStructure MotherNode) {
      this.TopNode = TopNode;
      edgeMap = null;
      this.MotherNode = MotherNode;
      edgeLabelList = null;
      SubStructures = new ArrayList();
   }   	
 
   
 // --- PUBLIC INTERACE PART DERIVED FROM DEPENDENCY STRUCTURE ---
 // Explantion for these is in DependencyStructure
   
   public Iterator labels() {
      if (edgeLabelList == null) return null;
      return edgeLabelList.iterator();
   }
   
   public List getLexicalValue() {
      ArrayList Result = new ArrayList();
      if (isLeaf()) {
         Result.add(new String(TextLine));
         return Result;
      }
      for (int i=0;i<SubStructures.size();i++) {
         Result.add(((DutchDependencyStructure)SubStructures.get(i)).getFeatureValue(LEX));
         return Result;
      }
      return null;
   }
   
   public String getFeatureValue(String featName) {
      if (featName == LEX) {
      	 if (TextLine==null) return "";
      	 return new String(TextLine);
      }
      if (featName == POS) return new String(cat);
      return null;
   }
   
   public DependencyStructure get(List path) {
      if (path.size() == 1) {
         return (DutchDependencyStructure)edgeMap.get((String)path.get(0));
      } else {
        DutchDependencyStructure Step;	
        Step = (DutchDependencyStructure)edgeMap.get((String)path.get(0));
        if (Step==null) return null;
      	return (DutchDependencyStructure)Step.get(path.subList(1, path.size()-1));
      }
   }
   
   public DependencyStructure get(String edgeLabel) {
      return (DependencyStructure)edgeMap.get(edgeLabel);
   }
   
   public boolean isComplete() { //Information is not available assuming it always is
      return true;
   }
   
   public boolean isLeaf() {
      return (SubStructures.size() == 0);
   }
   
   public boolean isTop() {
      return (TopNode==this);
   }
   
   public DependencyStructure getContext() {
      return MotherNode;
   }
   
   public String getRole() {
      return new String(rel);
   }
   
   public DependencyStructure getTopContext() {
      return TopNode;
   }
	
/**
 * @return node id of the root of this structure
 */
public String nodeId(){ 
	return null;
}

public String toXMLString(){
	return "not implemented";
}

//**************************************************************************


public HashMap edgeMap(){ 
	return edgeMap;
}


public  ArrayList edgeLabelList(){
	return edgeLabelList;
}


// This read processes a line from the script and build daughters and set their vars   
   public void ProcessLine(String ProcessStr, HashMap MotherEdgeMap,ArrayList MotherEdgeLabelList) {
      int pos = ProcessStr.indexOf("daughter");
      if (pos > -1) {
      // This line is not for us but for a subnode for us
      // fetch subid
         int start = ProcessStr.indexOf(',',pos)+1;
         int end = ProcessStr.indexOf(',',start);
         int Subid = (new Integer(ProcessStr.substring(start,end))).intValue();
         DutchDependencyStructure Daughter;
         if (Subid >= SubStructures.size()) {
            Daughter = new DutchDependencyStructure(TopNode,this);
            SubStructures.add(Subid,Daughter); 
         } else {
            Daughter = (DutchDependencyStructure)SubStructures.get(Subid);
         }
         if (edgeMap == null) {
            edgeMap = new HashMap();
            edgeLabelList = new ArrayList();
         }
         Daughter.ProcessLine(ProcessStr.substring(end,ProcessStr.length()),edgeMap,edgeLabelList);
      } else {
      // This is line is ours to inteprete, not for a subnote
         pos = ProcessStr.indexOf(",rel,");
         if (pos > -1) {
         // Relation information is in this line from the script
            int start = ProcessStr.indexOf('\"',pos)+1;
            int end = ProcessStr.indexOf('\"',start);
            rel = ProcessStr.substring(start,end);
            MotherEdgeMap.put(rel,this);
            MotherEdgeLabelList.add(rel);
         } else {
            pos = ProcessStr.indexOf("pnode,pcategory,");
            if (pos > -1) {
            // Category information is in this line from the script
               int start = ProcessStr.indexOf('\"',pos)+1;
               int end = ProcessStr.indexOf('\"',start);
               cat = ProcessStr.substring(start,end);
            } else {
               pos = ProcessStr.indexOf("pos,pos,pos");
               if (pos > -1) {
               // Category information is in this line from the script
                  int start = ProcessStr.indexOf('\"',pos)+1;
                  int end = ProcessStr.indexOf('\"',start);
                  cat = ProcessStr.substring(start,end);
               } else {
                  pos = ProcessStr.indexOf(",lex,");
                  if (pos > -1) {
                  // Textline information is in this line from the script
                     int start = ProcessStr.indexOf('\"',pos)+1;
                     int end = ProcessStr.indexOf('\"',start);
                     TextLine = ProcessStr.substring(start,end);
                  } 
               }
            }
         } 
      }
   }
   
// After the script only the leafs have the good textual information and that even in wrong format
// This function corrects it. It puts the right textual value on every node and for the leafs it cuts
// the right part from the main sentence
// The Vector returns the begin position and the end position in the string of this chunk
   
   public Vector ReconstructTextInTree(String topline) {
      if (isLeaf()) {
      // the script provides a tag like /[0,1] the last index indicates the word
         if ((TextLine!=null) && (TextLine.compareTo("")!=0)) { // We can have empty leaves we don't process them
            int start = TextLine.lastIndexOf(',')+1;
            int end = TextLine.lastIndexOf(']');
            int index = (new Integer(TextLine.substring(start,end))).intValue();
            String workingline = topline;
         // count the words The site counts only space. (so for example "e-mail" and "loop,ren" are 1 word)
            start = 0;
            for (int i=1;i!=index;i++) {
              int pos = workingline.indexOf(' ');            
               while (workingline.charAt(pos)==' ') pos++;
               workingline = workingline.substring(pos,workingline.length());
               start += pos;
            }
            end = start + workingline.indexOf(' ');
            if (end <= start) end = start+workingline.length();
            if (workingline.indexOf(' ') > 0) workingline=workingline.substring(0,workingline.indexOf(' '));
            TextLine = workingline;
            Vector Result = new Vector(2);
            Result.add(new Integer(start));
            Result.add(new Integer(end));
            return Result;
         }
         return null;
      } else {
      // Our textuale value is the derived from the daughters
         Vector Positions;
         int start = -1;
         int end = -1;
         Iterator i = SubStructures.iterator();
         while (i.hasNext()) {
            Positions = ((DutchDependencyStructure)i.next()).ReconstructTextInTree(topline);
            if (Positions!=null) {
               if ((start ==-1) || (start > ((Integer)Positions.get(0)).intValue()))
                  start = ((Integer)Positions.get(0)).intValue();
               if ((end ==-1) || (end < ((Integer)Positions.get(1)).intValue()))
                  end = ((Integer)Positions.get(1)).intValue();
            }
         }
         if ((start > -1) && (end > -1)) {
            TextLine = topline.substring(start,end);
            Vector Result = new Vector(2);
            Result.add(new Integer(start));
            Result.add(new Integer(end));
            return Result;         
         }
         TextLine = "";
         return null;
      }
   }


}
