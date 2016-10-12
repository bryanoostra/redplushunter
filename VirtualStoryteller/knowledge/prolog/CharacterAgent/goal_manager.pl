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

:- module(goal_manager,
	[	
		select_goal_rule/2,			% (+Agens, -GoalSelectionSchema)
		goal_rules_defined/0,		% See whether there are goal rules defined.
		possible_goal/2,			% (+Agens, -Goal) 			See which goals are possible in the current context of the simulation
		possible_justifiable_goal/4,	% (+Agens, -Goal, -MetaGoal, -Plan)	See if we can justify a goal using a plan.
		adopt_goal/1,				% (+Goal) 					Adopt goal.
		drop_goal/1,				% (+Goal)					Drop a goal.
		adopt_justifiable_goal/1,	% (+Goal)					Adopt a goal for justification.
		adopted_goal/1,				% (?Goal)					See which goal is adopted.
		adopted_justifiable_goal/1, % (?Goal)					See which goal is adopted for justification.
		suggest_goal/1,				% (+Goal)	Assert given goal as a suggested goal.
		suggested_goal/1,			% (?Goal)	True if Goal was suggested.
		assert_causal_anchor/1		% (+Ind)	Assert Ind to be a causal anchor of the fabula network.
	]).
	
:- dynamic adopted_goal/1,
			adopted_justifiable_goal/1,
			suggested_goal/1,
			causal_anchor/1.

:- use_module(basicCharacterAgent).

	% RDF meta predicates, for term expansion at compile time
:- rdf_meta 
		select_goal_rule(r, t),
		possible_goal(r, -),
		possible_justifiable_goal(r, t, t, t),		
		adopt_goal(t),
		drop_goal(t),
		adopt_justifiable_goal(t),
		suggested_goal(t),
		causal_anchor(r),
		assert_causal_anchor(r),
		in_causal_chain(r, -).
		
% ----------------------------------------------------------------------		

% Options
use_coherent_goal_selection(false).

% See whether there are any goal rules defined.
goal_rules_defined :-
	schemas:goal_selection_schema(_)
,	!		% Cut, we don't want to leave choice points for determining IF there are goal_rules!
.

% Try to trigger goals.
select_goal_rule(Char, GS) :-
	schemas:goal_selection_schema(GS)
,	schema_agens(GS, Char)				% Rule should be filled in with given character
,	schemas:goal_selection(GS, Goal)	% Retrieve goal from schema. 
,	schemas:validate_schema(GS, [])		% Rule's preconditions are valid
,	possible_goal(Char, Goal)			% Goal itself must be valid
,	ties_in_causally(GS)				% Goal rule ties in causally
.

% Sees which goals can be adopted. Since schema validations might result in duplicate schemas (if there are multiple ways to validate a schema),
% we generate the whole set and select a member. Assumption here is that the set of goals and the number of calls to this predicate small enough 
% that it remains quick. I estimate that might become a problem with > 1000 goals.
%
% Common usage: 
%	possible_goal(+Agens, -Goal)
%
possible_goal(Agens, Goal) :-
	
	% Generate set of goals that can possibly be adopted (comparison: ==)
	setof(
		G
	,	(
			goal_schema(G)
		,	schema_agens(G, Agens)
		,	validate_schema(G, [])
		,	passes_adoption_filter(G)
		,	\+ validate_goal_success_conditions(G)   % Not already achieved
		,	\+ validate_goal_failure_conditions(G)   % Not already failed		
		)
	,	Set
	)
	% Remove duplicates. Comparison: unification (=)
,	filter_duplicates(Set, Set2)
,	member(Goal, Set2)
.

% Retrieves which goals are possible for given agent, if Plan were executed
% CAREFUL: this predicate might be very slow, depending on domain definitions
% Eventually we might want a predicate that allows for specifying plan depth
% as well, so you can iterate over possible plannable goals.
%
% Common usage: 
%	possible_justifiable_goal(+Agens, -Goal, -MetaGoal, -Plan)	- To get the goals & a plan that would justify it
%	possible_justifiable_goal(+Agens, +Goal, -MetaGoal, -Plan)	- To get a plan that justifies given goal for an agent
%
possible_justifiable_goal(Agens, Goal, MetaGoal, Plan) :-
	goal_schema(Goal)
,	schema_agens(Goal, Agens)
	% The goal should not be adopted already
,	\+ adopted_goal(Goal)
	% We should be able to make the schema possible (which fails if it already IS possible)
%,	make_schema_possible(Goal, MetaGoal, Plan)
,	meta_goal(Goal, MetaGoal, Plan)

	% See what is necessary for justification
	% The goal, with its variables bound according to the justification, should not already have been adopted for justification.
,	\+ adopted_justifiable_goal(Goal)

	/*  The below check doesn't work, since the success/failure condition truth values might change due to execution of the justifying plan.
		At this point, we don't know whether the goal will be very useful unless we have executed the plan.
		The assumption is that the goal _will_ be useful, because we have to work to make the preconditions true, it will probably not be immediately
		successful or failed. If it turns out to be, then it will just not be accepted as a goal and we have done superfluous work.
	
,	\+ validate_goal_success_conditions(Goal)   % Not already achieved
,	\+ validate_goal_failure_conditions(Goal)   % Not already failed
	
	*/

