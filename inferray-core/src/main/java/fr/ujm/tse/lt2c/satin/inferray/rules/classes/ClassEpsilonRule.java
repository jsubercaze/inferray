package fr.ujm.tse.lt2c.satin.inferray.rules.classes;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.AbstractFastRule;

/**
 * The Epsilon class covers :
 * <ul>
 * <li>PRP-INV1/2</li>
 * <li>PRP-EQP1/2</li>
 * <li></li>
 * </ul>
 *
 * These rules have the following template
 * <pre>
 * p1 property p2
 *  x p1 y
 * -----------
 * ? p1/p2 ?
 *
 *  Field with ?
 *  has indices :
 *  x p1 y
 *  0    1
 * </pre>
 * @author Julien Subercaze
 *
 *         Feb. 14
 */
public class ClassEpsilonRule extends AbstractFastRule {

	/**
	 * Type of the property to process
	 */
	final int propertyType;
	/**
	 * Must return or not the indices
	 */
	final boolean invert;

	private final static Logger logger = Logger
			.getLogger(ClassEpsilonRule.class);

	public ClassEpsilonRule(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore newTriples, final int propertyType,
			final boolean hasInverse, final String rulename) {
		super(dictionary, tripleStore, usableTriples, newTriples, true,
				rulename);
		this.propertyType = propertyType;
		this.invert = hasInverse;

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
		final int newTriples = 0;
		// Get the triples from the property
		final LongPairArrayList mainlist = maintripleStore
				.getbyPredicate(this.propertyType);
		if (mainlist == null || mainlist.isEmpty()) {
			return 0;
		}
		final long[] values = new long[2];
		// Ok there are some p1 ... p2
		for (int i = -1; i < mainlist.size() - 1;) {
			values[0] = mainlist.getQuick(++i);
			values[1] = mainlist.getQuick(++i);
			// Must be different
			if (values[0] != values[1]) {
				// Get this p for 0
				addList(values, true);
				addList(values, false);
			}
		}
		return newTriples;
	}

	/**
	 * Add the list for a given index
	 *
	 * @param values
	 * @param index
	 * @return
	 */
	private int addList(final long[] values, final boolean first) {
		final int propIndex = (int) values[first ? 0 : 1];

		final LongPairArrayList listUsable = usableTriples.getbyPredicate(propIndex);

		if (listUsable == null || listUsable.isEmpty()) {
			return 0;
		}
		// Infer - size known in advance
		if (invert) {
			final LongPairArrayList _output = new LongPairArrayList(
					listUsable.size());
			for (int j = -1; j < listUsable.size() - 1;) {
				final long tmp = listUsable.getQuick(++j);
				_output.add(listUsable.getQuick(++j));
				_output.add(tmp);
			}
			// Add to the new triple store
			outputTriples.batchInsertion((int) values[first ? 1 : 0], _output);
		} else {
			// Simple batch insertion of the copy
			outputTriples.batchInsertion((int) values[first ? 1 : 0],
					listUsable);
		}
		return listUsable.size();
	}
}
