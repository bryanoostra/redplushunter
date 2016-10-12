% ---------------------------------------------
% Templates for schemas, the imperative version
% ---------------------------------------------

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#EatSomething', Text, _) :-
    Text = ['Want to eat something'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#BringCake', Text, Schema) :-
    schema_patiens(Schema,Patiens),
    schema_target(Schema,Target),
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),      
    Text = ['Want to bring', PatiensDesc, 'to', TargetDesc].    
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#SeekSupport', Text, Schema) :-
    schema_target(Schema,Target),
    object_template(Target, TargetDesc),      
    Text = ['Decide to ask', TargetDesc, 'for help'].     
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Poison', Text, Schema) :-
    schema_patiens(Schema,Patiens),
    schema_target(Schema,Target),
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),            
    Text = ['Assure', TargetDesc, 'that you will try to poison', PatiensDesc].       

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#TellAboutPoisonPlan', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),    
    Text = ['Tell ', TargetDesc, ' that you have a plan to poison', PatiensDesc].     

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#BecomeHungry', Text, Schema) :-
	schema_agens(Schema,Agens),
	object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'becomes hungry'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Die', Text, Schema) :-
	schema_agens(Schema,Agens),
	object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'dies'].

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#BakeCake', Text, Schema) :-
    schema_patiens(Schema, Patiens),
    object_template(Patiens, PatiensDesc),
    Text = ['Bake', PatiensDesc].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#PoisonFood', Text, Schema) :-
    schema_patiens(Schema,Patiens),
    object_template(Patiens, PatiensDesc),    
    Text = ['Poison', PatiensDesc].    

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Cry', Text, _) :-
    Text = ['Start crying'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Laugh', Text, _) :-
    Text = ['Start laughing'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#ExpressAnger', Text, Schema) :-
    schema_target(Schema,Target),
    object_template(Target, TargetDesc),    
    Text = ['Become angry at', TargetDesc].    
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#ExpressSympathy', Text, Schema) :-
    schema_target(Schema,Target),
    object_template(Target, TargetDesc),  
    Text = ['Tell', TargetDesc, 'that you feel very sorry'].    

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#WhatToDo', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    object_template(Patiens, PatiensDesc),    
    Text = ['Ask', PatiensDesc, 'what you should do'].      
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#SkipTo', Text, Schema) :-
    schema_target(Schema,Target),    
    object_template(Target, TargetDesc),
    Text = ['Skip to', TargetDesc].

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#SneakTo', Text, Schema) :-
    schema_target(Schema,Target),    
    object_template(Target, TargetDesc),
    Text = ['Sneak to', TargetDesc].
        
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#ShuffleTo', Text, Schema) :-
    schema_target(Schema,Target),    
    object_template(Target, TargetDesc),
    Text = ['Shuffle to', TargetDesc].    
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Give', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),    
    Text = ['Give', PatiensDesc, 'to', TargetDesc].     
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Thank', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),    
    Text = ['Thank', TargetDesc, 'for giving', PatiensDesc].  
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Greet', Text, Schema) :-
    schema_patiens(Schema,Patiens),        
    object_template(Patiens, PatiensDesc),    
    Text = ['Greet', PatiensDesc].   
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#GreetBack', Text, Schema) :-
    schema_patiens(Schema,Patiens),        
    object_template(Patiens, PatiensDesc),    
    Text = ['Greet', PatiensDesc, 'back'].       
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#GreetAgain', Text, Schema) :-
    schema_patiens(Schema,Patiens),        
    object_template(Patiens, PatiensDesc),    
    Text = ['Greet', PatiensDesc, 'again'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#TakeFrom', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    Text = ['Take', PatiensDesc, 'away from', TargetDesc]. 
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#TakeBack', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    Text = ['Take', PatiensDesc, 'back from', TargetDesc].

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#TakeAgain', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    Text = ['Take', PatiensDesc, 'back from', TargetDesc].
        
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#TellSomeoneTookSomething', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    schema_instrument(Schema,Instrument),         
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    object_template(Instrument, InstrumentDesc),        
    Text = ['Explain to', TargetDesc, 'that', InstrumentDesc, 'stole', PatiensDesc, 'from you'].       
              
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Eat', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    object_template(Patiens, PatiensDesc),    
    Text = ['Eat', PatiensDesc].         
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#LeaveBinging', Text, Schema) :-
    schema_patiens(Schema, Patiens),        
    object_template(Patiens, PatiensDesc),    
    Text = ['Leave', PatiensDesc, 'to their food binging'].      
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#BeMean', Text, Schema) :-
    schema_agens(Schema,Agens),        
    object_template(Agens, AgensDesc),    
    Text = ['Pretend', AgensDesc, 'was mean'].               
    
    
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#WatchBirds', Text, _) :-    
    Text = ['Watch the birds'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#WatchClouds', Text, _) :-    
    Text = ['Watch the clouds'].    
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#BuildSandCastle', Text, _) :-
    Text = ['Build a sandcastle'].               
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#RollSand', Text, _) :-
    Text = ['Roll in the sand'].               
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#EnjoySun', Text, _) :-
    Text = ['Enjoy the sun'].    
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#TakeNap', Text, _) :-
    Text = ['Take a nap'].               
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Dive', Text, _) :-
    Text = ['Dive'].               
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#TreadWater', Text, _) :-
    Text = ['Thread water'].    
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Fart', Text, _) :-
    Text = ['Fart'].   

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Fart2', Text, _) :-
    Text = ['Fart again'].               
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Fart3', Text, _) :-
    Text = ['Fart one last time'].               
        
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Dust', Text, _) :-
    Text = ['Dust'].               
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#WashDishes', Text, _) :-
    Text = ['Do the dishes'].               
      
    
    
    
    
    
    
    
    
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
	Desc = [Sd, 'is adjacent to', Od], !.	
       
    