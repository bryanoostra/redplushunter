% Cognition schemas

cognition_schema([
	type(ps:'HateDrunkards'),
	arguments([agens(Agens), patiens(Patiens)] ),
	
	preconditions([
		condition(true, [
			rule(Agens, owlr:typeOrSubType, fabula:'Character'),
			rule(Patiens, owlr:typeOrSubType, fabula:'Character'),
			rule(Agens, owlr:isNot, Patiens)
		]),
		condition(true, [
			fact(Patiens, swc:hasAttribute, ps:drunk)
		])
	]),
	effects([
		condition(true, [
			fact(Agens, ps:hates, Patiens)
		])
	])
]).
  
	
	
