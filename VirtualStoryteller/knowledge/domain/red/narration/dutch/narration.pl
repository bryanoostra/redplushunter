% ----------------------
% Templates for schemas
% ----------------------

schema_template('http://www.owl-ontologies.com/FabulaKnowledge.owl#StartExposition', Text, _) :-
    %Text = ['Once upon a time, there was a little girl with a red cap. She wanted to bring a birthday cake to her grandmother...'].
    %Text = ['Er was eens een klein meisje met een rood kapje op haar hoofd'].  
	Text = ['Er was eens een klein meisje met een rood kapje op haar hoofd. Ze wilde een verjaardagstaart naar haar oma brengen...'].
 	       
schema_template('http://www.owl-ontologies.com/FabulaKnowledge.owl#StartRisingAction', Text, _) :-
    %Text = ['Ze wilde een verjaardagstaart naar haar oma brengen...'].
    Text = [''].  

schema_template('http://www.owl-ontologies.com/Red.owl#EatSomething', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'wants to eat something'].
	Text = [AgensDesc, 'wil wat gaan eten.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#BringCake', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),
    schema_target(Schema,Target),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),      
    %Text = [AgensDesc, 'wants to bring', PatiensDesc, 'to', TargetDesc].    
    Text = [AgensDesc, 'wil', PatiensDesc, 'naar', TargetDesc, 'brengen.'].
   
schema_template('http://www.owl-ontologies.com/Red.owl#SeekSupport', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),      
    %Text = [AgensDesc, 'decides to ask', TargetDesc, 'for help'].     
    Text = [AgensDesc, 'besluit om', TargetDesc, 'om hulp te vragen.'].
    
%Text for when there is a target to speak at
schema_template('http://www.owl-ontologies.com/Red.owl#Poison', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),
    schema_target(Schema,Target),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),            
    %Text = [AgensDesc, 'assures', TargetDesc, 'that she will try to poison', PatiensDesc].       
    Text = [AgensDesc, 'vertelt', TargetDesc, 'dat ze', PatiensDesc, 'wil gaan proberen te vergiftigen.'].

