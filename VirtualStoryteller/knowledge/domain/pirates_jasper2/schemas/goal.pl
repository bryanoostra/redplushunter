% Goal database for the Pirates_Jasper2 domain
% @author Jasper Bragt
% @date October 2009
% -----------------------------------------------------------

% ------------------------------------------
% RemoveRat
% ------------------------------------------
% Captain's order to remove a rat from the ship
% PRE:	there is actually a rat on the ship
%		only a captian can issue orders
% SUC:	the order has been issued

goal_schema([
	type(psj2:'OrderRemoveRat'),
	arguments([agens(Agens), patiens(Patiens), location(Target)]),
	preconditions([
	    condition(true, [
			fact(Agens,		swc:hasRole, 		psj2:'Captain'), 	% agens is the captain
			rule(Patiens,	owlr:typeOrSubType, psj2:'Rat'),		% and there is a rat 
			fact(Patiens,	swc:at,				Target),			% in the vicinity
			fact(Agens,		swc:at,				Target)				% of the captain
		])
	]),
	success_conditions([
        condition(true, [
			fact(Agens,		 psj2:hasOrdered, 	 psj2:'RemoveRat')
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
	arguments([agens(Agens), patiens(Patiens)]),
	dramatic_choice('http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#RemoveRatChoice'),
	positive_motivations(['http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#loyal_1']),	
	negative_motivations(['http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#likes_animals_1']),
	preconditions([
	    condition(true, [
			fact(Agens,		swc:hasRole, 		psj2:'Pirate'),		% agens is a pirate
			rule(Patiens,	owlr:typeOrSubType, psj2:'Rat'),		% patiens is a rat	
			fact(_,			psj2:hasOrdered,	psj2:'RemoveRat')	% and the captain has ordered to remove a rat
		])%,
%		condition(false, [
%			fact(Agens,		psj2:hasExecuted,	psj2:'RemoveRat')	% the order has not yet been executed
%		])	
	]),
	success_conditions([
        condition(true, [
			fact(Patiens, 	swc:at, 	 		psj2:'Sea')			% the rat is launched into the sea
        ])
    ])
]).

% ------------------------------------------
% HideRat
% ------------------------------------------
% Goal for hiding the rat from the captain
% PRE:	agens is a pirate; agens likes animals; removeRat has been ordered
% SUC:	the rat is in the rumbarrel in the hold

goal_schema([
	type(psj2:'HideRat'),
	arguments([agens(Agens), patiens(Patiens), target(Target)]),
	dramatic_choice('http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#RemoveRatChoice'),
	positive_motivations([
		'http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#loyal_1', 
		'http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#likes_animals_1'
	]),	
	negative_motivations([]),
	preconditions([
	    condition(true, [
			fact(Agens,		swc:hasRole, 		psj2:'Pirate'),		% agens is a pirate
			fact(_,			psj2:hasOrdered,	psj2:'RemoveRat'),	% and the captain has ordered to remove a rat
			rule(Patiens,	owlr:typeOrSubType, psj2:'Rat'),		% so the patiens is a rat	
			rule(Target,	owlr:typeOrSubType, swc:'Container')	% and the target is a container
		]),
		condition(false, [
			fact(Agens,	swc:has, Target)							% the container is not held by the agens
		])%,
%		condition(false, [
%			fact(Agens,		psj2:hasExecuted,	psj2:'RemoveRat')	% the order has not yet been executed
%		])
	]),
	success_conditions([
       condition(true, [
			fact(Target,	swc:contains, 	 	Patiens)			% the rat is hidden in a container
       ])
    ])
]).

% ------------------------------------------
% GetRum
% ------------------------------------------
% Goal for getting rum
% PRE:	there are a pirate and a bottle, the bottle is filled with rum
% SUC:	the pirate has the bottle

goal_schema([
	type(psj2:'GetRum'),
	arguments([agens(Agens), patiens(Patiens), R]),
	%dramatic_choice(psj2:'RemoveRat'),
	positive_motivations([
		'http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#aggressive_1', 
		'http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#alcohol_1']), 	%(2,8)
	preconditions([
	    condition(true, [
			fact(Agens,   swc:hasRole, 		  	psj2:'Pirate'),		% agens is a pirate,
			rule(Patiens, owlr:typeOrSubType, 	psj2:'RumBottle'),	% target is a bottle,
			fact(Patiens, swc:contains,			R),					% which is filled with rum
			rule(R,		  owlr:typeOrSubType, 	psj2:'Rum')
		])
	]),
	success_conditions([
		condition(true, [
			fact(Agens,   swc:has, 			  	Patiens)			% agens has the bottle
      ])
    ])
]).