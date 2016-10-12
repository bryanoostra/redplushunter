package natlang.rdg.libraries;

import natlang.rdg.model.*;

/**	This class is a library containing all rules concerning the syntactic category 
 *	OTI
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class OtiLib extends ConstituentLib implements LibraryConstants
{
	/**	Constructs the SmainLib by creating the necessary rules 
	 */
	public OtiLib()
	{
		String[] s1 = {CMP, BODY};
		rules.add(new Rule(OTI, s1));
	}
}