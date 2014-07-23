package fr.ujm.tse.lt2c.satin.inferray.rules.profile;

import fr.ujm.tse.lt2c.satin.inferray.configuration.MyConfiguration;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.AbstractDictionary;
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
class RDFSRuleProfile extends AbstractRulesProfile {

	public RDFSRuleProfile(final NodeDictionary dictionary,
			final CacheTripleStore mainTripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore outputTriples, final MyConfiguration config) {
		super(dictionary, mainTripleStore, usableTriples, outputTriples);
		// Add axiomatic triples to the triple stores
		initializeAxiomaticTriples(config);
		// Initialize set of rules
		initializeRules();

	}

	/**
	 * Let's unleash the worst avatar of the semweb stupidity; for the sake of
	 * compatibility with so called "standards". Seriously, ..., come on, who
	 * came with that ****. Maybe the worst are the others who silently
	 * acknowledged, don't know, don't want to ...
	 *
	 * @param config
	 */
	private void initializeAxiomaticTriples(final MyConfiguration config) {
		mainTripleStore.add(AbstractDictionary.rdftype,
				(int) AbstractDictionary.rdftype,
				AbstractDictionary.rdfProperty);
		mainTripleStore.add(AbstractDictionary.rdfsubject,
				(int) AbstractDictionary.rdftype,
				AbstractDictionary.rdfProperty);
		mainTripleStore.add(AbstractDictionary.rdfpredicate,
				(int) AbstractDictionary.rdftype,
				AbstractDictionary.rdfProperty);
		mainTripleStore.add(AbstractDictionary.rdfobject,
				(int) AbstractDictionary.rdftype,
				AbstractDictionary.rdfProperty);
		mainTripleStore.add(AbstractDictionary.rdffirst,
				(int) AbstractDictionary.rdftype,
				AbstractDictionary.rdfProperty);
		mainTripleStore.add(AbstractDictionary.rdfrest,
				(int) AbstractDictionary.rdftype,
				AbstractDictionary.rdfProperty);
		mainTripleStore.add(AbstractDictionary.rdfValue,
				(int) AbstractDictionary.rdftype,
				AbstractDictionary.rdfProperty);
		mainTripleStore.add(AbstractDictionary.rdf_1,
				(int) AbstractDictionary.rdftype,
				AbstractDictionary.rdfProperty);
		mainTripleStore.add(AbstractDictionary.rdfnil,
				(int) AbstractDictionary.rdftype, AbstractDictionary.rdfList);
		// Domain
		mainTripleStore.add(AbstractDictionary.rdftype,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfsResource);
		mainTripleStore.add(AbstractDictionary.rdfsdomain,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfProperty);
		mainTripleStore.add(AbstractDictionary.rdfsrange,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfProperty);
		mainTripleStore.add(AbstractDictionary.rdfssubClassOf,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfProperty);
		mainTripleStore.add(AbstractDictionary.rdfssubPropertyOf,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfProperty);
		mainTripleStore.add(AbstractDictionary.rdfsubject,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfStatement);
		mainTripleStore.add(AbstractDictionary.rdfpredicate,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfStatement);
		mainTripleStore.add(AbstractDictionary.rdfobject,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfStatement);
		mainTripleStore.add(AbstractDictionary.rdfsMember,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfsResource);
		mainTripleStore
		.add(AbstractDictionary.rdffirst,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfList);
		mainTripleStore
		.add(AbstractDictionary.rdfrest,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfList);
		mainTripleStore.add(AbstractDictionary.rdfsSeeAlso,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfsResource);
		mainTripleStore.add(AbstractDictionary.rdfsisDefinedBy,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfsResource);
		mainTripleStore.add(AbstractDictionary.rdfsComment,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfsResource);
		mainTripleStore.add(AbstractDictionary.rdfsLabel,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfsResource);
		mainTripleStore.add(AbstractDictionary.rdfValue,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfsResource);
		// Range
		mainTripleStore.add(AbstractDictionary.rdftype,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsClass);
		mainTripleStore.add(AbstractDictionary.rdfsdomain,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsClass);
		mainTripleStore.add(AbstractDictionary.rdfsrange,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsClass);
		mainTripleStore.add(AbstractDictionary.rdfssubClassOf,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsClass);
		mainTripleStore.add(AbstractDictionary.rdfssubPropertyOf,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfProperty);
		mainTripleStore.add(AbstractDictionary.rdfsubject,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsResource);
		mainTripleStore.add(AbstractDictionary.rdfpredicate,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsResource);
		mainTripleStore.add(AbstractDictionary.rdfobject,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsResource);
		mainTripleStore.add(AbstractDictionary.rdfsMember,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsResource);
		mainTripleStore.add(AbstractDictionary.rdffirst,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsResource);
		mainTripleStore.add(AbstractDictionary.rdfrest,
				(int) AbstractDictionary.rdfsrange, AbstractDictionary.rdfList);
		mainTripleStore.add(AbstractDictionary.rdfsSeeAlso,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsResource);
		mainTripleStore.add(AbstractDictionary.rdfsisDefinedBy,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsResource);
		mainTripleStore.add(AbstractDictionary.rdfsComment,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsLiteral);
		mainTripleStore.add(AbstractDictionary.rdfsLabel,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsLiteral);
		mainTripleStore.add(AbstractDictionary.rdfValue,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsResource);
		// MISC
		mainTripleStore.add(AbstractDictionary.rdfAlt,
				(int) AbstractDictionary.rdfssubClassOf,
				AbstractDictionary.rdfsContainer);
		mainTripleStore.add(AbstractDictionary.rdfBag,
				(int) AbstractDictionary.rdfssubClassOf,
				AbstractDictionary.rdfsContainer);
		mainTripleStore.add(AbstractDictionary.rdfSeq,
				(int) AbstractDictionary.rdfssubClassOf,
				AbstractDictionary.rdfsContainer);
		mainTripleStore.add(AbstractDictionary.rdfsContainerMembershipProperty,
				(int) AbstractDictionary.rdfssubClassOf,
				AbstractDictionary.rdfProperty);
		mainTripleStore.add(AbstractDictionary.rdf_1,
				(int) AbstractDictionary.rdftype,
				AbstractDictionary.rdfsContainerMembershipProperty);
		mainTripleStore.add(AbstractDictionary.rdf_1,
				(int) AbstractDictionary.rdfsdomain,
				AbstractDictionary.rdfsResource);
		mainTripleStore.add(AbstractDictionary.rdf_1,
				(int) AbstractDictionary.rdfsrange,
				AbstractDictionary.rdfsResource);
		mainTripleStore.add(AbstractDictionary.rdfsisDefinedBy,
				(int) AbstractDictionary.rdfssubPropertyOf,
				AbstractDictionary.rdfsSeeAlso);
		mainTripleStore.add(AbstractDictionary.rdfXMLLiteral,
				(int) AbstractDictionary.rdftype,
				AbstractDictionary.rdfsDatatype);
		mainTripleStore.add(AbstractDictionary.rdfXMLLiteral,
				(int) AbstractDictionary.rdfssubClassOf,
				AbstractDictionary.rdfsLiteral);
		mainTripleStore.add(AbstractDictionary.rdfsDatatype,
				(int) AbstractDictionary.rdfssubClassOf,
				AbstractDictionary.rdfsClass);
		mainTripleStore.add(AbstractDictionary.xsdnonNegativeInteger,
				(int) AbstractDictionary.rdftype,
				AbstractDictionary.rdfsDatatype);
		mainTripleStore.add(AbstractDictionary.xsdstring,
				(int) AbstractDictionary.rdftype,
				AbstractDictionary.rdfsDatatype);
		mainTripleStore.add(AbstractDictionary.rdftype,
				(int) AbstractDictionary.rdfssubPropertyOf,
				AbstractDictionary.rdftype);
		mainTripleStore.add(AbstractDictionary.rdfsdomain,
				(int) AbstractDictionary.rdfssubPropertyOf,
				AbstractDictionary.rdfsdomain);
		mainTripleStore.add(AbstractDictionary.rdfsrange,
				(int) AbstractDictionary.rdfssubPropertyOf,
				AbstractDictionary.rdfsrange);
		mainTripleStore.add(AbstractDictionary.rdfssubPropertyOf,
				(int) AbstractDictionary.rdfssubPropertyOf,
				AbstractDictionary.rdfssubPropertyOf);
		mainTripleStore.add(AbstractDictionary.rdfssubClassOf,
				(int) AbstractDictionary.rdfssubPropertyOf,
				AbstractDictionary.rdfssubClassOf);

	}

