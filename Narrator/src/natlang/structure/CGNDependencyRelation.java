package natlang.structure;
import java.util.*;
import java.io.*;

/**
 * CGNDependencyRelation
 * is a class for the dependency relations between nodes in a dependency tree
 *
 * the make method requires an input format like this:

%% word			tag	morph		edge	parent	secedge comment
#BOS 1 6 984736249 0
zoals			VG2	T802		CMP	503
uh			TSW	T001		--	0
al			BW	T901		MOD	500
gezegd			WW7	T320		VC	500
uh			TSW	T001		--	0
we			VNW1	T501f		SU	506
zijn			WW2	T302		HD	506
dan			BW	T901		MOD	506
toe			VZ2	T702		SVP	506
aan			VZ1	T701		HD	505
uh			TSW	T001		--	0
agendapunt		N1	T102		HD	501
negen			TW1	T407		APPOS	501
de			LID	T602		DET	502
mededelingen		N3	T107		HD	502
.			LET	T007		--	0
#500			SSUB	--		BODY	503
#501			NP	--		HD	504
#502			NP	--		APPOS	504
#503			CP	--		TAG	507
#504			NP	--		OBJ1	505
#505			PP	--		PC	506
#506			SMAIN	--		NUCL	507
#507			DU	--		--	0
#EOS 1

*/

public class CGNDependencyRelation {
	
String word;
String tag;
String morph;
String edge;
String parent;
String secedge="";
String comment="";

public void setWord(String word){this.word=word;}
public void setTag(String tag){this.tag=tag;}
public void setMorph(String morph){this.morph=morph;}
public void setEdge(String edge){this.edge=edge;}
public void setParent(String parent){this.parent=parent;}
public void setSecEdge(String secedge){this.secedge=secedge;}
public void setComment(String comment){this.comment=comment;}

private static  StringTokenizer tokens;
private static  String delims = "\t ";

public String toString(){
	String result="";
	result+=word+"\t";
	result+=tag+"\t";
	result+=morph+"\t";
	result+=edge+"\t";
	result+=parent+"\t";
	result+=secedge+"\t";
	result+=comment;
	return result;	
}

 /**
 * parse a String and return a new CGN_Partition
 * format for s-like: [U548c] VNW(onbep,grad,stan,vrij,zonder,comp) minder werken, meer slapen
 * @throws IOException if s is not of valid format
 */
public static CGNDependencyRelation parse(String input) throws IOException {
	String s=input.trim();
	if (s.equals("")) 
		throw new IOException("CGNDependencyRelation parse: empty rule: "+s);
	try{
		tokens = new StringTokenizer(s,delims,false);
		String token;
		CGNDependencyRelation result = new CGNDependencyRelation();
		token = tokens.nextToken();
		result.setWord(token);
		token = tokens.nextToken();
		result.setTag(token);
		token = tokens.nextToken();
		result.setMorph(token);
		token = tokens.nextToken();
		result.setEdge(token);
		token = tokens.nextToken();
		result.setParent(token);
		if (tokens.hasMoreTokens()){
			token = tokens.nextToken();
			result.setSecEdge(token);
		}
		if (tokens.hasMoreTokens()){
			token = tokens.nextToken();
			result.setComment(token);
		}	
		return result;
	}catch (Exception e){
		throw new IOException("CGNDependencyRelation.parse.error in: "+s);
	}
}

public static void main(String[] args){
//	String test="zoals			VG2	T802		CMP	503";
	String input;

	try{
		FileReader in = new FileReader(args[0]);
		BufferedReader b = new BufferedReader(in);
		input = new String();
		while((input = b.readLine()) != null)
		{			
			CGNDependencyRelation rel = CGNDependencyRelation.parse(input);
			System.out.println("relations="+rel);
		}
	}catch(IOException exc){
		System.out.println(exc.getMessage());
	}
}

} // end class CGNDependencyRelation