%Text for when there is no target to speak at
schema_template('http://www.owl-ontologies.com/Red.owl#Poison', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    %Text = [AgensDesc, 'wants to try to poison', PatiensDesc].       
    Text = [AgensDesc, 'wil', PatiensDesc, 'gaan proberen te vergiftigen.'].

schema_template('http://www.owl-ontologies.com/Red.owl#TellAboutPoisonPlan', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),    
    %Text = ['"Well,', TargetDesc, '" says', AgensDesc, ', "I have a plan to poison', PatiensDesc, '. You just hold on!"'].     
    Text = ['"Ok, ik heb een plan om', PatiensDesc, 'te gaan vergiftigen. Wacht maar af..." zegt', AgensDesc, 'tegen', TargetDesc, '.'].

schema_template('http://www.owl-ontologies.com/Red.owl#BecomeHungry', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'became quite hungry'].
    Text = [AgensDesc, 'krijgt behoorlijk honger.'].

schema_template('http://www.owl-ontologies.com/Red.owl#BakeCake', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema, Patiens),
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    %Text = [AgensDesc, 'bakes', PatiensDesc].
    Text = [AgensDesc, 'bakt', PatiensDesc, '.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#PoisonFood', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    schema_patiens(Schema,Patiens),
    object_template(Patiens, PatiensDesc),    
    %Text = ['With a few drops of cyankali, ', AgensDesc, 'poisons', PatiensDesc].
    Text = ['Met enkele korreltjes cyaankali vergiftigt', AgensDesc, PatiensDesc, '.'].    

schema_template('http://www.owl-ontologies.com/Red.owl#Cry', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'bursts out in tears'].
    Text = [AgensDesc, 'barst in snikken uit.'].

schema_template('http://www.owl-ontologies.com/Red.owl#Laugh', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'bursts out laughing out loud'].
    Text = [AgensDesc, 'barst in lachen uit.'].
        
schema_template('http://www.owl-ontologies.com/Red.owl#ExpressAnger', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    schema_target(Schema,Target),
    object_template(Target, TargetDesc),    
    Text = [TargetDesc, 'heeft', AgensDesc, 'kwaad gemaakt.'].       
    
schema_template('http://www.owl-ontologies.com/Red.owl#ExpressSympathy', Text, Schema) :-
	schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    schema_target(Schema,Target),
    object_template(Target, TargetDesc),        
    Text = ['"Och, och, wat vervelend." zegt', AgensDesc, 'tegen', TargetDesc, '.'].  
    
schema_template('http://www.owl-ontologies.com/Red.owl#WhatToDo', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),   
    %Text = ['What should I do,', PatiensDesc, '", asks', AgensDesc].    
    Text = ['"Wat moet ik doen" vraagt', AgensDesc, 'aan', PatiensDesc, '.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#SkipTo', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),    
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),
    %Text = [AgensDesc, 'skips to', TargetDesc].
    Text = [AgensDesc, 'huppelt naar', TargetDesc, '.'].

schema_template('http://www.owl-ontologies.com/Red.owl#SneakTo', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),    
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),
    %Text = [AgensDesc, 'sneaks to', TargetDesc].
    Text = [AgensDesc, 'sluipt naar', TargetDesc, '.'].
        
schema_template('http://www.owl-ontologies.com/Red.owl#ShuffleTo', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_target(Schema,Target),    
    object_template(Agens, AgensDesc),
    object_template(Target, TargetDesc),
    %Text = [AgensDesc, 'shuffles to', TargetDesc].    
    Text = [AgensDesc, 'schuifelt naar', TargetDesc, '.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#Give', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),    
    %Text = ['"Here you go, ', TargetDesc, '" says', AgensDesc, ', giving', PatiensDesc, 'to', TargetDesc].     
    Text = ['"Alsjeblieft, voor jou!" zegt', AgensDesc, 'en geeft', PatiensDesc, 'aan', TargetDesc, '.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#Thank', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),
    object_template(Target, TargetDesc),    
    %Text = ['"Thanks for', PatiensDesc, '!" says', AgensDesc, 'to', TargetDesc, '.'].     
    Text = ['"Dankjewel voor', PatiensDesc, '!" zegt', AgensDesc, 'tegen', TargetDesc, '.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#Greet', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    %Text = ['"Hello, ', TargetDesc, '" says', AgensDesc].   
    Text = ['"Hallo!" zegt', AgensDesc, 'tegen', PatiensDesc, '.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#GreetBack', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    %Text = ['"Oh, hey, ', TargetDesc, '" says', AgensDesc].       
    Text = ['"Jij ook hallo..." zegt', AgensDesc, 'tegen', PatiensDesc, '.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#GreetAgain', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    %Text = ['"Oh hey, hello again ', TargetDesc, '" says', AgensDesc].       
    Text = ['"Nogmaals hallo!" zegt', AgensDesc, 'tegen', PatiensDesc, '.'].

schema_template('http://www.owl-ontologies.com/Red.owl#TakeFrom', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    %Text = ['"Give me', PatiensDesc, '," says', AgensDesc, 'and forcefully takes it away from', TargetDesc]. 
    %Text = ['"Geef', PatiensDesc, 'maar hier!" zegt', AgensDesc, 'en pakt', PatiensDesc, 'van', TargetDesc, 'af.'].
    Text = ['"Geef hier!" zegt', AgensDesc, 'en pakt', PatiensDesc, 'van', TargetDesc, 'af.'].

schema_template('http://www.owl-ontologies.com/Red.owl#TakeBack', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    %Text = ['"Give back, it's mine!" says', AgensDesc, 'and takes', PatiensDesc, 'back from', TargetDesc, '.']. 
    Text = ['"Geef terug, die is van mij!" zegt', AgensDesc, 'en pakt', PatiensDesc, 'weer terug van', TargetDesc, '.'].

schema_template('http://www.owl-ontologies.com/Red.owl#TakeAgain', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    %Text = ['"Give back!" says', AgensDesc, 'and takes', PatiensDesc, 'back from', TargetDesc, '.']. 
    Text = ['"Geef terug!" zegt', AgensDesc, 'en pakt', PatiensDesc, 'weer terug van', TargetDesc, '.'].
        
schema_template('http://www.owl-ontologies.com/Red.owl#TellSomeoneTookSomething', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    schema_target(Schema,Target),        
    schema_instrument(Schema,Instrument),         
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    object_template(Target, TargetDesc),    
    object_template(Instrument, InstrumentDesc),        
    %Text = ['"Oh', TargetDesc, '", says', AgensDesc, ', "', InstrumentDesc, 'stole', PatiensDesc, 'from me!"'].       
    %Text = ['"Oh', TargetDesc, 'luister,', InstrumentDesc, 'heeft', PatiensDesc, 'van me gestolen! zegt', AgensDesc, '.'].
    Text = ['"Oh', TargetDesc, 'luister!" zegt', AgensDesc, '. "Daarnet heeft', InstrumentDesc, PatiensDesc, 'van me gestolen!"'].
              
    
schema_template('http://www.owl-ontologies.com/Red.owl#Eat', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),    
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    %Text = [AgensDesc, 'eats', PatiensDesc].         
    Text = [AgensDesc, 'eet', PatiensDesc, 'op.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#LeaveBinging', Text, Schema) :-
    schema_agens(Schema,Agens),
    schema_patiens(Schema,Patiens),        
    object_template(Agens, AgensDesc),
    object_template(Patiens, PatiensDesc),    
    %Text = ['"Riiiiiight, " says', AgensDesc, ', "well, I will just let you enjoy your cakes then,', TargetDesc, ', goodbye!"'].       
    Text = ['"Nou', PatiensDesc, 'ik laat je maar met rust." zegt', AgensDesc, '.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#BeMean', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'was mean'].
    Text = [AgensDesc, 'is gemeen.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#Die', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    Text = [AgensDesc, 'gaat langzaam dood.'].                 
    

schema_template('http://www.owl-ontologies.com/Red.owl#WatchBirds', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'watches the birds flying over.'].
    Text = [AgensDesc, 'kijkt naar de overvliegende vogels.'].

schema_template('http://www.owl-ontologies.com/Red.owl#WatchClouds', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'watches the clouds in the sky.'].
    Text = [AgensDesc, 'kijkt naar de wolken in de lucht.'].

schema_template('http://www.owl-ontologies.com/Red.owl#BuildSandCastle', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'builds a sandcastle.'].
    Text = [AgensDesc, 'bouwt een zandkasteel.'].

schema_template('http://www.owl-ontologies.com/Red.owl#RollSand', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'rolls in the sand.'].
    Text = [AgensDesc, 'rolt in het zand.'].

schema_template('http://www.owl-ontologies.com/Red.owl#EnjoySun', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'enjoys the sun.'].
    Text = [AgensDesc, 'geniet van de zon.'].

schema_template('http://www.owl-ontologies.com/Red.owl#TakeNap', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'takes a nap on the beach.'].
    Text = [AgensDesc, 'doet een dutje op het strand.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#Dive', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'dives to the bottom of the lake and swims back up again.'].
    Text = [AgensDesc, 'duikt naar de bodem van het meer en zwemt weer terug omhoog.'].

schema_template('http://www.owl-ontologies.com/Red.owl#TreadWater', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'threads water for a minute.'].
    Text = [AgensDesc, 'watertrappelt een minuut lang.'].

schema_template('http://www.owl-ontologies.com/Red.owl#Fart', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'farts in the water.'].
    Text = [AgensDesc, 'laat een scheet onder water.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#Fart2', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'farts in the water for the second time.'].
    Text = [AgensDesc, 'laat nog een scheet onder water.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#Fart3', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'farts in the water for the last time.'].
    Text = [AgensDesc, 'laat voor de laatste keer een scheet onder water.'].
    
schema_template('http://www.owl-ontologies.com/Red.owl#Dust', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'dusts a cupboard.'].
    Text = [AgensDesc, 'stoft een kast af.'].

schema_template('http://www.owl-ontologies.com/Red.owl#WashDishes', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'washes some the dishes.'].
    Text = [AgensDesc, 'doet een deel van de afwas.'].

% Jeroen-edit
schema_template('http://www.owl-ontologies.com/Red.owl#PickNose', Text, Schema) :-
    schema_agens(Schema,Agens),
    object_template(Agens, AgensDesc),
    %Text = [AgensDesc, 'dusts a cupboard.'].
    Text = [AgensDesc, 'pulkt in haar neus. Bah!'].





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
	