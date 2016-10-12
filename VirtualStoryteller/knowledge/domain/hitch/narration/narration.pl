schema_template(hitch:'StartExposition', Text, _) :-  
    Text = ['Dont Panic. Welcome to the galaxy.'].
    
% GOALS    
    
schema_template(hitch:'BeAtLocation', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target), 
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),
    Text = [AgensDesc, 'wants to be at ',TargetDesc,'.'].
    
schema_template(hitch:'FleeFromSpecies', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    Text = [AgensDesc, 'wants to run away from ',PatiensDesc,'.'].

% ACTIONS

schema_template(hitch:'TransitMove', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),    
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),
    Text = [AgensDesc, 'moves to',TargetDesc,'.'].
    
% PROPERTIES
% Are these ever used?
    
%relation_template((S, hitch:'at', O), Desc) :-
%	object_template(S, Sd),
%	object_template(O, Od),
%	Desc = [Sd, 'is at', Od], !.