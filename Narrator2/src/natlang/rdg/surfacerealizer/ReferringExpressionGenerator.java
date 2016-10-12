package natlang.rdg.surfacerealizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import narrator.lexicon.CharacterInfo;
import narrator.lexicon.LexicalChooser;
import natlang.rdg.RSDepTreeCreator;
import natlang.rdg.discourse.Entity;
import natlang.rdg.discourse.EntityHistory;
import natlang.rdg.lexicalchooser.BelongToElement;
import natlang.rdg.lexicalchooser.ChosenAdjectives;
import natlang.rdg.lexicalchooser.ContainsElement;
import natlang.rdg.lexicalchooser.EntityAdjectivePair;
import natlang.rdg.lexicalchooser.Hierarchy;
import natlang.rdg.lexicalchooser.PartOfElement;
import natlang.rdg.lexicalchooser.PossibleAdjectives;
import natlang.rdg.lexicalchooser.Property;
import natlang.rdg.lexicalchooser.RelationElement;
import natlang.rdg.libraries.LibraryConstants;
import natlang.rdg.model.MOrderedEdge;
import natlang.rdg.model.OrderedDepTreeModel;
import natlang.rdg.model.RSDepTreeNode;
import parlevink.parlegraph.model.MLabeledEdge;

import narrator.reader.CharacterModel;
import narrator.lexicon.Entry;
import narrator.shared.Settings;

//import com.hp.hpl.jena.ontology.OntModel;

/**
 * This component is responsible for the generation of adequate and varied referring
 * expressions, and is part of the Surface Realizer.
 * 
 * @author Nanda Slabbers
 * @author Marissa Hoek
 *
 */
