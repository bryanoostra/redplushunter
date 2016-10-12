package natlang.rdg.libraries;

import natlang.rdg.model.*;

/**	This class is a library containing all rules concerning the syntactic category 
 *	AP
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class ApLib extends ConstituentLib implements LibraryConstants
{
	/**	Constructs the SmainLib by creating the necessary rules 
	 */
	public ApLib()
	{
		String[] s1 = {HD, OBJ1};
		rules.add(new Rule(AP, s1));
		
		String[] s2 = {HD, PC};
		rules.add(new Rule(AP, s2));
		
		String[] s3 = {MOD, HD};
		rules.add(new Rule(AP, s3));

		String[] s4 = {HD, MODB};
		rules.add(new Rule(AP, s4));
	}
}
