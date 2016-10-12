% Een thread heeft precondities, en definieert setting information, die variabelen kan bevatten.

% :- episode(head(Vars), [pos-precs], [neg-precs], [code]).
   	
thread_schema([
	type(commit:'juvi'),
	preconditions([
		condition(true, [
			rule(Henk, owlr:typeOrSubType, commit:'Human')
		])
	]),
	characters([
		character(Henk),
		character(commit:ingrid),
		character(commit:juvi1),
		character(commit:juvi2)
	]),
		location([]),
	settings([]),
	goals([])
]).
		