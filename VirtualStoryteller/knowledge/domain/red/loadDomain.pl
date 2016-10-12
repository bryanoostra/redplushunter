% Only executable if load.pl was loaded
:- ensure_loaded('../../prolog/load.pl').

% Define Namespaces for domain
:- rdf_db:rdf_register_ns(red, 'http://www.owl-ontologies.com/Red.owl#').
:- rdf_db:rdf_load('ontology/Red.owl').

%Setting (names of Characters, Locations and Objects)
%:- rdf_db:rdf_load('setting/setting.ttl').
:- rdf_db:rdf_load('setting/dutch/setting.ttl').

% Load schemas into schema management module
:- consult(schemas:'schemas/schema.pl').

% Narrator English
%:- consult(narrator:'narration/narration.pl').
%:- consult(narrator:'narration/narration_imperative.pl').
%:- consult(narrator:'narration/narration_category.pl').

% Narrator Dutch
:- consult(narrator:'narration/dutch/narration.pl').
:- consult(narrator:'narration/dutch/narration_imperative.pl').
:- consult(narrator:'narration/dutch/narration_category.pl').

% TODO: bring episode and suggestion clauses under in their respective modules too.
:- consult(schemas:'threads/threads.pl').
:- consult('suggestions/suggestion.pl').
