package fr.ujm.tse.lt2c.satin.inferray.rules.classes;

import java.util.Arrays;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.AbstractFastRule;

/**
 * <p>
 * <b>After these rules, the property triples in the second triplestore are
 * sorted by objects</b>
 * </p>
 * Class beta groups the following rules :
 * <ul>
 * <li>SCM-SCO</li>
 * <li>SCM-EQC2</li>
 * <li>SCM-SPO</li>
 * <li>SCM-EQP2</li>
 * </ul>
 *
 * All these rules have the following properties :
 * <ol>
 * <li>same predicate in both parts</li>
 * </ol>
 *
 * Rules can be simultaneously run in the following groups :
 * <ul>
 * <li>SCM-SCO & SCM-EQC2</li>
 * <li>SCM-SPO & SCM-EQP2</li>
 * </ul>
 *
 *
 *
 * @author Julien Subercaze
 *
 *         Dec. 2013
 *
 */
public class ClassBetaRule extends AbstractFastRule {

	private static Logger logger = Logger.getLogger(ClassBetaRule.class);

	/**
	 * Property of the left part of the rule
	 */
	final int ruleProperty;
	/**
	 * Property of the inferred triples
	 */
	final int equalityOutputProperty;
	/**
	 * Execute the equivalent rule together. For instance RDFS requires this
	 * value to false
	 */
	private final boolean inferEquivalent;

	public ClassBetaRule(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore newTriples, final int ruleProperty,
			final int equalityOutputProperty, final boolean inferEquivalent,
			final String ruleName) {
		super(dictionary, tripleStore, usableTriples, newTriples, true,
				ruleName);
		this.ruleProperty = ruleProperty;
		this.equalityOutputProperty = equalityOutputProperty;
		this.inferEquivalent = inferEquivalent;
	}

	public ClassBetaRule(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore newTriples, final int ruleProperty,
			final int equalityOutputProperty, final boolean inferEquivalent,
			final String ruleName, final boolean invertible) {
		super(dictionary, tripleStore, usableTriples, newTriples, invertible,
				ruleName);
		this.ruleProperty = ruleProperty;
		this.equalityOutputProperty = equalityOutputProperty;
		this.inferEquivalent = inferEquivalent;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected final int process(final CacheTripleStore ts1,
			final CacheTripleStore ts2, final CacheTripleStore outputTriples) {
		if (logger.isTraceEnabled()) {
			logger.trace("---------- Starting " + this.ruleName
					+ " ---------------" + ts1.getID() + " : " + ts2.getID());
			logger.trace("Infere Equivalent ? " + this.inferEquivalent);

		}
		// Number of inferred triples
		int newTriples = 0;

		// Inversion of indices for range reason, see object sorting later
		LongPairArrayList list1 = ts2.getbyPredicate(ruleProperty);
		if (list1 == null || list1.isEmpty()) {
			return 0;
		}
		final LongPairArrayList list2 = ts1.getbyPredicate(ruleProperty);
		if (list2 == null || list2.isEmpty()) {
			return 0;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("List 1 " + list1);
			logger.trace("List 2 " + list2);
		}
		// list1 = list1.copy();
		// list1.objectSort();
		list1 = list1.objectSortedCopy();
		// 2 outputs for sub** and equivalent***
		final LongPairArrayList output = new LongPairArrayList();
		final LongPairArrayList outputEquivalent = new LongPairArrayList();

		// Usual counters
		int counter = 0;

		// For caching results
		long previous = -1;
		int sizeLastAdd = 0;
		// Here only s1,o1,s2 and o2 are stored, p
		// being the same
		final long[] values = new long[4];

		// Process the lists
		for (int i = 0; i < list1.size(); i++) {
			values[0] = list1.getQuick(i);
			values[1] = list1.getQuick(++i);

			if (previous == values[1]) {

				if (sizeLastAdd == 0) {
					continue;
				}
				final int last = sizeLastAdd / 2;
				// Do not put inferEquivalent before ! Would miss triples !
				if (output.duplicateInsertionNewSubjectWithEqualityCheck(
						values[0], last) && inferEquivalent) {

					outputEquivalent.add(values[0]);
					outputEquivalent.add(values[1]);
					outputEquivalent.add(values[1]);
					outputEquivalent.add(values[0]);
				}
				newTriples += last;
			} else {
				sizeLastAdd = output.size();
				boolean broke = false;
				for (int j = counter; j < list2.size(); j += 2) {
					values[2] = list2.getQuick(j);
					values[3] = list2.getQuick(j + 1);
					if (logger.isTraceEnabled()) {
						logger.trace("Values " + Arrays.toString(values));
					}
					// Indexed by subject
					if (values[1] == values[2]) {

						if (values[0] == values[3]) {
							// Do not move that up !! I know it's tempting you
							if (inferEquivalent) {

								// add the equivalent
								outputEquivalent.add(values[0]);
								outputEquivalent.add(values[1]);
								outputEquivalent.add(values[1]);
								outputEquivalent.add(values[0]);
								newTriples += 2;
							}
						} else {
							// add property
							output.add(values[0]);
							output.add(values[3]);
							newTriples++;
						}

					} else if (values[2] > values[1]) {

						broke = true;
						counter = j;
						break;
					}
				}
				sizeLastAdd = output.size() - sizeLastAdd;
				if (!broke) {
					if (i < list1.size()-1) {
						values[0] = list1.getQuick(i);
						values[1] = list1.getQuick(++i);
						while (values[0] == previous) {
							final int last = sizeLastAdd / 2;
							// Do not put inferEquivalent before ! Would miss
							// triples !
							if (output
									.duplicateInsertionNewSubjectWithEqualityCheck(
											values[0], last)
											&& inferEquivalent) {
								outputEquivalent.add(values[0]);
								outputEquivalent.add(values[1]);
								outputEquivalent.add(values[1]);
								outputEquivalent.add(values[0]);
							}
							newTriples += last;
							if (i == list1.size()) {
								break;
							}
							values[0] = list1.getQuick(i);
							values[1] = list1.getQuick(++i);
						}
					}

					break;
				}
			}
			previous = values[1];
		}
		// Appends the triple to the triple store in batch mode
		outputTriples.batchInsertion(this.ruleProperty, output);
		if (logger.isTraceEnabled()) {
			logger.trace("output " + output);
		}
		if (inferEquivalent) {
			if (logger.isTraceEnabled()) {
				logger.trace("Adding eq prop " + outputEquivalent);
			}
			outputTriples.batchInsertion(this.equalityOutputProperty,
					outputEquivalent);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Main triple after execution " + ts1);
			logger.trace("Output after execution " + outputTriples);

		}
		return newTriples;

	}
}
