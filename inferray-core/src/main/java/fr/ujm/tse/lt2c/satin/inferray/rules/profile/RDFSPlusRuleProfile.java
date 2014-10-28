package fr.ujm.tse.lt2c.satin.inferray.rules.profile;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.AbstractDictionary;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.AbstractFastRule;
import fr.ujm.tse.lt2c.satin.inferray.rules.classes.ClassSameAsRule;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCA_CAX_EQC1;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCA_CAX_SCO;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCA_SCM_DOM1;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCA_SCM_DOM2;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCA_SCM_RNG1;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCA_SCM_RNG2;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCB_EQ_TRANS;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCB_SCM_SCO_EQC2;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCB_SCM_SPO_EQP2;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCE_PRP_EQP_1_2;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCE_PRP_INV_1_2;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCG_PRP_DOM;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCG_PRP_RNG;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCG_PRP_SPO1;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FC_PRP_FP;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FC_PRP_IFP;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FC_PRP_SYMP;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FC_PRP_TRP;

/**
 * Profile containing RDFS rules
 * 
 * @author Julien Subercaze
 * 
 */
class RDFSPlusRuleProfile extends AbstractRulesProfile {

	public RDFSPlusRuleProfile(final NodeDictionary dictionary,
			final CacheTripleStore mainTripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore outputTriples) {
		super(dictionary, mainTripleStore, usableTriples, outputTriples);
		// Enable rules at parsing time
		this.hasSCMEQPSCMEQC = true;
		// Add axiomatic triples to the triple stores
		initializeAxiomaticTriples();
		// Initialize set of rules
		initializeRules();

	}

	private void initializeAxiomaticTriples() {

	}

	private void initializeRules() {
		rules = new AbstractFastRule[19];

		rules[0] = new FCB_SCM_SCO_EQC2(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[1] = new FCB_SCM_SPO_EQP2(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[2] = new FCG_PRP_DOM(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[3] = new FCG_PRP_RNG(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[4] = new FCG_PRP_SPO1(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[5] = new FCA_CAX_SCO(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[6] = new FCA_SCM_DOM1(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[7] = new FCA_SCM_DOM2(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[8] = new FCA_SCM_RNG1(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[9] = new FCA_SCM_RNG2(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[10] = new ClassSameAsRule(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[11] = new FCB_EQ_TRANS(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[12] = new FCA_CAX_EQC1(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[13] = new FC_PRP_TRP(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[14] = new FC_PRP_FP(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);
		rules[15] = new FC_PRP_IFP(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[16] = new FC_PRP_SYMP(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[17] = new FCE_PRP_EQP_1_2(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[18] = new FCE_PRP_INV_1_2(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);

	}

	@Override
	public String getName() {
		return "RDFSPlus";
	}

	@Override
	public void finalization() {
		if (logger.isTraceEnabled()) {
			logger.trace("----------Starting finalization process-------------");
		}
		final boolean remaped = dictionary.hasRemapOccured();
		final int rdfType = (int) AbstractDictionary.rdftype;
		final long resource = AbstractDictionary.rdfsResource;
		for (long i = (long)NodeDictionary.SPLIT_INDEX + 1; i < dictionary
				.getCntResources(); i++) {
			if (remaped && dictionary.wasRemoved(i)) {
				// This value was skipped during a remaping operation
				continue;
			}
			mainTripleStore.add(i, rdfType, resource);
		}
		mainTripleStore.getbyPredicate(rdfType).totalSortingNoDuplicate();
	}

}
