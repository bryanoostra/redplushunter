% action database for red
% @author swartjes
% @date 21 jan 2008


% AGENS toddles off to TARGET
action_schema([
	type(red:'SkipTo'),
    arguments([agens(Agens), target(Target)]),
	duration(1),
	preconditions([
	    condition(true, [
	    	%Thijs: everybody can skip when next line is commented out
	        fact(Agens, rdf:type, red:'LittleGirl'),
	        fact(Agens, swc:at, Location)
	    ]),
	    condition(true, [
	        rule(Path, owlr:typeOrSubType, swc:'Path')
	    ]),
	    condition(true, [
	        fact(Path, swc:from, Location),
	        fact(Path, swc:to, Target)
	    ])
	        
	]),
	effects([
		condition(true, [
		    fact(Agens,	swc:at, Target)
		 ]),
		 condition(false, [
            fact(Agens, swc:at, Location)
         ])
    ])
]).

% AGENS sneaks off to TARGET
action_schema([
	type(red:'SneakTo'),
    arguments([agens(red:wolf), target(Target)]),
	duration(1),
	preconditions([
	    condition(true, [
	    	fact(red:wolf, swc:at, Location)
	    ]),
	    condition(true, [
	        rule(Path, owlr:typeOrSubType, swc:'Path')
	    ]),
	    condition(true, [
	        fact(Path, swc:from, Location),
	        fact(Path, swc:to, Target)
	    ])
	        
	]),
	effects([
		condition(true, [
		    fact(red:wolf,	swc:at, Target)
		 ]),
		 condition(false, [
            fact(red:wolf, swc:at, Location)
         ])
    ])
]).

% AGENS toddles off to TARGET
action_schema([
	type(red:'ShuffleTo'),
    arguments([agens(red:grandma), target(Target)]),
	duration(1),
	preconditions([
		%Thijs: grandma should also be able to shuffle when she doesn't have the plan to poison (therefore commented out)
		%condition(true, [
		%	fabula(_, rdf:type, red:'Poison')
		%]),
	    condition(true, [
	        fact(red:grandma, swc:at, Location)
	    ]),
	    condition(true, [
	        rule(Path, owlr:typeOrSubType, swc:'Path')
	    ]),
	    condition(true, [
	        fact(Path, swc:from, Location),
	        fact(Path, swc:to, Target)
	    ])
	        
	]),
	effects([
		condition(true, [
		    fact(red:grandma,	swc:at, Target)
		 ]),
		 condition(false, [
            fact(red:grandma, swc:at, Location)
         ])
    ])
]).

% AGENS greets TARGET
action_schema([
	type(red:'Greet'),
	arguments([agens(Agens), patiens(Patiens)]),
	duration(1),
	preconditions([
		% Cannot greet yourself
		condition(true, [
			rule(Agens, owlr:isNot, Patiens),
	        rule(Location, owlr:typeOrSubType, swc:'Location')			
		]),
		% Must be at same location
		condition(true, [
            fact(Agens, swc:at, Location),
            fact(Patiens, swc:at, Location)
		]),
		%A first greeting can only be performed once
		condition(false, [
		 	fact(Agens, red:greeted, Patiens)
		]),
		%When somebody greeted you, you can only greet back
		condition(false, [
		 	fact(Patiens, red:greeted, Agens)
		])
	]),
	effects([
		condition(true, [
		 	fact(Agens, red:greeted, Patiens)
		])
	])
]).

% AGENS greets PATIENS back
action_schema([
	type(red:'GreetBack'),
	arguments([agens(Agens), patiens(Patiens)]),
	duration(1),
	preconditions([
		% Cannot greet yourself back
		condition(true, [
			rule(Agens, owlr:isNot, Patiens),
	        rule(Location, owlr:typeOrSubType, swc:'Location')			
		]),
		% Must be at same location
		condition(true, [
            fact(Agens, swc:at, Location),
            fact(Patiens, swc:at, Location)
		]),
		% Have been greeted by the other
		condition(true, [
		 	fact(Patiens, red:greeted, Agens)
		]),
		% Have not yet greeted yourself
		condition(false, [
		 	fact(Agens, red:greeted, Patiens)
		])		
	]),
	effects([
		condition(true, [
		 	fact(Agens, red:greeted, Patiens)
		])
	])
]).

