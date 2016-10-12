% goal database
% @author swartjes
% @date 27 september 2007

% goal(( Head, PosPreconditions, NegPreconditions,
%	PosSuccConditions,	NegSuccConditions, PosFailConditions, NegFailConditions))
% head((goalName, [Agens, Patiens, Target, Instrument | Vars])) :- .

	

    
% Goal for getting to the palace (TEST)
schema([
	head([
		type('http://www.owl-ontologies.com/StoryWorldCore.owl#AttendBall'),
		agens(Agens),loc(CurLoc)
		]),
	class(goal),	
	posPreconditions([(Agens, swc:supportedBy, CurLoc),(CurLoc, owlr:isNot, 'http://www.owl-ontologies.com/StoryWorldSettings/Cinderella#palace')]), 
%	posSuccessConditions([(Agens,  'http://www.owl-ontologies.com/StoryWorldCore.owl#supportedBy', 'http://www.owl-ontologies.com/StoryWorldSettings/Cinderella#square')])  % go to the square
	posSuccessConditions([(Agens,  swc:supportedBy, 'http://www.owl-ontologies.com/StoryWorldSettings/Cinderella#palace')])  % go to the square
    ]).          

% Make goal schema compatible to be used as abstract operator by defining the successconditions as effects.
% NOTE: this must be at the bottom otherwise we get a loop.
%schema(S|posEffects(PE)) :-
%	schema(S), 
%	getFromSchema(S, class(goal)),
%	getFromSchema(S, posSuccessConditions(PE)).
	
% Make goal schema compatible to be used as abstract operator by defining the successconditions as effects.
% NOTE: this must be at the bottom otherwise we get a loop.
%schema(S|negEffects(NE)) :-
%	schema(S), 
%	getFromSchema(S, class(goal)),
%	getFromSchema(S, negSuccessConditions(NE)).	


% -----------------------------------------------------------------------------
% In order to check which schemas are applicable in a certain case, you can use
% schema(S), getFromSchema(S, head(H)), getFromSchema(S, class(goal)), validateOperator(H, A, B).