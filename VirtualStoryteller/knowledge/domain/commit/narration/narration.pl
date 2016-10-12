schema_template(fabula:'exposition', Text, _) :-  
    Text = ['Welkom in Oostdorp. Leef mee met het wel en wee van de politieagenten Henk en Ingrid'].
    
% GOALS    
    
schema_template(commit:'BeAtLocation', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target), 
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),
    Text = [AgensDesc, 'wil op',TargetDesc,'zijn.'].
    
schema_template(commit:'StillThirst', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'wil zijn dorst lessen.'].
           

% ACTIONS

schema_template(commit:'WalkThisWay', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),
    Text = [AgensDesc, 'verplaatst zichzelf naar',TargetDesc,'.'].
    
schema_template(commit:'ExampleAction', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'doet een ExampleAction.'].
 
 schema_template(commit:'Drink', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),
    Text = [AgensDesc, 'drinkt',TargetDesc,'.'].
       
% PROPERTIES
% Are these ever used?
    
%relation_template((S, commit:'at', O), Desc) :-
%	object_template(S, Sd),
%	object_template(O, Od),
%	Desc = [Sd, 'is at', Od], !.