% AGENS greets PATIENS again
action_schema([
	type(red:'GreetAgain'),
	arguments([agens(Agens), patiens(Patiens)]),
	duration(1),
	preconditions([
		% Cannot greet yourself again
		condition(true, [
			rule(Agens, owlr:isNot, Patiens),
	        rule(Location, owlr:typeOrSubType, swc:'Location')			
		]),
		% Must be at same location
		condition(true, [
            fact(Agens, swc:at, Location),
            fact(Patiens, swc:at, Location)
		]),
		% Greeted someone already
		condition(true, [
		 	fact(Agens, red:greeted, Patiens)
		])		
	]),
	effects([
		%greeted still true
		condition(true, [
		 	fact(Agens, red:greeted, Patiens)
		])
	])
]).

% AGENS gives PATIENS to TARGET
action_schema([
	type(red:'Give'),
    arguments([agens(Agens), patiens(Patiens), target(Target)]),
	duration(1),
	preconditions([
	    condition(true, [
	        rule(Agens, owlr:isNot, Target),
	        rule(Agens, owlr:typeOrSubType, swc:'Character'),
	        rule(Target, owlr:typeOrSubType, swc:'Character'),
	        rule(Loc, owlr:typeOrSubType, swc:'Location')	        
	    ]),
	    condition(true, [
	        fact(Agens, swc:at, Loc),
	        fact(Target, swc:at, Loc)	        
	    ]),
	    condition(true, [
	        fact(Agens, swc:has, Patiens)
	    ])
	]),
	effects([
		condition(true, [
		    fact(Target, swc:has, Patiens)
		 ]),
		 condition(true, [
		    fact(Target, swc:owns, Patiens)
		 ]),
		 condition(false, [
            fact(Agens, swc:has, Patiens)
         ]),
         condition(false, [
            fact(Agens, swc:owns, Patiens)
         ])
    ])
]).

% AGENS thanks TARGET for giving PATIENS
action_schema([
	type(red:'Thank'),
    arguments([agens(Agens), patiens(Patiens), target(Target)]),
	duration(1),
	preconditions([
	    condition(true, [
	        rule(Agens, owlr:isNot, Target),
	        rule(Agens, owlr:typeOrSubType, swc:'Character'),
	        rule(Target, owlr:typeOrSubType, swc:'Character'),
	        rule(Loc, owlr:typeOrSubType, swc:'Location')	        
	    ]),
	    condition(true, [
	        fact(Agens, swc:at, Loc),
	        fact(Target, swc:at, Loc)	        
	    ]),
	    condition(true, [
	        fact(Agens, swc:has, Patiens)
	    ]),
	    condition(true, [
			fabula(Act, rdf:type, red:'Give'),
			fabula(Act, fabula:agens, Target),
			fabula(Act, fabula:target, Agens),
			fabula(Act, fabula:patiens, Patiens)
		])
	]),
	effects([
	])
]).

