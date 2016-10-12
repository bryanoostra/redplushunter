% OpenDoor
% OpenContainer

% -------------------------------------------------
% OpenDoor -- opening something that can be opened
% -------------------------------------------------
action_schema([
	type(fabula:'OpenDoor'),
    arguments([    agens(Agens), patiens(Patiens), instrument(Road) ]),
    duration(1),
    preconditions([
    			
		% Instrument is located "at" a door that is openable, but closed.
		condition(true, [
			fact(Agens, swc:at, CurrLoc),
			fact(Road, swc:from, CurrLoc), % CAREFUL, this disables any improvisation actions with effect "fromGeographicArea" or "toGeographicArea".
			fact(Road, swc:hasDoor, Patiens),
			fact(Patiens, swc:hasAttribute, swc:closed),
			fact(Patiens, swc:hasAttribute, swc:openable)
		]),
		
	    % The door must not be open
        condition(false, [
	        fact(Patiens, swc:hasAttribute, swc:open)
	    ])
		
	]),
	effects([
	    % The door is now open
		condition(true, [
		    fact(Patiens, swc:hasAttribute, swc:open)
		]),

		% The door is no longer closed
		condition(false, [
			fact(Patiens, swc:hasAttribute, swc:closed)
		])
	])
]).

% ------------------------------------
% OpenContainer -- opening a container
% ------------------------------------
action_schema([
	type(fabula:'OpenContainer'),
    arguments([ agens(Agens), patiens(Patiens) ]),
    duration(1),
    preconditions([
		
		% Instrument is located "at" a door that is openable, but closed.
		condition(true, [
			fact(Agens, swc:at, CurrLoc),
			fact(Patiens, swc:at, CurrLoc),
			rule(Patiens, owlr:typeOrSubType, swc:'Container'),
			fact(Patiens, swc:hasAttribute, swc:closed),
			fact(Patiens, swc:hasAttribute, swc:openable)
		]),
		
		% The container must not be open
		condition(false, [
	        fact(Patiens, swc:hasAttribute, swc:open)
	    ])
	]),
	
	effects([
	    % The container is now open
		condition(true, [
		    fact(Patiens, swc:hasAttribute, swc:open)
		]),
	    % The container is no longer closed
	    condition(false, [
	    	fact(Patiens, swc:hasAttribute, swc:closed)
	    ])
	])
]).