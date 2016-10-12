package natlang.deptree.model;
import java.util.*;

public class TreeNodeData{

String cat;
String tag;
/*
 * the yield list of a node is the concat of the yields of the childeren of the node
 */
List yield;

public TreeNodeData(){
	this("","");
}
	
public TreeNodeData(String cat, String tag){
	this.cat=cat;
	this.tag=tag;
}

public void setCat(String cat){
	this.cat = cat;
}

public void setTag(String tag){
	this.tag=tag;
}

public void setYield(List y){ yield=y;}

public String getCat(){ return cat;}
public String getTag(){ return tag;}
public List getYield(){ return yield;}

public String toString(){
	return "TreeNodeData: ("+cat+","+tag+")";
}

} // end class TreeNodeData