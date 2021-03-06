package fr.ujm.tse.lt2c.satin.inferray.rules.impl;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.classes.ClassAlphaRule;

/**
 * Implementation of SCM_DOM2 rule using the {@link ClassAlphaRule}
 *
 * Specifications for SCM_DOM2 are :
 * <ul>
 * <li>p1 : rdfs:domain</li>
 * <li>p2 : rdf:subPropertyOf</li>
 * <li>Equality : s1 = o2</li>
 * <li>Indices : 3 - 1 - 2</li>
 * </ul>
 *
 * @author Julien Subercaze
 *
 *         Dec 2013
 *
 */
public class FCA_SCM_DOM2 extends ClassAlphaRule {

	public FCA_SCM_DOM2(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore, final CacheTripleStore usableTriples,
			final CacheTripleStore outputTriples) {
		super(dictionary, tripleStore, usableTriples, outputTriples,
				(int) NodeDictionary.rdfsdomain,
				(int) NodeDictionary.rdfssubPropertyOf,  3, 1, 2, "SCM_DOM2");
	}

}