% AGENS takes PATIENS from TARGET
action_schema([
	type(red:'TakeFrom'),
    arguments([agens(Agens), patiens(Patiens), target(Target)]),
	duration(1),
	preconditions([
		% Two different characters and a location
	    condition(true, [
	        rule(Agens, owlr:isNot, Target),
	        rule(Agens, owlr:typeOrSubType, swc:'Character'),
	        rule(Target, owlr:typeOrSubType, swc:'Character'),
	        rule(Loc, owlr:typeOrSubType, swc:'Location')
	    ]),  
	    % I am mean
	    condition(true, [
	    	fact(Agens, swc:hasAttribute, red:mean)
	    ]),	    
	    % Target has the thing
	    condition(true, [
	        fact(Target, swc:has, Patiens)
	    ]),
	    % I do not own the thing (otherwise, use TakeBack)
	    condition(false, [
	     	fact(Agens, swc:owns, Patiens)
	    ]),
	    % It is the first time Agens takes Patiens from Target (otherwise, use TakeAgain)
	    condition(false, [
			fabula(Act, rdf:type, red:'TakeFrom'),
			fabula(Act, fabula:agens, Agens),
			fabula(Act, fabula:target, Target),
			fabula(Act, fabula:patiens, Patiens)
		]),
  		% At the same location
	    condition(true, [
	        fact(Agens, swc:at, Loc),
	        fact(Target, swc:at, Loc)	        
	    ]) 
	        
	]),
	effects([
		% I have the thing
		condition(true, [
		    fact(Agens, swc:has, Patiens)
		 ]),
		 % Target no longer has the thing
		 condition(false, [
            fact(Target, swc:has, Patiens)
         ])
    ])
]).

% AGENS takes PATIENS from TARGET (when agens is owner)
action_schema([
	type(red:'TakeBack'),
    arguments([agens(Agens), patiens(Patiens), target(Target)]),
	duration(1),
	preconditions([
		% Two different characters and a location
	    condition(true, [
	        rule(Agens, owlr:isNot, Target),
	        rule(Agens, owlr:typeOrSubType, swc:'Character'),
	        rule(Target, owlr:typeOrSubType, swc:'Character'),
	        rule(Loc, owlr:typeOrSubType, swc:'Location')
	    ]),  
	    % Target has the thing
	    condition(true, [
	        fact(Target, swc:has, Patiens)
	    ]),
	    % I do own the thing
	    condition(true, [
	     	fact(Agens, swc:owns, Patiens)
	    ]),
	    % At the same location
	    condition(true, [
	        fact(Agens, swc:at, Loc),
	        fact(Target, swc:at, Loc)	        
	    ]) 
	        
	]),
	effects([
		% I have the thing
		condition(true, [
		    fact(Agens, swc:has, Patiens)
		 ]),
		 % Target no longer has the thing
		 condition(false, [
            fact(Target, swc:has, Patiens)
         ])
    ])
]).

% AGENS takes PATIENS from TARGET (when it's not the first time Agens takes Patiens from Target)
action_schema([
	type(red:'TakeAgain'),
    arguments([agens(Agens), patiens(Patiens), target(Target)]),
	duration(1),
	preconditions([
		% Two different characters and a location
	    condition(true, [
	        rule(Agens, owlr:isNot, Target),
	        rule(Agens, owlr:typeOrSubType, swc:'Character'),
	        rule(Target, owlr:typeOrSubType, swc:'Character'),
	        rule(Loc, owlr:typeOrSubType, swc:'Location')
	    ]),  
	    % Target has the thing
	    condition(true, [
	        fact(Target, swc:has, Patiens)
	    ]),
	    % I do own the thing
	    %condition(true, [
	    % 	fact(Agens, swc:owns, Patiens)
	    %]),
	    % It is not the first time Agens takes Patiens from Target
	    condition(true, [
			fabula(Act, rdf:type, red:'TakeFrom'),
			fabula(Act, fabula:agens, Agens),
			fabula(Act, fabula:target, Target),
			fabula(Act, fabula:patiens, Patiens)
		]),
  		% At the same location
	    condition(true, [
	        fact(Agens, swc:at, Loc),
	        fact(Target, swc:at, Loc)	        
	    ]) 
	        
	]),
	effects([
		% I have the thing
		condition(true, [
		    fact(Agens, swc:has, Patiens)
		 ]),
		 % Target no longer has the thing
		 condition(false, [
            fact(Target, swc:has, Patiens)
         ])
    ])
]).

% AGENS eats TARGET
action_schema([
	type(red:'Eat'),
	arguments([agens(Agens), patiens(Patiens)]),
	duration(1),
	preconditions([
		% Theres a character and food
		condition(true, [
			rule(Agens, owlr:typeOrSubType, swc:'Character'),
			rule(Patiens, owlr:typeOrSubType, red:'Food')
		]),
		% Must have the food
		condition(true, [
            fact(Agens, swc:has, Patiens)
		])
	]),
	effects([
		% No longer hungry
		condition(false, [
		 	fact(Agens, swc:hasAttribute, red:hungry)
		]),
		% Food no longer at eater
		condition(false, [
			fact(Agens, swc:has, Patiens)
		])
	])
]).

% AGENS cries
action_schema([
	type(red:'Cry'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% One character
		condition(true, [
			rule(Agens, owlr:typeOrSubType, swc:'Character')
		])
	]),
	effects([
	])
]).

