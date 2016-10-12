package natlang.rdg.libraries;

import natlang.rdg.model.*;

/**
 * Library class for relative clauses.
 * All relative clauses start with the relative pronoun, no matter whether this is the
 * subject, object or even something else
 * 
 * @author Nanda Slabbers 
 */
public class RelLib extends ConstituentLib implements LibraryConstants
{
	public RelLib()
	{
		String[] s1 = {RHD, HD};
		rules.add(new Rule(REL, s1));
		
		String[] s2 = {RHD, SU, HD};
		rules.add(new Rule(REL, s2));
		
		String[] s3 = {RHD, OBJ1, HD};
		rules.add(new Rule(REL, s3));
			
		String[] s4 = {RHD, SVP, HD};
		rules.add(new Rule(REL, s4));
		
		String[] s5 = {RHD, OBJ1, SVP, HD};		
		rules.add(new Rule(REL, s5));
		
		String[] s6 = {RHD, SU, SVP, HD};
		rules.add(new Rule(REL, s6));
		
		String[] s7 = {RHD, OBJ2, OBJ1, HD};
		rules.add(new Rule(REL, s7));
		
		String[] s8 = {RHD, PREDC, HD};
		rules.add(new Rule(REL, s8));
		
		String[] s9 = {RHD, SU, MOD, HD};
		rules.add(new Rule(REL, s9));
		
		String[] s10 = {RHD, LD, HD};
		rules.add(new Rule(REL, s10));
		
		String[] s11 = {RHD, PREDC, HD, PC};
		rules.add(new Rule(REL, s11));
		
		String[] s12 = {RHD, SU, PREDC, HD};
		rules.add(new Rule(REL, s12));
		
		String[] s13 = {RHD, OBJ1, MOD, HD};
		rules.add(new Rule(REL, s13));
		
		/*

		String[] s14 = {SU, OBJ2, MOD, OBJ1, HD};	
		rules.add(new Rule(REL, s14));
		String[] s27 = {SU, OBJ1, MOD, SVP, HD, VC};	//toegevoegd	
		rules.add(new Rule(REL, s27));

		String[] s29 = {SU, OBJ1, PREDC, HD};
		rules.add(new Rule(REL, s29));
				
		String[] s30 = {SU, OBJ1, PREDC, SVP, HD};
		rules.add(new Rule(REL, s30));
		
		
		String[] s25 = {HD};
		rules.add(new Rule(REL, s25));
		String[] s28 = {OBJ1, HD};
		rules.add(new Rule(REL, s28));
		
		String[] s5 = {SU, PREDC, HD};
		rules.add(new Rule(REL, s5));
		String[] s6 = {SU, PREDC, PC, HD};
		rules.add(new Rule(REL, s6));
		String[] s7 = {SU, PREDC, HD, PC};
		rules.add(new Rule(REL, s7));
			
		String[] s8 = {SU, MOD, HD};
		rules.add(new Rule(REL, s8));
		String[] s13 = {SU, OBJ1, MOD, HD};
		rules.add(new Rule(REL, s13));
		String[] s19 = {SU, OBJ1, MOD, MOD, HD};
		rules.add(new Rule(REL, s19));
		
		String[] s15 = {SU, VC, HD};
		rules.add(new Rule(REL, s15));
		String[] s9 = {SU, OBJ1, VC, HD};
		rules.add(new Rule(REL, s9));		
		String[] s10 = {SU, OBJ1, HD, VC};
		rules.add(new Rule(REL, s10));
		String[] s11 = {SU, OBJ1, SVP, HD, VC};
		rules.add(new Rule(REL, s11));
		String[] s12 = {SU, OBJ1, SVP, VC, HD};
		rules.add(new Rule(REL, s12));
		
		String[] s16 = {SU, LD, HD};
		rules.add(new Rule(REL, s16));
		String[] s17 = {SU, MOD, LD, HD};
		rules.add(new Rule(REL, s17));
		String[] s24 = {SU, LD, HD, VC};			//toegevoegd
		rules.add(new Rule(REL, s24));
		
		String[] s26 = {MOD, SU, SVP, HD};
		rules.add(new Rule(SMAIN, s26));					//toegevoegd!
				
		String[] s18 = {SU, MOD, VC, HD};
		rules.add(new Rule(REL, s18));
		
		String[] s20 = {SU, OBJ1, LD, SVP, HD};
		rules.add(new Rule(REL, s20));
		String[] s21 = {SU, MOD, PREDC, HD};
		rules.add(new Rule(REL, s21));
		String[] s22 = {SU, MOD, OBJ1, SVP, HD};
		rules.add(new Rule(REL, s22));*/
	}
}
