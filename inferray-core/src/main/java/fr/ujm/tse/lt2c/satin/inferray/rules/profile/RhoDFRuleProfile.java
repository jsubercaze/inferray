package fr.ujm.tse.lt2c.satin.inferray.rules.profile;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.AbstractFastRule;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCA_CAX_SCO;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCA_SCM_DOM1;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCA_SCM_DOM2;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCA_SCM_RNG1;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCA_SCM_RNG2;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCB_SCM_SCO_EQC2;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCB_SCM_SPO_EQP2;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCG_PRP_DOM;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCG_PRP_RNG;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCG_PRP_SPO1;

/**
 * <p>
 * Profile containing RhoDF rules.
 * </p>
 * Set the rhoDF rules in an order that both :
 * <ul>
 * <li>Maximize code reuse</li>
 * <li>Minimize the sorting of the triple store</li>
 * </ul>
 *
 * @author Julien Subercaze
 *
 */
class RhoDFRuleProfile extends AbstractRulesProfile {

	public RhoDFRuleProfile(final NodeDictionary dictionary,
			final CacheTripleStore mainTripleStore, final CacheTripleStore usableTriples,
			final CacheTripleStore outputTriples) {
		super(dictionary, mainTripleStore, usableTriples, outputTriples);
		this.hasSCMEQPSCMEQC = true;
		// Initialize all rules
		rules = new AbstractFastRule[10];
		rules[0] = new FCB_SCM_SCO_EQC2(dictionary, this.mainTripleStore,
				this.mainTripleStore, outputTriples);
		rules[1] = new FCB_SCM_SPO_EQP2(dictionary, this.mainTripleStore,
				this.mainTripleStore, outputTriples);
		rules[2] = new FCG_PRP_DOM(dictionary, this.mainTripleStore,
				this.mainTripleStore, outputTriples);
		rules[3] = new FCG_PRP_RNG(dictionary, this.mainTripleStore,
				this.mainTripleStore, outputTriples);
		rules[4] = new FCG_PRP_SPO1(dictionary, this.mainTripleStore,
				this.mainTripleStore, outputTriples);
		rules[5] = new FCA_CAX_SCO(dictionary, this.mainTripleStore,
				this.mainTripleStore, outputTriples);
		rules[6] = new FCA_SCM_DOM1(dictionary, this.mainTripleStore,
				this.mainTripleStore, outputTriples);
		rules[7] = new FCA_SCM_DOM2(dictionary, this.mainTripleStore,
				this.mainTripleStore, outputTriples);
		rules[8] = new FCA_SCM_RNG1(dictionary, this.mainTripleStore,
				this.mainTripleStore, outputTriples);
		rules[9] = new FCA_SCM_RNG2(dictionary, this.mainTripleStore,
				this.mainTripleStore, outputTriples);
	}

	@Override
	public String getName() {
		return "RhoDF";
	}

	@Override
	public void finalization() {
		// FIXME do it or not ?
	}

}