% AGENS laughs
action_schema([
	type(red:'Laugh'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% One character
		condition(true, [
			rule(Agens, owlr:typeOrSubType, swc:'Character')
		])
	]),
	effects([
	])
]).

% AGENS expresses anger
action_schema([
	type(red:'ExpressAnger'),
	arguments([agens(Agens), target(Target)]),
	duration(1),
	preconditions([
		% One character
		condition(true, [
			rule(Agens, owlr:typeOrSubType, swc:'Character'),
			rule(Target, owlr:typeOrSubType, swc:'Character')
		]),
		% Cannot tell yourself
		condition(true, [
			rule(Agens, owlr:isNot, Target)			
		])
	]),
	effects([
	])
]).

% AGENS expresses sympathy
action_schema([
	type(red:'ExpressSympathy'),
	arguments([agens(Agens), target(Target)]),
	duration(1),
	preconditions([
		% Two characters
		condition(true, [
			rule(Agens, owlr:typeOrSubType, swc:'Character'),
			rule(Target, owlr:typeOrSubType, swc:'Character')
		]),
		% At same location
		condition(true, [
			fact(Agens, swc:at, Loc),
			fact(Target, swc:at, Loc)
		]),
		% Target told what happened
		condition(true, [
			fact(Target, red:told_what_happened, Agens)
		]),
		% Target not yet asked what to do
		condition(false, [
			fact(Agens, red:asked_what_to_do, Target)
		]),
		% Did not yet express sympathy
		condition(false, [
			fabula(Act, rdf:type, red:'ExpressSympathy'),
			fabula(Act, fabula:agens, Agens),
			fabula(Act, fabula:target, Target)
		]),
		% Cannot tell yourself
		condition(true, [
			rule(Agens, owlr:isNot, Target)			
		])
	]),
	effects([
	])
]).

% AGENS asks PATIENS what to do
action_schema([
	type(red:'WhatToDo'),
	arguments([agens(Agens), patiens(Patiens)]),
	duration(1),
	preconditions([
		% Two characters
		condition(true, [	
			rule(Agens, owlr:typeOrSubType, swc:'Character'),
			rule(Patiens, owlr:typeOrSubType, swc:'Character')			
		]),
		% At same location
		condition(true, [
			fact(Agens, swc:at, Loc),
			fact(Patiens, swc:at, Loc)
		]),
		% You're not just going to say "what to do?". Have to have told what happened.
		condition(true, [
			fact(Agens, red:told_what_happened, Patiens)
		]),
		% You're not going to ask multiple times
		condition(false, [
			fact(Agens, red:asked_what_to_do, Patiens)
		])
	]),
	effects([
		condition(true, [
			fact(Agens, red:asked_what_to_do, Patiens)
		])
	])
]).

