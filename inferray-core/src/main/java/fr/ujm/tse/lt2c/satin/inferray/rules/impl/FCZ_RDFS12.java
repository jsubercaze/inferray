package fr.ujm.tse.lt2c.satin.inferray.rules.impl;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.AbstractDictionary;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.classes.ClassZetaRule;

public class FCZ_RDFS12 extends ClassZetaRule {

	public FCZ_RDFS12(final NodeDictionary dictionary, final CacheTripleStore tripleStore,
			final CacheTripleStore usableTriples, final CacheTripleStore outputTriples) {
		super(dictionary, tripleStore, usableTriples, outputTriples,
				AbstractDictionary.rdfsContainerMembershipProperty,
				AbstractDictionary.rdfssubPropertyOf,
				AbstractDictionary.rdfsMember, "RDFS12");

	}

}
