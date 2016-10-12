% ---------------------------------------------
% Templates for schemas, the imperative version
% ---------------------------------------------

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#EatSomething', Text, _) :-
    %Text = ['Want to eat something'].
    Text = ['Wil iets gaan eten'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#BringCake', Text, Schema) :-
    schema_patiens(Schema,Patiens),
    schema_target(Schema,Target),
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),      
    %Text = ['Want to bring', PatiensDesc, 'to', TargetDesc].
    Text = ['Wil', PatiensDesc, 'naar', TargetDesc, 'brengen'].    
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#SeekSupport', Text, Schema) :-
    schema_target(Schema,Target),
    object_template(Target, TargetDesc),      
    %Text = ['Decide to ask', TargetDesc, 'for help'].     
    Text = ['Besluit om', TargetDesc, 'om hulp te vragen'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Poison', Text, Schema) :-
    schema_patiens(Schema,Patiens),
    schema_target(Schema,Target),
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),            
    %Text = ['Assure', TargetDesc, 'that you will try to poison', PatiensDesc].       
    Text = ['Vertel', TargetDesc, 'dat je wil proberen om', PatiensDesc, 'te vergiftigen'].

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#TellAboutPoisonPlan', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),    
    %Text = ['Tell ', TargetDesc, ' that you have a plan to poison', PatiensDesc].     
    Text = ['Vertel', TargetDesc, 'dat je een plan hebt om', PatiensDesc, 'te vergiftigen'].

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#BecomeHungry', Text, Schema) :-
	schema_agens(Schema,Agens),
	object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'becomes hungry'].
    Text = [AgensDesc, 'krijgt honger'].

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Die', Text, Schema) :-
	schema_agens(Schema,Agens),
	object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'dies'].
    Text = [AgensDesc, 'gaat dood'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#BakeCake', Text, Schema) :-
    schema_patiens(Schema, Patiens),
    object_template(Patiens, PatiensDesc),
    %Text = ['Bake', PatiensDesc].
    Text = ['Bak', PatiensDesc].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#PoisonFood', Text, Schema) :-
    schema_patiens(Schema,Patiens),
    object_template(Patiens, PatiensDesc),    
    %Text = ['Poison', PatiensDesc].    
    Text = ['Vergiftig', PatiensDesc].

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Cry', Text, _) :-
    %Text = ['Start crying'].
    Text = ['Begin te huilen'].

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Laugh', Text, _) :-
    %Text = ['Start laughing'].
    Text = ['Begin te lachen'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#ExpressAnger', Text, Schema) :-
    schema_target(Schema,Target),
    object_template(Target, TargetDesc),    
    Text = ['Word boos op', TargetDesc].    

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#ExpressSympathy', Text, Schema) :-
    schema_target(Schema,Target),
    object_template(Target, TargetDesc),  
    Text = ['Vertel', TargetDesc, 'dat je het erg vervelend vind'].    
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#WhatToDo', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    object_template(Patiens, PatiensDesc),    
    %Text = ['Ask', PatiensDesc, 'what you should do'].    
    Text = ['Vraag', PatiensDesc, 'wat je zou kunnen doen'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#SkipTo', Text, Schema) :-
    schema_target(Schema,Target),    
    object_template(Target, TargetDesc),
    %Text = ['Skip to', TargetDesc].
    Text = ['Huppel naar', TargetDesc].

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#SneakTo', Text, Schema) :-
    schema_target(Schema,Target),    
    object_template(Target, TargetDesc),
    %Text = ['Sneak to', TargetDesc].
    Text = ['Sluip naar', TargetDesc].
        
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#ShuffleTo', Text, Schema) :-
    schema_target(Schema,Target),    
    object_template(Target, TargetDesc),
    %Text = ['Shuffle to', TargetDesc].    
    Text = ['Schuifel naar', TargetDesc].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Give', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),    
    %Text = ['Give', PatiensDesc, 'to', TargetDesc].     
    Text = ['Geef', PatiensDesc, 'aan', TargetDesc].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Thank', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),    
    %Text = ['Thank', TargetDesc, 'for giving', PatiensDesc].     
    Text = ['Bedank', TargetDesc, 'voor het geven van', PatiensDesc].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Greet', Text, Schema) :-
    schema_patiens(Schema,Patiens),        
    object_template(Patiens, PatiensDesc),    
    %Text = ['Greet', TargetDesc].   
    Text = ['Groet', PatiensDesc].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#GreetBack', Text, Schema) :-
    schema_patiens(Schema,Patiens),        
    object_template(Patiens, PatiensDesc),    
    %Text = ['Greet', TargetDesc, 'back'].       
    Text = ['Groet', PatiensDesc, 'terug'].

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#GreetAgain', Text, Schema) :-
    schema_patiens(Schema,Patiens),        
    object_template(Patiens, PatiensDesc),    
    %Text = ['Greet', PatiensDesc, 'again'].       
    Text = ['Groet', PatiensDesc, 'nogmaals'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#TakeFrom', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    %Text = ['Take', PatiensDesc, 'away from', TargetDesc]. 
    Text = ['Pak', PatiensDesc, 'af van', TargetDesc].

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#TakeBack', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    %Text = ['Take', PatiensDesc, 'back from', TargetDesc]. 
    Text = ['Pak', PatiensDesc, 'terug van', TargetDesc].

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#TakeAgain', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    %Text = ['Take', PatiensDesc, 'back from', TargetDesc]. 
    Text = ['Pak', PatiensDesc, 'terug van', TargetDesc].
        
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#TellSomeoneTookSomething', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    schema_instrument(Schema,Instrument),         
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    object_template(Instrument, InstrumentDesc),        
    %Text = ['Explain to', TargetDesc, 'that', InstrumentDesc, 'stole', PatiensDesc, 'from you'].       
    Text = ['Vertel aan', TargetDesc, 'dat', InstrumentDesc, PatiensDesc, 'van je gestolen heeft'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Eat', Text, Schema) :-
    schema_patiens(Schema,Patiens),    
    object_template(Patiens, PatiensDesc),    
    %Text = ['Eat', PatiensDesc].         
    Text = ['Eet', PatiensDesc, 'op'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#LeaveBinging', Text, Schema) :-
    schema_patiens(Schema, Patiens),        
    object_template(Patiens, PatiensDesc),    
    %Text = ['Leave', TargetDesc, 'to their food binging'].      
    Text = ['Laat', PatiensDesc, 'maar lekker met rust'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#BeMean', Text, Schema) :-
    schema_agens(Schema,Agens),        
    object_template(Agens, AgensDesc),    
    %Text = ['Pretend', AgensDesc, 'was mean'].               
    Text = ['Doe vanaf nu alsof', AgensDesc, 'gemeen is'].
    
    
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#WatchBirds', Text, _) :-
    %Text = ['Watch the birds'].               
    Text = ['Kijk naar de vogels'].

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#WatchClouds', Text, _) :-
    %Text = ['Watch the clouds'].               
    Text = ['Kijk naar de wolken'].

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#BuildSandCastle', Text, _) :-
    %Text = ['Build a sandcastle'].               
    Text = ['Bouw een zandkasteel'].    
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#RollSand', Text, _) :-
    %Text = ['Roll through the sand'].               
    Text = ['Rol door het zand'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#EnjoySun', Text, _) :-
    %Text = ['Enjoy the sun'].               
    Text = ['Geniet van de zon'].    

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#TakeNap', Text, _) :-
    %Text = ['Take a nap'].               
    Text = ['Doe een dutje'].    
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Dive', Text, _) :-
    %Text = ['Dive'].               
    Text = ['Duik'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#TreadWater', Text, _) :-
    %Text = ['Thread water'].               
    Text = ['Watertrappel'].       

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Fart', Text, _) :-
    %Text = ['Fart'].               
    Text = ['Laat een scheet'].       

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Fart2', Text, _) :-
    %Text = ['Fart again'].               
    Text = ['Laat nog een scheet'].       

schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Fart3', Text, _) :-
    %Text = ['Fart one last time'].               
    Text = ['Laat voor de laatste keer een scheet'].       
            
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#Dust', Text, _) :-
    %Text = ['Dust '].               
    Text = ['Stof af'].
    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#WashDishes', Text, _) :-
    %Text = ['Do the dishes'].               
    Text = ['Was af'].     

% Jeroen-edit    
schema_template_imperative('http://www.owl-ontologies.com/Red.owl#PickNose', Text, _) :-
    %Text = ['Dust '].               
    Text = ['Pulk in je neus.'].  
    
    
    
relation_template((S, 'http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#at', O), Desc) :-
	object_template(S, Sd),
	object_template(O, Od),
	%Desc = [Sd, 'is at', Od], !.	  	
	Desc = [Sd, 'is bij', Od], !.
	
relation_template((S, 'http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#has', O), Desc) :-
	object_template(S, Sd),
	object_template(O, Od),
	%Desc = [Sd, 'has', Od], !.	
	Desc = [Sd, 'heeft', Od], !.
	
relation_template((S, 'http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#adjacent', O), Desc) :-
	object_template(S, Sd),
	object_template(O, Od),
	%Desc = [Sd, 'is adjacent to', Od], !.	
	Desc = [Sd, 'is dichtbij', Od], !.
	