% AGENS tells about the wolf taking something
% AGENS tells TARGET that INSTRUMENT took PATIENS (this is not a correct representation, but we don't support more complex sentences at this point)
action_schema([
	type(red:'TellSomeoneTookSomething'),
	arguments([agens(Agens), patiens(Patiens), target(Target), instrument(MeanOne)]),
	duration(1),
	preconditions([
		% Two characters
		condition(true, [	
			rule(Agens, owlr:typeOrSubType, swc:'Character'),
			rule(Target, owlr:typeOrSubType, swc:'Character')			
		]),
		% Cannot tell yourself
		condition(true, [
			rule(Agens, owlr:isNot, Target)			
		]),
		% Cannot tell the character that did it == MeanOne
		condition(true, [
			rule(Target, owlr:isNot, MeanOne)			
		]),
		% Cannot tell multiple times
		condition(false, [
			fact(Agens, red:told_what_happened, Target)
		]),
		% At same location
		condition(true, [
			fact(Agens, swc:at, Loc),
			fact(Target, swc:at, Loc)
		]),
		% The wolf really DID take something
		condition(true, [
			fabula(Act, rdf:type, red:'TakeFrom'),
			fabula(Act, fabula:agens, MeanOne),
			fabula(Act, fabula:patiens, Patiens),
			fabula(Act, fabula:target, Agens)
		])
	]),
	effects([
		condition(true, [
			fact(Agens, red:told_what_happened, Target)
		])
	])
]).

% AGENS bakes a cake
action_schema([
	type(red:'BakeCake'),
	arguments([agens(red:grandma), patiens(Patiens)]),
	%We want instant feedback on user actions. 
	%Easiest way to accomplish this is to set all durations to 1.
	duration(1),
	preconditions([
		% Unbaked
		condition(true, [
			fact(Patiens, swc:hasAttribute, red:unbaked)
		]),
		% Grandma is at home
		condition(true, [
			fact(red:grandma, swc:at, red:grandmas_house)
		]),	
		% Patiens is a cake
		condition(true, [
			rule(Patiens, owlr:typeOrSubType, red:'Cake')
		]),
		% But not located anywhere yet
		condition(false, [
			fact(Patiens, swc:at, _)
		])

	]),
	effects([
		% Agens has (baked) the cake
		condition(true, [
			fact(red:grandma, swc:has, Patiens)
		]),
		% Agens owns the cake
		condition(true, [
			fact(red:grandma, swc:owns, Patiens)
		])
		% No longer an unbaked cake
		%condition(false, [
		%	fact(Patiens, swc:hasAttribute, red:unbaked)
		%])
	])
]).

% allright, red:red can also bake cakes in Grandma's house
% AGENS bakes a cake
action_schema([
	type(red:'BakeCake'),
	arguments([agens(red:red), patiens(Patiens)]),
	%We want instant feedback on user actions. 
	%Easiest way to accomplish this is to set all durations to 1.
	duration(1),
	preconditions([
		% Unbaked
		condition(true, [
			fact(Patiens, swc:hasAttribute, red:unbaked)
		]),
		% Grandma is at home
		condition(true, [
			fact(red:red, swc:at, red:grandmas_house)
		]),	
		% Patiens is a cake
		condition(true, [
			rule(Patiens, owlr:typeOrSubType, red:'Cake')
		]),
		% But not located anywhere yet
		condition(false, [
			fact(Patiens, swc:at, _)
		])

	]),
	effects([
		% Agens has (baked) the cake
		condition(true, [
			fact(red:red, swc:has, Patiens)
		]),
		% Agens owns the cake
		condition(true, [
			fact(red:red, swc:owns, Patiens)
		]),	
		% No longer an unbaked cake
		condition(false, [
			fact(Patiens, swc:hasAttribute, red:unbaked)
		])
	])
]).

% AGENS poisons food
action_schema([
	type(red:'PoisonFood'),
	arguments([agens(Agens), patiens(Patiens)]),
	duration(1),
	preconditions([
		% Patiens is food
		condition(true, [
			rule(Patiens, owlr:typeOrSubType, red:'Food')
		]),
		% I have the food
		condition(true, [
			fact(Agens, swc:has, Patiens)
		]),
		% Food is not yet poisoned
		condition(false, [
			fact(Patiens, swc:hasAttribute, red:poisoned)
		]),
		% I am at grandma's house
		condition(true, [
			fact(Agens, swc:at, red:grandmas_house)
		])
	]),
	effects([
		% Food is poisoned
		condition(true, [
			fact(Patiens, swc:hasAttribute, red:poisoned)
		])
	])
]).

% Speech act for communicating "Poison" goal.
% For agents WITH a Poison goal.
% AGENS tells TARGET that he/she wants to poison PATIENS
action_schema([
	type(red:'TellAboutPoisonPlan'),
	arguments([agens(Agens), patiens(Patiens), target(Target)]),
	duration(1),
	preconditions([
		% I have a poison goal.
		condition(true, [
			fabula(G, rdf:type, red:'Poison'),
			fabula(G, fabula:agens, Agens),
			fabula(G, fabula:patiens, Patiens),
			%The poison goal can maybe also be without a target? in that case comment out:
			fabula(G, fabula:target, Target)
		]),
		% Cannot tell yourself
		condition(true, [
			rule(Agens, owlr:isNot, Target)			
		]),
		% Cannot tell the character that did it == MeanOne
		condition(true, [
			rule(Target, owlr:isNot, Patiens)			
		]),
		% You're not going to tell multiple times
		condition(false, [
			fact(Agens, red:told_about_poison_plan, Target)
		]),
		% We are co-located.
		condition(true, [
			fact(Agens, swc:at, Loc),
			fact(Target, swc:at, Loc)
		])
	]),
	effects([
		condition(true, [
			fact(Agens, red:told_about_poison_plan, Target)
		])
	])
]).

% Speech act for communicating "Poison" goal.
% For agents WITHOUT a Poison goal.
% AGENS tells TARGET that he/she wants to poison PATIENS
action_schema([
	type(red:'TellAboutPoisonPlan'),
	arguments([agens(Agens), patiens(Patiens), target(Target)]),
	duration(1),
	preconditions([
		% Cannot tell yourself
		condition(true, [
			rule(Agens, owlr:isNot, Target)			
		]),
		% Cannot tell the character that did it == MeanOne
		condition(true, [
			rule(Target, owlr:isNot, Patiens)			
		]),
		% I have a poison goal.
		% But human controlled characters don't have goals!
		% So comment out:
		%condition(true, [
		%	fabula(G, rdf:type, red:'Poison'),
		%	fabula(G, fabula:agens, Agens),
		%	fabula(G, fabula:patiens, Patiens),
		%	fabula(G, fabula:target, Target)
		%]),
		% Target just asked me for advice
		condition(true, [
			fabula(Act, rdf:type, red:'WhatToDo'),
			fabula(Act, fabula:agens, Target),
			fabula(Act, fabula:patiens, Agens)
		]),
		% Patiens took something from Target
		condition(true, [
			fabula(Act2, rdf:type, red:'TakeFrom'),
			fabula(Act2, fabula:agens, Patiens),
			fabula(Act2, fabula:target, Target)
		]),
		% You're not going to tell multiple times
		condition(false, [
			fact(Agens, red:told_about_poison_plan, Target)
		]),
		% We are co-located.
		condition(true, [
			fact(Agens, swc:at, Loc),
			fact(Target, swc:at, Loc)
		])
	]),
	effects([
		condition(true, [
			fact(Agens, red:told_about_poison_plan, Target)
		])
	])
]).

% AGENS leaves PATIENS binging
action_schema([
	type(red:'LeaveBinging'),
	arguments([agens(Agens), patiens(Patiens)]),
	duration(1),
	preconditions([
		% Cannot leave yourself binging
		condition(true, [
			rule(Agens, owlr:isNot, Patiens),
	        rule(Location, owlr:typeOrSubType, swc:'Location')			
		]),
		% Must be at same location
		condition(true, [
            fact(Agens, swc:at, Location),
            fact(Patiens, swc:at, Location)
		])
	]),
	effects([
		condition(true, [
		])
	])
]).


%%%%% additional actions to spice things up a bit and to increase the number of actions for users %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% AGENS watches birds 
action_schema([
	type(red:'WatchBirds'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% Agens is in forest
		condition(true, [
			fact(Agens, swc:at, red:forest)
		])
	]),
	effects([	])
]).

