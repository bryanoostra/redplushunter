package natlang.rdg.libraries;

import natlang.rdg.model.*;

public class ExclLib extends ConstituentLib implements LibraryConstants
{
	public ExclLib()
	{
		String[] s1 = {TOP};
		rules.add(new Rule("excl", s1));
	}
}
