event_schema([
	type(red:'BecomeHungry'),
	arguments([agens(Ag)]),
    preconditions([
%
% EDIT: Multiple character should be able to become hungry.
% This might give uninteresting results when both/all characters are AI controlled (everybody focused on finding food and eating it asap).
% But when the wolf is the only AI-controlled character, it should always be able to become hungry, regardless if the human controlled characters are already hungry.  
%   
%Apperently it doensn't work when this is not commented out:
%		% Not hungry
        condition(false, [
        	fact(Ag, swc:hasAttribute, red:hungry)
        ]),
        % was a character 
	    condition(true, [
        	rule(Ag, owlr:typeOrSubType, swc:'Character')
        ])
    ]),
   
    effects([
        % Characters can happen to be hungry
        condition(true, [
        	fact(Ag, swc:hasAttribute, red:hungry)
        ])
    ])
]).	 

event_schema([
	type(red:'Die'),
	arguments([agens(Agens)]),
    preconditions([
	    % TARGET was poisoned
	    condition(true, [
	    	fact(Target, swc:hasAttribute, red:poisoned)
        ]),  
        % AGENS has eaten TARGET
	    condition(true, [
	    	fabula(Act, rdf:type, red:'Eat'),
	    	fabula(Act, fabula:agens, Agens),
	    	fabula(Act, fabula:patiens, Target)
	    ])
    ]),
   
    effects([
        % AGENS will die
        condition(true, [
        	fact(Agens, swc:hasAttribute, red:dead)
        ])
    ])
]).	 
