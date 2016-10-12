% WalkFromTo
% WalkToFrom
% WalkFromToDoor
% WalkToFromDoor

% -------------------------------------------
% WalkFromTo -- walking on a road, "upstream"
% -------------------------------------------

% Agens walks from Location to Target  via Instrument
action_schema([
	type(fabula:'WalkFromTo'),
	arguments([	agens(Agens), target(Target), location(CurLoc), instrument(Road)]),
	duration(1),
	preconditions([
		
		% There is an "upstream" road to walk on, from where the instrument is
		condition(true, [
			fact(Agens,	swc:at,        CurLoc),
			fact(Road,	swc:from, CurLoc),
			rule(Road,	owlr:typeOrSubType,     swc:'WalkablePath'), 
			fact(Road,	swc:to,   Target)
		]),
		
	    % There is no door on this path
		condition(false, [
		    fact(Road,    swc:hasDoor,  _)   
		])
	]),
	effects([
	    % We are now at the target location
		condition(true, [
			fact(Agens,	swc:at,		Target)
		]),

	    % We are no longer at our current location
	    condition(false, [
			fact(Agens,	swc:at, CurLoc)
		])
	])
]).


% --------------------------------------------------------------------
% WalkFromToDoor -- walking on a road that contains a door, "upstream"
% --------------------------------------------------------------------

% Agens walks from Location to Target through Door via Instrument
action_schema([
	type(fabula:'WalkFromToDoor'),
	arguments([ agens(Agens), target(Target), location(CurLoc), instrument(Road), door(Door) ]),
	duration(1),
	preconditions([
		
		% There is an "upstream" road to walk on, from where the instrument is		
		condition(true, [
			rule(Road, 	owlr:typeOrSubType, swc:'WalkablePath'),
			fact(Road,	swc:to, Target),
			fact(Road,	swc:from, CurLoc),
			fact(Agens,	swc:at, CurLoc)
		]),
		
		% There is a door on this road, which is open
		condition(true, [
			fact(Road,    swc:hasDoor,    Door),
			fact(Door,    swc:hasAttribute, swc:open)
		])
	]),
	effects([
	    % We are now at the target location	
		condition(true, [
			fact(Agens,	swc:at,		Target)
		]), 
		
	    % We are no longer at our current location		
	    condition(false, [
			fact(Agens,	swc:at,		CurLoc)			
		])
	])
]).
