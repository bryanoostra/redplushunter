% ---------------------------------------------------------------------------------
% Copyright (C) 2008 Human Media Interaction - University of Twente
%  
% This file is part of The Virtual Storyteller.
% 
% The Virtual Storyteller is free software: you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation, either version 3 of the License, or
% (at your option) any later version.
% 
% The Virtual Storyteller is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
% 
% You should have received a copy of the GNU General Public License
% along with The Virtual Storyteller. If not, see <http://www.gnu.org/licenses/>.
% ---------------------------------------------------------------------------------

%%%
% Basic Character Agent Prolog functions
% @author Edze Kruizinga
% 08-09-2006 - 15-06-2007

:- module(basicCharacterAgent,
	[ setAgentID/1,					% +AgentID
	  hasAction/2,					% +Agens, -Actions
%JB:   
	  set_interest_desire/2,	% +MotivationName, +Strength
	  character_motivations/2,		% +Agens, -Motivations
	  motivation_strength/2,		% +MotivationName, -Strength
	  interest_desire/2,			% +InterestName, -Desire
%JB	  
	  canDo/3,						% +Agens, +Action, -ActionHead
%%%%%%%%% Start Thijs %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	  possible_action/2,			% +Agens, -Action
	  possible_action/3,			% +Agens, -Action, -Type
   	  possible_transitmove_action/3,% +Agens, -Target, -Action
%	  possible_destination/2,		% +Agens, +Target
	  possible_improvisation/1, 	% -Framing
	  locat/1, 						% -Location
  	  charact/1, 					% -Character
%%%%%%%%% End Thijs %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	  
	  goal_intention/2,				% +GoalSchema, -Intentions
%	  goal_motivation/2,			% +GoalSchema, -MotivatingIndividual
%	  goal_success_cause/2,			% +GoalSchema, -CausingIndividual
%	  goal_failure_cause/2,			% +GoalSchema, -CausingIndividual	  
%	  schema_enablement/2,			% +Schema, -EnablingIndividual
%	  schema_enablement/5,			% +Schema, -Truth, -Subj, -Pred, -Obj
	  test_enforce_schema_conditions/1, % +Schema
	  test_execute_plan/1,			% +Plan
	  agent/1
	]).
	
agent(character).	

	% RDF meta predicates, for term expansion at compile time
:- rdf_meta 
	canDo(r, r, -),
	query(r, r, o),
	rule(r, r, o),
	fact(r, r, o),
	fabula(r, r, o),
	fabula(r, r, o, r).

% setAgentID/1 +AgentID
% add a belief to the KB so that the agent knows who it is, remove others
% Swartjes: where is this used? I don't think it is currently used anywhere. Character Agent remembers it Java-side.
setAgentID(Agens) :-
	rdfRetract((myself, iam, _)),
	rdfAssert((myself, iam, Agens)).	


%% hasActions
%% Retrieve the actions AgentID has
%%
hasAction(Agens, ActionName) :-
	query(Agens, swc:'hasAction', ActionName).

%JB
set_interest_desire(InterestName, Desire) :-
	query(rule(InterestName, owlr:typeOrSubType, swc:'Interest')),
	!,
	rdfRetract((InterestName, swc:'hasDesire', _)),
	rdfAssert((InterestName, swc:'hasDesire', Desire)).

	
	
%JB
motivation_strength(MotivationName, Strength) :-
	query(MotivationName, swc:'hasStrength', literal(type(_, Strength))).
%	atom_number(X, Strength).

%JB
interest_desire(InterestName, Desire) :-
	query(InterestName, swc:'hasDesire', literal(type(_, Desire))).

%JB
character_motivations(Agens, MotivationName) :-
	query(Agens, swc:'hasInterest', MotivationName).

character_motivations(Agens, MotivationName) :-
	query(Agens, swc:'hasPersonalityTrait', MotivationName).
	

actionSubAction(ActionName, SubActionName) :-
	query(SubActionName, rdfs:subClassOf, ActionName).

hasSubAction(Agens, SubActionName) :-
	hasAction(Agens, ActionName),
	actionSubAction(ActionName, SubActionName).

