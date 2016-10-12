package natlang;
import java.util.*;
import java.io.*;
import natlang.util.*;
import parlevink.util.Resources;

/**
 * class Conjugator 
 * provides static functions for Dutch verb conjugation (werkwoordvervoeging)
 * file "data/natlang/language/DutchVerbs.txt" contains Dutch irregular verbs
 * all other verbs are supposed to be regular.
 * @see VerbItem
 * @deprecated - Not used in current Narrator   
**/

public class Conjugator{

// constants used for conjugation of the verb
public static int PERS1 = 1;
public static int PERS2 = 2;
public static int PERS3 = 3;    
public static int SINGULAR = 1; // singular
public static int PLURAL = 2;   // plural
public static int PRESENT = 1; //tt
public static int PAST = 2;  // vt
public static int PART = 3;  // ovt

private static Vector irregVerbs;  // Vector of VerbItem (irregular verbs)
private static String FileName;
private static String ShortFileName = "DutchVerbs.txt";
/**
 * ready iff irregular verbs are loaded from file
 */
public static boolean ready;
private static Resources myResources;

public Conjugator(){
     myResources = new Resources(this);	
}


public static boolean ready(){
	return ready;
}




public static void readIrreg(){
      irregVerbs = new Vector();
      FileName = myResources.getDataFileName(ShortFileName);
      System.out.println("Reading File with Irregular Verbs :"+FileName);
      MyReader buf = new MyReader(FileName);
  //try{
      buf.open();
  //   }catch(IOException exc) {}
      VerbItem vi;
      String line, str,s1,s2,s3;
      int hz;
      StringTokenizer tokens;
      while (! buf.EOF()){
         str = buf.readLine();
         tokens = new StringTokenizer(str," \t");
         s1 = tokens.nextToken();
         // System.out.println("gelezen ww : " + s1);
         s2 = tokens.nextToken();
         s3 = tokens.nextToken();
         hz = Integer.parseInt(tokens.nextToken());  // hz == hebben|zijn == 0|1
         vi = new VerbItem(s1,s2,s3,hz);
         irregVerbs.addElement(vi);  
      }
      buf.close();
      ready = true;
      //System.out.println("irregular verbs read from file");
}

private static boolean isKlinker(char c){
	return (c=='o'||c=='a'||c=='i'||c=='u'||c=='e');
}

/**
 *  if stam ends with medeklinker in "tkofschip" then
 *  participum ends with "t" and vt ends with "te"
 *  else it ends with "d" ( "de" )
 *
 *  Beware: verbazen -> verbaasde  (omdat 'z' niet in tkofschip)
 *          durven   -> gedurfd    (omdat 'v' niet in tkofschip) 
 */
private static boolean IntKofschip(char c){
	return (c=='t'||c=='k'||c=='f'||c=='s'||c=='c'||c=='h'||c=='p');
} 

private static String conjHebben(int pers, int num, int time){
        String result = "";
        if (time == 1){
	switch (pers){
		case 1: if (num==1) result = "heb";
                        else result = "hebben";       
                break;
                case 2: if (num==1) result = "hebt";
                        else result = "hebben";
                break;
                case 3: if (num==1) result = "heeft";
                        else result = "hebben";
                break;
        }
        }
        else if (time == 2){
		if (num==1) result = "had";
        	else result = "hadden";
	}
 	return result;
}

private static String conjZijn(int pers, int num, int time){
	String result = "";
        if (time == 1){
	switch (pers){
		case 1: if (num==1) result = "ben";
                        else result = "zijn";       
                break;
                case 2: if (num==1) result = "bent";
                        else result = "zijn";
                break;
                case 3: if (num==1) result = "is";
                        else result = "zijn";
                break;
        }
        }
        else if (time == 2){
		if (num==1) result = "was";
        	else result = "waren";
	}
 	return result;
}

/*
   return conjugate form of verb 
   argument sob is supposed to contain values for num and per attributes 
public String conjugateDutch(SOB sob, int time, String verb){
       String persoon = "";
       if (sob.hasA("per"))
                persoon = ((Atomic)sob.get("per")).toString();
       String number = "";
       if (sob.hasA("num"))
                number  = ((Atomic)sob.get("num")).toString();
       int pers, num;
       if (persoon.equals("perP1")) pers = 1;
       else if (persoon.equals("perP2")) pers = 2;
       else pers = 3;
       if (number.equals("P")) num = 2;
       else num = 1;
       return conjugateDutch(pers,num,time,verb);
}
*/

/*
  outputs verb conjugate for Dutch verbs
  arguments : pers = 1 | 2 | 3
             number= 1 | 2     (singular|plural)
             time  = 1 | 2 | 3  tt|vt|ovt (heeft gegeten...)
             verb infinitive
*/
public static String conjugateDutch(int pers, 
      		                int num,
                	        int time,
                        	String verb){
  
  if (verb.equals("hebben")) 
         return conjHebben(pers,num,time);
  if (verb.equals("zijn"))
	 return conjZijn(pers,num,time);
  if (time == 1) // tt 
        return conjugateDutchTT(pers,num,verb);
  if (time == 2) // vt
        return conjugateDutchVT(pers,num,verb);
  return conjugateDutchOVT(pers, num, verb);
}

private static String conjugateDutchOVT(int pers, int num, String verb){
	String result = conjHebben(pers,num,1) + " ";
        result = result + detPart(verb);
        return result;
}

public static String detPart(String verb){
    String result = "";
    String pre, post;
    int len = verb.length(); 
    VerbItem vi =  getIrregVerbItem(verb);
    if (vi != null) 
        return vi.part;
    if (len>5 && (verb.startsWith("ge") ||
                  verb.startsWith("be"))){
              vi = getIrregVerbItem(verb.substring(2,len));
              if (vi !=null){
                   result = verb.substring(0,2);
                   post = vi.part;
                   if (post.startsWith("ge"))
                        post = post.substring(2,post.length());
	           result = result + post;
                   return result;
              }
    }
    if (len>5 && (verb.startsWith("ver") ||
                  verb.startsWith("her") ||
                  verb.startsWith("ont"))){
              vi = getIrregVerbItem(verb.substring(3,len));
              if (vi !=null){
                   result = verb.substring(0,3);
                   post = vi.part;
                   if (post.startsWith("ge"))
                        post = post.substring(2,post.length());
	           result = result + post;
                   return result;
              }
    }
    String basis = detStam(verb);
    len = basis.length();
    if ( (len>4) && ( basis.startsWith("ge") ||
                      basis.startsWith("ver") ||
                      basis.startsWith("her") ||
                      basis.startsWith("ont") ||
                      basis.startsWith("be")  ) )
            pre = ""; 
    else
            pre = "ge"; 
    char c = basis.charAt(len-1);
    if (IntKofschip(c)){
            if (c !='f' && c!='s')
                    result = pre+basis+"t";
            else if ((c=='f') && verb.charAt(verb.length()-3) == 'v')
                    result = pre+basis+"d";
            else if ((c=='s') && verb.charAt(verb.length()-3) == 'z')
                    result = pre+basis+"d";
	    else
                    result = pre+basis+"t";
    }
    else
            result = pre+basis+"d";  
    return result;
}

// tiempo prensentalia
// num = 2 : zien/vegen/eten/blijven
// num = 1 : pers 1: stam 
//           pers 2|3 : stam+t
 
private static String conjugateDutchTT(int pers, int num, String verb){
  	if (num==2)
		 return verb;
        else{
		String stam = detStam(verb);
        	if ( pers == 1 )
			return stam;
                else{
               		if (stam.endsWith("t"))
				 return stam; 
               		else{
                            char c = stam.charAt(stam.length()-1);
                            if ( c == 'a')
                                 return stam+c+"t";
                            else
				 return stam+"t";
                        }
                }
        }
}

// tiempo historica
// de vorm is voor alle personen gelijk
// num = 1 :  zag/veegde/at/bleef
// num = 2 : zagen/veegden/aten/bleven

private static String conjugateDutchVT(int pers, int num, String verb){  
       VerbItem vi = getIrregVerbItem(verb);
       String basis;
       if (vi !=null ) {
       	    basis = vi.past;
            if (num==1) return basis;
            if (num==2) return basis+"en";
       }
       else {
            basis = detStam(verb);
            int len = basis.length();
            char c = basis.charAt(len-1);
            if (IntKofschip(c)) basis = basis+"te";
            else basis = basis+"de";
       	    if (num == 1) return basis;
            if (num == 2) return basis+"n";
       }
       return verb;
}

public static String detStam(String verb){
        String stam;
        int len = verb.length();
        if (verb.endsWith("zien")){
             stam = verb.substring(0,len-1);
             return stam;
        }
        else if (verb.endsWith("doen")){
             stam = verb.substring(0,len-1);
             return stam;
        }
        else if (verb.endsWith("gaan")){
             stam = verb.substring(0,len-2);
             return stam;
        }
        else{
        	char last = verb.charAt(len-1);     // last == 'n'
        	char last1 = verb.charAt(len-2);
        	if (last1 != 'e') {
			stam = verb.substring(0,len-1);
                	return stam;
                }
        	else{ // verb ends with "en"
			char last2 = verb.charAt(len-3);
                	char last3 = verb.charAt(len-4);    // verb heeft minstens 4 karakters 
                        if (len==4){         // azen; eten; 
                         	stam = verb.substring(0,len-3)+last3+last2;
                        }
                        else if (last2=='i'){
                       		if ( !isKlinker(last3) )
                         		stam = verb.substring(0,len-1); // skie-n; neurie-n;
                       		else 
                         		stam = verb.substring(0,len-2); // roei-en; aai-en
		       		return stam;
                	}
                	else{
                  		if ( !isKlinker(last2) && (last2==last3) ){  // innen,ballen
					stam = verb.substring(0,len-3);
                        		return stam;
                  		}
                  		else if ( !isKlinker(last3) ) {    // durven 
					stam = verb.substring(0,len-2);
                                }
                                else{
					char last4 = verb.charAt(len-5);
                        		String oeij = ""+last4+last3;
                        		if (    oeij.equals("ij") ||
                            			oeij.equals("oe") ||
                            			oeij.equals("eu") ||
                            			oeij.equals("ui") ||
                            			oeij.equals("ie") ){
                            			stam = verb.substring(0,len-2);
                        		}
                       			else
                               			stam = verb.substring(0,len-3)+last3+last2;
                  		}
                        }
                  	if (stam.charAt(stam.length()-1) =='v')
                        	stam = stam.substring(0,stam.length()-1)+"f";
                  	else if (stam.charAt(stam.length()-1) =='z')
                        	stam = stam.substring(0,stam.length()-1)+"s";
          		return stam;
		}
	}
} // end method detStam

private static VerbItem getIrregVerbItem(String verb){
    VerbItem vi;
    for (Enumeration e = irregVerbs.elements(); e.hasMoreElements();){
	vi = (VerbItem) e.nextElement();
        if (vi.inf.equals(verb)) 
           return vi;
    } 
    return null;
}

/*
  FOR TESTING ONLY
*/
public static void main(String[] arg){

Conjugator conj = new Conjugator();
conj.readIrreg();
if (!conj.ready()) return;

System.out.println("stam van doen : " + conj.detStam("doen"));// doe

System.out.println("stam van eten : " + conj.detStam("eten"));// eet
System.out.println("stam van blijven : " + conj.detStam("blijven"));// blijf
System.out.println("stam van gaan : " + conj.detStam("gaan"));// ga
System.out.println("stam van koppen : " + conj.detStam("koppen"));// kop
System.out.println("stam van kopen : " + conj.detStam("kopen"));// koop
System.out.println("stam van moeten : " + conj.detStam("moeten"));// moet
System.out.println("stam van zien : " + conj.detStam("zien"));// zie
System.out.println("stam van zuigen : " + conj.detStam("zuigen"));// zuig
System.out.println("stam van beven : " + conj.detStam("beven"));// beef
System.out.println("stam van verbazen : " + conj.detStam("verbazen"));// verbaas
System.out.println("stam van durven : " + conj.detStam("durven"));// durf

System.out.println("part van azen : " + conj.detPart("azen"));
System.out.println("part van eten : " + conj.detPart("eten"));
System.out.println("part van blijven : " + conj.detPart("blijven"));
System.out.println("part van gaan : " + conj.detPart("gaan"));
System.out.println("part van koppen : " + conj.detPart("koppen"));
System.out.println("part van kopen : " + conj.detPart("kopen"));
System.out.println("part van moeten : " + conj.detPart("moeten"));
System.out.println("part van zien : " + conj.detPart("zien"));
System.out.println("part van zuigen : " + conj.detPart("zuigen"));
System.out.println("part van veren : " + conj.detPart("veren"));
System.out.println("part van vereren : " + conj.detPart("vereren"));
System.out.println("part van herzien : " + conj.detPart("herzien"));
System.out.println("part van herverkopen: " + conj.detPart("herverkopen"));
System.out.println("part van herhalen: " + conj.detPart("herhalen"));
System.out.println("part van verkopen : " + conj.detPart("verkopen"));
System.out.println("part van verliezen : " + conj.detPart("verliezen"));
System.out.println("part van beven : " + conj.detPart("beven"));
System.out.println("part van bevelen : " + conj.detPart("bevelen"));
System.out.println("part van bevliegen : " + conj.detPart("bevliegen"));
System.out.println("part van verbazen : " + conj.detPart("verbazen"));
System.out.println("part van verheffen : " + conj.detPart("verheffen"));
System.out.println("part van durven : " + conj.detPart("durven"));

System.out.println("hij " + conj.conjugateDutch(3,1,1,"eten"));// hij eet
System.out.println("ik " + conj.conjugateDutch(1,1,2,"eten"));// ik at
System.out.println("jij " + conj.conjugateDutch(2,1,3,"eten")); // jij hebt gegeten
System.out.println("ik " + conj.conjugateDutch(1,1,2,"blijven"));// ik bleef
System.out.println("ik " + conj.conjugateDutch(1,1,1,"blijven"));// ik blijf
System.out.println("ik " + conj.conjugateDutch(1,1,2,"gaan"));// ik ging
System.out.println("ik " + conj.conjugateDutch(1,1,2,"ballen"));// ik balde
System.out.println("ik " + conj.conjugateDutch(1,1,3,"ballen"));// ik heb gebald
}

} // end class Conjugator

class VerbItem{
public String inf, past, part;
public int heb;
  public VerbItem(String str1, String str2, String str3, int h){
           inf = str1;
           past = str2;
           part = str3;
           heb = h;
  }

} // end class VerbItem;

