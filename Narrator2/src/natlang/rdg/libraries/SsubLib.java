package natlang.rdg.libraries;

import natlang.rdg.model.*;

/**	This class is a library containing all rules concerning the syntactic category 
 *	SSUB
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class SsubLib extends ConstituentLib implements LibraryConstants
{
	/**	Constructs the SsubLib by creating the necessary rules 
	 */
	public SsubLib()
	{
		String[] tmp = {SU, MOD, MOD, OBJ1, SVP, VC, HD};
		rules.add(new Rule(SSUB, tmp));
		String[] fjd = {};
		rules.add(new Rule(SSUB, fjd));
		String[] s0 = {SUP, SU, LD, HD};
		rules.add(new Rule(SSUB, s0));
		String[] s1 = {SU, HD};
		rules.add(new Rule(SSUB, s1));
		String[] s2 = {SU, OBJ1, HD};
		rules.add(new Rule(SSUB, s2));
		String[] s3 = {SU, OBJ1, SVP, HD};
		rules.add(new Rule(SSUB, s3));
		String[] s4 = {SU, OBJ2, OBJ1, HD};
		rules.add(new Rule(SSUB, s4));
		String[] s14 = {SU, OBJ2, MOD, OBJ1, HD};	
		rules.add(new Rule(SSUB, s14));
		String[] s27 = {SU, OBJ1, MOD, SVP, HD, VC};	
		rules.add(new Rule(SSUB, s27));

		String[] s29 = {SU, OBJ1, PREDC, HD};
		rules.add(new Rule(SSUB, s29));
				
		String[] s30 = {SU, OBJ1, PREDC, SVP, HD};
		rules.add(new Rule(SSUB, s30));
		
		String[] s23 = {SU, SVP, HD};	
		rules.add(new Rule(SSUB, s23));
		String[] s25 = {HD};
		rules.add(new Rule(SSUB, s25));
		String[] s28 = {OBJ1, HD};
		rules.add(new Rule(SSUB, s28));
		
		String[] s5 = {SU, PREDC, HD};
		rules.add(new Rule(SSUB, s5));
		String[] s6 = {SU, PREDC, PC, HD};
		rules.add(new Rule(SSUB, s6));
		//String[] s7 = {SU, PREDC, PC, HD};
		//rules.add(new Rule(SSUB, s7));
			
		String[] s8 = {SU, MOD, HD};
		rules.add(new Rule(SSUB, s8));
		String[] s13 = {SU, OBJ1, MOD, HD};
		rules.add(new Rule(SSUB, s13));
		String[] s19 = {SU, OBJ1, MOD, MOD, HD};
		rules.add(new Rule(SSUB, s19));
		
		String[] s15 = {SU, HD, VC};
		rules.add(new Rule(SSUB, s15));
		String[] s9 = {SU, OBJ1, HD, VC};
		rules.add(new Rule(SSUB, s9));		
		String[] s10 = {SU, OBJ1, HD, VC};
		rules.add(new Rule(SSUB, s10));
		String[] s11 = {SU, OBJ1, SVP, HD, VC};
		rules.add(new Rule(SSUB, s11));
		String[] s12 = {SU, OBJ1, SVP, HD, VC};
		rules.add(new Rule(SSUB, s12));
		
		String[] s16 = {SU, LD, HD};
		rules.add(new Rule(SSUB, s16));
		String[] s17 = {SU, MOD, LD, HD};
		rules.add(new Rule(SSUB, s17));
		String[] s24 = {SU, LD, HD, VC};
		rules.add(new Rule(SSUB, s24));
		
		String[] s26 = {MOD, SU, SVP, HD};
		rules.add(new Rule(SMAIN, s26));
				
		String[] s18 = {SU, MOD, HD, VC};
		rules.add(new Rule(SSUB, s18));
		
		String[] s20 = {SU, OBJ1, LD, SVP, HD};
		rules.add(new Rule(SSUB, s20));
		String[] s21 = {SU, MOD, PREDC, HD};
		rules.add(new Rule(SSUB, s21));
		String[] s22 = {SU, MOD, OBJ1, SVP, HD};
		rules.add(new Rule(SSUB, s22));
		
		String[] s31 = {SU, PREDC, HD, MODB};
		rules.add(new Rule(SSUB, s31));
		
		String[] s32 = {SU, SVP, HD, MODB};
		rules.add(new Rule(SSUB, s32));
		
		String[] s33 = {SU, MOD, MOD, HD};
		rules.add(new Rule(SSUB, s33));
		
		String[] s34 = {SU, MOD, LD, HD, VC};
		rules.add(new Rule(SSUB, s34));
		
		String[] tmp2 = {SU, MOD, OBJ1, LD, HD};
		rules.add(new Rule(SSUB, tmp2));
				
		String[] s35 = {SU, LD, SVP, HD};
		rules.add(new Rule(SSUB, s35));
		
		String[] s36 = {SU, PREDC, VC, HD};
		rules.add(new Rule(SSUB, s36));
		
		String[] s37 = {SU, HD, MODB};
		rules.add(new Rule(SSUB, s37));
		
		String[] s38 = {SU, OBJ1, MOD, VC, HD};
		rules.add(new Rule(SSUB, s38));
		
		String[] s39 = {SU, OBJ1, LD, VC, HD};
		rules.add(new Rule(SSUB, s39));		

		/*String[] s40 = {MODA, SU, OBJ1, MOD, VC, HD};		// een moda in ssub? kon toch niet????
		rules.add(new Rule(SSUB, s40));		
		
		String[] s41 = {SU, OBJ1, LD, HD};		// een moda in ssub? kon toch niet????
		rules.add(new Rule(SSUB, s41));		
		
		String[] s42 = {SU, VC, HD, MODB};		// een moda in ssub? kon toch niet????
		rules.add(new Rule(SSUB, s42));	
		
		String[] s43 = {MODA, SU, MOD, VC, HD};		// een moda in ssub? kon toch niet????
		rules.add(new Rule(SSUB, s43));*/
		
		String[] s40 = {SU, LD, SVP, HD, VC};
		rules.add(new Rule(SSUB, s40));	
		
	}
}