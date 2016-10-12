package natlang.rdg.libraries;

import natlang.rdg.model.*;

/**	This class is a library containing all rules concerning the syntactic category 
 *	TI
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class TiLib extends ConstituentLib implements LibraryConstants
{
	/**	Constructs the SmainLib by creating the necessary rules 
	 */
	public TiLib()
	{
		
		String[] s1 = {OBJ1, HD};
		rules.add(new Rule(TI, s1));
		String[] s2 = {OBJ1, VC, HD};		
		rules.add(new Rule(TI, s2));
		String[] s3 = {OBJ1, SVP, HD};
		rules.add(new Rule(TI, s3));
		String[] s4 = {OBJ1, OBJ2, HD};
		rules.add(new Rule(TI, s4));
		
		String[] s5 = {OBJ1, MOD, HD};
		rules.add(new Rule(TI, s5));
		String[] s6 = {OBJ1, HD, MOD};
		rules.add(new Rule(TI, s6));
		String[] s7 = {MOD, OBJ1, HD};
		rules.add(new Rule(TI, s7));
		
		
		String[] s8 = {SU, OBJ1, HD};
		rules.add(new Rule(TI, s8));
		String[] s9 = {SU, OBJ1, VC, HD};		
		rules.add(new Rule(TI, s9));
		String[] s10 = {SU, OBJ1, SVP, HD};
		rules.add(new Rule(TI, s10));
		String[] s11 = {SU, OBJ1, OBJ2, HD};
		rules.add(new Rule(TI, s11));
		
		String[] s12 = {SU, OBJ1, MOD, HD};
		rules.add(new Rule(TI, s12));
		String[] s13 = {SU, OBJ1, HD, MOD};
		rules.add(new Rule(TI, s13));
		String[] s14 = {SU, MOD, OBJ1, HD};
		rules.add(new Rule(TI, s14));
		
		String[] s15 = {SU, HD};
		rules.add(new Rule(TI, s15));
		String[] s16 = {SU, SVP, HD};
		rules.add(new Rule(TI, s16));
	}
}

		