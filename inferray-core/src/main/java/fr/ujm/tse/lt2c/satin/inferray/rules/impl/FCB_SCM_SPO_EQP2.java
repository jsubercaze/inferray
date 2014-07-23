package fr.ujm.tse.lt2c.satin.inferray.rules.impl;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.classes.ClassBetaRule;

/**
 * Implementation of SCM_SCO and SCM_EQC2 rules using the {@link ClassBetaRule}
 *
 * Specifications for SCM_SCO and SCM_EQC2 are :
 * <ul>
 * <li>p1 : subPropertyOf</li>
 * <li>p2 : equivalentProperty</li>
 * </ul>
 *
 * @author Julien Subercaze
 *
 *         Dec 2013
 *
 */
public class FCB_SCM_SPO_EQP2 extends ClassBetaRule {

	public FCB_SCM_SPO_EQP2(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore, final CacheTripleStore usableTriples,
			final CacheTripleStore outputTriples) {
		super(dictionary, tripleStore, usableTriples, outputTriples,
				(int) NodeDictionary.rdfssubPropertyOf,
				(int) NodeDictionary.owlequivalentProperty, true, "SCM_SPO_EQP2",true);
	}

}
