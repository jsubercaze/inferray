package fr.ujm.tse.lt2c.satin.inferray.rules.impl;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.classes.ClassEpsilonRule;

/**
 *
 * @author Julien Subercaze
 *
 *         Dec 2013
 *
 */
public class FCE_PRP_INV_1_2 extends ClassEpsilonRule {

	public FCE_PRP_INV_1_2(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore outputTriples) {
		super(dictionary, tripleStore, usableTriples, outputTriples,
				(int) NodeDictionary.owlinverseOf, true, "PRP_INV_1_2");
	}

}
