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

:- module(reactive_layer,
	[	
		select_action_rule/2		% (+Agens, -ActionSelectionRule)  See which actions are triggered in the current context of the simulation
	]).
	
:- use_module(basicCharacterAgent).

	% RDF meta predicates, for term expansion at compile time
:- rdf_meta 
		select_action(r, -)
,		select_action_rule(r, t)
.
		
% ----------------------------------------------------------------------		

% Try to trigger actions.
select_action_rule(Char, AS) :-
	action_selection_schema(AS)
,	schemas:action_selection(AS, Action)	% Retrieve action from schema. 
,	schemas:schema_agens(Action, Char)		% Rule should be filled in with given character
,	schemas:validate_schema(AS, [])			% The rule's preconditions are satisfied
,	schemas:validate_schema(Action, [])		% The action's preconditions are satisfied
.

% Try to trigger actions (old way).
select_action(Char, Action) :-
	action_selection_schema(AS)
%,	schemas:schema_agens(AS, Char)
,	schemas:action_selection(AS, Action)	% Retrieve action from schema. 
,	schemas:schema_agens(Action, Char)
,	schemas:validate_schema(AS, [])
,	schemas:validate_schema(Action, [])
.