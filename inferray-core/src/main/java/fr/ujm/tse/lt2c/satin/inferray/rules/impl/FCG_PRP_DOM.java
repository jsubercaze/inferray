package fr.ujm.tse.lt2c.satin.inferray.rules.impl;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.classes.ClassGammaRule;

public class FCG_PRP_DOM extends ClassGammaRule {

	public FCG_PRP_DOM(final NodeDictionary dictionary, final CacheTripleStore tripleStore,
			final CacheTripleStore usableTriples, final CacheTripleStore newTriples) {
		super(dictionary, tripleStore, usableTriples, newTriples,
				(int) NodeDictionary.rdfsdomain, (int) NodeDictionary.rdftype, 2,
				false, "PRP-DOM");

	}

}
