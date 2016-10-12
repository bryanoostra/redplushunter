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
package vs.fabula;

import java.util.HashSet;
import java.util.Set;

import vs.IExplainable;
import vs.communication.FabulaCausality;
import vs.communication.FabulaElement;

/**
 * Collects fabula for explainability purposes 
 * @author swartjes
 *
 */
public class FabulaCollector implements IExplainable {

	private final Set<FabulaElement> m_fabulaElements;
	private final Set<FabulaCausality> m_fabulaCausalities;
	
	public FabulaCollector() {
		m_fabulaElements = new HashSet<FabulaElement>();
		m_fabulaCausalities = new HashSet<FabulaCausality>();
	}

	public void addFabulaCausality(FabulaCausality fc) {
		m_fabulaCausalities.add(fc);
	}
	
	public void addFabulaElement(FabulaElement fe) {
		m_fabulaElements.add(fe);
	}	
	
	public void addAllFabulaElements(Set<FabulaElement> fes) {
		m_fabulaElements.addAll(fes);
	}
	
	public void addAllFabulaCausalities(Set<FabulaCausality> fcs) {
		m_fabulaCausalities.addAll(fcs);
	}

	@Override
	public Set<FabulaCausality> explainCausalities() {
		return m_fabulaCausalities;
	}
	
	@Override
	public Set<FabulaElement> explainElements() {
		return m_fabulaElements;
	}
	
	public void resetFabulaCausalities() {
		m_fabulaCausalities.clear();
	}
	
	public void resetFabulaElements() {
		m_fabulaElements.clear();
	}

}