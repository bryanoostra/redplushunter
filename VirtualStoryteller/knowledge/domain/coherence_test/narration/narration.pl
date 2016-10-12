% ----------------------
% Templates for schemas
% ----------------------

%---------------------------------
% GOALS
%---------------------------------
schema_template(ps:'WoundEnemy', Text, Schema) :-
	schema_agens(Schema, Agens),
	schema_patiens(Schema, Patiens),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    Text = [AgensDesc, 'wants to attack', PatiensDesc, ' (WoundEnemy)'].
    
schema_template(ps:'Defend', Text, Schema) :-
	schema_agens(Schema, Agens),
	schema_patiens(Schema, Patiens),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    Text = [AgensDesc, 'wants to defend himself from', PatiensDesc, ' (Defend)'].   
    
schema_template(ps:'RefillWaterSupply', Text, Schema) :-
	schema_agens(Schema, Agens),
	schema_patiens(Schema, Patiens),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    Text = [AgensDesc, 'wants to refill', PatiensDesc, ' (RefillWaterSupply)'].     
    
schema_template(ps:'GetDrunk', Text, Schema) :-
	schema_agens(Schema, Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'wants to get drunk. (WoundEnemy)'].    

%---------------------------------
% ACTIONS AND EVENTS
%---------------------------------
schema_template(ps:'Stab', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_instrument(Schema,Instrument),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    object_template(Instrument, InstrumentDesc),        
    Text = [AgensDesc, 'stabs', PatiensDesc, 'with', InstrumentDesc, '(Stab)'].
    
schema_template(ps:'DrawWeapon', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    Text = [AgensDesc, 'draws', PatiensDesc, '. (DrawWeapon)'].    

schema_template(ps:'OutOfWater', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = ['Suddenly, ', AgensDesc, 'is out of water! (OutOfWater)'].
    
schema_template('http://www.owl-ontologies.com/StoryWorldSettings/Pirates#GetThirsty', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'becomes thirsty for some rum. (GetThirsty)'].    

schema_template(ps:'SayLetsGetSomeRum', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = ['"Arr, lets get some fine ole rum," says', AgensDesc,'(SayLetsGetSomeRum)'].
    
schema_template(ps:'SayEnGarde', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    Text = ['"Prepare to meet your seaman\'s grave, ', PatiensDesc, '" says', AgensDesc, '(SayEnGarde)'].    
    
schema_template(ps:'SayNoWay', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    Text = ['"No way, ', PatiensDesc, '!" says', AgensDesc, '(SayNoWay)'].       

schema_template(ps:'SailToLand', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),
    schema_instrument(Schema,Instrument),
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc), 
    object_template(Instrument, InstrumentDesc),     
    Text = [AgensDesc, 'sets sail towards', TargetDesc, '(SailToLand)'].
    
schema_template(ps:'GetOffBoat', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),
    object_template(Agens, AgensDesc),    
    object_template(Target, TargetDesc), 
    Text = ['Moored at', TargetDesc, ',', AgensDesc, 'sets foot on land (GetOffBoat)'].    
    
schema_template(ps:'GoAboard', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),
    object_template(Agens, AgensDesc),    
    object_template(Target, TargetDesc), 
    Text = [AgensDesc, 'goes aboard', TargetDesc, ' (GoAboard)'].      
    
schema_template(ps:'FillBoatWithWater', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),
    schema_instrument(Schema,Instrument),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),     
    object_template(Instrument, InstrumentDesc), 
    Text = [AgensDesc, 'walks back and forth, filling', PatiensDesc, 'with water from', InstrumentDesc, '(FillBoatWithWater)'].        

schema_template(ps:'DrinkRum', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    Text = [AgensDesc, 'drinks rum from', PatiensDesc, '. (DrinkRum)']. 
    
schema_template(ps:'JumpShip', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = ['In panic, ', AgensDesc, 'jumps ship! (JumpShip)'].       