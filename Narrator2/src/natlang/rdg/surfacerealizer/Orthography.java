package natlang.rdg.surfacerealizer;

import natlang.rdg.libraries.*;
import natlang.rdg.model.*;

/** Performs Orthography. At the moment only add a full stop at the end and capitalizes
 *	the first letter. Later it should also add comma's (especially important because
 *	we focus on aggregation..)
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class Orthography implements LibraryConstants
{
	/** adds a comma, if needed */
	public StringBuffer doOrthography(StringBuffer str, RSDepTreeNode node)
	{
		System.out.println(node.getData());
		String rootData = node.getData().get("root");
		if (rootData==null || rootData.equals(""))
			return str;
		
		String pos = node.getData().get(RSTreeNodeData.POS);
		String root = node.getData().get(RSTreeNodeData.ROOT);
			
		if (((pos.equals(VG) && !root.equals("en")) || pos.equals(COMP) || pos.equals(BETR))
				&& ((str.length() == 0) || (str.charAt(str.length()-1) != ',')))
			str.append(", ");
		else if (!node.getData().get("pos").equals("cm"))
				str.append(" ");
		str.append(node.getData().get(RSTreeNodeData.WORD));
		return str;
	}
	
	/** Finishes orthograph by capitalizing first letter and adding a full stop */
	public String finish(StringBuffer str) throws Exception
	{
		if (str.length() == 0)
			return ""; //throw new Exception("surface form is empty");
		
		while ((str.charAt(0) == ' ') || (str.charAt(0) == ','))
			str.deleteCharAt(0);
		while ((str.charAt(str.length() - 1) == ' ') || (str.charAt(str.length() - 1) == ','))
			str.deleteCharAt(str.length() - 1);
		
		if (str.charAt(str.length() - 1) != '!')
			str.append(". ");
		char first = str.charAt(0);					//full stop and space at end
		first = Character.toUpperCase(first);		//First letter capital
		str.setCharAt(0, first);
		return str.toString();
	}
}