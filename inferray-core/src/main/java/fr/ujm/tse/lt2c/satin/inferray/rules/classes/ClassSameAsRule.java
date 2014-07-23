package fr.ujm.tse.lt2c.satin.inferray.rules.classes;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.AbstractDictionary;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.AbstractFastRule;

/**
 * Same-as special Rule
 *
 * Encompasses :
 * <ul>
 * <li>eq-rep-o</li>
 * <li>eq-rep-p</li>
 * <li>eq-rep-s</li>
 * <li>eq-sym</li>
 * </ul>
 *
 * Since same-as will be added for s-o symetrically, eq-rep-o is implide by
 * eq-rep-s
 *
 * @author Julien Subercaze
 *
 *         Dec. 13
 */
public class ClassSameAsRule extends AbstractFastRule {

	private final static Logger logger = Logger
			.getLogger(ClassSameAsRule.class);

	public ClassSameAsRule(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore newTriples) {
		super(dictionary, tripleStore, usableTriples, newTriples, false,
				"Same-as-Rules");

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

		}
		// Number of inferred triples
		final int newTriples = 0;
		//

		// Get the data for first part
		final LongPairArrayList list1 = ts1
				.getbyPredicate((int) AbstractDictionary.owlsameAs);
		if (list1 == null || list1.size() == 0) {
			return 0;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("List 1" + list1);
		}
		// Usual counter
		final long[] values = new long[5];

		// Array traversal
		for (int i = 0; i < list1.size(); i++) {
			values[0] = list1.getQuick(i);
			values[1] = list1.getQuick(++i);
			if (values[0] < NodeDictionary.SPLIT_INDEX) {

				// If [0] is a p - eq-rep-p - easiest one
				final LongPairArrayList list2 = usableTriples
						.getbyPredicate((int) values[0]);
				if (list2 == null) {
					continue;
				}

				outputTriples.batchInsertion((int) values[1], list2);
			} else {

				// eq-rep-s and add object
				// Add the object to enable eq-rep-o next time
				outputTriples.add(values[1], (int) NodeDictionary.owlsameAs,
						values[0]);
				// The costly iteration over every <code>p</code> ....
				for (int p = 0; p < usableTriples.getMaxActiveProperty(); p++) {
					final LongPairArrayList list2 = usableTriples
							.getbyPredicateRawIndex(p);
					if (list2 == null || list2.size() == 0) {
						continue;
					}
					// Instantiate later as possible the output list
					long val = list2.getQuick(0);
					// Since it's sorted check if first item is larger than the
					// current value
					if (val > values[0]) {
						continue;
					}
					// Same with last value - always better to check than to
					// instantiate smth for nothing
					final int listsize = list2.size();
					val = list2.getQuick(listsize - 2);
					if (val < values[0]) {
						continue;
					}
					// Ok here we need to instantiate the output triples
					final LongPairArrayList output = new LongPairArrayList();
					val = values[0];

					for (int j = 0; j < listsize; j++) {
						final long v1 = list2.getQuick(j);
						final long v2 = list2.getQuick(++j);
						if (v1 == values[0]) {
							// Add the triple to the output
							output.add(values[1]);
							output.add(v2);
						}
						// Break if we are over
						if (v2 > values[0]) {
							break;
						}

					}
					// Add the list to the triple stores if smth was inferred
					if (output.size() > 0) {
						outputTriples.batchInsertionRawIndex(p, output);
					}
				}
			}
		}

		return newTriples;
	}
}