public class ReferringExpressionGenerator implements RDGTransformer,
		LibraryConstants {
	EntityHistory history;

	CharacterInfo characters;

	OrderedDepTreeModel tree; // dependency tree currently being transformed 

	Vector ents; // entities already mentioned in current sentence

	Vector clents; // entities already mentioned in current clause

	PossibleAdjectives pa;

	ChosenAdjectives ca;

	Hierarchy ha;

	LexicalChooser lc;

	RSDepTreeCreator dtc;

	InferenceModule im;

	boolean cause; // indicates whether the current dependency tree contains 

	// a cause relation (used for pronominalization)
	boolean parallelism; // indicates whether two clauses in the current 

	// dependency tree appear in strong parallelism (su and obj1 are the same), 
	// also used for pronominalization
	boolean parallelism2; // indicates whether the current dependency tree appears in strong parallelism 

	// with the previous dependency tree (additional requirement: both trees
	// represent simple sentences)
	String prevsu; // subject of the previous sentence (used to check for strong parallelism between two consecutive sentences) 

	String prevobj; // object of the previous sentence (used to check for strong parallelism between two consecutive sentences)

	/**
	 * Constructor - Initializes global variables
	 *
	 */
	public ReferringExpressionGenerator(CharacterInfo characters) {
		history = new EntityHistory();
		this.characters = characters;
		pa = new PossibleAdjectives(characters, null);
		ca = new ChosenAdjectives();
		ha = new Hierarchy();
		cause = false;
		parallelism = false;
		parallelism2 = false;
		prevsu = "";
		prevobj = "";
		//if (Settings.BRIDGING)
		im = new InferenceModule(null, history, characters, ha);
	}

	public void setTree(OrderedDepTreeModel deptree) {
		tree = deptree;
	}

	public void setLexicalChooser(LexicalChooser l) {
		lc = l;
		dtc = new RSDepTreeCreator(lc);
	}

	/**
	 * Starts the transformation
	 */
	public boolean transform() throws Exception {
		ents = new Vector();
		clents = new Vector();
		history.newSentence();
		cause = false;
		parallelism = false;

		if (!check())
			return false;

		// check for one of the types of parallelism (used for pronominalization algorithm)
		checkForParallelism();
		checkForParallelism2();

		if (tree.getRoot().getData().get("cat").equals("smain")) {
			clents = new Vector();
			history.newClause();
		}
		
		//System.out.println("Tree: "+tree);

		transform(tree.getRoot());
		return true;
	}

	/**
	 * Transforms the dependency graph depth first, which can be achieved using the 
	 * ids of the edges (since the trees have already been ordered)
	 * @param node
	 */
	public void transform(RSDepTreeNode node) {

		int cntr = 0;

		while (true) {
			MOrderedEdge edge = tree.getOrderedEdge(node, cntr);

			if (edge == null)
				break;

			cntr++;

			RSDepTreeNode source = (RSDepTreeNode) edge.getSource();
			RSDepTreeNode target = (RSDepTreeNode) edge.getTarget();
			
			//System.out.println(target.getData());
			
			if (Settings.REFEXP) {
			// recognize the beginning of a clause 
			if (target.getData().get("cat") != null
					&& (target.getData().get("cat").equals("smain") || target
							.getData().get("cat").equals("ssub"))) {
				clents = new Vector();
				history.newClause();
			}

			if (target.getData().get("root") != null) {
				// check if the dependency tree contains a cause-relation (used for pronominalization algorithm)
				if (target.getData().get("pos").equals("comp")) {
					if (target.getData().get("root").equals("omdat")
							|| target.getData().get("root").equals("want")
							|| target.getData().get("root").equals(
									"doordat")
							|| target.getData().get("root").equals("zodat")
							|| target.getData().get("root").equals("dat"))
						cause = true;
					else
						cause = false;
				}
			}

			// if the current node is an entity node, this node should be replaced by an adequate referring expression
			if (target.getData().get("pos") != null
					&& target.getData().get("pos").equals("ent")) {
				String entity = target.getData().get("root");
				CharacterModel ch = characters.getChar(entity);
				Entity ent = history.getEntity(entity);

				System.out.println("Generate referring expression: "
						+ entity);
				
				boolean focalization = Settings.FOCALIZATION&&entity.equals(Settings.FOCALIZEDCHARACTER)&&!Settings.PERSPECTIVE.equals(THIRD);
					if (ch != null) {
						String lb = edge.getLabel();
						String gender = ch.getGender();
						String concept = ch.getConcept();
						String noun = "";

						if (Settings.BRIDGING && ent == null)
							im.tell(concept, entity);

						// main algorithm
						// step 1: choose the kind of expression
						boolean pron = pronominalize(source, target, gender);

						RSDepTreeNode refexpnode = null;

						// if a pronoun can be used: generate correct pronoun
						if (focalization || (pron /*&& Math.random()<Settings.PRONOUNCHOICE*/)) {
							// update entity history
							if (ent!=null){
								ent.incProns();
								ent.resetSents();
							} else {
								Entity e = new Entity(entity, concept, gender);
								history.addEntity(e);
							}

							String det = "";
							Entry entr = lc.getEntry(concept, false);
							if (entr != null)
								det = entr.getDet();

							// get pronoun based on grammatical role, gender and possibly determiner
							String pronoun;
							if (focalization) pronoun = this.getPronounFocalization(lb);
							else pronoun = getPronoun(lb, gender, det);
							//System.out.println(lb+", "+gender+", "+det);
							System.out.println(" RZ: pronoun: " + pronoun);
							// remove old entity node from tree
							tree.removeMVertex(target);
							tree.removeMEdge(edge);

							// generate new node(s) for the referring expression and add them to the tree
							refexpnode = dtc.createPronNode(lb, pronoun);
							tree.addMEdge(createOrderedEdge(lb, node,
									refexpnode, cntr - 1));
						} else // if no pronoun can be used: generate noun phrase
						{
							if (ent != null)
								ent.resetProns();

							// check if the name will be used (currently random if the referring expression does not include any adjectives)
							if (ent != null
									&& target.getAdjs() != null
									&& target.getAdjs().size() == 0
									&& !source.getData().get("cat")
											.equals("np") && useName(entity)) {
								ent.resetSents();

								// if the expression should only include the name
								if (onlyName(entity)) {
									// retrieve name from character info
									String name = "";
/*									Entry entr = lc
											.getEntry(ch.getName(), true);
									if (entr != null)
										name = entr.getRoot();*/
									
									//actually retrieve it from CharacterInfo
									name = ch.getName();

									// remove old entity node from tree 
									tree.removeMVertex(target);
									tree.removeMEdge(edge);

									// generate new node(s) for the referring expression and add them to the tree
									refexpnode = dtc.createNameNode(lb, name);
									tree.addMEdge(createOrderedEdge(lb, node,
											refexpnode, cntr - 1));
								} else // if the expression should also include the noun
								{
									String name = "";

									// retrieve noun
									Entry entr1 = lc.getEntry(concept, true);
									if (entr1 != null)
										noun = entr1.getRoot();

									// retrieve name
									Entry entr2 = lc.getEntry(ch.getName(),
											true);
									if (entr2 != null)
										name = entr2.getRoot();

									// remove old entity node from tree
									tree.removeMVertex(target);
									tree.removeMEdge(edge);

									// generate new (node) for the referring expression and add them to the tree
									refexpnode = createNameNpNode(lb, entity,
											noun, name);
									tree.addMEdge(createOrderedEdge(lb, node,
											refexpnode, cntr - 1));
								}
							} else // if the referring expression does not include the name:
							{ // generate a somewhat more complex referring expression
								// including a determiner and possibly some adjectives
								// noun?
								System.out.println(" RZ: np-node: " + lb
										+ " - " + entity);
								refexpnode = createNpNode(lb, entity, target
										.getAdjs());

								// if node in np: other child nodes from source should be added to
								// the referring expression (such as a relative clause!)
								if (source.getData().get("cat").equals("np")) {
									if (!target.getData().get("rel").equals(
											"modb")) {
										// remove old entity node from the tree
										tree.removeMVertex(target);
										tree.removeMEdge(edge);

										// store childnodes from source in order to add them to the refexp later
										Iterator oldnodes = source
												.getChildNodes();

										// remove these childnodes from tree
										Iterator on = source.getChildNodes();
										while (on.hasNext()) {
											RSDepTreeNode n = (RSDepTreeNode) on
													.next();
											tree.removeMEdge(n
													.getIncomingEdge());
										}

										// add the new node (the referring expression) to the tree and keep the label of the final node that's added to the tree 
										int idx = 0;
										Iterator newnodes = refexpnode
												.getChildNodes();
										while (newnodes.hasNext()) {
											RSDepTreeNode newnode = (RSDepTreeNode) newnodes
													.next();
											tree.addMEdge(createOrderedEdge(
													newnode.getIncomingLabel(),
													source, newnode, idx));
											idx++;
										}

										// finally add the old nodes (stored earlier) to the tree with the correct labels										
										while (oldnodes.hasNext()) {
											RSDepTreeNode oldnode = (RSDepTreeNode) oldnodes
													.next();
											tree.addMEdge(createOrderedEdge(
													oldnode.getIncomingLabel(),
													source, oldnode, idx));
											idx++;
										}

										tree.removeMVertex(refexpnode);
									} else {
										// remove old entity node from the tree										
										tree.removeMVertex(target);
										tree.removeMEdge(edge);

										// remove these childnodes from tree
										int idx = 0;
										Iterator on = source.getChildNodes();
										while (on.hasNext()) {
											on.next();
											idx++;
										}
										tree.addMEdge(createOrderedEdge("modb",
												source, refexpnode, idx));
									}

									refexpnode = source;
								} else {
									// remove old entity node from tree
									tree.removeMVertex(target);
									tree.removeMEdge(edge);

									// add new expression to the tree
									tree.addMEdge(createOrderedEdge(lb, node,
											refexpnode, cntr - 1));
								}
							}
						}

						// update salience value of the entity currently being turned into a referring expression
						updateSalience(refexpnode, entity);

						if (ent != null) {
							ent.setFirst(false);
							if (noun != null && !noun.equals(""))
								ent.addNoun(noun);
							addEntity(ent);
						} else {
							Entity tmp = new Entity(entity, concept, gender);
							tmp.setFirst(false);
							if (noun != null && !noun.equals(""))
								ent.addNoun(noun);
							history.addEntity(tmp);
							addEntity(tmp);
						}
					
					} else // if the entity is not in the character info, simply use the entity
					{
						System.out.println("Use just entity...");
						String name = target.getData().get("root");
						
						Entry e = null;
						try{
							e = lc.getEntry(name,true);
						} catch(NullPointerException exception){
							//if it's not found, just use the word and treat it as a noun
							e = new Entry();
							e.setConcept(name);
							e.setRoot(name);
							e.setPos("noun");
						}

						if (e != null) {
							target.getData().set("pos", "nom");
							target.getData().set("root", e.getRoot());
						}
					}
					
				} else
					transform(target); // call this function recursively
			} else // if no referring expressions should be used (based on parameter in Narrator):
			{ // simply generate an expression containing a determiner and a noun
				// algorithm is similar to the one described above
				if (target.getData().get("pos") != null
						&& target.getData().get("pos").equals("ent")) {
					String entity = target.getData().get("root");
					CharacterModel ch = characters.getChar(entity);

					if (ch != null) {
						String lb = edge.getLabel();
						RSDepTreeNode refexpnode = createNpNode(lb, entity,
								target.getAdjs());

						if (source.getData().get("cat").equals("np")) {
							tree.removeMVertex(target);
							tree.removeMEdge(edge);

							Iterator oldnodes = source.getChildNodes();

							Iterator on = source.getChildNodes();
							while (on.hasNext()) {
								RSDepTreeNode n = (RSDepTreeNode) on.next();
								tree.removeMEdge(n.getIncomingEdge());
							}

							int idx = 0;
							Iterator newnodes = refexpnode.getChildNodes();
							while (newnodes.hasNext()) {
								RSDepTreeNode newnode = (RSDepTreeNode) newnodes
										.next();
								tree.addMEdge(createOrderedEdge(newnode
										.getIncomingLabel(), source, newnode,
										idx));
								idx++;
							}

							while (oldnodes.hasNext()) {
								RSDepTreeNode oldnode = (RSDepTreeNode) oldnodes
										.next();
								tree.addMEdge(createOrderedEdge(oldnode
										.getIncomingLabel(), source, oldnode,
										idx));
								idx++;
							}

							refexpnode = source;
						} else {
							tree.removeMVertex(target);
							tree.removeMEdge(edge);
							tree.addMEdge(createOrderedEdge(lb, node,
									refexpnode, cntr - 1));
						}
					} else {
						Entry e = null;
						try{
							e = lc.getEntry(target.getData().get("root"),
								true);
						} catch(NullPointerException exception){
							//if it's not found, just use the word and treat it as a noun
							String name = target.getData().get("root");
							e = new Entry();
							e.setConcept(name);
							e.setRoot(name);
							e.setPos("noun");
						}
						
						if (e != null)
						{
							target.getData().set("pos", "nom");
							target.getData().set("root", e.getRoot());
						}
					}
				} else
					transform(target);
			}
		}
	}

	public boolean check() {
		if (tree == null)
			return false;
		return true;
	}

	public Iterator getResult() {
		List l = new ArrayList();
		l.add(tree);
		return l.iterator();
	}

	/**
	 * Creates a referring expression containing the noun and the name (such as 'prinses amalia')
	 * @param rel
	 * @param entity
	 * @param noun
	 * @param name
	 */
	public RSDepTreeNode createNameNpNode(String rel, String entity,
			String noun, String name) {
		RSDepTreeNode npNode = dtc.createCatNode(rel, "np");
		RSDepTreeNode nounNode = dtc.createNounNode(entity, noun);
		RSDepTreeNode nameNode = dtc.createNameNode("modb", name);

		tree.addMEdge(createOrderedEdge("hd", npNode, nounNode, 0));
		tree.addMEdge(createOrderedEdge("modb", npNode, nameNode, 1));

		return npNode;
	}

	/**
	 * Create NP node, a referring expression consisting of a determiner, a number
	 * of adjectives (possibly 0) and a noun
	 * @param label
	 * @param entity
	 * @param adj
	 */
	public RSDepTreeNode createNpNode(String label, String entity, Vector adj) {
		System.out.println("Creating NP node: "+label+", "+entity);
		
		// if there is a relation, partof, contains or belongto-element specified, 
		// this node is created recursively
		RSDepTreeNode relnode = null; 
		String relnodeWord = "van";
		
		String det = "";
		String noun = "";
		Vector adjs = new Vector();

		CharacterModel ch = characters.getChar(entity);
		Entity ent = history.getEntity(entity);
		String concept = ch.getConcept();
		String gender = ch.getGender();

		// if a more general concept may be used, update concept
		if (Settings.BRIDGING && useMoreGeneralConcept(entity, concept)) {
			Vector posscon = im.getPossibleConcepts(entity);
			if (posscon != null && posscon.size() > 1)
				concept = (String) posscon.elementAt(1);
		}

		// select a noun from the lexicon
		Entry entr = lc.getEntry(concept, true);
		if (entr != null)
			noun = entr.getRoot();

		// get the entity's salience value
		int salience = 0;
		if (ent != null)
			salience = ent.getSalience();

		if (ent != null)
			ent.resetSents();

		// select the correct determiner
		System.out.println(entr);
		
		if ((ent != null || im.unique(entity)) && entr != null)
			det = entr.getDet();
		else{
			//if(entr.isMassNoun())
			//	det = "";
			//else
				det = "een";
		}
		// check if a relation/belongto/partof element has been specified
		RelationElement re = ha.getRelationElement(entity);
		BelongToElement bte = ha.getBelongToElement(entity);
		PartOfElement poe = ha.getPartOfElement(entity);
		ContainsElement ce = ha.getContainsElement(entity);
		
		// if a relation element is specified: change noun and add expression for other element
		// example: king01 -> de vader van de prinses (or 'haar vader')
		if (re != null && history.getEntity(re.getEnt2()) != null) {
			Entry entrnew = lc.getEntry(re.getConc(), true);
			if (entrnew != null) {
				noun = entrnew.getRoot();
				det = entrnew.getDet();
			}

			if (highestSalience(re.getEnt2(), "") && Settings.REFEXP) {
				CharacterModel ch2 = characters.getChar(re.getEnt2());
				det = getPronoun("poss", ch2.getGender(), "");
			} else
				relnode = createNpNode("obj1", re.getEnt2(), new Vector());
		}
		// if belong to element has been specified: add referring expression for other entity
		// example: bedroom01 -> de slaapkamer van de prinses (or haar slaapkamer)
		else if (bte != null && ent == null) {
			int bridge = InferenceModule.VANINDEF;
			if (Settings.BRIDGING) //) && history.getEntity(bte.getEnt2()) != null)
				bridge = im.chooseKindOfExpression(bte.getEnt2(), entity,
						concept, getAllEntities());

			if (bridge == InferenceModule.NOVANDEF || bridge == InferenceModule.VANDEF)
				det = entr.getDet();
			
			
			// if bte.getEnt2 has the highest salience, do not 
			// use a van-clause
			if (highestSalience(bte.getEnt2(), "") && Settings.REFEXP) {
				CharacterModel ch2 = characters.getChar(bte.getEnt2());
				det = getPronoun("poss", ch2.getGender(), "");
			} else if(history.getEntity(bte.getEnt2()) != null &&
					uniqueBelongToGivenSaliences(bte.getEnt1())){ //checkPartOfRelations
				if(bridge == InferenceModule.VANINDEF)
					bridge = InferenceModule.NOVANINDEF;
				if(bridge == InferenceModule.VANDEF)
					bridge = InferenceModule.NOVANDEF;
			} else
				relnode = createNpNode("obj1", bte.getEnt2(), new Vector());
		}
		// if part of element specified: check if a bridging description can be used,
		// otherwise add referring expression for the other entity
		// example gate01 -> de poort van het kasteel, of de poort
		else if (poe != null && ent == null) {
			System.out.println("PART OF ELEMENT? " + poe);
						
			int bridge = InferenceModule.VANINDEF;
			if (Settings.BRIDGING) // && history.getEntity(poe.getEnt2()) != null)
				bridge = im.chooseKindOfExpression(poe.getEnt2(), entity,
						concept, getAllEntities());

			if (bridge == InferenceModule.NOVANDEF || bridge == InferenceModule.VANDEF)
				det = entr.getDet();

//			if (bridge > 1) {
				if (highestSalience(poe.getEnt2(), "") && Settings.REFEXP) {
					CharacterModel ch2 = characters.getChar(poe.getEnt2());
					det = getPronoun("poss", ch2.getGender(), "");
				} 
				else if(history.getEntity(poe.getEnt2()) != null &&
						uniquePartOfGivenSaliences(poe.getEnt1())){ //checkPartOfRelations
					
					if(bridge == InferenceModule.VANINDEF)
						bridge = InferenceModule.NOVANINDEF;
					if(bridge == InferenceModule.VANDEF)
						bridge = InferenceModule.NOVANDEF;
				} else
					relnode = createNpNode("obj1", poe.getEnt2(), new Vector());
//			}
		}
		else if (ce != null && ent == null) {
			System.out.println("PART OF ELEMENT? " + ce);
						
			int bridge = InferenceModule.VANINDEF;
			if (Settings.BRIDGING) // && history.getEntity(ce.getEnt2()) != null)
				bridge = im.chooseKindOfExpression(ce.getEnt2(), entity,
						concept, getAllEntities());

			if (bridge == InferenceModule.NOVANDEF || bridge == InferenceModule.VANDEF)
				det = entr.getDet();

//			if (bridge > 1) {
				if (highestSalience(ce.getEnt2(), "") && Settings.REFEXP) {
					CharacterModel ch2 = characters.getChar(ce.getEnt2());
					det = getPronoun("poss", ch2.getGender(), "");
				} 
				else if(history.getEntity(ce.getEnt2()) != null &&
						uniqueContainsGivenSaliences(ce.getEnt1())){ //checkPartOfRelations
					
					if(bridge == InferenceModule.VANINDEF)
						bridge = InferenceModule.NOVANINDEF;
					if(bridge == InferenceModule.VANDEF)
						bridge = InferenceModule.NOVANDEF;
				} else{
					relnode = createNpNode("obj1", ce.getEnt2(), new Vector());
					relnodeWord = "uit";
				}
//			}
		}
		// update entity history: if entity has already been mentioned, simply add the
		// noun the entity object, otherwise add new entity object to the history
		if (ent != null) {
			ent.setFirst(false);
			ent.addNoun(noun);
		} else {
			Entity tmp = new Entity(entity, concept, gender);
			tmp.addNoun(noun);
			tmp.setFirst(false);
			history.addEntity(tmp);
		}

		// add the different types of adjectives!
		Vector dist = getDistractorSet(entity, concept, salience);

		// add distinguishing adjectives
		if (dist.size() > 0) {
			Vector tmpadjs = getDistinguishingAdjectives(ch, dist);
			for (int i = 0; i < tmpadjs.size(); i++)
				adjs.add((String) tmpadjs.elementAt(i));
		}

		if (Settings.ADDADJECTIVES) {
			// if first reference to entity; add static properties from character info
			if (ent == null) {
				Vector tmpadjs = ch.getStatProps();
				for (int i = 0; i < tmpadjs.size(); i++) {
					Property p = (Property) tmpadjs.elementAt(i);
					adjs.add(p.getValue());
				}
			}

			// add document planner adjectives (internal states)
			if (adj != null)
				for (int i = 0; i < adj.size(); i++)
					adjs.add((String) adj.elementAt(i));

			// if first reference in sentence, get adjectives from other references within same sentence
			Vector othadj = getNewAdjectives(entity, adjs);
			for (int i = 0; i < othadj.size(); i++)
				adjs.add((String) othadj.elementAt(i));

			// add remaining adjectives (from list with possible adjectives)
			if (adjs.size() == 0) {
				String a = ca.getAdjective(entity);
				if (!a.equals("")) {
					//chance of 25% that adjective is added
					double d = Math.random();
					if (d >= 0.75)
						adjs.add(a);
				} else {
					a = pa.getPossibleAdjective(entity);
					if (!a.equals("")) {
						ca.addEAP(new EntityAdjectivePair(entity, a));
						adjs.add(a);
					}
				}
			}
		}

		// check whether the adjectives have to be inflected		
		// has to be done in all cases except when the determiner is 'een' and the
		// original determiner was 'het':
		// een mooiE jongen		de mooiE jongen
		// een mooi meisje		het mooiE meisje
		boolean morph = true;
		if (det.equals("een")) {
			String ordet = lc.getOriginalDeterminer(concept, noun);
			if (ordet.equals("het"))
				morph = false;
		}

		// build the tree: generate all needed nodes and combine them into one refexp node 
		RSDepTreeNode result = dtc.createCatNode(label, "np");

		RSDepTreeNode modnode = null;
		boolean first = true;
		for (int i = 0; i < adjs.size(); i++) {
			RSDepTreeNode adjnode = null;

			String tmpadj = (String) adjs.elementAt(i);
			//adverbs don't work anymore
/*			String adv = pa.getPossibleAdverb(tmpadj);
			if (!adv.equals("")) {
				adjnode = dtc.createCatNode("mod", "ap");
				RSDepTreeNode adjnode2 = dtc.createAdjNode(tmpadj, "adj", "hd",
						morph);
				RSDepTreeNode advnode = dtc.createAdjNode(adv, "adj", "mod",
						false);
				tree.addMEdge(createOrderedEdge("mod", adjnode, advnode, 0));
				tree.addMEdge(createOrderedEdge("hd", adjnode, adjnode2, 1));
			} else*/
			adjnode = dtc.createAdjNode(tmpadj, "adj", "mod", morph);
			if (first) {
				first = false;
				modnode = adjnode;
			} else
				modnode = createConjNode(adjnode, modnode);
		}

		RSDepTreeNode detnode = dtc.createDetNode(det);
		RSDepTreeNode nounnode = dtc.createNounNode(entity, noun);
		RSDepTreeNode ppnode = null;
		RSDepTreeNode prepnode = null;

		if (relnode != null) {
			ppnode = dtc.createCatNode("modb", "pp");
			prepnode = dtc.createPrepNode(relnodeWord); // used to be "van" istead of relnodeWord

			tree.addMEdge(createOrderedEdge("hd", ppnode, prepnode, 0));
			tree.addMEdge(createOrderedEdge("obj1", ppnode, relnode, 1));
		}

		tree.addMEdge(createOrderedEdge("det", result, detnode, 0));
		if (modnode == null) {
			tree.addMEdge(createOrderedEdge("hd", result, nounnode, 1));
			if (ppnode != null)
				tree.addMEdge(createOrderedEdge("modb", result, ppnode, 2));
		} else {
			tree.addMEdge(createOrderedEdge("mod", result, modnode, 1));
			tree.addMEdge(createOrderedEdge("hd", result, nounnode, 2));
			if (ppnode != null)
				tree.addMEdge(createOrderedEdge("modb", result, ppnode, 3));
		}

		return result;
	}

	/**
	 * Creates a conjunction node, needed if a noun has two adjectives, such as 'de mooie EN lieve prinses'
	 * @param adjNode
	 * @param modNode
	 */
	public RSDepTreeNode createConjNode(RSDepTreeNode adjNode,
			RSDepTreeNode modNode) {
		RSDepTreeNode result = dtc.createCatNode("mod", "conj");
		RSDepTreeNode crdNode = dtc.createCrdNode();

		tree.addMEdge(createOrderedEdge("crd", result, crdNode, 1));
		tree.addMEdge(createOrderedEdge("cnj", result, modNode, 0));
		tree.addMEdge(createOrderedEdge("cnj", result, adjNode, 2));

		return result;
	}

	/**
	 * Creates an ordered edge
	 * @param label
	 * @param src
	 * @param tgt
	 * @param position
	 */
	public MOrderedEdge createOrderedEdge(String label, RSDepTreeNode src,
			RSDepTreeNode tgt, int position) {
		MLabeledEdge edge = new MLabeledEdge(label);
		edge.setSource(src);
		edge.setTarget(tgt);

		MOrderedEdge oedge = new MOrderedEdge(edge);
		oedge.setPosition(position);

		return oedge;
	}

	/**
	 * Get the distractor set; the set of entities which the current entity should be distinguished from
	 * This is the set of entities which belong to the same concept and which have a salience value
	 * equal to or higher than the salience value of the current entity. Furthermore the entities
	 * that have already been mentioned in the current clause can be removed.
	 * @param entity
	 * @param concept
	 * @param salience
	 */
	public Vector getDistractorSet(String entity, String concept, int salience) {
		Vector result = new Vector();

		// if salience is 0, add all characters from characterinfo which are an instance of the same concept
		if (salience == 0) {
			for (int i = 0; i < characters.getChars().size(); i++) {
				CharacterModel ch = (CharacterModel) characters.getChars()
						.elementAt(i);
				if (!ch.getEntity().equals(entity)
						&& ch.getConcept().equals(concept)) //&& !contains2(clents, ch.getEntity()))
					result.add(ch);
			}
		}
		// otherwise add characters from history with salience higher than 'salience' 
		else {
			for (int i = 0; i < history.getEnts().size(); i++) {
				Entity e = (Entity) history.getEnts().elementAt(i);
				if (!e.getEnt().equals(entity)
						&& e.getConcept().equals(concept)
						&& e.getSalience() >= salience) {
					if (!contains2(clents, e.getEnt())) {
						CharacterModel c = characters.getChar(e.getEnt());
						result.add(c);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Simply checks of the Vector v contains the element e
	 * @param v
	 * @param e
	 */
	public boolean contains(Vector v, String e) {
		for (int i = 0; i < v.size(); i++)
			if (e.equals((String) v.elementAt(i)))
				return true;
		return false;
	}

	/**
	 * Simply checks of the Vector v contains the element e
	 * @param v
	 * @param e
	 */
	public boolean contains2(Vector v, String e) {
		for (int i = 0; i < v.size(); i++)
			if (e.equals(((Entity) v.elementAt(i)).getEnt()))
				return true;
		return false;
	}

	/**
	 * Get the distinguishing adjectives following the incremental algorithm designed by Dale.
	 * The algorithm checks for all properties defined for the current entity whether it
	 * rules out at least one of the elements of the disctractor set
	 * @param ch
	 * @param chars
	 */
	public Vector getDistinguishingAdjectives(CharacterModel ch, Vector chars) {
		Vector result = new Vector();
		Vector props = ch.getStatProps();

		// loop props
		for (int i = 0; i < props.size(); i++) {
			Property prop = (Property) props.elementAt(i);
			boolean goodprop = false;

			// exclude all characters which are ruled out by the current prop
			for (int j = 0; j < chars.size(); j++) {
				CharacterModel tmp = (CharacterModel) chars.elementAt(j);
				if (ruleOutChar(prop, tmp)) {
					chars.remove(tmp);
					goodprop = true;
				}
			}

			// and add the adjective to the list with necessary adjectives
			if (goodprop)
				result.add(prop.getValue());

			// stop if the distractor set is empty
			if (chars.size() == 0)
				break;
		}

		return result;
	}

	/**
	 * Checks if a property rules out a character, this means that the algorithm
	 * checks whether the property is also defined for this character AND whether
	 * the value if different from the given value  
	 * @param prop
	 * @param ch
	 */
	public boolean ruleOutChar(Property prop, CharacterModel ch) {
		Vector props = ch.getStatProps();

		for (int i = 0; i < props.size(); i++) {
			Property p = (Property) props.elementAt(i);
			if (p.getProp().equals(prop.getProp())) {
				if (p.getValue().equals(prop.getValue()))
					return false;
				else
					return true;
			}
		}

		return false;
	}

/* THIS WHOLE PIECE WAS EDITED BY MARISSA	*//**
	 * Decides whether the entity can be pronominalized
	 * @param n
	 * @param t
	 * @param g
	 *//*
	public boolean pronominalize(RSDepTreeNode n, RSDepTreeNode t, String g) {
		//Check what verb is the head in this sentence
		RSDepTreeNode verbNode = n.getChildNode(HD);
		Entry verbEntry = null;
		String verb = "";
		if (verbNode!=null) verbEntry = n.getChildNode(HD).getData().getEntry();
		if (verbEntry!=null) verb = verbEntry.getRoot();
		//System.out.println("blaverb: "+verb);
		String callString = lc.getEntry("call", false).getRoot();
		if (verb.equals(callString)) return false;		
		
		String ent = t.getData().get("root");
		Entity entity = history.getEntity(ent);
		System.out.println("pronom: " + ent);
		System.out.println(" RZ: is a: "
				+ t.getParentNode().getData().get("cat"));
		System.out.println(" RZ: and : " + g);
		// RZ: de volgende if heb ik toegevoegd om dingen zoals
		// "Met *het* voer hij naar..." waar *het* een pronoun is naar
		// "het schip".
		if (g.equals("neutral")|| g.equals("NULL")
				&& t.getParentNode().getData().get("cat").equals("pp"))
			return false;
		if (entity == null) { // if the entity has not been used before -> NP
			//System.out.println("-1");
			return false;
		}
		if (entity != null && entity.getFirst()) { // if this is the first reference to the entity in this paragraph -> NP  
			//entity.setFirst(false);
			System.out.println("5");
			return false;
		}
		if (n.getData().get("cat").equals("np")) { // if entity appears in np node (when a relative clause is defined for the entity) -> NP			
			//System.out.println("3");
			return false;
		}
		if (g.equals("neutral") && alreadyMentioned(ent)) { // if the entity is an object and used in current sentence -> PRON
			//System.out.println("0");

			// additional requirement: check that ent does not appear in a pp node
			// in order to avoid expressions such as 'de prinses ging naar de woestijn
			// en de ridder ging ook naar hem'
			if (!t.getParentNode().getData().get("cat").equals("pp"))
				return true;
			else
				return false;
		}
		if (entity != null && entity.getNrprons() > 2 && !alreadyMentioned(ent)) { // if the entity has been pronominalized for at least 3 time (and not mentioned in current sentence) -> NP
			//System.out.println("6");
			return false;
		}
		//TODO Change this to clauses intead of
		if (entity != null && entity.getNrsents() > 1) { // if the entity has not been mentioned for at least 2 clauses -> NP
			//System.out.println("7");
			return false;
		}
		
		if (alreadyMentioned(ent)) { // if the entity has already been mentioned in the current sentence

			//System.out.println("already mentioned");
			if (cause) { // if the current dependency tree contains a causal relation (following Kehler's algorithm) -> PRON
				//System.out.println("8");
				return true;
			}
			if (parallelism) { // if there is strong parallelism (su and obj1 are the same) with first clause of the sentence -> PRON
				//System.out.println("9");
				return true;
			}
			
			//TODO I removed this part cause it creates confusing surface text
			if (onlyOfThatGender(ent, g)) { // if this is the only entity of this gender in the sentence sofar -> PRON
				//System.out.println("10");
				return true;
			}
		} else { // if the entity has not been mentioned in the current sentence			
			//return false;
			
			if (parallelism2) { // if there is strong parallelism (su and obj1 are the same) with the previous sentence -> PRON
				//System.out.println("11");
				return true;
				//TODO
				//return false;
			}
			if (changeOfThread())
			 {	// if there is a change of thread (currently not implemented!) -> NP
			 //System.out.println("11");
			 return false;
			 }
			if (t.getAdjs() != null && t.getAdjs().size() > 0) { // if adjectives should be added -> NP
				//System.out.println("12");
				return false;
			}
		}
		//TODO I removed this because it doesn't work properly
		if (highestSalience(ent, t.getIncomingLabel())) { // if the entity is the most salient entity -> PRON			
			//System.out.println("13");
			return true;
		}
		// in all other cases -> NP
		//System.out.println("14");
		return false;
	}*/
	
	/**
	 * Decides whether the entity can be pronominalized
	 * @param n
	 * @param t
	 * @param g
	 */
	public boolean pronominalize(RSDepTreeNode n, RSDepTreeNode t, String g) {
		String ent = t.getData().get("root");
		Entity entity = history.getEntity(ent);
		System.out.println("pronom: " + ent);
		System.out.println(" RZ: is a: "
				+ t.getParentNode().getData().get("cat"));
		System.out.println(" RZ: and : " + g);
		// RZ: de volgende if heb ik toegevoegd om dingen zoals
		// "Met *het* voer hij naar..." waar *het* een pronoun is naar
		// "het schip".
		if (g.equals("NULL")) return false;
		if (g.equals("neutral")
				&& t.getParentNode().getData().get("cat").equals("pp"))
			return false;
		if (entity == null) { // if the entity has not been used before -> NP
			//System.out.println("-1");
			return false;
		}
		if (entity != null && entity.getFirst()) { // if this is the first reference to the entity in this paragraph -> NP  
			//entity.setFirst(false);
			System.out.println("5");
			return false;
		}
		if (n.getData().get("cat").equals("np")) { // if entity appears in np node (when a relative clause is defined for the entity) -> NP			
			//System.out.println("3");
			return false;
		}
		if (g.equals("neutral") && alreadyMentioned(ent)) { // if the entity is an object and used in current sentence -> PRON
			//System.out.println("0");

			// additional requirement: check that ent does not appear in a pp node
			// in order to avoid expressions such as 'de prinses ging naar de woestijn
			// en de ridder ging ook naar hem'
			if (!t.getParentNode().getData().get("cat").equals("pp"))
				return true;
			else
				return false;
		}
		if (entity != null && entity.getNrprons() > 2 && !alreadyMentioned(ent)) { // if the entity has been pronominalized for at least 3 time (and not mentioned in current sentence) -> NP
			//System.out.println("6");
			return false;
		}
		if (entity != null && entity.getNrsents() > 1) { // if the entity has not been mentioned for at least 2 sentences -> NP
			//System.out.println("7");
			return false;
		}
		if (alreadyMentioned(ent)) { // if the entity has already been mentioned in the current sentence

			//System.out.println("already mentioned");
			if (cause) { // if the current dependency tree contains a causal relation (following Kehler's algorithm) -> PRON
				//System.out.println("8");
				return true;
			}
			if (parallelism) { // if there is strong parallelism (su and obj1 are the same) with first clause of the sentence -> PRON
				//System.out.println("9");
				return true;
			}
			if (onlyOfThatGender(ent, g)) { // if this is the only entity of this gender in the sentence sofar -> PRON
				//System.out.println("10");
				return true;
			}
		} else { // if the entity has not been mentioned in the current sentence			
			if (parallelism2) { // if there is strong parallelism (su and obj1 are the same) with the previous sentence -> PRON
				//System.out.println("11");
				return true;
			}
			/*if (changeOfThread())
			 {	// if there is a change of thread (currently not implemented!) -> NP
			 //System.out.println("11");
			 return false;
			 }*/
			if (t.getAdjs() != null && t.getAdjs().size() > 0) { // if adjectives should be added -> NP
				//System.out.println("12");
				return false;
			}
		}
		if (highestSalience(ent, t.getIncomingLabel())) { // if the entity is the most salient entity -> PRON			
			//System.out.println("13");
			return true;
		}
		// in all other cases -> NP
		//System.out.println("14");
		return false;
	}


	/**
	 * Checks if the dependency tree contains strong parallelism (su and obj are the same
	 * in two clauses of the sentence)
	 */
	public void checkForParallelism() {
		RSDepTreeNode root = tree.getRoot();

		Vector v1 = getNodes(root, "su", new Vector());
		Vector v2 = getNodes(root, "obj1", new Vector());

		if (occursTwice(v1) && occursTwice(v2))
			parallelism = true;
	}

	/**
	 * Checks if the dependency tree contains strong parallelism (su and obj are the same
	 * in two clauses of the sentence)
	 */
	public void checkForParallelism2() {
		// first check if the sentence is not complex
		Iterator it = tree.getMVertices();
		while (it.hasNext()) {
			RSDepTreeNode tmp = (RSDepTreeNode) it.next();
			if (tmp.getData().get("cat") != null
					&& tmp.getData().get("cat").equals("ssub")) // euhm wat als 2 hoofdzinnen verbonden zijn?
			{
				prevsu = "";
				prevobj = "";
				parallelism2 = false;
				return;
			}
		}

		// if the sentence is not complex: determine su and obj1
		String newsu = "";
		String newobj = "";

		it = tree.getMVertices();
		while (it.hasNext()) {
			RSDepTreeNode tmp = (RSDepTreeNode) it.next();
			if (tmp.getData().get("rel").equals("su")
					&& tmp.getData().get("root") != null)
				newsu = tmp.getData().get("root");
			else if (tmp.getData().get("rel").equals("obj1")
					&& !tmp.getParentNode().getData().get("cat").equals("pp")
					&& tmp.getData().get("root") != null)
				newobj = tmp.getData().get("root");
		}

		// compare them to prevsu and prevobj and determine parallelism2
		boolean result = newsu.equals(prevsu) && newobj.equals(prevobj);
		parallelism2 = result;

		// finally store them in prevsu and prevobj
		prevsu = newsu;
		prevobj = newobj;
	}

	/**
	 * Checks if the vector contains a certain element twice
	 * @param v
	 */
	public boolean occursTwice(Vector v) {
		for (int i = 0; i < v.size(); i++) {
			Object o = v.elementAt(i);
			if (occursIn(v, o, i))
				return true;
		}

		return false;
	}

	/**
	 * Checks if the vector contains a certain element and starts looking
	 * from a certain index (in order to check if the vector contains the element twice)
	 * @param v
	 * @param o
	 * @param idx
	 */
	public boolean occursIn(Vector v, Object o, int idx) {
		for (int i = idx + 1; i < v.size(); i++)
			if (v.elementAt(i).equals(o))
				return true;
		return false;
	}

	/**
	 * Gets all nodes in the tree that have a certain label
	 * @param n
	 * @param lb
	 * @param found
	 */
	public Vector getNodes(RSDepTreeNode n, String lb, Vector found) {
		if (n == null)
			return found;
		Iterator ch = n.getChildNodes();
		while (ch.hasNext()) {
			RSDepTreeNode tmp = (RSDepTreeNode) ch.next();
			if (tmp.getData() != null && tmp.getData().get("rel") != null
					&& tmp.getData().get("rel").equals(lb)
					&& tmp.getData().get("index") != null) {
				// additional check in order to exclude the obj1 nodes that are part of a pp node
				if (!tmp.getParentNode().getData().get("cat").equals("pp"))
					found.add(tmp.getData().get("index"));
			}
			getNodes(tmp, lb, found);
		}
		return found;
	}

	/**
	 * Retrieves the adjectives used in the same sentence but with later references to the same 
	 * entity. These adjectives are then moved to the first reference, such that the later
	 * reference can be pronominalized
	 * @param ent
	 * @param adj
	 */
	public Vector getNewAdjectives(String ent, Vector adj) { // moet nog wat beter gecontroleerd (althans, of de tweede verwijzing idd gepronominalizeerd wordt)
		Vector result = new Vector();

		Iterator ch = tree.getRoot().getChildNodes();
		while (ch.hasNext()) {
			RSDepTreeNode tmp = (RSDepTreeNode) ch.next();
			if (tmp.getData().get("index") != null
					&& tmp.getData().get("index").equals(ent)) {
				Vector tmpadj = tmp.getAdjs();
				for (int i = 0; i < tmpadj.size(); i++)
					if (!contains(adj, (String) tmpadj.elementAt(i)))
						result.add(tmpadj.elementAt(i));
				tmp.resetAdjs();
			}
		}

		return result;
	}

	public String getPronounFocalization(String deplabel){
		String result = null;
		
		if (Settings.PERSPECTIVE.equals(FIRST)){
			//nominatief: ik
			if (deplabel.equals(SU))
				result = "ik";
			//accusatief, datief: mij
			else result = "mij";
		}
		if (Settings.PERSPECTIVE.equals(SECOND)){
			boolean reducedForm = true;
			
			//nominatief: jij of je
			if (deplabel.equals(SU)){
				//TODO: Choose between jij or je
				if (reducedForm) result = "je";
				else result = "jij";
			}
			//accusatief, datief: jou of je
			else
				if (reducedForm) result = "je";
				else result = "jou";
		}
		
		return result;
	}
	
	/**
	 * Returns the correct pronoun based on the grammatical role, gender and possible
	 * the determiner
	 * @param rel
	 * @param gender
	 * @param det
	 */
	public String getPronoun(String rel, String gender, String det) {
		gender = gender.toLowerCase();
		rel = rel.toLowerCase();
		det = det.toLowerCase();
		if (gender.indexOf(FEMALE) >= 0) {
			if (rel.indexOf(SU) >= 0)
				return new String("zij");
			else if (rel.indexOf(OBJ) >= 0)
				return new String("haar");
			else if (rel.indexOf(POSS) >= 0)
				return new String("haar");
		} else if (gender.indexOf(MALE) >= 0) {
			if (rel.indexOf(SU) >= 0)
				return new String("hij");
			else if (rel.indexOf(OBJ) >= 0)
				return new String("hem");
			else if (rel.indexOf(POSS) >= 0)
				return new String("zijn");
		} else if (gender.indexOf(NEUTRAL) >= 0) {
			if (det.equals("de")) {
				if (rel.indexOf(SU) >= 0)
					return new String("die");
				else if (rel.indexOf(OBJ) >= 0)
					return new String("hem");
			} else {
				if (rel.indexOf(SU) >= 0)
					return new String("dat");
				else if (rel.indexOf(OBJ) >= 0)
					return new String("het");
			}
		}
		return null;
	}

	/**
	 * Checks if the entity has already been mentioned in current sentence
	 * @param ent
	 */
	public boolean alreadyMentioned(String ent) {
		for (int i = 0; i < ents.size(); i++) {
			Entity e = (Entity) ents.elementAt(i);

			if (e.getEnt().equals(ent))
				return true;
		}
		return false;
	}

	/**
	 * Checks if this is the only entity of that gender in the current sentence
	 * @param ent
	 * @param gen
	 */
	public boolean onlyOfThatGender(String ent, String gen) {
		for (int i = 0; i < ents.size(); i++) {
			Entity e = (Entity) ents.elementAt(i);

			if (!e.getEnt().equals(ent) && e.getGender().equals(gen))
				return false;
		}
		return true;
	}

	/**
	 * Checks if the given entity is one of the top 3 in the 
	 * salience list.
	 * @param ent the entity to be checked
	 * @return true if ent is in the top 3 of the salience list
	 */
	private boolean uniquePartOfGivenSaliences(String sEnt1){
		System.out.println("uniquePartOfGivenSaliences(" + sEnt1  +  ")");
//		System.exit(0);
		CharacterModel ent1 = characters.getChar(sEnt1);
		
		Vector<Entity> v = (Vector<Entity>)history.getEnts();
		
		Vector<PartOfElement> poeV = ha.getPartOfElementVector();
		System.out.println(poeV.size());
//		System.exit(0);
		String sEnt2 = null;
		 
		
		Iterator <PartOfElement> poeIt = poeV.iterator();
		while(poeIt.hasNext() && sEnt2 == null){
			PartOfElement poe = poeIt.next();
			System.out.println(" o " + poe);
			if(poe.getEnt1().equals(sEnt1))
				sEnt2 = poe.getEnt2();
				
		}
		
		System.out.println("sEnt2: " + sEnt2  );
		CharacterModel ent2 = characters.getChar(sEnt2);
		
		// sort the salience vector:
		// insert sort
		for(int i = 0; i < v.size(); i++){
			for(int j = i+1; j<v.size(); j++){
				if(v.get(i).getSalience() < v.get(j).getSalience()){
					//switch elements i and j
					Entity temp = v.get(i);
					v.setElementAt(v.get(j), i);
					v.setElementAt(temp, j);
				}
			}
		}
		
		
		
		// print the sorted salience vector
		System.out.println(" printing v " + v.size());
		Entity entityEnt2 = null; 
		for(int i=0; i<v.size(); i++){
			if(v.get(i).getEnt().equals(ent2.getEntity())){
				System.out.print(" !!! ");
				entityEnt2 = v.get(i);
			}
			System.out.println(" - " + v.get(i).getEnt());
		}
		
		
		int sal = 0;
		if(entityEnt2 != null)
			sal = entityEnt2.getSalience();
		for(int i = 0; i<v.size() && v.get(i).getSalience() >= sal; i++){
			poeIt = poeV.iterator();
			System.out.println(v.get(i).getEnt());
			System.out.println("----------------");
			while(poeIt.hasNext()){
				PartOfElement poe = poeIt.next();
				System.out.println("-> " + poe);
				
				for(int j = 0; j < v.size() ; j++){
					if(v.get(j).getEnt().equals(poe.getEnt1())
							&& v.get(j).getConcept().equals(ent1.getConcept())){
						System.out.println("gevonden: " + v.get(j).getEnt());
						return false;
					}
						
				}
			}
			if(i == v.size()-1){
				System.out.println("reached last element of vector v ");
				Vector<CharacterModel> vChars = characters.getChars();
				poeIt = poeV.iterator();
				while(poeIt.hasNext()){
					PartOfElement poe = poeIt.next();
					System.out.println("--> " + poe);
					for(int j = 0; j < vChars.size() ; j++){
						if(!vChars.get(j).getEntity().equals(sEnt1) && 
								vChars.get(j).getEntity().equals(poe.getEnt1())
								&& vChars.get(j).getConcept().equals(ent1.getConcept())){
							System.out.println("gevonden: " + vChars.get(j).getEntity());
							return false;
						}
							
					}
				}
			}
				
		}
		return true;
		
	}
	
	private boolean uniqueContainsGivenSaliences(String sEnt1){
		return false;
	}
	
	/**
	 * Checks if the given entity is one of the top 3 in the 
	 * salience list.
	 * @param ent the entity to be checked
	 * @return true if ent is in the top 3 of the salience list
	 */
	private boolean uniqueBelongToGivenSaliences(String sEnt1){
		System.out.println("uniqueBelongToGivenSaliences(" + sEnt1  +  ")");
		System.exit(0);
		CharacterModel ent1 = characters.getChar(sEnt1);
		
		Vector<Entity> v = (Vector<Entity>)history.getEnts();
		
		Vector<BelongToElement> btoV = ha.getBelongToElementVector();
		System.out.println(btoV.size());
//		System.exit(0);
		String sEnt2 = null;
		 
		
		Iterator <BelongToElement> btoIt = btoV.iterator();
		while(btoIt.hasNext() && sEnt2 == null){
			BelongToElement bto = btoIt.next();
			System.out.println(" o " + bto);
			if(bto.getEnt1().equals(sEnt1))
				sEnt2 = bto.getEnt2();
				
		}
		
		System.out.println("sEnt2: " + sEnt2  );
		CharacterModel ent2 = characters.getChar(sEnt2);
		
		// sort the salience vector:
		// insert sort
		for(int i = 0; i < v.size(); i++){
			for(int j = i+1; j<v.size(); j++){
				if(v.get(i).getSalience() < v.get(j).getSalience()){
					//switch elements i and j
					Entity temp = v.get(i);
					v.setElementAt(v.get(j), i);
					v.setElementAt(temp, j);
				}
			}
		}
		
		// print the sorted salience vector
		System.out.println(" printing v " + v.size());
		Entity entityEnt2 = null; 
		for(int i=0; i<v.size(); i++){
			if(v.get(i).getEnt().equals(ent2.getEntity())){
				System.out.print(" !!! ");
				entityEnt2 = v.get(i);
			}
			System.out.println(" - " + v.get(i).getEnt());
		}
		
		int sal = 0;
		if(entityEnt2 != null)
			sal = entityEnt2.getSalience();
		for(int i = 0; i<v.size() && v.get(i).getSalience() >= sal; i++){
			btoIt = btoV.iterator();
			System.out.println(v.get(i).getEnt());
			System.out.println("----------------");
			while(btoIt.hasNext()){
				BelongToElement bto = btoIt.next();
				System.out.println("-> " + bto);
				
				for(int j = 0; j < v.size() ; j++){
					if(v.get(j).getEnt().equals(bto.getEnt1())
							&& v.get(j).getConcept().equals(ent1.getConcept())){
						System.out.println("gevonden: " + v.get(j).getEnt());
						return false;
					}
						
				}
			}
			if(i == v.size()-1){
				System.out.println("reached last element of vector v ");
				Vector<CharacterModel> vChars = characters.getChars();
				btoIt = btoV.iterator();
				while(btoIt.hasNext()){
					BelongToElement bto = btoIt.next();
					System.out.println("--> " + bto);
					for(int j = 0; j < vChars.size() ; j++){
						if(!vChars.get(j).getEntity().equals(sEnt1) && 
								vChars.get(j).getEntity().equals(bto.getEnt1())
								&& vChars.get(j).getConcept().equals(ent1.getConcept())){
							System.out.println("gevonden: " + vChars.get(j).getEntity());
							return false;
						}
							
					}
				}
			}
				
		}
		return true;
		
	}
	
	/**
	 * Checks if the entity is the entity with the highest salience value for that gender 
	 * @param ent
	 * @param rel
	 */
	public boolean highestSalience(String ent, String rel) {
		// this should exclude the entities that have already been mentioned in the current clause!
		String gender = "";
		String pos = "";
		int sal = 0;
		int msal = 0;
		int fsal = 0;

		Entity current = history.getEntity(ent);
		if (current == null) // if the entity has not been mentioned before -> no
			return false;
		else {
			gender = current.getGender();
			pos = current.getPos();
			if (pos.equals(rel))
				sal = current.getSalience() + 35;
			else
				sal = current.getSalience();
		}

		if (gender.equals("neutral"))
			return false;

		Vector entities = history.getEnts();

		for (int i = 0; i < entities.size(); i++) {
			Entity entity = (Entity) entities.elementAt(i);

			if (entity.getEnt().equals(ent)) {
				// if first reference in current paragraph -> no (staat al ergens?)
				if (entity.getFirst())
					return false;
			} else if (!contains2(clents, entity.getEnt())) {
				// add temporarily 35 points to the salience value if the 
				// grammatical roles are the same
				boolean par = false;
				if (pos != null && entity.getPos().equals(pos))
					par = true;

				if (entity.getGender().equals("male")) {
					if (par) {
						if (entity.getSalience() > (msal - 35))
							msal = entity.getSalience() + 35;
					} else {
						if (entity.getSalience() > msal)
							msal = entity.getSalience();
					}
				} else if (entity.getGender().equals("female")) {
					if (par) {
						if (entity.getSalience() > (fsal - 35))
							fsal = entity.getSalience() + 35;
					} else {
						if (entity.getSalience() > fsal)
							fsal = entity.getSalience();
					}
				}
			}
		}

		return ((gender.equals("male") && (sal > msal)) || (gender
				.equals("female") && (sal > fsal)));
	}

	/**
	 * Calculates the salience value for the entity and updates the history
	 * @param node
	 * @param entity
	 */
	public boolean updateSalience(RSDepTreeNode node, String entity) {
		boolean head = false;
		int result = 0;

		String pos = node.getIncomingLabel();
		RSDepTreeNode n = node.getParentNode();

		// sets the booleans issup, newent, firsthead, ispp, ishead 
		boolean issup = false;
		if (n != null)
			issup = n.getChildNode("sup") != null;
		boolean newent = true;
		boolean firsthead = true;
		for (int i = 0; i < clents.size(); i++) {
			Entity tmp = (Entity) clents.elementAt(i);
			if (tmp.getEnt().equals(entity)) {
				newent = false;
				firsthead = tmp.getHead();
			}
		}
		boolean ispp = n.getData().get("cat").equals("pp");
		boolean ishead = true;
		if (n != null
				&& (n.getIncomingLabel().equals("modb") || node
						.getIncomingLabel().equals("det")))
			ishead = false;

		// based on these booleans: calculate the salience value
		if (pos.equals("su")) {
			if (issup)
				result += 70; //sup
			else
				result += 80; //subject
		} else if (pos.equals("obj1") && !ispp)
			result += 50; //direct object
		else
			result += 40; //indirect object

		if (newent)
			result += 100; //sentence recency

		if (newent)
			result += 50; //non-adverbial

		if (ishead && firsthead) {
			head = true;
			history.updateHead(entity);
			result += 80; //head
		}

		// update history
		history.updateSaliencePos(entity, result, pos);

		// print new salience table
		System.out.println("na update salience van " + entity + " met "
				+ result + " punten:");
		history.printSalienceTable();

		// if the expression contains another entity: call this function recursively
		RSDepTreeNode poss = node.getChildNode("det");
		if (poss != null && poss.getData().get("index") != null) {
			updateSalience(poss, poss.getData().get("index"));
		}
		RSDepTreeNode child = node.getChildNode("modb");
		if (child != null && child.getData().get("cat") != null
				&& child.getData().get("cat").equals("pp")) {
			RSDepTreeNode child2 = child.getChildNode("obj1");
			String newentity = child2.getChildNode("hd").getData().get("index");
			updateSalience(child2, newentity);
		}

		return head;
	}

	/**
	 * Returns whether the expression should include the name (currently done randomly)
	 * @param ent
	 */
	public boolean useName(String ent) {
		//TODO
		CharacterModel ch = characters.getChar(ent);
		Entity entity = history.getEntity(ent);
		
		//If there is no name, don't use it.
		if (ch.getName().equals(""))
			return false;
		
		//If this character has not been mentioned for a while,
		//use the name again.
		if (entity != null && entity.getNrsents() > 1)
			return true;
		
		if (ch.getName() != null && !ch.getName().equals("")) {
			double d = Math.random();
			if (d <= 0.25)
				return true;
		}

		return false;
	}

	/**
	 * Returns if the noun should be included, which is the case when the concept 
	 * is a title/function noun
	 * @param ent
	 */
	public boolean onlyName(String ent) {
		CharacterModel ch = characters.getChar(ent);

		if (ch == null)
			return true;

		String con = ch.getConcept();
		if (con.equals(""))
			return true;

		if (con.equals("princess") || con.equals("king")
				|| con.equals("knight") || con.equals("dwarf"))
			return false;

		return true;
	}

	/**
	 * Sets new paragraph
	 *
	 */
	public void newParagraph() {
		history.newParagraph();
	}

	/**
	 * Adds an entity to the list of entities mentioned sofar in the current sentence 
	 * and to the list of entities mentioned sofar in the current clause
	 * @param ent
	 */
	public void addEntity(Entity ent) {
		for (int i = 0; i < ents.size(); i++) {
			Entity tmp = (Entity) ents.elementAt(i);
			if (tmp.getEnt().equals(ent.getEnt())) {
				tmp.setPos(ent.getPos());
				return;
			}
		}
		ents.add(ent);

		for (int i = 0; i < clents.size(); i++) {
			Entity tmp = (Entity) clents.elementAt(i);
			if (tmp.getEnt().equals(ent.getEnt())) {
				tmp.setPos(ent.getPos());
				return;
			}
		}
		clents.add(ent);
	}

	/**
	 * Checks whether a more general concept can be used which can be done if the most
	 * specific concept has been used in the previous sentence, if this concept has been
	 * used for at least two times and if it is not the first reference in the current
	 * paragraph
	 * @param entity
	 * @param concept
	 */
	public boolean useMoreGeneralConcept(String entity, String concept) {
		// requirements for choosing a more general concept:
		// the most specific concept is used in the PREVIOUS utterance
		// the most specific concept has been used the last 2 times
		// it is not the first reference in the current paragraph
		Entity ent = history.getEntity(entity);
		if (ent != null) {
			Vector tmp = ent.getNouns();
			if (tmp.size() < 2
					|| !tmp.elementAt(tmp.size() - 1).equals(
							tmp.elementAt(tmp.size() - 2)))
				return false;
			if (ent.getNrsents() == 1 && !ent.getFirst())
				return true;
		}
		return false;
	}

	/**
	 * Returns a Vector with all entities (represented as Strings)
	 */
	public Vector getAllEntities() {
		return history.getAllEntities();
	}
}