% AGENS watches clouds 
action_schema([
	type(red:'WatchClouds'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% Agens is in forest
		condition(true, [
			fact(Agens, swc:at, red:forest)
		])
	]),
	effects([	])
]).

% AGENS builds a sandcastle 
action_schema([
	type(red:'BuildSandCastle'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% Agens is on beach
		condition(true, [
			fact(Agens, swc:at, red:beach)
		])
	]),
	effects([	])
]).

% AGENS rolls in the sand 
action_schema([
	type(red:'RollSand'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% Agens is on beach
		condition(true, [
			fact(Agens, swc:at, red:beach)
		])
	]),
	effects([	])
]).

% AGENS bathing in the sun
action_schema([
	type(red:'EnjoySun'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% Agens is on beach
		condition(true, [
			fact(Agens, swc:at, red:beach)
		])
	]),
	effects([	])
]).

% AGENS takes an nap
action_schema([
	type(red:'TakeNap'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% Agens is on beach
		condition(true, [
			fact(Agens, swc:at, red:beach)
		])
	]),
	effects([	])
]).

% AGENS dive
action_schema([
	type(red:'Dive'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% Agens is at lake
		condition(true, [
			fact(Agens, swc:at, red:lake)
		])
	]),
	effects([	])
]).

% AGENS treads water
action_schema([
	type(red:'TreadWater'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% Agens is at lake
		condition(true, [
			fact(Agens, swc:at, red:lake)
		])
	]),
	effects([	])
]).

