package natlang.rdg.libraries;

import natlang.rdg.model.*;

/**	This class is a library containing all rules concerning the syntactic category 
 *	CP
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class CpLib extends ConstituentLib implements LibraryConstants
{
	/**	Constructs the CpLib by creating the necessary rules 
	 */
	public CpLib()
	{
		String[] s1 = {CMP, BODY};
		rules.add(new Rule(CP, s1));
	}
}