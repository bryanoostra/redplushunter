% Goal database for the Pirates_Jasper2 domain
% @author Jasper Bragt
% @date Aug 2008

% -----------------------------------------------------------

% ------------------------------------------
% LaunchCanon
% ------------------------------------------
% Goal for firing the canon and
% launching the canonball from the canon
% PRE:	there is a pirate, a canon, a canonball, a torch, the torch is lit
% SUC:	canon has been fired

goal_schema([
	type(psj2:'LaunchCanon'),
	arguments([agens(Agens), patiens(Patiens), target(Target), instrument(Instrument)]),
	positive_motivations(['http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#alcohol_1']),	%(5)
	negative_motivations(['http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#alcohol_2']),	%(8) -> -3
%	urgency(0.6),
	preconditions([
	    condition(true, [
			fact(Agens, 	 swc:hasRole, 		 psj2:'Pirate'),		% agens is a pirate,
			fact(_,			 psj2:hasOrdered,	 psj2:'LaunchCanon'),
%			fact(Agens, 	 psj2:hasInterest, 	 X),					% which has an interest
%			rule(X, 		 owlr:typeOrSubType, psj2:'Explosions'),	% which is about explosions,
%			fact(X,			 psj2:hasStrength,	 Y),
%			rule(Y,			 owlr:greaterEqual,	 7),					% which has some required strength,
			rule(Target, 	 owlr:typeOrSubType, psj2:'Canon'),			% a canon,
			rule(Patiens, 	 owlr:typeOrSubType, psj2:'CanonBall'),		% a canonball
			rule(Instrument, owlr:typeOrSubType, psj2:'Torch'),			% and a torch exist
			fact(Instrument, swc:hasAttribute,   psj2:lit)				% torch is lit
		])
	]),
	success_conditions([
        condition(true, [
			fact(Target, 	 swc:hasAttribute, 	 psj2:fired)			% canon has been fired
        ])
    ])
]).

% ------------------------------------------
% LaunchRat
% ------------------------------------------
% Goal for removing the rat from the ship by launching it from the canon
% PRE:	agens is a pirate; ship has a canon, a torch, the torch is lit; there is a rat; removeRat has been ordered
% SUC:	canon has been fired

goal_schema([
	type(psj2:'LaunchRat'),
	arguments([agens(Agens), target(Target)]),
	positive_motivations(['http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#loyal_1']),	
	negative_motivations(['http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#likes_animals_1']),
%	urgency(0.6),
	preconditions([
	    condition(true, [
			fact(Agens,		swc:hasRole, 		psj2:'Pirate'),		
			fact(_,			psj2:hasOrdered,	psj2:'RemoveRat'),
			fact(Target, 	swc:at,			 	psj2:'Ship'),
			rule(Target,	owlr:typeOrSubType, psj2:'Rat')%,
		])
	]),
	success_conditions([
        condition(true, [
			fact(Target, 	 swc:hasAttribute, 	 psj2:fired)			% canon has been fired
        ])%,
%        condition(false, [
%			fact(Patiens, 	 swc:at, 	 psj2:'Ship')			% Rat is not on the ship anymore
%        ])
    ])
]).

% ------------------------------------------
% RemoveRat
% ------------------------------------------

goal_schema([
	type(psj2:'RemoveRat'),
	arguments([agens(Agens)]),
	preconditions([
	    condition(true, [
			fact(Agens,	swc:hasRole, 		psj2:'Captain'),		% agens is a pirate,
%			fact(R, 	swc:at,			 	psj2:'Ship'),
			rule(_,		owlr:typeOrSubType, psj2:'Rat')%,			% a rat
%			rule(X, 	owlr:typeOrSubType, psj2:'Canon'),			% a canon,
%			rule(Y, 	owlr:typeOrSubType, psj2:'CanonBall'),		% a canonball
%			rule(Z, 	owlr:typeOrSubType, psj2:'Torch'),			% and a torch exist
%			fact(Z, 	swc:hasAttribute,   psj2:lit)				% torch is lit
		])
	]),
	success_conditions([
        condition(true, [
			fact(Agens,		 psj2:hasOrdered, 	 psj2:'RemoveRat')
        ])
    ])
]).

