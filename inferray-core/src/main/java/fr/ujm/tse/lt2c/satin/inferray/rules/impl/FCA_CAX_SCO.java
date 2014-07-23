package fr.ujm.tse.lt2c.satin.inferray.rules.impl;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.classes.ClassAlphaRule;

/**
 * Implementation of CAX-SCO rule using the {@link ClassAlphaRule}
 *
 * Specifications for CAX-SCO are :
 * <ul>
 * <li>p1 : rdfs:SubClassOf</li>
 * <li>p2 : rdf:type</li>
 * <li>Indices : 3 - 4 - 2</li>
 * </ul>
 *
 * @author Julien Subercaze
 *
 *         Dec 2013
 *
 */
public class FCA_CAX_SCO extends ClassAlphaRule {

	public FCA_CAX_SCO(final NodeDictionary dictionary, final CacheTripleStore tripleStore,
			final CacheTripleStore usableTriples, final CacheTripleStore outputTriples) {
		super(dictionary, tripleStore, usableTriples, outputTriples,
				(int) NodeDictionary.rdfssubClassOf, (int) NodeDictionary.rdftype,
				3, 4, 2, "CAX-SCO");
	}

}
