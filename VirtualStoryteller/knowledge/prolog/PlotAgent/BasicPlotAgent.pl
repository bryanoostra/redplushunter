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
% Basic Plot Agent Prolog functions
% @author Ivo Swartjes
% 26 nov 2007
	
:- module(basicPlotAgent,[
%	condition_to_triple/5,
    possible_action/2,			% +Agens, -Action
    possible_action/3,			% +Agens, -Action, -Type    
    possible_transitmove_action/3,% +Agens, -Target, -Action    
	agent/1
%	setting_enables_event/2
	]).	
	
	% RDF meta predicates, for term expansion at compile time
:- rdf_meta 
	possible_action(r, -).	



agent(plot).

% Retrieves Individuals that describe fabula elements that enable given schema
% Current implementation only deals with facts (so this excludes: "johns belief that pete just asked him a question, enabled him to answer
%	however, if this is desired it is easily added (condition_to_quad). 
% ASSUMPTION is that each Individual has a fabula:time logged in the KB. Otherwise, enablements cannot be found.
% DEPRECATED: USE fabula MODULE

%setting_enables_event(EventSchema, Individual) :-
%	event_schema(EventSchema)
%,	schema_preconditions(EventSchema, Precs)					% -> e.g. [ condition(..), condition(..), ...]
%,	member(Prec, Precs)									% -> e.g. condition(true, [...])
%,	condition_member(EnablingCondition, Prec)			% -> e.g. condition(true, fact(...))
%,	condition_to_triple(EnablingCondition, Truth, S, P, O)
%,	query(fabula(S, P, O, G))							% There is a quad in the fabula in graph G
%,	query(Individual, fabula:hasContent, G)				% Individual yields the enabling element itself, and its content is G
%,	query(rule(Individual, owlr:typeOrSubType, fabula:'SettingElement'))	% Individual should be a setting element
%,	schema_agens(EventSchema, _)
%,	graph_truth(G, Truth)
% And there is no later element that enables the schema (in other words, take the latest).
%,	query(Individual, fabula:time, Time)
%,	query(SomeOtherIndividual, fabula:time, SomeOtherTime)
%,	\+ (
%		later(SomeOtherTime, Time)
%	,	setting_enables_event(Schema, SomeOtherIndividual)
%	)
%.

% Determines the truth of the graph
graph_truth(GraphInd, true) :-
	query(rule(GraphInd, owlr:typeOrSubType, fabula:'TruthGraph'))
,	!
.

graph_truth(GraphInd, false) :-
	query(rule(GraphInd, owlr:typeOrSubType, fabula:'FalsehoodGraph'))
,	!
.

later(Time1, Time2) :-
	Time1 = literal(type('http://www.w3.org/2001/XMLSchema#int', Val1))
,	Time2 = literal(type('http://www.w3.org/2001/XMLSchema#int', Val2))
,	Val1 > Val2
.


condition_to_triple(Condition, Truth, S, P, O) :-
	Condition = condition(Truth, Fact),
	Fact = fact(S, P, O). 
	
condition_to_triple(Condition, Truth, S, P, O) :-
	Condition = condition(Truth, Fact),
	Fact = fabula(S, P, O). 	

condition_to_triple(Condition, Truth, S, P, O) :-
	Condition = condition(Truth, Fact),
	Fact = fabula(S, P, O, _). 	
	
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
	
	
% possible_transitmove_action/3, +Agens, -Target, -Action
% used get the possible transitMove actions of specified agent to a chosen target destination
possible_transitmove_action(Agens, Target, Action) :-
	action_schema(Action),
	possible_action(Agens, Action),
	schema_target(Action, Target),
	schema_type(Action, Type),
	query(rule(Type, owlr:'classOrSubClassOf', fabula:'TransitMove')).	
