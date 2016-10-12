package natlang.deptree.view;
import natlang.deptree.model.*;
import natlang.structure.Console;
import java.util.*;

/**
 * DependencyViewer class for viewing a DependencyStructure
 * by means of a simple user-interface
 */
 
public class DependencyViewer{

private DependencyStructure struct;
private boolean exit;
private char choice;
private String label;
private String selection = "rudlTLPxX";

/**
 * @param struct the DependencyStructure viewed by this viewer
 */
public DependencyViewer(DependencyStructure struct){
	this.struct=struct;
}

/**
 * start the viewer
 */
public void start(){
	exit = false;
	while (!exit){
		showMenu();
		showCurrentNode();
		choice = getChoice();
		doAction(choice);
	}
}


private void showMenu(){
	System.out.println("\n\n--------------MENU---------");
	if (!struct.isTop()) System.out.println("| go to root\t\t-r");
	if (!struct.isTop()) System.out.println("| up\t\t\t-u");
	if (!struct.isLeaf()) System.out.println("| down\t\t\t-d");
	if (!struct.isLeaf()) System.out.println("| show labels\t\t-l");
	System.out.println("| show Tag\t\t-T");
	System.out.println("| show lexeme\t\t-L");
	System.out.println("| show PoS\t\t-P");	
	System.out.println("| exit\t\t\t-x");
	System.out.println("--------------MENU---------");
}

private void showCurrentNode(){
	System.out.println("current node :"+ struct.nodeId());
}

private char getChoice(){
	String answer = Console.readString("choice>");
	if (answer.length()==0) return getChoice();
	char fc = answer.charAt(0);
	if (selection.indexOf(fc)==-1){
		System.out.println("no option");
		return getChoice();
	}
	return fc;
}

private void doAction(char fc){
	switch (fc) {
		case 'r': struct=struct.getTopContext();
			break;
		case 'u': struct=struct.getContext();
			break;
		case 'd': if (askLabel())
			  struct=struct.get(label);
			break;
		case 'l': showLabels();
			break;
		case 'T': showTag();
			break;
		case 'L': showLex();
			break;
		case 'P': showPos();
			break;
		case 'X': exit=true;
			break;
		case 'x': exit=true;
			break;
		default: ;
	}
}
	
private boolean askLabel(){
	showLabels();
	label = Console.readString("type label name>");
	return true;
}

private void showTag(){
	System.out.println("tag: "+struct.getFeatureValue(DependencyStructure.TAG));
}

private void showLex(){
	System.out.println("sentence: "+struct.getFeatureValue(DependencyStructure.LEX));
}

private void showLabels(){
	Iterator iter = struct.labels();
	if (iter==null) 
		System.out.println("this node has NO LABELS");
	else
		while (iter.hasNext())
			System.out.println("label: "+(String)iter.next());
}

private void showPos(){
	System.out.println("pos: "+struct.getFeatureValue(DependencyStructure.POS));	
}

} // end class DependencyViewer