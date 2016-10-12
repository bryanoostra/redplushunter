package narrator.shared;

import natlang.rdg.model.RSVertex;
import natlang.rdg.model.RhetRelation;

public class Printer {

	/**
	 * Prints the contents of a Dependency tree. Used for
	 * debugging purposes
	 * @param node The root node of the Dependency tree you
	 * want to print
	 * @param tabs How many tabs should precede each line
	 * @return String representation of the Dependencey tree.
	 * @author Nanda Slabbers
	 */
	public static String printGoed(RSVertex node, int tabs){
		StringBuilder sb = new StringBuilder();
		String stringtabs = "";
		for(int i = 0; i < tabs; i++)
			stringtabs += "\t";
		
		if (node.getType().equals("rhetrel"))
		{			
			sb.append(stringtabs + "rel: " + ((RhetRelation) node).getLabel()).append('\n');
			
			RSVertex nuc = ((RhetRelation) node).getNucleus();
			RSVertex sat = ((RhetRelation) node).getSatellite();
			
			
			
			if (sat != null)// && sat.getType().equals("rhetrel"))
			{
				
				sb.append(stringtabs + "sat:\n");
				sb.append(printGoed(sat, tabs + 1)).append('\n');
			}
			
			if (nuc != null)// && nuc.getType().equals("rhetrel"))
			{
				sb.append(stringtabs + "nuc:\n");
				sb.append(printGoed(nuc, tabs + 1)).append('\n');
			}			
		}
		else if (node.getType().equals("deptree"))
		{
			sb.append(stringtabs + "node: " + node).append('\n');
		}
		else {
			sb.append(stringtabs + "???: " + node).append('\n');
		}
		return sb.toString();
	}
}
