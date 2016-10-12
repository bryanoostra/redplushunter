package natlang.rdg.libraries;

import natlang.rdg.model.*;

/**	This class is a library containing all rules concerning the syntactic category 
 *	SMAIN
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class SmainLib extends ConstituentLib implements LibraryConstants
{
	/**	Constructs the SmainLib by creating the necessary rules
	 *	Whereas Alpino used MOD as label for locatives, we use LD (following CGN)
	 *	because otherwise we cannot create adequate rules
	 */
	public SmainLib()
	{
		// 2 consituents
		String[] s1 = {SU, HD};
		rules.add(new Rule(SMAIN, s1));
		String[] s1a = {MODA, HD, SU};
		rules.add(new Rule(SMAIN, s1a));
		String[] s1b = {SU, HD, MODB};
		rules.add(new Rule(SMAIN, s1b));

		// 3 constituents
		String[] s10 = {SU, HD, OBJ1};
		rules.add(new Rule(SMAIN, s10));
		String[] s10a = {MODA, HD, SU, OBJ1};
		rules.add(new Rule(SMAIN, s10a));
		String[] s10b = {SU, HD, OBJ1, MODB};
		rules.add(new Rule(SMAIN, s10b));

		String[] s11 = {SU, HD, PREDC};
		rules.add(new Rule(SMAIN, s11));
		String[] s11a = {MODA, HD, SU, PREDC};
		rules.add(new Rule(SMAIN, s11a));
		String[] s11b = {SU, HD, PREDC, MODB};
		rules.add(new Rule(SMAIN, s11b));

		String[] s12 = {SU, HD, PC};
		rules.add(new Rule(SMAIN, s12));
		String[] s12a = {MODA, HD, SU, PC};
		rules.add(new Rule(SMAIN, s12a));
		String[] s12b = {SU, HD, PC, MODB};
		rules.add(new Rule(SMAIN, s12b));
		
		String[] s13 = {SU, HD, LD};
		rules.add(new Rule(SMAIN, s13));
		String[] s13a = {MODA, HD, SU, LD};
		rules.add(new Rule(SMAIN, s13a));
		String[] s13b = {SU, HD, LD, MODB};
		rules.add(new Rule(SMAIN, s13b));

		String[] s14 = {SU, HD, VC};
		rules.add(new Rule(SMAIN, s14));
		String[] s14a = {MODA, HD, SU, VC};
		rules.add(new Rule(SMAIN, s14a));
		String[] s14b = {SU, HD, VC, MODB};
		rules.add(new Rule(SMAIN, s14b));		
			
		String[] s15 = {SU, HD, SE};
		rules.add(new Rule(SMAIN, s15));
		String[] s15a = {MODA, HD, SU, SE};
		rules.add(new Rule(SMAIN, s15a));
		String[] s15b = {SU, HD, SE, MODB};
		rules.add(new Rule(SMAIN, s15b));

		String[] s16 = {SU, HD, SVP};
		rules.add(new Rule(SMAIN, s16));
		String[] s16a = {MODA, HD, SU, SVP};
		rules.add(new Rule(SMAIN, s16a));
		String[] s16b = {SU, HD, SVP, MODB};
		rules.add(new Rule(SMAIN, s16b));
		
		String[] s17 = {SUP, HD, SU};
		rules.add(new Rule(SMAIN, s17));
		String[] s17a = {MODA, HD, SUP, SU};
		rules.add(new Rule(SMAIN, s17a));
		String[] s17b = {SUP, HD, SU, MODB};
		rules.add(new Rule(SMAIN, s17b));

		// 4 consituents
		String[] s30 = {SU, HD, OBJ1, SVP};
		rules.add(new Rule(SMAIN, s30));
		String[] s30a = {MODA, HD, SU, OBJ1, SVP};
		rules.add(new Rule(SMAIN, s30a));
		String[] s30b = {SU, HD, OBJ1, SVP, MODB};
		rules.add(new Rule(SMAIN, s30b));

		String[] s31 = {SU, HD, OBJ2, OBJ1};
		rules.add(new Rule(SMAIN, s31));
		String[] s31a = {MODA, HD, SU, OBJ2, OBJ1};
		rules.add(new Rule(SMAIN, s31a));
		String[] s31b = {SU, HD, OBJ2, OBJ1, MODB};
		rules.add(new Rule(SMAIN, s31b));

		String[] s32 = {SU, HD, PREDC, PC};
		rules.add(new Rule(SMAIN, s32));
		String[] s32a = {MODA, HD, SU, PREDC, PC};
		rules.add(new Rule(SMAIN, s32a));
		String[] s32b = {SU, HD, PREDC, PC, MODB};
		rules.add(new Rule(SMAIN, s32b));
		
		String[] s33 = {SU, HD, OBJ1, VC};
		rules.add(new Rule(SMAIN, s33));
		String[] s33a = {MODA, HD, SU, OBJ1, VC};
		rules.add(new Rule(SMAIN, s33a));
		String[] s33b = {SU, HD, OBJ1, VC, MODB};
		rules.add(new Rule(SMAIN, s33b));

		String[] s34 = {SU, HD, OBJ1, PREDC};
		rules.add(new Rule(SMAIN, s34));
		String[] s34a = {MODA, HD, SU, OBJ1, PREDC};
		rules.add(new Rule(SMAIN, s34a));
		String[] s34b = {SU, HD, OBJ1, PREDC, MODB};
		rules.add(new Rule(SMAIN, s34b));
		
		String[] s35 = {SUP, HD, SU, LD};
		rules.add(new Rule(SMAIN, s35));
		String[] s35a = {MODA, HD, SUP, SU, LD};
		rules.add(new Rule(SMAIN, s35a));
		String[] s35b = {SUP, HD, SU, LD, MODB};
		rules.add(new Rule(SMAIN, s35b));

		String[] s36 = {SU, HD, OBJ1, LD};			
		rules.add(new Rule(SMAIN, s36));
		String[] s36a = {MODA, HD, SU, OBJ1, LD};			
		rules.add(new Rule(SMAIN, s36a));
		String[] s36b = {SU, HD, OBJ1, LD, MODB};			
		rules.add(new Rule(SMAIN, s36b));
		
		String[] s37 = {SU, HD, SE, LD};
		rules.add(new Rule(SMAIN, s37));
		String[] s37a = {MODA, HD, SU, SE, LD};
		rules.add(new Rule(SMAIN, s37a));
		String[] s37b = {SU, HD, SE, LD, MODB};
		rules.add(new Rule(SMAIN, s37b));
				
		String[] s38 = {SU, HD, PREDC, VC};
		rules.add(new Rule(SMAIN, s38));
		String[] s38a = {MODA, HD, SU, PREDC, VC};
		rules.add(new Rule(SMAIN, s38a));
		String[] s38b = {SU, HD, PREDC, VC, MODB};
		rules.add(new Rule(SMAIN, s38b));
		
		String[] s39 = {SU, HD, SVP, LD};
		rules.add(new Rule(SMAIN, s39));
		String[] s39a = {MODA, HD, SU, SVP, LD};
		rules.add(new Rule(SMAIN, s39a));				
		String[] s39b = {SU, HD, SVP, LD, MODB};
		rules.add(new Rule(SMAIN, s39b));

		String[] s40 = {SU, HD, LD, VC};
		rules.add(new Rule(SMAIN, s40));
		String[] s40a = {MODA, HD, SU, LD, VC};
		rules.add(new Rule(SMAIN, s40a));
		String[] s40b = {SU, HD, LD, VC, MODB};
		rules.add(new Rule(SMAIN, s40b));		

		String[] s41 = {SUP, HD, SU, PC};
		rules.add(new Rule(SMAIN, s41));
		String[] s41a = {MODA, HD, SUP, SU, PC};
		rules.add(new Rule(SMAIN, s41a));
		String[] s41b = {SUP, HD, SU, PC, MODB};
		rules.add(new Rule(SMAIN, s41b));
		
		String[] s42 = {SU, HD, PREDC, LD};
		rules.add(new Rule(SMAIN, s42));
		String[] s42a = {MODA, HD, SU, PREDC, LD};
		rules.add(new Rule(SMAIN, s42a));
		String[] s42b = {SU, HD, PREDC, LD, MODB};
		rules.add(new Rule(SMAIN, s42b));
				
		// 5 constituents
		String[] s60 = {SU, HD, OBJ1, SVP, VC};
		rules.add(new Rule(SMAIN, s60));
		String[] s60a = {MODA, HD, SU, OBJ1, SVP, VC};
		rules.add(new Rule(SMAIN, s60a));
		String[] s60b = {SU, HD, OBJ1, SVP, VC, MODB};
		rules.add(new Rule(SMAIN, s60b));

		String[] s61 = {SU, HD, OBJ1, SVP, PREDC};	
		rules.add(new Rule(SMAIN, s61));
		String[] s61a = {MODA, HD, SU, OBJ1, SVP, PREDC};	
		rules.add(new Rule(SMAIN, s61a));
		String[] s61b = {SU, HD, OBJ1, SVP, PREDC, MODB};	
		rules.add(new Rule(SMAIN, s61b));

		String[] s62 = {SU, HD, OBJ1, SVP, LD};
		rules.add(new Rule(SMAIN, s62));
		String[] s62a = {MODA, HD, SU, OBJ1, SVP, LD};
		rules.add(new Rule(SMAIN, s62a));
		String[] s62b = {SU, HD, OBJ1, SVP, LD, MODB};
		rules.add(new Rule(SMAIN, s62b));

		String[] s63 = {SU, HD, OBJ1, PREDC, VC};
		rules.add(new Rule(SMAIN, s63));
		String[] s63a = {MODA, HD, SU, OBJ1, PREDC, VC};
		rules.add(new Rule(SMAIN, s63a));
		String[] s63b = {SU, HD, OBJ1, PREDC, VC, MODB};
		rules.add(new Rule(SMAIN, s63b));
		
		String[] s64 = {SU, HD, OBJ2, OBJ1, VC};
		rules.add(new Rule(SMAIN, s64));
		String[] s64a = {MODA, HD, SU, OBJ2, OBJ1, VC};
		rules.add(new Rule(SMAIN, s64a));
		String[] s64b = {SU, HD, OBJ2, OBJ1, VC, MODB};
		rules.add(new Rule(SMAIN, s64b));
		
		
		
		
		// same rules, but now with one modifier
		
		// 2 consituents
		String[] s101 = {MOD, HD, SU};
		rules.add(new Rule(SMAIN, s101));
		String[] s101a = {MODA, HD, SU, MOD};
		rules.add(new Rule(SMAIN, s101a));
		String[] s101b = {MOD, HD, SU, MODB};
		rules.add(new Rule(SMAIN, s101b));

		// 3 constituents
		String[] s110 = {MOD, HD, SU, OBJ1};
		rules.add(new Rule(SMAIN, s110));
		String[] s110a = {MODA, HD, SU, OBJ1, MOD};
		rules.add(new Rule(SMAIN, s110a));
		String[] s110b = {MOD, HD, SU, OBJ1, MODB};
		rules.add(new Rule(SMAIN, s110b));

		String[] s111 = {MOD, HD, SU, PREDC};
		rules.add(new Rule(SMAIN, s111));
		String[] s111a = {MODA, HD, SU, MOD, PREDC};
		rules.add(new Rule(SMAIN, s111a));
		String[] s111b = {MOD, HD, SU, PREDC, MODB};
		rules.add(new Rule(SMAIN, s111b));

		String[] s112 = {MOD, HD, SU, PC};
		rules.add(new Rule(SMAIN, s112));
		String[] s112a = {MODA, HD, SU, MOD, PC};
		rules.add(new Rule(SMAIN, s112a));
		String[] s112b = {MOD, HD, SU, PC, MODB};
		rules.add(new Rule(SMAIN, s112b));
		
		String[] s113 = {MOD, HD, SU, LD};
		rules.add(new Rule(SMAIN, s113));
		String[] s113a = {MODA, HD, SU, MOD, LD};
		rules.add(new Rule(SMAIN, s113a));
		String[] s113b = {MOD, HD, SU, LD, MODB};
		rules.add(new Rule(SMAIN, s113b));

		String[] s114 = {MOD, HD, SU, VC};
		rules.add(new Rule(SMAIN, s114));
		String[] s114a = {MODA, HD, SU, MOD, VC};
		rules.add(new Rule(SMAIN, s114a));
		String[] s114b = {MOD, HD, SU, VC, MODB};
		rules.add(new Rule(SMAIN, s114b));		
			
		String[] s115 = {MOD, HD, SU, SE};
		rules.add(new Rule(SMAIN, s115));
		String[] s115a = {MODA, HD, SU, MOD, SE};
		rules.add(new Rule(SMAIN, s115a));
		String[] s115b = {MOD, HD, SU, SE, MODB};
		rules.add(new Rule(SMAIN, s115b));

		String[] s116 = {MOD, HD, SU, SVP};
		rules.add(new Rule(SMAIN, s116));
		String[] s116a = {MODA, HD, SU, MOD, SVP};
		rules.add(new Rule(SMAIN, s116a));
		String[] s116b = {MOD, HD, SU, SVP, MODB};
		rules.add(new Rule(SMAIN, s116b));
		
		String[] s117 = {SUP, HD, MOD, SU};
		rules.add(new Rule(SMAIN, s117));
		String[] s117a = {MODA, HD, SUP, MOD, SU};
		rules.add(new Rule(SMAIN, s117a));
		String[] s117b = {SUP, HD, MOD, SU, MODB};
		rules.add(new Rule(SMAIN, s117b));

		// 4 consituents
		String[] s130 = {MOD, HD, SU, OBJ1, SVP};
		rules.add(new Rule(SMAIN, s130));
		String[] s130a = {MODA, HD, SU, OBJ1, MOD, SVP};
		rules.add(new Rule(SMAIN, s130a));
		String[] s130b = {MOD, HD, SU, OBJ1, SVP, MODB};
		rules.add(new Rule(SMAIN, s130b));

		String[] s131 = {MOD, HD, SU, OBJ2, OBJ1};
		rules.add(new Rule(SMAIN, s131));
		String[] s131a = {MODA, HD, SU, OBJ2, MOD, OBJ1};
		rules.add(new Rule(SMAIN, s131a));
		String[] s131b = {MOD, HD, SU, OBJ2, OBJ1, MODB};
		rules.add(new Rule(SMAIN, s131b));

		String[] s132 = {MOD, HD, SU, PREDC, PC};
		rules.add(new Rule(SMAIN, s132));
		String[] s132a = {MODA, HD, SU, MOD, PREDC, PC};
		rules.add(new Rule(SMAIN, s132a));
		String[] s132b = {MOD, HD, SU, PREDC, PC, MODB};
		rules.add(new Rule(SMAIN, s132b));
		
		String[] s133 = {MOD, HD, SU, OBJ1, VC};
		rules.add(new Rule(SMAIN, s133));
		String[] s133a = {MODA, HD, SU, OBJ1, MOD, VC};
		rules.add(new Rule(SMAIN, s133a));
		String[] s133b = {MOD, HD, SU, OBJ1, VC, MODB};
		rules.add(new Rule(SMAIN, s133b));

		String[] s134 = {MOD, HD, SU, OBJ1, PREDC};
		rules.add(new Rule(SMAIN, s134));
		String[] s134a = {MODA, HD, SU, OBJ1, MOD, PREDC};
		rules.add(new Rule(SMAIN, s134a));
		String[] s134b = {MOD, HD, SU, OBJ1, PREDC, MODB};
		rules.add(new Rule(SMAIN, s134b));
		
		String[] s135 = {SUP, HD, MOD, SU, LD};
		rules.add(new Rule(SMAIN, s135));
		String[] s135a = {MODA, HD, SUP, SU, MOD, LD};
		rules.add(new Rule(SMAIN, s135a));
		String[] s135b = {SUP, HD, MOD, SU, LD, MODB};
		rules.add(new Rule(SMAIN, s135b));

		String[] s136 = {MOD, HD, SU, OBJ1, LD};			
		rules.add(new Rule(SMAIN, s136));
		String[] s136a = {MODA, HD, SU, OBJ1, MOD, LD};			
		rules.add(new Rule(SMAIN, s136a));
		String[] s136b = {MOD, HD, SU, OBJ1, LD, MODB};			
		rules.add(new Rule(SMAIN, s136b));
		
		String[] s137 = {MOD, HD, SU, SE, LD};
		rules.add(new Rule(SMAIN, s137));
		String[] s137a = {MODA, HD, SU, SE, MOD, LD};
		rules.add(new Rule(SMAIN, s137a));
		String[] s137b = {MOD, HD, SU, SE, LD, MODB};
		rules.add(new Rule(SMAIN, s137b));
				
		String[] s138 = {MOD, HD, SU, PREDC, VC};
		rules.add(new Rule(SMAIN, s138));
		String[] s138a = {MODA, HD, SU, PREDC, MOD, VC};
		rules.add(new Rule(SMAIN, s138a));
		String[] s138b = {MOD, HD, SU, PREDC, VC, MODB};
		rules.add(new Rule(SMAIN, s138b));
		
		String[] s139 = {MOD, HD, SU, SVP, LD};
		rules.add(new Rule(SMAIN, s139));
		String[] s139a = {MODA, HD, SU, SVP, MOD, LD};
		rules.add(new Rule(SMAIN, s139a));				
		String[] s139b = {MOD, HD, SU, SVP, LD, MODB};
		rules.add(new Rule(SMAIN, s139b));

		String[] s140 = {MOD, HD, SU, LD, VC};
		rules.add(new Rule(SMAIN, s140));
		String[] s140a = {MODA, HD, SU, LD, MOD, VC};
		rules.add(new Rule(SMAIN, s140a));
		String[] s140b = {MOD, HD, SU, LD, VC, MODB};
		rules.add(new Rule(SMAIN, s140b));		

		String[] s141 = {SUP, HD, MOD, SU, PC};
		rules.add(new Rule(SMAIN, s141));
		String[] s141a = {MODA, HD, SUP, MOD, SU, PC};
		rules.add(new Rule(SMAIN, s141a));
		String[] s141b = {SUP, HD, MOD, SU, PC, MODB};
		rules.add(new Rule(SMAIN, s141b));
		
		String[] s142 = {MOD, HD, SU, PREDC, LD};
		rules.add(new Rule(SMAIN, s142));
		String[] s142a = {MODA, HD, SU, PREDC, MOD, LD};
		rules.add(new Rule(SMAIN, s142a));
		String[] s142b = {MOD, HD, SU, PREDC, LD, MODB};
		rules.add(new Rule(SMAIN, s142b));
				
		// 5 constituents
		String[] s160 = {MOD, HD, SU, OBJ1, SVP, VC};
		rules.add(new Rule(SMAIN, s160));
		String[] s160a = {MODA, HD, SU, OBJ1, MOD, SVP, VC};
		rules.add(new Rule(SMAIN, s160a));
		String[] s160b = {MOD, HD, SU, OBJ1, SVP, VC, MODB};
		rules.add(new Rule(SMAIN, s160b));

		String[] s161 = {MOD, HD, SU, OBJ1, SVP, PREDC};	
		rules.add(new Rule(SMAIN, s161));
		String[] s161a = {MODA, HD, SU, OBJ1, MOD, SVP, PREDC};	
		rules.add(new Rule(SMAIN, s161a));
		String[] s161b = {MOD, HD, SU, OBJ1, SVP, PREDC, MODB};	
		rules.add(new Rule(SMAIN, s161b));

		String[] s162 = {MOD, HD, SU, OBJ1, SVP, LD};
		rules.add(new Rule(SMAIN, s162));
		String[] s162a = {MODA, HD, SU, OBJ1, MOD, SVP, LD};
		rules.add(new Rule(SMAIN, s162a));
		String[] s162b = {MOD, HD, SU, OBJ1, SVP, LD, MODB};
		rules.add(new Rule(SMAIN, s162b));

		String[] s163 = {MOD, HD, SU, OBJ1, PREDC, VC};
		rules.add(new Rule(SMAIN, s163));
		String[] s163a = {MODA, HD, SU, OBJ1, MOD, PREDC, VC};
		rules.add(new Rule(SMAIN, s163a));
		String[] s163b = {MOD, SU, OBJ1, PREDC, VC, MODB};
		rules.add(new Rule(SMAIN, s163b));

		String[] s164 = {MOD, HD, SU, OBJ2, OBJ1, VC};
		rules.add(new Rule(SMAIN, s164));
		String[] s164a = {MODA, HD, SU, OBJ2, OBJ1, MOD, VC};
		rules.add(new Rule(SMAIN, s164a));
		String[] s164b = {MOD, HD, SU, OBJ2, OBJ1, VC, MODB};
		rules.add(new Rule(SMAIN, s164b));


		// uh nu ff losse regels met minstens 2 modifiers, moet eigenlijk ook ooit nog in het net...
								
		String[] s53 = {MOD, HD, SU, OBJ2, MOD, OBJ1, LD};
		rules.add(new Rule(SMAIN, s53));				
	
		String[] s56 = {MOD, HD, SU, MOD, OBJ1, PREDC};
		rules.add(new Rule(SMAIN, s56));							
		
		String[] s44 = {MOD, HD, SU, MOD};
		rules.add(new Rule(SMAIN, s44));
		
		String[] s46 = {MOD, HD, SU, MOD, MOD};
		rules.add(new Rule(SMAIN, s46));
												
		String[] s217 = {MOD, HD, SU, MOD, LD};
		rules.add(new Rule(SMAIN, s217));
		
		String[] s19 = {MOD, HD, SU, MOD, PREDC, LD};
		rules.add(new Rule(SMAIN, s19));

		String[] s21 = {MOD, HD, SU, MOD, MOD, LD};
		rules.add(new Rule(SMAIN, s21));
		
		String[] s22 = {MOD, HD, SU, OBJ1, MOD};
		rules.add(new Rule(SMAIN, s22));
		
		String[] s23 = {MOD, HD, SU, MOD, PREDC};
		rules.add(new Rule(SMAIN, s23));
								
		String[] s65 = {MOD, HD, SU, MOD, SVP};
		rules.add(new Rule(SMAIN, s65));					
				
		String[] s27 = {MOD, HD, SU, MOD, VC};
		rules.add(new Rule(SMAIN, s27));
		
		String[] s28 = {MOD, HD, SU, MOD, OBJ1, VC};
		rules.add(new Rule(SMAIN, s28));
		
		String[] s29 = {MOD, HD, SU, MOD, PREDC, VC};
		rules.add(new Rule(SMAIN, s29));
				
		String[] s231 = {MOD, HD, SU, MOD};
		rules.add(new Rule(SMAIN, s231));
		
		String[] s233 = {MOD, HD, SU, OBJ2, MOD, OBJ1};
		rules.add(new Rule(SMAIN, s233));
										
		String[] s72 = {MOD, HD, SU, MOD, MOD, LD};
		rules.add(new Rule(SMAIN, s72));		
										
		String[] s87 = {MOD, HD, SU, MOD, OBJ1, PREDC, SVP};
		rules.add(new Rule(SMAIN, s87));
		
		String[] s88 = {MODA, HD, SU, MOD, MOD, MOD};
		rules.add(new Rule(SMAIN, s88));
						
		String[] s93 = {MOD, HD, SU, OBJ1, MOD, SVP};
		rules.add(new Rule(SMAIN, s93));
						
		String[] s96 = {MODA, HD, SU, OBJ1, MOD, MOD};
		rules.add(new Rule(SMAIN, s96));		
	}
}