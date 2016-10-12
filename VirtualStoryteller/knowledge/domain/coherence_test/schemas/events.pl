% A water supply is out of water.
event_schema([
	type(ps:'OutOfWater'),
	arguments([	agens(Supply) ]),
	preconditions([
		% There is a full water supply
		condition(true, [
			rule(Supply, owlr:typeOrSubType, ps:'WaterSupply'),
			fact(Supply, swc:hasAttribute, swc:full)
		])
	]),
	effects([
		% It is now empty
		condition(true, [			
			fact(Supply, swc:hasAttribute, swc:empty)
		]),
		% And no longer true
		condition(false, [			
			fact(Supply, swc:hasAttribute, swc:full)
		])
	])
]).

event_schema([
	type(ps:'GetThirsty'),
	arguments([ agens(Ag) ]),
    preconditions([

        % There exists a pirate that was not thirsty
	    condition(true, [
        	rule(Ag, owlr:typeOrSubType, ps:'Pirate')
        ]),
        condition(false, [
        	fact(Ag, swc:hasAttribute, ps:thirsty)
        ])
    ]),
   
    effects([
        % Pirates can "just" become thirsty
        condition(true, [
        	fact(Ag, swc:hasAttribute, ps:thirsty)
        ])
    ])
]).	
		