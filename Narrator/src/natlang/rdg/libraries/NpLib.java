package natlang.rdg.libraries;

import natlang.rdg.model.*;

/**	This class is a library containing all rules concerning the syntactic category 
 *	SMAIN
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class NpLib extends ConstituentLib implements LibraryConstants
{
	/**	Constructs the SmainLib by creating the necessary rules 
	 */
	public NpLib()
	{
		String[] s1a = {HD};
		rules.add(new Rule(NP, s1a));
		String[] s1b = {DET, HD};
		rules.add(new Rule(NP, s1b));
		
		String[] s2a = {MOD, HD};
		rules.add(new Rule(NP, s2a));
		String[] s2b = {DET, MOD, HD};
		rules.add(new Rule(NP, s2b));
		
		String[] s3a = {HD, MODB};
		rules.add(new Rule(NP, s3a));
		String[] s3b = {DET, HD, MODB};
		rules.add(new Rule(NP, s3b));
		
		String[] s4a = {MOD, HD, MODB};
		rules.add(new Rule(NP, s4a));
		String[] s4b = {DET, MOD, HD, MODB};
		rules.add(new Rule(NP, s4b));
	}
}