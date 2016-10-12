package natlang.rdg.libraries;

import natlang.rdg.model.*;

/**	This class is a library containing all rules concerning the syntactic category 
 *	INF
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class InfLib extends ConstituentLib implements LibraryConstants
{
	/**	Constructs the SmainLib by creating the necessary rules 
	 */
	public InfLib()
	{
		String[] s0 = {HD};
		rules.add(new Rule(INF, s0));
		String[] s1 = {OBJ1, HD};
		rules.add(new Rule(INF, s1));
		String[] s2 = {OBJ1, SVP, HD};
		rules.add(new Rule(INF, s2));
		String[] s3 = {OBJ1, VC, HD};		
		rules.add(new Rule(INF, s3));
		String[] s4 = {OBJ1, VC};
		rules.add(new Rule(INF, s4));
		
		String[] s5 = {SU, OBJ1, HD};
		rules.add(new Rule(INF, s5));
		String[] s6 = {SU, OBJ1, SVP, HD};
		rules.add(new Rule(INF, s6));
		String[] s7 = {SU, OBJ1, VC, HD};		
		rules.add(new Rule(INF, s7));
		String[] s8 = {SU, OBJ1, VC};
		rules.add(new Rule(INF, s8));
		String[] s9 = {SU, HD};
		rules.add(new Rule(INF, s9));
		String[] s10 = {SU, VC, HD};
		rules.add(new Rule(INF, s10));
		String[] s11 = {SU, LD, HD};
		rules.add(new Rule(INF, s11));
		String[] s13 = {SU, MOD, LD, HD};
		rules.add(new Rule(INF, s13));
		
		String[] s12 = {MOD, HD};
		rules.add(new Rule(INF, s12));
		String[] s14 = {SU, MOD, HD};
		rules.add(new Rule(INF, s14));
	}
}