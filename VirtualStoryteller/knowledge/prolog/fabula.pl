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

% ----------------------------------------------------------------------
% Fabula module
% Provides search predicates for constructing fabula
%
% ----------------------------------------------------------------------

:- module(fabula,
	[	
		setting_enables_event/2,
		schema_enablement/2,
		schema_motivation/2,
		goal_success_cause/2,
		goal_failure_cause/2,
		action_rule_cause/2,
		goal_rule_cause/2,
		internal_element_cause/2
	]).

% Modules
:- use_module(library(semweb/rdf_db)).
:- use_module('knowledgebase.pl').
:- use_module('schema_management.pl').

:- rdf_meta
	establishes_conditions(t, r, r, r)
,	establishes_conditions(t, r, r)
,	latest(t, r, r, r)
.


% Retrieves Individuals that describe fabula elements that motivate given schema
% Finds G  -m->  G
% Finds IE -m->  A
schema_motivation(Schema, Individual) :-
	% Only works for goal schemas 
	(	
		goal_schema(Schema)
	;	action_selection_schema(Schema)
	)
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
% Finds IE  -e->  G
% Finds IE  -e->  A
schema_enablement(Schema, Individual) :-
	(	% Only works for goal schemas and action schemas
		goal_schema(Schema)
	;	action_schema(Schema)
	)
,	schema_agens(Schema, Agens)
,	schema_preconditions(Schema, Precs)					% -> e.g. [ condition(..), condition(..), ...]
,	establishes_conditions(Precs, Agens, Individual, fabula:'InternalElement')
% And there is no later element that causes the schema (in other words, take the latest).
,	latest(Precs, Agens, Individual, fabula:'InternalElement')
.

% Determine the IC causing success for a given goal that was finished.
% Finds IE  -psi->  O+  ( -res->  G )
goal_success_cause(GoalSchema, Individual) :-
	goal_schema(GoalSchema)		% Only works for goals
,	schema_agens(GoalSchema, Agens)
,	schemas:validate_goal_success_conditions(GoalSchema)	% Make sure all variables get instantiated in one way that the conditions are established.
,	! 				% Cut, to prevent finding other ways to establish success conditions (and hence, other causalities)
,	goal_success_conditions(GoalSchema, SC)
,	establishes_conditions(SC, Agens, Individual, fabula:'InternalElement')
% And there is no later element that causes the schema (in other words, take the latest).
,	latest(SC, Agens, Individual, fabula:'InternalElement')
.	

% Determine the IC causing success for a given goal that was finished.
% Finds IE  -psi->  O-  ( -res->  G )
% Possible bug: failure conditions may contain uninstantiated variables, creating too many causalities.
goal_failure_cause(GoalSchema, Individual) :-
	goal_schema(GoalSchema)		% Only works for goals
,	schema_agens(GoalSchema, Agens)
,	goal_failure_conditions(GoalSchema, FC)
,	establishes_conditions(FC, Agens, Individual, fabula:'InternalElement')
% And there is no later element that causes the schema (in other words, take the latest).
,	latest(FC, Agens, Individual, fabula:'InternalElement')
.

% Retrieves Individuals that describe fabula elements that enable given schema
% Current implementation only deals with facts (so this excludes: "johns belief that pete just asked him a question, enabled him to answer
%	however, if this is desired it is easily added (condition_to_quad). 
% ASSUMPTION is that each Individual has a fabula:time logged in the KB. Otherwise, enablements cannot be found.
setting_enables_event(EventSchema, Individual) :-
	event_schema(EventSchema)
,	schema_preconditions(EventSchema, Precs)					% -> e.g. [ condition(..), condition(..), ...]
,	establishes_conditions(Precs, Individual, fabula:'SettingElement')
,	latest(Precs, _, Individual, fabula:'SettingElement')
.

% Retrieves Individuals that describe fabula elements that enable given action rule
% ASSUMPTION is that each Individual has a fabula:time logged in the KB. Otherwise, enablements cannot be found.
% Finds IE  -m->  A
action_rule_cause(ActionRule, Individual) :-
	action_selection_schema(ActionRule)
,	schema_preconditions(ActionRule, Precs)
,	schema_agens(ActionRule, Agens)
,	establishes_conditions(Precs, Agens, Individual, fabula:'InternalElement')
,	latest(Precs, Agens, Individual, fabula:'InternalElement')
.

% Retrieves Individuals that describe fabula elements that enable given goal rule
% ASSUMPTION is that each Individual has a fabula:time logged in the KB. Otherwise, enablements cannot be found.
% Finds IE  -psi->  G
goal_rule_cause(GoalRule, Individual) :-
	goal_selection_schema(GoalRule)
,	schema_preconditions(GoalRule, Precs)
,	schema_agens(GoalRule, Agens)
,	establishes_conditions(Precs, Agens, Individual, fabula:'InternalElement')
,	latest(Precs, Agens, Individual, fabula:'InternalElement')
.

% Retrieves Individuals that describe fabula elements that psychologically cause given internal element operator
% ASSUMPTION is that each Individual has a fabula:time logged in the KB. Otherwise, enablements cannot be found.
% Finds IE  -psi->  IE
internal_element_cause(InternalElementOperator, Individual) :-
	internal_element_schema(InternalElementOperator)
,	schema_preconditions(InternalElementOperator, Precs)
,	schema_agens(InternalElementOperator, Agens)
,	establishes_conditions(Precs, Agens, Individual, fabula:'InternalElement')
,	latest(Precs, Agens, Individual, fabula:'InternalElement')
.

% =====================
% INTERNAL PREDICATES
% =====================

% Sees whether certain conditions are established by the description of a certain individual of type type for a certain agens.
establishes_conditions(Conditions, Agens, Individual, Type) :-
	query(Individual, fabula:character, Agens)			% Agens of schema should be equal to character of enabler.
,	establishes_conditions(Conditions, Individual, Type)
.

% Version without agens requirement (for plot)
establishes_conditions(Conditions, Individual, Type) :-
	nonvar(Type)
,	member(Prec, Conditions)									% -> e.g. condition(true, [...])
,	condition_member(CausingCondition, Prec)			% -> e.g. condition(true, fact(...))
,	condition_to_triple(CausingCondition, Truth, S, P, O)
,	query(fabula(S, P, O, G))							% There is a quad in the fabula in graph G
,	query(Individual, fabula:hasContent, G)				% Individual yields the enabling element itself, and its content is G
,	query(rule(Individual, owlr:typeOrSubType, Type))	% Individual should be an internal element (not a perception)
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
latest(Conditions, Agens, Individual, Type) :-
	query(Individual, fabula:time, Time)
,	query(SomeOtherIndividual, fabula:time, SomeOtherTime)
,	Individual \= SomeOtherIndividual
,	\+ (
		later(SomeOtherTime, Time)
	,	( 	
			% Use particular version of establishes_conditions depending on whether Agens is bound.
			nonvar(Agens) 
		->	establishes_conditions(Conditions, Agens, SomeOtherIndividual, Type)
		;	establishes_conditions(Conditions, SomeOtherIndividual, Type)
		)
	)
.


% Also true if cannot parse time.
latest(_, _, Individual, _) :-
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





