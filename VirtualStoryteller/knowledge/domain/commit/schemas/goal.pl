% Description of the commit domain setting.
% Authors:	Thomas de Groot & Jeroen Linssen
% StartDate:		2012-07-17
% UpdateDate:		2012-07-18

% Simplification: Policemen interacting with juvenile delinquents

% everybody wants to go to the square
goal_schema([
	type(commit:'BeAtLocation'), 
	arguments([agens(Agens), location(Location), target(commit:square)]),
	urgency(0.1),
	preconditions([
	    condition(true, [
	        rule(Agens, owlr:typeOrSubType, commit:'Human'),
	        fact(Agens, commit:at, Location),
	        rule(Location, owlr:isNot, commit:square)
	    ])
	]), 
    success_conditions([
        condition(true, [
            fact(Agens, commit:at, commit:square)
        ])
    ])
]).

% Henk wants to be near the statue at the square
%goal_schema([
%	type(commit:'BeAtObject'), 
%	arguments([agens(Agens), location(Location), target(commit:statue)]),
%	urgency(0.1),
%	preconditions([
%	    condition(true, [
%	        rule(Agens, owlr:is, commit:henk),
%	        fact(Agens, commit:at, Location)
%	    ]),
%	    condition(false, [
%	    	
%	    ])
%	]), 
%    success_conditions([
%        condition(true, [
%            fact(Agens, commit:at, commit:square)
%        ])
%    ])
%]).


% Drink
%%%% Is not yet adopted?
goal_schema([
	type(commit:'StillThirst'),
	arguments([agens(Agens), patiens(Patiens)]),
	urgency(0.1),
	preconditions([
	    condition(true, [
	        rule(Agens, owlr:typeOrSubType, commit:'Human'),
	        fact(Agens, commit:hasAttribute, commit:thirsty)
	    ])
	]), 
    success_conditions([
        condition(false, [
            fact(Agens, commit:hasAttribute, commit:thirsty)
        ])
    ])
]).