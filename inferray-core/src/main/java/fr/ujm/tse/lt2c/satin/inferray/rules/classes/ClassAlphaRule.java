package fr.ujm.tse.lt2c.satin.inferray.rules.classes;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.AbstractFastRule;

/**
 * 
 * Class alpha groups the following rules :
 * <ul>
 * <li>CAX-SCO</li>
 * <li>SCM-DOM1</li>
 * <li>SCM-DOM2</li>
 * <li>SCM-RNG1</li>
 * <li>SCM-RNG2</li>
 * </ul>
 * 
 * All these rules have the following properties :
 * <ol>
 * <li>2 fixed predicates in the head triples</li>
 * <li>Equality between first subject second object or first object second
 * subject</li>
 * <li>Inferred triple contains only s,p,o from the head</li>
 * </ol>
 * 
 * Boolean s1o2 indicates whether the rules has a s1 equal to o2 template or a
 * o1 equal to s2 template.
 * 
 * @author Julien Subercaze
 * 
 *         Dec. 2013
 */
public class ClassAlphaRule extends AbstractFastRule {
	private static Logger logger = Logger.getLogger(ClassAlphaRule.class);

	/**
	 * Property of the first part of the rule
	 */
	final int property1;
	/**
	 * Property of the second part of the rule
	 */
	final int property2;

	/**
	 * index of the subject for the inferred triples
	 * 
	 * Indices are the following :
	 * 
	 * <pre>
	 *  _________________
	 * |s1,p1,o1,s2,p2,o2|
	 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
	 *  0  1  2  3  4  5
	 * </pre>
	 */
	final int subjectIndexOutput;
	/**
	 * index of the property for the inferred triples
	 */
	final int propertyIndexOutput;
	/**
	 * index of the object for the inferred triples
	 */
	final int objectIndexOutput;

	/**
	 * 
	 * @param dictionary
	 *            Dictionary of resources/numerical values
	 * @param tripleStore
	 *            main triplestore
	 * @param usableTriples
	 *            new triples from previous inference
	 * @param outputTriples
	 *            inferred triples
	 * @param property1
	 *            first property to use
	 * @param property2
	 *            second property
	 * @param s1o2
	 *            is the equality between s1 and o2 or o1 and s2
	 * @param subjectIndexOutput
	 *            index of the inferred subject
	 * @param propertyIndexOutput
	 *            index of the inferred property
	 * @param objectIndexOutput
	 *            index of the inferred object
	 * @param rulename
	 *            name of the rule
	 */
	public ClassAlphaRule(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore outputTriples, final int property1,
			final int property2, final int subjectIndexOutput,
			final int propertyIndexOutput, final int objectIndexOutput,
			final String rulename) {
		super(dictionary, tripleStore, usableTriples, outputTriples, rulename);
		this.property1 = property1;
		this.property2 = property2;

		this.subjectIndexOutput = subjectIndexOutput;
		this.propertyIndexOutput = propertyIndexOutput;
		this.objectIndexOutput = objectIndexOutput;
	}

	@Override
	protected final int process(final CacheTripleStore ts1,
			final CacheTripleStore ts2, final CacheTripleStore outputTriples) {
		if (logger.isTraceEnabled()) {
			logger.trace("---------- Starting " + this.ruleName
					+ " ---------------" + ts1.getID() + " : " + ts2.getID());

		}
		// Number of inferred triples
		int newTriples = 0;

		// Get the LongPairArrayLists
		final LongPairArrayList list1 = ts1.getbyPredicate(property1);
		if (list1 == null || list1.size() == 0) {
			if (logger.isTraceEnabled()) {
				logger.trace("List 1 is either empty or null, for property "+this.dictionary.get(property1));
			}
			return 0;
		}
		LongPairArrayList list2 = ts1.getbyPredicate(property2);
		if (list2 == null || list2.size() == 0) {
			if (logger.isTraceEnabled()) {
				logger.trace("List 2 is either empty or null, for property "+this.dictionary.get(property2));
			}
			return 0;
		}

		// Sort by objects if required - Total sorting
		// list2 = list2.copy();
		// list2.objectSort();
		list2 = list2.objectSortedCopy();

		// Fire in the hole
		if (logger.isTraceEnabled()) {
			logger.trace("After sorting");
			logger.trace(ts1);
			logger.trace(ts2);
		}
		// Will contain the inferred triples
		final LongPairArrayList output = new LongPairArrayList();

		// Counter will keep the index in the second list, avoiding to restart
		// useless loops
		int counter = 0;
		// Previous values of the first list for matching
		long previous = -1;
		// Size of the last insertion
		int sizeLastAdd = 0;
		// Values for comparison, force values in cache
		final long[] values = new long[6];
		values[1] = property1;
		values[4] = property2;
		for (int i = 0; i < list1.size(); i++) {
			values[0] = list1.getQuick(i);
			values[2] = list1.getQuick(++i);
			if (values[0] == previous && sizeLastAdd != 0) {
				final int last = sizeLastAdd / 2;
				// Result caching, reuse the results from previous insertion
				// with the new x
				// Get the number of triples added previously and replace
				output.duplicateInsertionWithNewObject(values[2], last);
				newTriples += last;
			} else {
				sizeLastAdd = output.size();
				boolean broke = false;
				for (int j = counter; j < list2.size(); j += 2) {
					values[3] = list2.getQuick(j);
					values[5] = list2.getQuick(j + 1);
					if (values[0] == values[5]) {

						output.add(values[subjectIndexOutput]);
						output.add(values[objectIndexOutput]);

						newTriples++;
					} else if (values[5] > values[0]) {
						counter = j;
						broke = true;
						break;
					}
				}

				sizeLastAdd = output.size() - sizeLastAdd;
				if (!broke) {
					// Reached the end of second list - Check if subjects in
					// first list remains the same. See example in the paper
					if (i < list1.size()-1) {
						values[0] = list1.getQuick(i);
						values[2] = list1.getQuick(++i);
						while (values[0] == previous) {
							// Infer
							final int last = sizeLastAdd / 2;
							output.duplicateInsertionWithNewObject(values[2],
									sizeLastAdd >> 1);
							newTriples += last;
							if (i == list1.size()) {
								break;
							}
							values[0] = list1.getQuick(i);
							values[2] = list1.getQuick(++i);
						}
					}
					break;
				}
			}
			previous = values[0];
		}
		// Appends the triple to the triple store in batch mode
		outputTriples.batchInsertion((int) values[propertyIndexOutput], output);
		if (logger.isTraceEnabled()) {
			logger.trace("Inferred "+output);
			logger.trace(ts1);
			logger.trace(outputTriples);
		}
		return newTriples;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
