package natlang.rdg.libraries;

import natlang.rdg.model.*;

/**	This class is a library containing all rules concerning the syntactic category 
 *	CONJ (a conjunction)
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class ConjLib extends ConstituentLib implements LibraryConstants
{
	/**	Constructs the SmainLib by creating the necessary rules 
	 */
	public ConjLib()
	{
		String[] s0 = {};
		rules.add(new Rule(CONJ, s0));
		String[] s1 = {CNJ, CRD, CNJ};
		rules.add(new Rule(CONJ, s1));
		String[] s2 = {CNJSATELLITE, CRD, CNJNUCLEUS};
		rules.add(new Rule(CONJ, s2));
		
		StringBuffer cnjell = new StringBuffer(CNJ);
		cnjell.append(ELLIPTED);
		String[] s3 = {CNJ, CRD, cnjell.toString()};
		rules.add(new Rule(CONJ, s3));
		
		StringBuffer cnjraised = new StringBuffer(CNJ);
		cnjraised.append(RAISEDRIGHTNODE);
		String[] s4 = {cnjraised.toString(), CRD, cnjell.toString()};
		rules.add(new Rule(CONJ, s4));
		
		String[] s11 = {cnjraised.toString(), CRD, CNJ};
		rules.add(new Rule(CONJ, s11));
		
		String[] s5 = {cnjraised.toString(), MOD, CRD, CNJ};
		rules.add(new Rule(CONJ, s5));
		
		String[] s6 = {CNJ, MOD, CRD, CNJ};
		rules.add(new Rule(CONJ, s6));
		String[] s13 = {CNJ, CRD, CNJ, MODB};
		rules.add(new Rule(CONJ, s13));
		//String[] s7 = {CNJNUCLEUS, CRD, CNJSATELLITE, MOD};
		String[] s7 = {CNJSATELLITE, MOD, CRD, CNJNUCLEUS};
		rules.add(new Rule(CONJ, s7));
		String[] s14 = {CNJSATELLITE, CRD, CNJNUCLEUS, MODB};
		rules.add(new Rule(CONJ, s14));
		String[] s14a = {MODA, CNJSATELLITE, CRD, CNJNUCLEUS};
		rules.add(new Rule(CONJ, s14a));
		
		String[] s8 = {CNJ, CRD, cnjell.toString(), MOD};
		rules.add(new Rule(CONJ, s8));
		
		String[] s9 = {cnjraised.toString(), MOD, CRD, cnjell.toString()};
		rules.add(new Rule(CONJ, s9));
		
		String[] s10 = {cnjraised.toString(), MOD, CRD, CNJ,};
		rules.add(new Rule(CONJ, s10));
		
		String[] s12 = {cnjraised.toString(), MOD, CRD, CNJ};
		rules.add(new Rule(CONJ, s12));
		
		String[] s15 = {CNJNUCLEUS, MOD, CRD, CNJSATELLITE, MODB};
		rules.add(new Rule(CONJ, s15));
	}
}