% ------------------------------------------
% OrderLaunchCanon
% ------------------------------------------

goal_schema([
	type(psj2:'Order'),
	arguments([agens(Agens)]),
	preconditions([
	    condition(true, [
			fact(Agens, 	 swc:hasRole, 		 psj2:'Captain')%,		% agens is a pirate,
%			rule(Target, 	 owlr:typeOrSubType, psj2:'Canon'),			% a canon,
%			rule(Patiens, 	 owlr:typeOrSubType, psj2:'CanonBall'),		% a canonball
%			rule(Instrument, owlr:typeOrSubType, psj2:'Torch'),			% and a torch exist
%			fact(Instrument, swc:hasAttribute,   psj2:lit)				% torch is lit
		])
	]),
	success_conditions([
        condition(true, [
			fact(Agens,		 psj2:hasOrdered, 	 psj2:'LaunchCanon')		% fire canon has been ordered
        ])
    ])
]).

% ------------------------------------------
% GetRum
% ------------------------------------------
% Goal for gathering bottles
% PRE:	there are a pirate and a bottle, the bottle is filled with rum
% SUC:	the pirate has the bottle

goal_schema([
	type(psj2:'GetRum'),
	arguments([agens(Agens), patiens(Patiens)]),
	positive_motivations(['http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#aggressive_1', 'http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#alcohol_2']), 	%(2,8)
	preconditions([
	    condition(true, [
			fact(Agens,   swc:hasRole, 		  psj2:'Pirate'),	% agens is a pirate,
			rule(Patiens, owlr:typeOrSubType, psj2:'RumBottle'),% target is a bottle,
			fact(Patiens, swc:hasAttribute,   psj2:full)		% which is filled with rum
		])
	]),
	success_conditions([
		condition(true, [
			fact(Agens,   swc:has, 			  Patiens)			% agens has the bottle
      ])
    ])
]).

%goal_schema([
%	type(psj2:'GetRum2'),
%	arguments([agens(Agens), patiens(Patiens)]),
%	positive_motivations(['http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#aggressive_1', 'http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#alcohol_2']), 	%(2,8)
%	negative_motivations(['http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#alcohol_1']),																						%(5)  -> 5
%	preconditions([
%	    condition(true, [
%			fact(Agens,   swc:hasRole, 		  psj2:'Pirate'),	% agens is a pirate,
%			rule(Patiens, owlr:typeOrSubType, psj2:'RumBottle'),% target is a bottle,
%			fact(Patiens, swc:hasAttribute,   psj2:full)		% which is filled with rum
%		])
%	]),
%	success_conditions([
%		condition(true, [
%			fact(Agens,   swc:has, 			  Patiens)			% agens has the bottle
%      ])
%    ])
%]).
    	
% DEBUG HELPER FUNCTIONS
% validGoal(S, P) :-
%    goal_schema(S), validate_schema(S, P).
    
%validGoalFor(Agens, S, P) :-
%    goal_schema(S), schema_agens(S, Agens), validate_schema(S, P).    
    
%validGoalFor(Agens,S,[]),goal_success_conditions(S,C),plan(Agens,C,Plan).    

% plan('http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#linda', [condition(true, [fact('http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#linda', 'http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#has', 'http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#vanilla_ice_1')]), condition(true, [fact('http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#linda', 'http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#at', 'http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#park_1')])], Plan).

% plan('http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#Erik',[condition(true, [fact('http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#Erik', 'http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#hasExecuted', 'http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#RemoveRat')])], Plan).
% plan('http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#Erik',[condition(true, [fact('http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#oRumBarrel_1', 'http://www.owl-ontologies.com/StoryWorldSettings/StoryWorldCore.owl#contains', 'http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#oRat_1')])], Plan).

%load_domain(pirates_jasper2).   
%goal_schema(S), validate_schema(S,[]),goal_success_conditions(S, SC), plan(psj2:'Jean-Baptiste', SC, Plan).
%query(psj2:'Ladder', rdfs:subClassOf, X).