package natlang.rdg.libraries;

import natlang.rdg.model.*;

public class SrelLib extends ConstituentLib implements LibraryConstants
{
	public SrelLib()
	{
		String[] s1 = {SU, HD};
		rules.add(new Rule(SREL, s1));
		String[] s2 = {SU, OBJ1, HD};
		rules.add(new Rule(SREL, s2));
		String[] s3 = {SU, OBJ1, SVP, HD};
		rules.add(new Rule(SREL, s3));
		String[] s4 = {SU, OBJ2, OBJ1, HD};
		rules.add(new Rule(SREL, s4));
		String[] s14 = {SU, OBJ2, MOD, OBJ1, HD};	
		rules.add(new Rule(SREL, s14));
		String[] s27 = {SU, OBJ1, MOD, SVP, HD, VC};	//toegevoegd	
		rules.add(new Rule(SREL, s27));

		String[] s29 = {SU, OBJ1, PREDC, HD};
		rules.add(new Rule(SREL, s29));
				
		String[] s30 = {SU, OBJ1, PREDC, SVP, HD};
		rules.add(new Rule(SREL, s30));
		
		String[] s23 = {SU, SVP, HD};			//toegevoegd
		rules.add(new Rule(SREL, s23));
		String[] s25 = {HD};
		rules.add(new Rule(SREL, s25));
		String[] s28 = {OBJ1, HD};
		rules.add(new Rule(SREL, s28));
		
		String[] s5 = {SU, PREDC, HD};
		rules.add(new Rule(SREL, s5));
		String[] s6 = {SU, PREDC, PC, HD};
		rules.add(new Rule(SREL, s6));
		String[] s7 = {SU, PREDC, HD, PC};
		rules.add(new Rule(SREL, s7));
			
		String[] s8 = {SU, MOD, HD};
		rules.add(new Rule(SREL, s8));
		String[] s13 = {SU, OBJ1, MOD, HD};
		rules.add(new Rule(SREL, s13));
		String[] s19 = {SU, OBJ1, MOD, MOD, HD};
		rules.add(new Rule(SREL, s19));
		
		String[] s15 = {SU, VC, HD};
		rules.add(new Rule(SREL, s15));
		String[] s9 = {SU, OBJ1, VC, HD};
		rules.add(new Rule(SREL, s9));		
		String[] s10 = {SU, OBJ1, HD, VC};
		rules.add(new Rule(SREL, s10));
		String[] s11 = {SU, OBJ1, SVP, HD, VC};
		rules.add(new Rule(SREL, s11));
		String[] s12 = {SU, OBJ1, SVP, VC, HD};
		rules.add(new Rule(SREL, s12));
		
		String[] s16 = {SU, LD, HD};
		rules.add(new Rule(SREL, s16));
		String[] s17 = {SU, MOD, LD, HD};
		rules.add(new Rule(SREL, s17));
		String[] s24 = {SU, LD, HD, VC};			//toegevoegd
		rules.add(new Rule(SREL, s24));
		
		String[] s26 = {MOD, SU, SVP, HD};
		rules.add(new Rule(SMAIN, s26));					//toegevoegd!
				
		String[] s18 = {SU, MOD, VC, HD};
		rules.add(new Rule(SREL, s18));
		
		String[] s20 = {SU, OBJ1, LD, SVP, HD};
		rules.add(new Rule(SREL, s20));
		String[] s21 = {SU, MOD, PREDC, HD};
		rules.add(new Rule(SREL, s21));
		String[] s22 = {SU, MOD, OBJ1, SVP, HD};
		rules.add(new Rule(SREL, s22));
	}
}
