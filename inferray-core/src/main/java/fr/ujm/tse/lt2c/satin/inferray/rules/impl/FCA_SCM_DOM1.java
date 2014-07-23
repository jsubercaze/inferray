package fr.ujm.tse.lt2c.satin.inferray.rules.impl;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.classes.ClassAlphaRule;

/**
 * Implementation of SCM_DOM1 rule using the {@link ClassAlphaRule}
 *
 * Specifications for SCM_DOM1 are :
 * <ul>
 * <li>p1 : rdfs:domain</li>
 * <li>p2 : rdf:subClassOf</li>
 * <li>Equality : o2 = s1</li>
 * <li>Indices : 0 - 1 - 5</li>
 * </ul>
 *
 * @author Julien Subercaze
 *
 *         Dec 2013
 *
 */
public class FCA_SCM_DOM1 extends ClassAlphaRule {

	public FCA_SCM_DOM1(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore, final CacheTripleStore usableTriples,
			final CacheTripleStore outputTriples) {
		super(dictionary, tripleStore, usableTriples, outputTriples,
				(int) NodeDictionary.rdfssubClassOf, (int) NodeDictionary.rdfsdomain,
				3, 4, 2, "SCM_DOM1");
	}

}
