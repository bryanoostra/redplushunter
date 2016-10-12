/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $
 * $Date: 2006/05/24 09:00:28 $    
 * @since version 0       
 */

package parlevink.parlegraph.utils;

import javax.swing.*;
import java.util.*;

/** 
 @@@to be documented.
 
 Je kan hier een actionmap onder een naam toevoegen. Als hij al bestond, wordt de oude als parent aan de nieuwe toegevoegd.
 Oei, dat overrulet dus de oude parent. Misschein nog veranderen dattie BOVENAN als parent wordt toegevoegd)
 */
public class ActionMapMap extends TreeMap {

    public void put(String name, ActionMap am) {
        am.setParent((ActionMap)get(name));
        super.put(name, am);
    }
    
}