/*
canDo/3     + Agens ? ActionName - NewOperator
    Is true when an operator can be found, given an agens and the name (type) of an action, 
    that is possible to execute.
*/
canDo(Agens, ActionName, NewOperator) :-
    action_schema(NewOperator),
	schema_type(NewOperator, ActionName), 
	schema_agens(NewOperator, Agens),
	validate_schema(NewOperator, []).

%%%%%%%%% Start Thijs %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% possible_action/2, +Agens, -Action
% used for instance to get all possible actions for a given character
possible_action(Agens, Action) :-
    action_schema(Action),
	schema_agens(Action, Agens),
	validate_schema(Action, []).
	
% possible_action/2, +Agens, -Action
% used for instance to get all possible actions for a given character
possible_action(_, Action) :-
   (event_schema(Action) ; framing_schema(Action) ),
	validate_schema(Action, []).	

% possible_action/3, +Agens, -Action, -Type
% used for instance to get all possible actions (with) for a given character
possible_action(Agens, Action, Type) :-
    action_schema(Action),
	schema_agens(Action, Agens),
	schema_type(Action, Type),
	validate_schema(Action, []).
	
% possible_action/3, +Agens, -Action, -Type
% used for instance to get all possible actions (with) for a given character
possible_action(_, Action, Type) :-
   (event_schema(Action) ; framing_schema(Action) ),
	schema_type(Action, Type),
	validate_schema(Action, []).
	
	
% DOMAINTOGGLE
		
% possible_transitmove_action/3, +Agens, -Target, -Action
% used get the possible transitMove actions of specified agent to a chosen target destination
possible_transitmove_action(Agens, Target, Action) :-
	action_schema(Action),
	possible_action(Agens, Action),
	schema_target(Action, Target),
	schema_type(Action, Type),
	query(rule(Type, owlr:'classOrSubClassOf', commit:'GroundMove')).
	
possible_transitmove_action(Agens, Target, Action) :-
	action_schema(Action),
	possible_action(Agens, Action),
	schema_target(Action, Target),
	schema_type(Action, Type),
	query(rule(Type, owlr:'classOrSubClassOf', fabula:'TransitMove')).

 %%
 % Jeroen:
 % possible_transitmove_action/3 is hardcoded again... We want a general form this!
 % Let's hardcode it to our convenience.
 % Original query:
 % query(rule(Type, owlr:'classOrSubClassOf', fabula:'TransitMove')).
 % Our query:
 % query(rule(Type, owlr:'classOrSubClassOf', commit:'GroundMove')).
%%

% not used anymore, but keep for now
% possible_destination/2, +Agens, +Target
% used to check if a chosen target destination is valid for human character agent (during drag/drop)
%possible_destination(Agens, Target) :-
%	possible_action(Agens, Action),
%	schema_target(Action, Target),
%	schema_type(Action, Type),
%	query(rule(Type, owlr:'classOrSubClassOf', fabula:'TransitMove'))
%true negative:	 query(rule(red:'Greet', owlr:'classOrSubClassOf', fabula:'TransitMove'))
%true positive:	 query(rule(red:'SkipTo', owlr:'classOrSubClassOf', fabula:'TransitMove'))
%false negative: query(rule(Action, owlr:'classOrSubClassOf', fabula:'TransitMove'))
%query(rule(Action, owlr:'typeOrSubType', fabula:'TransitMove'))
%query(rule(red:'SkipTo', owlr:'typeOrSubType', fabula:'TransitMove'))
%	query(rule(Action, rdfs:subClassOf, fabula:'TransitMove'))	
%	Action = action('http://www.owl-ontologies.com/FabulaKnowledge.owl#TransitMove', (AgentID, Agens, Patiens, Target, Instrument, [Vars, CurrLoc]))
%.

% possible_improvisation/1, -Framing
% used for instance to get all possible framing operators
possible_improvisation(Framing) :-
    framing_schema(Framing),
    %check Agens!
	validate_schema(Framing, []).
	
% NOT OPERATIONAL!
% locat/1, -Location
% used for instance to get all existing locations
locat(L) :-
	query(L, owlr:'typeOrSubType', swc:'Location').
%	thread_schema(S),
%    memberchk(settings(Settings), S),
%    member(Cond, Settings).
%	nonvar(S),
%	validate_schema(S, []),
%	memberchk(location(L), S).
%	member(location(L), S).
	
