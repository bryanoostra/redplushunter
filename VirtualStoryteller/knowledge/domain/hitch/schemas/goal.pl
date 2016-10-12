% AGENS has the goal to be at LOCATION Earth [only when he is not at earth!]


goal_schema([
	type(hitch:'BeAtLocation'), % all species want to go to earth
	arguments([agens(Agens), location(Location), target(hitch:earth)]),
	urgency(0.1),
	preconditions([
	    condition(true, [
	        rule(Agens, owlr:typeOrSubType, hitch:'Species'),
	        fact(Agens, hitch:at, Location),
	        rule(Location, owlr:isNot, hitch:earth)
	    ])
	]), 
    success_conditions([
        condition(true, [
            fact(Agens, hitch:at, hitch:earth)
        ])
    ])
]).
goal_schema([
	type(hitch:'BeAtLocation'), % all species want to go to beetlejuice
	arguments([agens(Agens), location(Location), target(hitch:beetlejuice)]),
	urgency(0.1),
	preconditions([
	    condition(true, [
	        rule(Agens, owlr:typeOrSubType, hitch:'Species'),
	        fact(Agens, hitch:at, Location),
	        rule(Location, owlr:isNot, hitch:beetlejuice)
	    ])
	]), 
    success_conditions([
        condition(true, [
            fact(Agens, hitch:at, hitch:beetlejuice)
        ])
    ])
]).

goal_schema([
	type(hitch:'JustBe'),
	urgency(1.0),
	arguments([agens(Agens), location(Location)]),
	preconditions([
		rule(Agens, owlr:typeOrSubType, hitch:'Character')
	]),
    success_conditions([
    	fact(Agens, hitch:at, Location)
    ])
]).

goal_schema([
	type(hitch:'FleeFromSpecies'), % Humans are afraid of Vogons
	arguments([agens(Agens), patiens(Patiens), location(Location), target(Target)]),
	urgency(0.9),
	preconditions([
		condition(true, [
			fact(Agens, hitch:at, Location),
			fact(Patiens, hitch:at, Location),
			fact(Agens, rdf:type, hitch:'Human'),
			fact(Patiens, rdf:type, hitch:'Vogon')
		])
	]),
	success_conditions([
		condition(true, [
			fact(Agens, hitch:at, Target),
			fact(Patiens, hitch:at, Location),
			rule(Location, owlr:isNot, Target)
		])
	])%,
	% Does not work
	%failure_conditions([
	%	condition(true, [
	%		%rule(Patiens2, owlr:is, hitch:ford),
	%		%fact(Patiens2, hitch:at, Location2),
	%		%rule(Location2, owlr:isNot, Location)
	%		fact(Agens, hitch:at, hitch:earth)
	%	])
	%])
]).