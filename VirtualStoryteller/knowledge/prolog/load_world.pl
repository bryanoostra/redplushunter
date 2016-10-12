% ---------------------------------------------------------------------------------
% Copyright (C) 2008 Human Media Interaction - University of Twente
%  
% This file is part of The Virtual Storyteller.
% 
% The Virtual Storyteller is free software: you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation, either version 3 of the License, or
% (at your option) any later version.
% 
% The Virtual Storyteller is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
% 
% You should have received a copy of the GNU General Public License
% along with The Virtual Storyteller. If not, see <http://www.gnu.org/licenses/>.
% ---------------------------------------------------------------------------------

% ------------------------------------------
% load_world.pl
% ------------------------------------------
%
% Sets up the knowledge base and prolog files necessary to have a basic setup for a world agent.
% Can also be used independently.
%
% Author: Ivo Swartjes
% Date:   11 mrt 2008
% ------------------------------------------

:- consult('load.pl').

% Set up world agent
:- nl,writeln('# Loading world agent files...'),nl.
:- use_module('WorldAgent/BasicWorldAgent.pl').
