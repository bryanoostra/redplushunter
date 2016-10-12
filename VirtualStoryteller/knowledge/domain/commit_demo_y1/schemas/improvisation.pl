
framing_schema([
	type(red:'BeMean'),
	scope(all),
	arguments([agens(Ag)]),
    preconditions([
%
% EDIT: Multiple character should be able to be mean.
% This might give uninteresting results when both/all characters are AI controlled (take take-back take-back etc...).
% But when the wolf is the only AI-controlled character, it should always be able to become mean, regardless if the human controlled characters are already mean.  
%      	% Nobody else is mean already
%    	condition(false, [
%    		fact(_, swc:hasAttribute, red:mean)
%    	]),
%
%Apperently it doensn't work when this is not commented out:
%      	% The agent/character is not mean already
%    	%condition(false, [
%    	%	fact(Ag, swc:hasAttribute, red:mean)
%    	%]),
%
		% Agens is a character
		condition(true, [
			rule(Ag, owlr:typeOrSubType, swc:'Character')
		])

    ]),
   
    effects([
        % Characters can happen to be mean
        condition(true, [
        	fact(Ag, swc:hasAttribute, red:mean)
        ])
    ])
]).	   