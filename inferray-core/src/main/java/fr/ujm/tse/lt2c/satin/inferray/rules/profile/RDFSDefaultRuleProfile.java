package fr.ujm.tse.lt2c.satin.inferray.rules.profile;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.AbstractFastRule;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCA_CAX_SCO;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCB_SCM_SCO;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCB_SCM_SPO;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCG_PRP_DOM;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCG_PRP_RNG;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCG_PRP_SPO1;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCZ_RDFS10;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCZ_RDFS12;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCZ_RDFS13;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCZ_RDFS6;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCZ_RDFS8;

/**
 * Profile containing RDFS rules
 *
 * @author Julien Subercaze
 *
 */
class RDFSDefaultRuleProfile extends AbstractRulesProfile {

	public RDFSDefaultRuleProfile(final NodeDictionary dictionary,
			final CacheTripleStore mainTripleStore, final CacheTripleStore usableTriples,
			final CacheTripleStore outputTriples) {
		super(dictionary, mainTripleStore, usableTriples, outputTriples);
		initializeRules();
		initializeAxiomaticTriples();

	}

	private void initializeAxiomaticTriples() {


	}

	private void initializeRules() {
		rules = new AbstractFastRule[11];
		// Skip rdf1
		// RDFS-11 -> CAX SCO
		rules[0] = new FCA_CAX_SCO(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);
		// RDFS 5 = SCM-SPO
		rules[1] = new FCB_SCM_SPO(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);
		// RDFS9
		rules[2] = new FCB_SCM_SCO(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);
		// rdfs2-3 = PRP-DOM, PRP-RNG
		rules[3] = new FCG_PRP_DOM(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);
		rules[4] = new FCG_PRP_RNG(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);
		// RDFS 4 executed in the end, detected by Inferray
		// RDFS7
		rules[5] = new FCG_PRP_SPO1(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);

		// RDFS6 8 10 12 13
		rules[6] = new FCZ_RDFS6(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);

		rules[7] = new FCZ_RDFS8(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);
		rules[8] = new FCZ_RDFS10(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);
		rules[9] = new FCZ_RDFS12(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);
		rules[10] = new FCZ_RDFS13(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);

	}

	@Override
	public String getName() {
		return "RDFS";
	}

	@Override
	public void finalization() {

	}

}