.

% Adopts new (given) goal
%	
% Common usage:
%	adopt_goal(+Goal)
%
adopt_goal(Goal) :-
	% Not already adopted
	\+ adopted_goal(Goal)
	% Preconditions still hold
,	validate_schema(Goal, [])
	% OK, adopt.
,	assert(adopted_goal(Goal))
.

% Drops a given goal
drop_goal(Goal) :-
	nonvar(Goal)
,	retract(adopted_goal(Goal))
.

% Adopt goal to justify
adopt_justifiable_goal(Goal) :-
	\+ adopted_justifiable_goal(Goal)
,	assert(adopted_justifiable_goal(Goal))
.

% Take on given goal as suggested
suggest_goal(Goal) :-
	nonvar(Goal)
,	assert(suggested_goal(Goal))
.

% -------- INTERNAL PREDICATES (not exported by module) ----------

% Try to make a plan that will enable the preconditions of a schema
% Fails if there are no untrue conditions: we cannot MAKE schema possible; it already is.
make_schema_possible(Schema, UntrueConditions, Plan) :-
	validate_schema(Schema, UntrueConditions)
,	UntrueConditions \= []
,	plan(_, false, UntrueConditions, Plan).   

% Find meta goals to justify goal
meta_goal(Goal, MetaGoal, MetaPlan) :-
	goal_rules_defined
,	goal_schema(Goal)
,	schema_agens(Goal, A)
,	validate_schema(Goal, UG)
,	goal_selection_schema(GS)
,	schema_agens(GS, A)
,	goal_selection(GS, Goal)
,	validate_schema(GS, UGS)
,	merge(UG, UGS, MetaGoal)
,	plan(A, false, MetaGoal, MetaPlan)
.

% Variant for domains without goal rules
meta_goal(Goal, MetaGoal, MetaPlan) :-
	\+ goal_rules_defined
,	goal_schema(Goal)
,	schema_agens(Goal, A)
,	validate_schema(Goal, MetaGoal)
,	plan(A, false, MetaGoal, MetaPlan)
.

% The empty set has no duplicates
filter_duplicates([], []).

% If the head of the set is a member (=) of the rest, then the result is the filtered tail 
filter_duplicates([S1|S], T) :-
	member(S1, S)
,	filter_duplicates(S, T)
.

% If the head of the set is NOT a member (=) of the rest, then the result is the filtered tail plus the head.
filter_duplicates([S1|S], [S1|T]) :-
	\+ member(S1, S)
,	filter_duplicates(S,T)
.

% A (validated) goal schema passes the adoption filter
% This will be the place to do coherence-based constraints, such as specify that the goal must be caused by something in the causal chain.
passes_adoption_filter(G) :-
	% (1) it is not yet adopted already
	\+ adopted_goal(G)
	% (2) ties in causally with the fabula so far
,	ties_in_causally(G)
.

% Determines if a (validated) goal schema ties in causally with the fabula so far.
% See TWikiTopic CoherentGoalSelection

% (0) any goal or goal rule ties in causally if it is not enabled.
ties_in_causally(_) :-
	use_coherent_goal_selection(false)
,	!
.

% (1) any goal or goal rule ties in causally if there's no causal anchor.
ties_in_causally(_) :-
	\+ causal_anchor(_)
,	!
.

% (2a) a goal ties in causally if there is a member of the nonempty set of motivations for the goal that is in the causal chain.
%	   goals typically tie in with motivates to other G's
ties_in_causally(G) :-
	goal_schema(G)
,	setof(I,
			%fabula:schema_enablement(G, I) ---> no cause.
		fabula:goal_motivation(G, I),
		Ind)
,	member(I1, Ind)
,	in_causal_chain(I1, [])
.

% (2b) a goal RULE ties in causally if there is a member of the nonempty set of causes of the goal rule of the goal that is in the causal chain.
%	   goal rules typically tie in with psi_causes to IE's
ties_in_causally(GR) :-
	goal_selection_schema(GR)
,	goal_selection(GR, G)
,	ties_in_causally(G)
,	setof(I,
		fabula:goal_rule_cause(GR, I),
		Ind)
,	member(I1, Ind)
,	in_causal_chain(I1, [])
.
		
% Assert individual Ind to be causal anchor of the causal chain.
assert_causal_anchor(Ind) :-
	assert(causal_anchor(Ind))
.

% Ind is in causal chain if its asserted to be a causal anchor.
in_causal_chain(Ind, _) :-
	causal_anchor(Ind)
.

% Ind is in causal chain if it causes something else or is caused by something else that is in causal chain.
in_causal_chain(Ind, Visited) :-
	(	causes(Ind, Ind2)
	;	causes(Ind2, Ind)  
	)
,   \+ member(Ind2, Visited)
,   in_causal_chain(Ind2, [Ind|Visited])
.

% An individual Ind causes individual Ind2 if any of the four causal relationships exists in the KB.
causes(Ind, Ind2) :-
	query(Ind, fabula:phi_causes, Ind2)
;	query(Ind, fabula:psi_causes, Ind2)
;	query(Ind, fabula:motivates, Ind2)
%;	query(Ind, fabula:enables, Ind2)  ---> no cause.
.
	
	
