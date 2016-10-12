% _______________________
% Goal selection rules.
% _______________________


% --------------------------------------------------------------------
% AllGoalsAreFine
% This rule selects all goals.
% --------------------------------------------------------------------
goal_selection_schema([
	type(ps:'AllGoalsAreFine'),
	arguments([ agens(Agens) ]),
	preconditions([
		condition(true, [
		% DISABLED
			fact(ps:easter, ps:sameday, ps:whitsunday)
		])
	]),
	selected_goal(Goal)
]) 	
	:-
goal_schema(Goal), 
schema_agens(Goal, Agens).		

% --------------------------------------------------------------------
% Revenge
% This rule selects goals to take revenge.
% --------------------------------------------------------------------
goal_selection_schema([
	type(ps:'RevengeRule'),
	arguments([ agens(Agens), patiens(Patiens) ]),
	preconditions([
		condition(true, [
	    	fact(Agens, ps:hates, Patiens)
	    ])
	]),
	selected_goal(Goal)
]) 	
	:-
goal_schema(Goal), 
schema_type(Goal, ps:'WoundEnemy'),
schema_agens(Goal, Agens),
schema_patiens(Goal, Patiens).

%-----------------
goal_selection_schema([
	type(ps:'Defend'),
	arguments([ agens(Agens), target(Target) ]),	
	preconditions([
			% Patiens has said "en garde"
		condition(true, [
			fabula(Act, rdf:type, ps:'SayEnGarde'),
			fabula(Act, fabula:agens, Target)
		]),
		% It wasn't me
		condition(true, [
			rule(Agens, owlr:isNot, Target)
		])		
	]),
	selected_goal(Goal)
])
	:- 
goal_schema(Goal), 
schema_type(Goal, ps:'Defend'),
schema_agens(Goal, Agens),
schema_target(Goal, Target).	

%-----------------
goal_selection_schema([
	type(ps:'JumpShipRule'),
	arguments([ agens(Agens), target(Target) ]),	
	preconditions([
		% Patiens has said "en garde"
		condition(true, [
			fabula(Act, rdf:type, ps:'SayEnGarde'),
			fabula(Act, fabula:agens, Target)
		]),
		% It wasn't me
		condition(true, [
			rule(Agens, owlr:isNot, Target)
		])
	]),
	selected_goal(Goal)
])
	:- 
goal_schema(Goal), 
schema_type(Goal, ps:'JumpShipGoal'),
schema_agens(Goal, Agens),
false. % Disabled

%-----------------
goal_selection_schema([
	type(ps:'RefillWaterSupplyRule'),
	arguments([ agens(Agens), patiens(WaterSupply) ]),	
	preconditions([
		% I am the captain of a ship
		condition(true, [
			fact(Agens, rdf:type, ps:'Captain'),
			fact(Agens, ps:owns, Ship),
			rule(Ship, owlr:typeOrSubType, ps:'Ship')			
		]),
		% And the ship has an empty water supply
		condition(true, [
			fact(Ship, ps:hasWaterSupply, WaterSupply),
			fact(WaterSupply, swc:hasAttribute, swc:empty)
		])		
	]),
	selected_goal(Goal)
])
	:- 
goal_schema(Goal), 
schema_type(Goal, ps:'RefillWaterSupply'),
schema_agens(Goal, Agens),
schema_patiens(Goal, WaterSupply).	

%-----------------
goal_selection_schema([
	type(ps:'GetDrunkRule'),
	arguments([ agens(Agens)]),	
	preconditions([
		% Agens is thirsty
        condition(true, [
        	fact(Agens, swc:hasAttribute, ps:thirsty)
        ])
	]),
	selected_goal(Goal)
])
	:- 
goal_schema(Goal), 
schema_type(Goal, ps:'GetDrunk'),
schema_agens(Goal, Agens).	