package fr.ujm.tse.lt2c.satin.inferray.rules.impl;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.AbstractDictionary;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.AbstractFastRule;

/**
 * PRP_SYMP
 *
 * @author Julien Subercaze
 *
 *         Feb. 14
 */
public class FC_PRP_SYMP extends AbstractFastRule {

	private final static Logger logger = Logger.getLogger(FC_PRP_SYMP.class);

	public FC_PRP_SYMP(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore newTriples) {
		super(dictionary, tripleStore, usableTriples, newTriples, true,
				"PRP-SYMP");

	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected int process(final CacheTripleStore ts1,
			final CacheTripleStore ts2, final CacheTripleStore outputTriples) {
		if (logger.isTraceEnabled()) {
			logger.trace("---------- Starting " + this.ruleName
					+ " ---------------" + ts1.getID() + " : " + ts2.getID());
			logger.trace(ts1);
			logger.trace(ts2);
		}
		// Number of inferred triples
		int newTriples = 0;
		//


		// Get the data for first part
		final LongPairArrayList list1 = ts1
				.getbyPredicate((int) AbstractDictionary.rdftype);
		if (list1 == null || list1.size() == 0) {
			return 0;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("List 1" + list1);
		}
		// Usual counters
		final long[] values = new long[2];
		// Array traversal
		for (int i = 0; i < list1.size(); i++) {
			values[0] = list1.getQuick(i);
			values[1] = list1.getQuick(++i);
			if (values[1] == AbstractDictionary.owlsymetricProperty) {
				final LongPairArrayList list2 = usableTriples
						.getbyPredicate((int) values[0]);
				if (list2 == null || list2.isEmpty()) {
					continue;
				}
				final LongPairArrayList _output = new LongPairArrayList(
						list2.size());
				// get the triples from the second list - reverse
				for (int j = -1; j < list2.size() - 1;) {
					final long tmp = list2.getQuick(++j);
					_output.add(list2.getQuick(++j));
					_output.add(tmp);
				}
				newTriples += list2.size();
				outputTriples.batchInsertion((int) values[0], _output);
			} else if (values[1] > AbstractDictionary.owlsymetricProperty) {
				break;
			}
		}
		return newTriples;
	}

}
