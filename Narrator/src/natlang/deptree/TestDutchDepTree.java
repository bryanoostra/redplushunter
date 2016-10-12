package natlang.deptree;

import natlang.deptree.model.*;
import natlang.deptree.view.*;

public class TestDutchDepTree {


public static void main(String[] args){
	String input = "De engelsman scoorde drie keer in deze wedstrijd";
	DutchDependencyStructure ds =  DepTreeParserConnection.parseFromAlpino(input);
	DependencyViewer viewer = new DependencyViewer(ds);
	viewer.start();	
}

}