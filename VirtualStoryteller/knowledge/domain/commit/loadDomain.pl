% Only executable if load.pl was loaded
:- ensure_loaded('../../prolog/load.pl').

% Define Namespaces for domain
:- rdf_db:rdf_register_ns(commit, 'http://www.ontology.com/commit.owl#').
:- rdf_db:rdf_load('ontology/commit.owl').
:- rdf_db:rdf_load('setting/setting.ttl').

% Load schemas into schema management module
:- consult(schemas:'schemas/schema.pl').

% TODO: bring episode and suggestion clauses under in their respective modules too.
:- consult(schemas:'threads/threads.pl').
:- consult('suggestions/suggestion.pl').

% Narration
:- consult(narrator:'narration/narration.pl').
:- consult(narrator:'narration/narration_category.pl').
:- consult(narrator:'narration/narration_imperative.pl').