% AGENS farts (for the first time)
action_schema([
	type(red:'Fart'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% Agens is at lake
		condition(true, [
			fact(Agens, swc:at, red:lake)
		]),
		condition(false, [
			fact(Agens, swc:hasAttribute, red:farted1)
		])
	]),
	effects([
		condition(true, [
			fact(Agens, swc:hasAttribute, red:farted1)
		])
	])
]).

% AGENS farts farts for 2nd time
action_schema([
	type(red:'Fart2'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% Agens is at lake
		condition(true, [
			fact(Agens, swc:at, red:lake)
		]),
		% Agens farted once
		condition(true, [
			fact(Agens, swc:hasAttribute, red:farted1)
		]),
		condition(false, [
			fact(Agens, swc:hasAttribute, red:farted2)
		])
	]),
	effects([
		condition(true, [
			fact(Agens, swc:hasAttribute, red:farted2)
		])
	])
]).

% AGENS farts for 3rd time
action_schema([
	type(red:'Fart3'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% Agens is at lake
		condition(true, [
			fact(Agens, swc:at, red:lake)
		]),
		% Agens farted twice
		condition(true, [
			fact(Agens, swc:hasAttribute, red:farted1),
			fact(Agens, swc:hasAttribute, red:farted2)
		]),
		condition(false, [
			fact(Agens, swc:hasAttribute, red:farted3)
		])
	]),
	effects([
		condition(true, [
			fact(Agens, swc:hasAttribute, red:farted3)
		])
	])
]).

% AGENS dust cupboard
action_schema([
	type(red:'Dust'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% Agens is in a house
		condition(true, [
			fact(Agens, swc:at, red:reds_house)
		])
	]),
	effects([	])
]).

% AGENS washes dishes
action_schema([
	type(red:'WashDishes'),
	arguments([agens(Agens)]),
	duration(1),
	preconditions([
		% Agens is in a house
		condition(true, [
			fact(Agens, swc:at, red:reds_house)
		])
	]),
	effects([	])
]).