% RESULT NOT SATISFIABLE!
% charact/1, -Character
% used for instance to get all existing characters
charact(C) :-
	thread_schema(S),
	nonvar(S),
	validate_schema(S, []),
	memberchk(characters(Chars), S),
	member(character(C), Chars).
%	member(character(Agens), Chars),
%	character(Agens),
%	nonvar(Agens),
%	query(Agens, owlr:'typeOrSubType', swc:'Character').

%%%%%%%%% End Thijs %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Retrieve which goals are possible for given agent	
%possible_goal(Agens, Goal) :-
%	goal_schema(Goal)
%,	schema_agens(Goal, Agens)
%,	validate_schema(Goal, []).


% Convert a goal to intentions for the planner, by taking its success conditions.		
goal_intention(GoalSchema, Intention) :-
	validate_schema(GoalSchema, [])
,	goal_success_conditions(GoalSchema, Intention)
,	!
.

goal_intention(_, []).

% Retrieves Individuals that describe fabula elements that motivate given schema
% DEPRECATED: USE fabula MODULE
goal_motivation(Schema, Individual) :-
	% Only works for goal schemas 
	goal_schema(Schema)
,	schema_preconditions(Schema, Precs)					% -> e.g. [ condition(..), condition(..), ...]
,	member(Prec, Precs)									% -> e.g. condition(true, [...])
,	condition_member(EnablingCondition, Prec)			% -> e.g. condition(true, fabula(...))
,	condition_to_triple(EnablingCondition, true, Individual, _, _)	% The condition is talking about an Individual (and in context of true facts)
,	query(rule(Individual, owlr:typeOrSubType, fabula:'Goal'))	% and this Individual should be a Goal 

%,	query(fabula(S, P, O, G)) 							% S might i
,	query(Individual, fabula:character, Agens)			% Agens of schema should be equal to character of enabler.
,	schema_agens(Schema, Agens)
.

% Retrieves Individuals that describe fabula elements that enable given schema
% ASSUMPTION is that each Individual has a fabula:time logged in the KB. Otherwise, enablements cannot be found.
% DEPRECATED: USE fabula MODULE
schema_enablement(Schema, Individual) :-
	(	% Only works for goal schemas and action schemas
		goal_schema(Schema)
	;	action_schema(Schema)
	)
,	schema_agens(Schema, Agens)
,	schema_preconditions(Schema, Precs)					% -> e.g. [ condition(..), condition(..), ...]
,	establishes_conditions(Precs, Agens, Individual)
% And there is no later element that causes the schema (in other words, take the latest).
,	latest(Precs, Agens, Individual)
.

% Determine the IC causing success for a given goal that was finished.
% DEPRECATED: USE fabula MODULE
goal_success_cause(GoalSchema, Individual) :-
	goal_schema(GoalSchema)		% Only works for goals
,	schema_agens(GoalSchema, Agens)
,	goal_success_conditions(GoalSchema, SC)
,	establishes_conditions(SC, Agens, Individual)
% And there is no later element that causes the schema (in other words, take the latest).
,	latest(SC, Agens, Individual)
.	

% Determine the IC causing success for a given goal that was finished.
% DEPRECATED: USE fabula MODULE
goal_failure_cause(GoalSchema, Individual) :-
	goal_schema(GoalSchema)		% Only works for goals
,	schema_agens(GoalSchema, Agens)
,	goal_failure_conditions(GoalSchema, FC)
,	establishes_conditions(FC, Agens, Individual)
% And there is no later element that causes the schema (in other words, take the latest).
,	latest(FC, Agens, Individual)
.

% Sees whether certain conditions are established by the description of a certain individual for a certain agens.
establishes_conditions(Conditions, Agens, Individual) :-
	member(Prec, Conditions)									% -> e.g. condition(true, [...])
,	condition_member(CausingCondition, Prec)			% -> e.g. condition(true, fact(...))
,	condition_to_triple(CausingCondition, Truth, S, P, O)
,	query(fabula(S, P, O, G))							% There is a quad in the fabula in graph G
,	query(Individual, fabula:hasContent, G)				% Individual yields the enabling element itself, and its content is G
,	query(rule(Individual, owlr:typeOrSubType, fabula:'InternalElement'))	% Individual should be an internal element (not a perception)
,	query(Individual, fabula:character, Agens)			% Agens of schema should be equal to character of enabler.
,	graph_truth(G, Truth)
.
	

