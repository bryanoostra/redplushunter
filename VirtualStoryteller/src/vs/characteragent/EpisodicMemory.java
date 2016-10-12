/* Copyright (C) 2008 Human Media Interaction - University of Twente
 * 
 * This file is part of The Virtual Storyteller.
 * 
 * The Virtual Storyteller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Virtual Storyteller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with The Virtual Storyteller. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package vs.characteragent;

import java.util.Set;

import vs.communication.FabulaCausality;
import vs.communication.FabulaElement;
import vs.fabula.BasicFabulaBuilder;
import vs.fabula.NamedGraphFabulaKnowledgeBase;
import vs.fabula.PrologFabulaKnowledgeBase;

public class EpisodicMemory extends BasicFabulaBuilder {

	public EpisodicMemory(ICharacterAgent owner) {
		super(owner);
		
		registerFabulaKnowledgeBase(new PrologFabulaKnowledgeBase());
		registerFabulaKnowledgeBase(new NamedGraphFabulaKnowledgeBase());
	}
	
	public void addAllFabulaElements(Set<FabulaElement> fes) {
		for (FabulaElement fe: fes) {
			super.addFabulaElement(fe);
		}
	}
	
	public void addAllFabulaCausalities(Set<FabulaCausality> fcs) {
		for (FabulaCausality fc: fcs) {
			super.addFabulaCausality(fc);
		}
		
	}
}
