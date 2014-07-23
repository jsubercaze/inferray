package fr.ujm.tse.lt2c.satin.inferray.rules.impl;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.classes.ClassGammaRule;

public class FCG_PRP_SPO1 extends ClassGammaRule {

	public FCG_PRP_SPO1(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore, final CacheTripleStore usableTriples,
			final CacheTripleStore newTriples) {
		super(dictionary, tripleStore, usableTriples, newTriples,
				(int) NodeDictionary.rdfssubPropertyOf,
				(int) NodeDictionary.rdftype, 1, true, "PRP-SPO1");

	}

}