% Determines the truth of the graph
graph_truth(GraphInd, true) :-
	query(rule(GraphInd, owlr:typeOrSubType, fabula:'TruthGraph'))
,	!
.

graph_truth(GraphInd, false) :-
	query(rule(GraphInd, owlr:typeOrSubType, fabula:'FalsehoodGraph'))
,	!
.

% See if individual is the latest enabler
latest(Conditions, Agens, Individual) :-
	query(Individual, fabula:time, Time)
,	query(SomeOtherIndividual, fabula:time, SomeOtherTime)
,	Individual \= SomeOtherIndividual
,	\+ (
		later(SomeOtherTime, Time)
	,	establishes_conditions(Conditions, Agens, SomeOtherIndividual)
	)
.

% Also true if cannot parse time.
latest(_, _, Individual) :-
	query(Individual, fabula:time, Time)
,	Time \= literal(type('http://www.w3.org/2001/XMLSchema#int', _))
,   !
.


% Test whether Time 1 is later than Time 2. Both times must be represented as xsd:ints, 
later(Time1, Time2) :-
	Time1 = literal(type('http://www.w3.org/2001/XMLSchema#int', Val1))
,	Time2 = literal(type('http://www.w3.org/2001/XMLSchema#int', Val2))
,	Val1 > Val2
.
	

% Retrieves conditions of character beliefs that fulfil preconditions of schema
% This equates to the preconditions of the schema that are true in the current state of the world
% Current implementation only deals with facts (so this excludes: "johns belief that pete just asked him a question, enabled him to answer
%	however, if this is desired it is easily added (condition_to_quad).
schema_enablement(Schema, Truth, S, P, O) :-
	schema_preconditions(Schema, Precs)					% -> e.g. [ condition(..), condition(..), ...]
,	member(Prec, Precs)									% -> e.g. condition(true, [...])
,	condition_member(EnablingCondition, Prec)			% -> e.g. condition(true, fact(...))
,	validate_condition(EnablingCondition)
,	condition_to_triple(EnablingCondition, Truth, S, P, O)
.

% Maps a condition term to its truth, subject, predicate and object
condition_to_triple(Condition, Truth, S, P, O) :-
	Condition = condition(Truth, Fact)
,	Fact = fact(S, P, O)
,	!
. 
	
condition_to_triple(Condition, Truth, S, P, O) :-
	Condition = condition(Truth, Fact)
,	Fact = fabula(S, P, O)
,	!
. 	

condition_to_triple(Condition, Truth, S, P, O) :-
	Condition = condition(Truth, Fact)
,	Fact = fabula(S, P, O, _)   % Assumption: graph that condition talks about is not relevant for use of condition_to_triple
,	!
. 	
	
% Make sure that preconditions of schema hold, if at all possible
test_enforce_schema_conditions(S) :-
	schema(S),
	validate_schema(S, []).
	
test_enforce_schema_conditions(S) :-
	schema_agens(S, Agens),
	nonvar(Agens),
	possible_goal_after_plan(Agens, S, Plan),
	test_execute_plan(Plan).
	
test_execute_plan(Plan) :-
	orderedSteps(Plan, Steps),
	!,
	execute_steps(Steps).
	
execute_steps([]).

execute_steps([S1 | S]) :-
	S1 = (_, Op),
	apply_operator_effects(Op),
	execute_steps(S).
		
orderedSteps((Steps, Orderings, _, _), Stps) :- 
    s_sort(Orderings, Steps, [], Stps).
    
s_sort(_,[],Acc,Acc).

s_sort(Orderings, [H|T],Acc, Sorted) :-
    insert(Orderings, H,Acc,NAcc), 
    s_sort(Orderings,T,NAcc,Sorted).
    
insert(Orderings, X,[Y|T],[Y|NT]):-
	\+ memberchk((X,Y), Orderings),
	insert(Orderings, X, T, NT).

insert(Orderings,X,[Y|T],[X,Y|T]):-
	memberchk((X,Y), Orderings).

insert(_,X,[],[X]).   	
			
	
	
