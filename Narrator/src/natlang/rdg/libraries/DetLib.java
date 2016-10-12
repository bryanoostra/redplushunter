package natlang.rdg.libraries;

/**	This class inflects determiners
 */
public class DetLib extends MorphLib implements LibraryConstants
{
	/** Inflects a determiner
	 *	@throws Exception when the node is not a determiner
	 */
	public String getInflectedForm(String root, String morph) throws Exception
	{
		if ((morph == null) || (root == null))
			throw new Exception("missing morph or root tag in DetLib");
			
		if (morph.indexOf(SINGULAR) >= 0)
			return root;
		
		if (root.equals("de") || root.equals("het"))
			return new String("de");
		
		if (root.equals("een"))
			return new String("");
		
		return root;
	}
}