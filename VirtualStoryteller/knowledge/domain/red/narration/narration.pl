% ----------------------
% Templates for schemas
% ----------------------

schema_template('http://www.owl-ontologies.com/FabulaKnowledge.owl#StartExposition', Text, _) :-
    %Text = ['Once upon a time, there was a little girl with a red cap.'].  
    Text = ['Once upon a time, there was a little girl with a red cap. She wanted to bring a birthday cake to her grandmother...'].
                
schema_template('http://www.owl-ontologies.com/FabulaKnowledge.owl#StartRisingAction', Text, _) :-
    %Text = ['She wanted to bring a birthday cake to her grandmother...'].          
    Text = [''].

schema_template('http://www.owl-ontologies.com/Red.owl#EatSomething', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'wants to eat something.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#BringCake', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),
    schema_target(Schema,Target),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),      
    Text = [AgensDesc, 'wants to bring', PatiensDesc, 'to', TargetDesc, '.'].    
    
schema_template('http://www.owl-ontologies.com/Red.owl#SeekSupport', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),      
    Text = [AgensDesc, 'decides to ask', TargetDesc, 'for help.'].     
    
%Text for when there is a target to speak at
schema_template('http://www.owl-ontologies.com/Red.owl#Poison', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),
    schema_target(Schema,Target),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),            
    Text = [AgensDesc, 'assures', TargetDesc, 'that she will try to poison', PatiensDesc, '.'].       

%Text for when there is no target to speak at
schema_template('http://www.owl-ontologies.com/Red.owl#Poison', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    Text = [AgensDesc, 'wants to try to poison', PatiensDesc, '.'].       

schema_template('http://www.owl-ontologies.com/Red.owl#TellAboutPoisonPlan', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),    
    Text = ['"Well,', TargetDesc, '" says', AgensDesc, ', "I have a plan to poison', PatiensDesc, '. You just hold on!"'].     

schema_template('http://www.owl-ontologies.com/Red.owl#BecomeHungry', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'becomes very hungry.'].

schema_template('http://www.owl-ontologies.com/Red.owl#BakeCake', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema, Patiens),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    Text = [AgensDesc, 'bakes', PatiensDesc, '.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#PoisonFood', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    schema_patiens(Schema,Patiens),
    object_template(Patiens, PatiensDesc),    
    Text = ['With a little bit of cyanide, ', AgensDesc, 'poisons', PatiensDesc, '.'].    

schema_template('http://www.owl-ontologies.com/Red.owl#Cry', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'bursts out in tears.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#Laugh', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'bursts out laughing out loud.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#ExpressAnger', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    schema_target(Schema,Target),
    object_template(Target, TargetDesc),    
    Text = [AgensDesc, 'becomes a bit angry at', TargetDesc, '.'].    
    
schema_template('http://www.owl-ontologies.com/Red.owl#ExpressSympathy', Text, Schema) :-
	schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    schema_target(Schema,Target),
    object_template(Target, TargetDesc),        
    Text = ['"Oh that is not nice, I feel very sorry for you." says', AgensDesc, 'to', TargetDesc, '.'].  
        
schema_template('http://www.owl-ontologies.com/Red.owl#WhatToDo', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),   
    Text = ['What should I do,', PatiensDesc, '", asks', AgensDesc, '.'].      
    
schema_template('http://www.owl-ontologies.com/Red.owl#SkipTo', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),    
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),
    Text = [AgensDesc, 'skips to', TargetDesc, '.'].

schema_template('http://www.owl-ontologies.com/Red.owl#SneakTo', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),    
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),
    Text = [AgensDesc, 'sneaks to', TargetDesc, '.'].
        
schema_template('http://www.owl-ontologies.com/Red.owl#ShuffleTo', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),    
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),
    Text = [AgensDesc, 'shuffles to', TargetDesc, '.'].    
    
schema_template('http://www.owl-ontologies.com/Red.owl#Give', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),    
    Text = ['"Here you go, ', TargetDesc, '" says', AgensDesc, ', giving', PatiensDesc, 'to', TargetDesc, '.'].     
    
