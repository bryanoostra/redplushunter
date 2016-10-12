% Previously, this didn't work: planning gave an error that said the goal of being at earth could
% not be reached.
% I think that there were conflicting preconditions in the Move action_schema and the BeAtEarth
% goal.

% AGENS moves from Location to Target
action_schema([
	type(hitch:'TransitMove'),
    arguments([
    	agens(Agens),
    	target(Target),
    	location(Location)
	]),
	duration(1),
	preconditions([
        condition(true, [
			fact(Agens, hitch:at, Location),
			rule(Location, owlr:isNot, Target),
			rule(Target, owlr:typeOrSubType, hitch:'Location')
		])
	]),
	effects([
		condition(true, [
		    fact(Agens,	hitch:at, Target)
		]),
        condition(false, [
            fact(Agens, hitch:at, Location)
        ])
    ])
]).