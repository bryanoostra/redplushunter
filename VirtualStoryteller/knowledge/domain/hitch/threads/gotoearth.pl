
% :- episode(head(Vars), [pos-precs], [neg-precs], [code]).

% Everything here needs to correspond to the information in the TTL setting file.

% A Human wants to go to Location Earth
thread_schema([
	type(hitch:'GoToEarth'),
	preconditions([
		condition(true, [
			rule(Human, owlr:typeOrSubType, hitch:'Human'),
			rule(Alien, owlr:typeOrSubType, hitch:'Alien'),
			rule(Vogon, owlr:typeOrSubType, hitch:'Vogon')
			%%%%%%%%fact(Human, hitch:at, Location)%,
			%fact(Alien, hitch:at, Location),
			%fact(Vogon, hitch:at, Location)
		])
	]),
	characters([
		character(Human),
		character(Alien),
		character(Vogon)
	]),
	location([]),
	settings([]),
	goals([
		% Adding goals is not necessary...
		%goal(Human, hitch:'FleeFromVogon')
	])
]).

% Neither is the stuff that went here, according to the VST for Dummies page on the Twiki.
		
		