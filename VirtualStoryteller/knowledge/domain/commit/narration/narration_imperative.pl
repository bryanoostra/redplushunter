%  Commands for the interactive version.

schema_template_imperative(commit:'WalkThisWay', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),    
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),
    Text = ['Loop naar',TargetDesc,'.'].
    
schema_template_imperative(commit:'ExampleAction', Text, Schema) :-
    schema_agens(Schema,Agens),    
    object_template(Agens, AgensDesc),
    Text = ['Doe een ExampleAction.'].