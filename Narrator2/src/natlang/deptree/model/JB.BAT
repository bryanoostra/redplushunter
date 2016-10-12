@rem -c
@echo off
:setjava
set javabase=../../../..
set srcdir=%javabase%/src
set classdir=%javabase%/classes
set libdir=%javabase%/lib
set libpath=
set datapath=;%javabase%/data
set classpath=%classdir%%datapath%%libpath%
set package=natlang.deptree.model
set docsdir=%javabase%/docs/%package%
set otherjavacflags=
set otherjavaflags=
set otherjavadocflags=
:endsetjava
if "%1" == "-java" goto java
if "%1" == "-jr"   goto java
if "%1" == "-j"    goto java
javac -classpath %classpath% -sourcepath %srcdir% -d %classdir% %otherjavacflags% %*
goto exit
:java
shift
set jfile=%1
set arglist=
:getarg
shift
if "%1" == "" goto run
set arglist=%arglist% %jfile%
set jfile=%1
goto getarg
:run
java -classpath %classpath% %otherjavaflags% %arglist% %package%.%jfile%
:exit
