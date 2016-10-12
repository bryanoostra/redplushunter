  	
thread_schema([
	type(psj2:'On_Ship'),
	preconditions([
		condition(true, [
			rule(Cpt,  owlr:typeOrSubType, fabula:'Character'),
			rule(Pir, owlr:typeOrSubType, fabula:'Character'),
			rule(Cpt, owlr:isNot, Pir)
		])
	]),
	characters([
		character(Cpt),
		character(Pir)
	]),
	location(psj2:oDeck_1)
]).
