@echo off
set base=..\..\..
set package=natlang.structure
set packagepath=natlang.structure
set gendocallowed=false
set classpathflag=-classpath %base%\classes;%base%\lib;
set sourcepathflag=-sourcepath %base%/src
set dflag=-d %base%\classes
set docdflag=-d %base%\docs\%packagepath%
set doclink1=-link http://parlevink.cs.utwente.nl/java/docs/api
set linkflag=%doclink1% %doclink2%
set otherjavacflags=
set otherjavaflags=
set otherjavadocflags=
set jcflags=%classpathflag% %sourcepathflag% %dflag% %otherjavacflags%
set jrflags=%classpathflag% %otherjavaflags%
set gendocflags=%classpathflag% %sourcepathflag% %docdflag% %linkflag% %otherjavadocflags% @packages
