% _______________________
% Action selection rules.
% _______________________


% --------------------------------------------------------------------
% Greet
% A character will greet.
% --------------------------------------------------------------------
action_selection_schema([
	type(red:'GreetRule'),
	arguments([ agens(Agens), patiens(Patiens) ]),
	preconditions([
		% One will greet if one hasn't yet greeted
		condition(false, [
			fact(Agens, red:greeted, Patiens)
		]),
		condition(false, [
			fact(Patiens, red:greeted, Agens)
		])
	]),
	selected_action(Act)
]) 	
	:-
action_schema(Act), 
schema_type(Act, red:'Greet'),
schema_agens(Act, Agens),
schema_patiens(Act, Patiens).	

% --------------------------------------------------------------------
% GreetBack
% A character will greet back if it has just been greeted.
% --------------------------------------------------------------------
action_selection_schema([
	type(red:'GreetBackRule'),
	arguments([ agens(Agens), target(Patiens) ]),
	preconditions([
		% Target just greeted me
		condition(true, [
			fact(Patiens, red:greeted, Agens)
		]),
		% One will greet back if one hasn't yet greeted back
		condition(false, [
			fact(Agens, red:greeted, Patiens)
		])
	]),
	selected_action(Act)
]) 	
	:-
action_schema(Act), 
schema_type(Act, red:'GreetBack'),
schema_agens(Act, Agens),
schema_patiens(Act, Patiens).	

% --------------------------------------------------------------------
% Cry
% A little girl will cry when something she owns has been taken from her.
% TODO: something like: when sadness > X (with decay). Now, Red keeps crying.
% For now, just do it once.
% --------------------------------------------------------------------
action_selection_schema([
	type(red:'CryRule'),
	arguments([ agens(Agens), patiens(Patiens)]),
	preconditions([
		% I am a little girl
		condition(true, [
			rule(Agens, owlr:typeOrSubType, red:'LittleGirl')
		]),
		% That hasn't cried yet
		condition(false, [
			fabula(I, rdf:type, red:'Cry'),
			fabula(I, fabula:agens, Agens),
			fabula(I, fabula:endtime, _)
		]),
		% I own something
		condition(true, [
			fact(Agens, swc:owns, Patiens)
		]),
		% But I don't have it now
		condition(false, [
			fact(Agens, swc:has, Patiens)
		]),
		% Because someone has taken it from me.
		condition(true, [
			fabula(Ind, rdf:type, red:'TakeFrom'),
			fabula(Ind, fabula:target, Agens),
			fabula(Ind, fabula:patiens, Patiens),
			fabula(Ind, fabula:endtime, _)	% Must have been successful!
		])
	]),
	selected_action(Act)
]) 	
	:-
action_schema(Act), 
schema_type(Act, red:'Cry'),
schema_agens(Act, Agens).	

% --------------------------------------------------------------------
% Angry
% Others will get angry when something they own has been taken from them.
% --------------------------------------------------------------------
action_selection_schema([
	type(red:'AngryRule'),
	arguments([ agens(Agens), target(Target)]),
	preconditions([

		% Haven't been angry yet
		condition(false, [
			fabula(I, rdf:type, red:'ExpressAnger'),
			fabula(I, fabula:agens, Agens),
			fabula(I, fabula:target, Target),
			fabula(I, fabula:endtime, _)
		]),
		% Because someone has taken something from me.
		condition(true, [
			fabula(Ind, rdf:type, red:'TakeFrom'),
			fabula(Ind, fabula:agens, Target),
			fabula(Ind, fabula:target, Agens),
			fabula(Ind, fabula:endtime, _)	% Must have been successful!
		])
	]),
	selected_action(Act)
]) 	
	:-
action_schema(Act), 
schema_type(Act, red:'ExpressAnger'),
schema_agens(Act, Agens),
schema_target(Act, Target).

% --------------------------------------------------------------------
% CommunicatePoison
% When adopting a "Poison" goal, one will tell.
% --------------------------------------------------------------------
action_selection_schema([
	type(red:'TellAboutPoisonPlanRule'),
	arguments([ agens(Agens)]),
	preconditions([
		% I'll do this whenever possible, but only once. 
		condition(false, [
			fabula(I, rdf:type, red:'TellAboutPoisonPlan'),
			fabula(I, fabula:agens, Agens)
		])
	]),
	selected_action(Act)
]) 	
	:-
action_schema(Act), 
schema_type(Act, red:'TellAboutPoisonPlan'),
schema_agens(Act, Agens).	

% --------------------------------------------------------------------
% LeaveGrandmaBinging
% When Grandma starts binging and baking cakes, leave her to it.
% --------------------------------------------------------------------
action_selection_schema([
	type(red:'LeaveBingingRule'),
	arguments([ agens(Agens), target(Target)]),
	preconditions([
		% I'll do this whenever possible, but only once to a person. 
		condition(false, [
			fabula(I, rdf:type, red:'LeaveBinging'),
			fabula(I, fabula:agens, Agens),
			fabula(I, fabula:target, Target)
		]),
		condition(true, [
			fabula(Act1, rdf:type, red:'Eat'),
			fabula(Act1, fabula:agens, Target),
			fabula(Act1, fabula:patiens, Cake1)
		]), 
		condition(true, [
			fabula(Act2, rdf:type, red:'Eat'),
			fabula(Act2, fabula:agens, Target),
			fabula(Act2, fabula:patiens, Cake2)
		]),
		condition(true, [
			rule(Cake1, owlr:isNot, Cake2)
		])		
	]),
	selected_action(Act)
]) 	
	:-
action_schema(Act), 
schema_type(Act, red:'LeaveBinging'),
schema_agens(Act, Agens),
schema_target(Act, Target).	
	
		
		