schema_template('http://www.owl-ontologies.com/Red.owl#Thank', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),    
    Text = ['"Thanks for', PatiensDesc, '!" says', AgensDesc, 'to', TargetDesc, '.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#Greet', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    Text = ['"Hello, ', PatiensDesc, '" says', AgensDesc, '.'].   
    
schema_template('http://www.owl-ontologies.com/Red.owl#GreetBack', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    Text = ['"Oh, hey, ', PatiensDesc, '" says', AgensDesc, '.'].       

schema_template('http://www.owl-ontologies.com/Red.owl#GreetAgain', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    Text = ['"Oh hey, hello again ', PatiensDesc, '" says', AgensDesc, '.'].       
    
schema_template('http://www.owl-ontologies.com/Red.owl#TakeFrom', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    Text = ['"Give me', PatiensDesc, '," says', AgensDesc, 'and forcefully takes it away from', TargetDesc, '.']. 
    
schema_template('http://www.owl-ontologies.com/Red.owl#TakeBack', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    Text = ['"Give back, it is mine!" says', AgensDesc, 'and takes', PatiensDesc, 'back from', TargetDesc, '.'].

schema_template('http://www.owl-ontologies.com/Red.owl#TakeAgain', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    Text = ['"Give back!" says', AgensDesc, 'and takes', PatiensDesc, 'back from', TargetDesc, '.'].
        
schema_template('http://www.owl-ontologies.com/Red.owl#TellSomeoneTookSomething', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    schema_instrument(Schema,Instrument),         
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    object_template(Instrument, InstrumentDesc),        
    Text = ['"Oh', TargetDesc, ', says', AgensDesc, ', "', InstrumentDesc, 'stole', PatiensDesc, 'from me!'].       
              
    
schema_template('http://www.owl-ontologies.com/Red.owl#Eat', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    Text = [AgensDesc, 'eats', PatiensDesc, '.'].         
    
schema_template('http://www.owl-ontologies.com/Red.owl#Die', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'dies.'].     
    
schema_template('http://www.owl-ontologies.com/Red.owl#LeaveBinging', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    Text = ['"Riiiiiight, " says', AgensDesc, ', "well, I will just let you enjoy your cakes then,', PatiensDesc, ', goodbye!"'].       
    
schema_template('http://www.owl-ontologies.com/Red.owl#BeMean', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'is mean.'].       
    

schema_template('http://www.owl-ontologies.com/Red.owl#WatchBirds', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'watches the birds flying over.'].

schema_template('http://www.owl-ontologies.com/Red.owl#WatchClouds', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'watches the clouds in the sky.'].

schema_template('http://www.owl-ontologies.com/Red.owl#BuildSandCastle', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'builds a sandcastle.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#RollSand', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'rolls in the sand.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#EnjoySun', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'enjoys bathing in the sun.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#TakeNap', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'takes a nap on the beach.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#Dive', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'dives to the bottom of the lake and swims back up again.'].

schema_template('http://www.owl-ontologies.com/Red.owl#TreadWater', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'threads water for a minute.'].

schema_template('http://www.owl-ontologies.com/Red.owl#Fart', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'farts in the water.'].

schema_template('http://www.owl-ontologies.com/Red.owl#Fart2', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'farts in the water for the second time.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#Fart3', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'farts in the water for the last time.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#Dust', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'dusts a cupboard.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#WashDishes', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'does the dishes.'].
       



relation_template((S, 'http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#at', O), Desc) :-
	object_template(S, Sd),
	object_template(O, Od),
	Desc = [Sd, 'is at', Od], !.	  	
	
relation_template((S, 'http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#has', O), Desc) :-
	object_template(S, Sd),
	object_template(O, Od),
	Desc = [Sd, 'has', Od], !.	
	
relation_template((S, 'http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#adjacent', O), Desc) :-
	object_template(S, Sd),
	object_template(O, Od),
	Desc = [Sd, 'is next to', Od], !.	
       
    