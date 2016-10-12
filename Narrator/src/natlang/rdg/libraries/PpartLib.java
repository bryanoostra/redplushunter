package natlang.rdg.libraries;

import natlang.rdg.model.*;

/**	This class is a library containing all rules concerning the syntactic category 
 *	PPART
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class PpartLib extends ConstituentLib implements LibraryConstants
{
	/**	Constructs the PpartLib by creating the necessary rules 
	 */
	public PpartLib()
	{
		String[] s1 = {SU, HD};
		rules.add(new Rule(PPART, s1));
		String[] s2 = {SU, OBJ1, HD};
		rules.add(new Rule(PPART, s2));
		String[] s3 = {SU, MOD, HD};
		rules.add(new Rule(PPART, s3));
		String[] s4 = {SU, MOD, OBJ1, HD};
		rules.add(new Rule(PPART, s4));
		String[] s5 = {SU, MOD, PREDC, HD};
		rules.add(new Rule(PPART, s5));
	}
}