	private void initializeRules() {
		rules = new AbstractFastRule[11];
		// Skip rdf1
		// RDFS-11 -> CAX SCO
		rules[0] = new FCA_CAX_SCO(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		// RDFS 5 = SCM-SPO
		rules[1] = new FCB_SCM_SPO(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		// RDFS9
		rules[2] = new FCB_SCM_SCO(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		// rdfs2-3 = PRP-DOM, PRP-RNG
		rules[3] = new FCG_PRP_DOM(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		rules[4] = new FCG_PRP_RNG(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);
		// RDFS 4 executed in the end, detected by Inferray
		// RDFS7
		rules[5] = new FCG_PRP_SPO1(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);

		// RDFS6 8 10 12 13
		rules[6] = new FCZ_RDFS6(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);

		rules[7] = new FCZ_RDFS8(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);
		rules[8] = new FCZ_RDFS10(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);
		rules[9] = new FCZ_RDFS12(dictionary, mainTripleStore, mainTripleStore,
				outputTriples);
		rules[10] = new FCZ_RDFS13(dictionary, mainTripleStore,
				mainTripleStore, outputTriples);

	}

	@Override
	public String getName() {
		return "RDFS";
	}

	@Override
	public void finalization() {
		if (logger.isTraceEnabled()) {
			logger.trace("----------Starting finalization process-------------");
		}
		final boolean remaped = dictionary.hasRemapOccured();
		final int rdfType = (int) AbstractDictionary.rdftype;
		final long resource = AbstractDictionary.rdfsResource;
		for (long i = NodeDictionary.SPLIT_INDEX; i < dictionary
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
