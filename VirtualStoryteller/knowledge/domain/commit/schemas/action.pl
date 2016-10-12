

% ------------------------------------------
% TransitMove
% ------------------------------------------
% AGENS moves from Location to Target via a Path
action_schema([
	type(commit:'WalkThisWay'),
    arguments([
    	agens(Agens),
    	target(Target),
    	location(Location)
	]),
	duration(1),
	preconditions([
        condition(true, [
			fact(Agens, commit:at, Location),
			rule(Location, owlr:isNot, Target),
			rule(Target, owlr:typeOrSubType, commit:'Location')
		]),
	    condition(true, [
	        rule(Path, owlr:typeOrSubType, commit:'Path')
	    ]),
	    condition(true, [
	        fact(Path, commit:from, Location),
	        fact(Path, commit:to, Target)
	    ])
	]),
	effects([
		condition(true, [
		    fact(Agens,	commit:at, Target)
		]),
        condition(false, [
            fact(Agens, commit:at, Location)
        ])
    ])
]).

% ------------------------------------------
% Example action
% ------------------------------------------

% Just to check whether the rest works as well.
action_schema([
	type(commit:'ExampleAction'),
    arguments([
    	agens(Agens),
    	location(Location)
	]),
	duration(1),
	preconditions([
        condition(true, [
			fact(Agens, commit:at, Location)
		])
	]),
	effects([
    ])
]).



% Move near an Object (within a Location)
%action_schema([
%	type(commit:'TransitMove'),
%    arguments([
%    	agens(Agens),
%    	target(Object),
%    	location(Location)
%	]),
%	duration(1),
%	preconditions([
%        condition(true, [
%			fact(Agens, commit:at, Location),
%			fact(Object, commit:at, Location),
%			rule(Location, owlr:typeOrSubType, commit:'Location'),
%			rule(Object, owlr:typeOrSubType, commit:'Object')
%		]),
%		condition(false, [
%			fact(Agens, commit:near, Object)
%		])
%	]),
%	effects([
%		condition(true, [
%		    fact(Agens,	commit:near, Object)
%		])
%    ])
%]).


% ------------------------------------------
% Drink
% ------------------------------------------
action_schema([
	type(commit:'Drink'),
	arguments([agens(Agens), patiens(Patiens)]),
	duration(1),
	preconditions([
		% There's a Character and an Object that is liquid
		condition(true, [
			rule(Agens, owlr:typeOrSubType, commit:'Character'),
			%rule(Patiens, owlr:typeOrSubType, commit:'Object'),
			fact(Patiens, commit:has, commit:beer1)
		]),
		% Agens must have the drink
%		condition(true, [
%            fact(Agens, commit:has, Patiens)
%		])
	]),
	effects([
		% Patiens no longer at Agens and no longer thirsty
		condition(false, [
			fact(Agens, commit:has, Patiens),
			fact(Agens, commit:hasAttribute, commit:thirsty)
		])
	])
]).