% Een thread heeft precondities, en definieert setting information, die variabelen kan bevatten.
thread_schema([
	type(ps:'OnShip'),
	preconditions([
		condition(true, [
			rule(Cpt, owlr:typeOrSubType, fabula:'Character'),
			rule(Cpt2, owlr:typeOrSubType, fabula:'Character'),
			rule(Cpt, owlr:isNot, Cpt2)
		])
	]),
	characters([
		character(Cpt),
		character(Cpt2)
	]),
	location(ps:oDeck_1),
	goals([
	])
	
]).


		
		