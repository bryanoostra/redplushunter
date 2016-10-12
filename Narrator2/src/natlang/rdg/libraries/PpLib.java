package natlang.rdg.libraries;

import natlang.rdg.model.*;

/**	This class is a library containing all rules concerning the syntactic category 
 *	PP
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class PpLib extends ConstituentLib implements LibraryConstants
{
	/**	Constructs the SmainLib by creating the necessary rules 
	 */
	public PpLib()
	{
		String[] s1 = {HD, OBJ1};
		rules.add(new Rule(PP, s1));
		
		String[] s2 = {HD, PC};
		rules.add(new Rule(PP, s2));
	}
}