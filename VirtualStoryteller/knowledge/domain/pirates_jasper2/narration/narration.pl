% ----------------------
% Templates for schemas
% ----------------------


schema_template('http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#IssueOrder', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'orders'].  

schema_template('http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#GoToDoor', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),        
	schema_location(Schema,CurLoc),        
    schema_instrument(Schema,Road),        
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),    
    object_template(CurLoc, CurLocDesc),        
    object_template(Road, RoadDesc),        
    Text = [AgensDesc, 'goes from', CurLocDesc, 'to', TargetDesc, 'via', RoadDesc].     
schema_template('http://www.owl-ontologies.com/FabulaKnowledge.owl#OpenDoor', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc), 
    Text = [AgensDesc, 'opens', PatiensDesc].
    
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
%schema_template('http://www.owl-ontologies.com/FabulaKnowledge.owl#TakeOut', Text, Schema) :-
%    schema_agens(Schema,Agens),
%    schema_patiens(Schema,Patiens),    
%    schema_target(Schema,Target),
%    object_template(Agens, AgensDesc),
%    object_template(Patiens, PatiensDesc), 
%    object_template(Target, TargetDesc), 
%    Text = [AgensDesc, 'takes', PatiensDesc, 'out of', TargetDesc].   

schema_template('http://www.owl-ontologies.com/FabulaKnowledge.owl#TakeFrom', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc), 
    object_template(Target, TargetDesc), 
    Text = [AgensDesc, 'takes', PatiensDesc, 'from ', TargetDesc].       
    
schema_template('http://www.owl-ontologies.com/FabulaKnowledge.owl#Take', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc), 
    Text = [AgensDesc, 'takes', PatiensDesc].


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% GoToDoor: agens(Agens), location(Target), location(CurLoc), instrument(Road), door(Door)
% Agens walks from Location to Target through Door via Instrument

schema_template('http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#GoToDoor', Text, Schema) :-
    schema_agens(Schema,Agens),
%    schema_patiens(Schema,Patiens),
%    schema_target(Schema,Target),
%    schema_instrument(Schema,Instrument),

    object_template(Agens, AgensDesc),
%    object_template(Patiens, PatiensDesc), 
%    object_template(Target, TargetDesc), 
%    object_template(Instrument, InstrumentDesc),
    
     Text = [AgensDesc, 'walks from', 'CurLoc', 'to', 'Target', 'through', 'Door', 'via', 'Instrument'].
%    Text = [AgensDesc, 'walks from', CurLocDesc, 'to', TargetDesc, 'through', DoorDesc, 'via', InstrumentDesc].


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% LoadCanon: agens(Agens), patiens(Patiens), instrument(Instrument), location(Location)
% Action semantics: AGENS loads PATIENS with INSTRUMENT

schema_template('http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#LoadCanon', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),
%    schema_target(Schema,Target),
    schema_instrument(Schema,Instrument),

    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc), 
%    object_template(Target, TargetDesc), 
    object_template(Instrument, InstrumentDesc),
    
    Text = [AgensDesc, 'loads', PatiensDesc, 'with', InstrumentDesc].
    
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% FireCanon: agens(Agens), patiens(Patiens), target(Target), instrument(Instrument), location(Location)
% Action semantics: AGENS fires PATIENS from TARGET with INSTRUMENT

schema_template('http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#FireCanon', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),
    schema_target(Schema,Target),
    schema_instrument(Schema,Instrument),

    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc), 
    object_template(Target, TargetDesc), 
    object_template(Instrument, InstrumentDesc),
    
    Text = [AgensDesc, 'fires', PatiensDesc, 'from', TargetDesc, 'with', InstrumentDesc].
      
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%