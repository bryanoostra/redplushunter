Readme file for the Narrator module

Requirements:
- Java 1.5

Installing:
- Copy the whole directory to the directory [your_path]
- Add the following files to your classpath (from the directory bin):
	- antlr.jar
	- icu4j.jar
	- jakarta-oro-2.0.5.jar
	- jdom.jar
	- jena.jar
	- jtp.jar
	- log4j.jar
	- xercesImpl.jar
- Add [your_path]/data to your classpath

Running:
- Call one of the following commands from the directory [your_pat]/classes:
	- java natlang.rdg.Narrator
		This class reads a fabula stored as OWL file and turns this into a Dutch text.
	- java natlang.rdg.testclasses.StoryTester *filename*
		This class reads a file with plot elements and relations and turns this into a Dutch text.
		The argument *filename* may be one of the following options: 'knight', 'amalia' or 'mood'.
		If no argument is specified the knight example is generated.

