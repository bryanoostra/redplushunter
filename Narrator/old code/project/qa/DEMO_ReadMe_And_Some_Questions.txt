README.txt

Dependency Tree and RST structures for the VST ?

author: Rieks op den Akker

DEMO - show dep trees

project.qa.DEMO is a quick and dirty adaptation of the class ProtoFrame made by Erik Dikkers.
It uses the parlevink.parlegraph package - by Dennis Reidsma, Job Zwiers - to show the 
Dependency Structures. (well you have to rearrange the nodes by hand to see a 'tree'
- and note the subtree sharing! - , since i didn't do anything on the layout and used only the
available viewers !) Note that parlegraph is made for visualizing and editing (nested) graphs.
They are handy if you want to show the graphs you make.
The package is not necessary if you don't need/want to show or edit graphically.

This class uses jdom for reading an XML-spec of a Alpino-Dependency Structure.
I added a static method in Erik's Analyzer class that makes a parlevink.parlegraph.MGRaphAdapter.
It uses Dennis' demo editor class to show the tree - that was adapted to be able to show new trees.
The Analyzer method for reading an XML-spec/Document to create the corresponding MGraph should be non-static and belongs in a class DependencyStructure that extends MGraphAdapter.
(see TO DO below). 
Note that parlegraph does not use jdom but our own xml.XMLizable and XMLTokenizers
for XML reading.
So here is a choice to be made: implement XMLizable interface and use XMLTokenizers or use jdom or use standard java 1.5 xml packages
for reading/writing XML-specs.

Note that the classes - among them: DependencyStructure - in package natlang.deptree.model 
have been designed for CGN dependency structures.
Methods to construct these from a file do not use XML-readers but a format as shown in the cgn-test.txt file
for relations. These classes may be a start for the new CGN Depencency Structures class.

But... do we really want/need this ?


BEFORE WE DO: Some Questions Concerning the NLGenerator of the VST

Feikje wants to use the CGN encodings (is this a hard req.?), where Eric uses Alpino encodings.
Is it only a matter of encoding or are these different
structures? ; if different, on what level do they differ? 
Why should we use CGN (instead of Alpino)?
For the Virtual Story Teller it is not necessary.

A question to be answered before that is: 
What is the use of making dependency structures ? and RST? Needed for the VST?
What is the interface between the NLG-module and the VST-Narrator?
In the current version of the VST (Sander Rensen) the NLGenerator uses Templates.
The Narrator receives jade.ACLMessages the content of which are Objects that are members of some ontology.
Can we use Arjan Egges' expressions for events communicated by messages, instead?

AN ASIDE: What (background) knowledge should the Narrator have to make interesting stories ?

The Narrator can use following sorts of knowledge for telling the story.
(most is shared with other agents in the system)

static (loaded upon creation of the Narrator)
- knowledge of the characters in the story (static): names, properties and attributes, ways to refer to the characters.
- knowledge of actions that they can perform: properties, different types of actions, maybe reasons why such action are
  done, and what one usually aims at when doing these actions: 
  	flee, hit, run away, run to, bump into, speak to, think/belief, long for, hope that, wants to, dislikes
  	(see what verbs occur in stories) 
- knowlegde of events that can happen in the story and what type of events they are and what there
  impact can be.
- knowledge about emotions (directed to or not directed to) types, and their properties: positive, negative.
- knowledge about needs, feelings hungry, thursty, painfull
Of course these knowledge should be related - directly or with frames - to NL phrases
(The Grammar and the Lexicon that Feikje wants to design).


dynamic 
-  communicated when actions/events ha: 
-  discourse representation: what has been told;
		- include some rhetorical indication of what was told (function): 
		  intro character: characters that have been introduced, reported events, tell cause, aim,
		  tell mood, emotion, feelings, thoughts of characters.
		- include info about the stage of the story (plot)
-  history of events that has happened (not everything need to be told immediately)


TO DO at least if you want to use - and show - DSs and RSs.

The classes have to be reorganized to make as much use as possible of the methods
implemented in parlevink.parlegraph

Is is really worth the job to recorganize the code for CGN-dependency structures?
Do we have XML-specs (and an XML-scheme or DTD ) of CGN-dep trees?
How do we get them (is there a parser available?).

In principe all necessary methods are there; the job to do is re-organisation.
What has to be dove furthe rdependes on the answers of questions above.

1) reorganize Erik's code especially Analyzer; move to the DependencyStructure class what
   belongs there.

2) Write different readers and writers for CGN and Alpino encoded dep structures.
   then you can use the same DependencyStructure class for both structrues, i guess.
   See what type of operations/transformers on Dependency Structures need to be defined
   for the Surface Realizer.
   
   
3) Therefore you should write the interface for the SurfaceRealizer
   and different class for the various Transformers.
   It's important to make a clear picture of how you organize this. 
   What types of transformations are needed on rhetorical structures ?

4) Make viewers specific for these DependencyStructures.

5) Make the package natlang.rstree for Rhetorical Structures.
   Start with the models. (see Dennis' help files how to proceed)
   R(hetorical)Structures are MGRaphAdapters.
   RStructures are of two types:
   - BasisRStrucures are the leaves of the structure; they wrap a Dependency Structure.
   - ComplexRStructures are LabeledEdges - directed - the label being the RST relation.
   	source and target of the edges are RStructures again. 
   

   

