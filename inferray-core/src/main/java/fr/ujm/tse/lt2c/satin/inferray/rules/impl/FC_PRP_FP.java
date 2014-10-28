package fr.ujm.tse.lt2c.satin.inferray.rules.impl;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.AbstractDictionary;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.AbstractFastRule;

/**
 * Dedicated class for prp-fp. Does not enter any template.
 *
 *
 * @author Julien Subercaze
 *
 *         Feb. 14
 */
public class FC_PRP_FP extends AbstractFastRule {

	private final static Logger logger = Logger.getLogger(FC_PRP_FP.class);

	public FC_PRP_FP(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore newTriples) {
		super(dictionary, tripleStore, usableTriples, newTriples, true,
				"PRP-FP");

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
			if (logger.isTraceEnabled()) {
				logger.trace("Nothing in L1");
			}
		} else {
			// final LongPairArrayList _copy = list1.copy();
			// _copy.objectSort();
			final LongPairArrayList _copy = list1.objectSortedCopy();
			newTriples += processTransitive(_copy);
		}
		/**
		 * Do not fire twice on first time
		 */
		if (ts1.getID() == ts2.getID()) {
			return newTriples;
		}

		/**
		 * Invert lists
		 */
		final LongPairArrayList list2 = ts2
				.getbyPredicate((int) AbstractDictionary.rdftype);
		if (list2 == null || list2.size() == 0) {
			if (logger.isTraceEnabled()) {
				logger.trace("Nothing in L2");
			}
		} else {
			// final LongPairArrayList _copy2 = list2.copy();
			// _copy2.objectSort();
			final LongPairArrayList _copy2 = list2.objectSortedCopy();
			newTriples += processTransitive(_copy2);
		}
		return newTriples;
	}

	/**
	 * Process the transitive property for the list of rdfType given here. List
	 * is neither empty nor null
	 *
	 * @param rdfType
	 * @return
	 */
	private int processTransitive(final LongPairArrayList rdfType) {
		if (logger.isTraceEnabled()) {

			logger.trace(dictionary);
			logger.trace("RDFType " + rdfType);
		}
		final long functional = AbstractDictionary.owlfunctionalProperty;
		int i = 1;
		// First check before iterating
		final int listSize = rdfType.size();
		long val = rdfType.getQuick(i);
		if (val > functional) {
			if (logger.isTraceEnabled()) {
				logger.trace("Return check one");
			}
			return 0;
		}
		if (rdfType.getQuick(listSize - 1) < functional) {
			if (logger.isTraceEnabled()) {
				logger.trace("Return check two");
			}
			return 0;
		}
		// Go to the first index with good object
		while (val < functional && i < listSize - 1) {
			i += 2;
			val = rdfType.getQuick(i);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("First good value index " + i);
			logger.trace("Val " + val);
		}
		// Value not contained, go home inferray you're drunk
		if (val > functional) {
			if (logger.isTraceEnabled()) {
				logger.trace("Finished here");
			}
			return 0;
		}
		long prop = rdfType.getQuick((i - 1));
		if (logger.isTraceEnabled()) {
			logger.trace("First good prop" + prop);
			logger.trace("Val " + val);
			logger.trace("Transitive " + functional);
		}
		// Value is contained
		for (; i < listSize; i += 2) {
			val = rdfType.getQuick(i);
			if (val != functional) {
				break;
			}
			prop = rdfType.getQuick((i - 1));

			// Get the value of the property that is transitive

			// Now go for the check in the two TS
			final int p = (int) prop;

			final LongPairArrayList listMain = maintripleStore
					.getbyPredicate(p);
			if (listMain == null || listMain.isEmpty()) {

				// System.exit(-1);
				continue;
			}
			final LongPairArrayList listNew = usableTriples.getbyPredicate(p);
			if (listNew == null || listNew.isEmpty()) {

				// System.exit(-1);
				continue;
			}
			// Lazy instantiation takes finally place
			final LongPairArrayList output = new LongPairArrayList(listMain.getSortingAlgorithm());
			// Do the usual traversal
			traverse(listMain, listNew, output, p);

		}
		return 0;
	}

	/**
	 * Traverse and add new triples
	 *
	 * @param list1
	 * @param list2
	 * @param output
	 * @return
	 */
	private int traverse(final LongPairArrayList list1,
			final LongPairArrayList list2, final LongPairArrayList output,
			final int p) {
		int newTriples = 0;

		// Counter will keep the index in the second list, avoiding to restart
		// useless loops
		int counter = 0;
		// Previous values of the first list for matching
		long previous = -1;
		// Size of the last insertion
		int sizeLastAdd = 0;
		// Values for comparison, force values in cache
		final long[] values = new long[4];

		for (int i = 0; i < list1.size(); i++) {

			values[0] = list1.getQuick(i);
			values[1] = list1.getQuick(++i);
			if (values[0] == previous && sizeLastAdd != 0) {
				final int last = sizeLastAdd / 2;
				// Result caching, reuse the results from previous insertion
				// with the new x
				// Get the number of triples added previously and replace

				// We reinsert with changing the subject
				output.duplicateInsertionWithNewSubject(values[0], last);
				newTriples += last;
			} else {
				sizeLastAdd = output.size();
				boolean broke = false;
				for (int j = counter; j < list2.size(); j += 2) {
					values[2] = list2.getQuick(j);
					values[3] = list2.getQuick(j + 1);

					if (values[0] == values[2] && values[1] != values[3]) {

						output.add(values[1]);
						output.add(values[3]);

						newTriples++;
					} else if (values[2] > values[0]) {
						counter = j;
						broke = true;
						break;
					}
				}

				sizeLastAdd = output.size() - sizeLastAdd;
				if (!broke) {

					// Reached the end of second list
					break;
				}
			}
			previous = values[0];
		}
		// Appends the triple to the triple store in batch mode
		outputTriples
		.batchInsertion((int) AbstractDictionary.owlsameAs, output);
		if (logger.isTraceEnabled()) {
			logger.trace("EOT");
			logger.trace("Outputting " + outputTriples);
		}
		return newTriples;
	}
}
