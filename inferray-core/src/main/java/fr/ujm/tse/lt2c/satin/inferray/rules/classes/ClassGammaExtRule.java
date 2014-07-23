package fr.ujm.tse.lt2c.satin.inferray.rules.classes;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.AbstractFastRule;

/**
 * Gamma extended rules are of the following form :
 *
 * <pre>
 * p property p2
 * x p y
 * ------------
 * ? ? ?
 * </pre>
 *
 * Rules :
 * <ul>
 * <li>PRP-EQP1/2</li>
 * <li>PRP-INV</li>
 * <li>PRP-SYMP</li>
 * </ul>
 *
 *
 * Indices are following :
 *
 * index of the subject for the inferred triples
 *
 * Indices are the following :
 *
 * <pre>
 *  _________________
 * |s1,p1,o1,s2,p2,o2|
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
 *  0  _  1  2  3  4
 * </pre>
 *
 * @author Julien Subercaze
 *
 *         Dec. 13
 */
public class ClassGammaExtRule extends AbstractFastRule {

	private final static Logger logger = Logger
			.getLogger(ClassGammaExtRule.class);
	/**
	 * Property of the first part of the rule
	 */
	final int ruleProperty;
	/**
	 * Index of the inferred subject.
	 */
	final int outputIndexSubject;
	/**
	 * For the inferred triple : <code>p</code>
	 */
	final int outputIndexProperty;
	/**
	 * For the inferred triple : <code>o</code>
	 */
	final int outputIndexObject;

	public ClassGammaExtRule(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore, final CacheTripleStore usableTriples,
			final CacheTripleStore newTriples, final int ruleProperty, final int outputProperty,
			final int subjectIndexOutput, final int outputIndexObject, final String ruleName) {
		super(dictionary, tripleStore, usableTriples, newTriples, ruleName);
		this.ruleProperty = ruleProperty;
		this.outputIndexProperty = outputProperty;
		this.outputIndexSubject = subjectIndexOutput;
		this.outputIndexObject = outputIndexObject;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected int process(final CacheTripleStore ts1, final CacheTripleStore ts2,
			final CacheTripleStore outputTriples) {
		if (logger.isTraceEnabled()) {
			logger.trace("---------- Starting " + this.ruleName
					+ " ---------------" + ts1.getID() + " : " + ts2.getID());

		}
		// Number of inferred triples
		int newTriples = 0;
		//
		final LongPairArrayList output = new LongPairArrayList();

		// Get the data for first part
		final LongPairArrayList list1 = ts1.getbyPredicate(ruleProperty);
		if (list1 == null || list1.size() == 0) {
			return 0;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("List 1" + list1);
		}
		final int subjectPadding = outputIndexSubject == 2 ? 0 : 1;
		// Usual counters
		long previous = -1;
		final long[] values = new long[5];
		int sizeLastAdd = 0;

		// Array traversal
		for (int i = 0; i < list1.size(); i++) {
			values[0] = list1.getQuick(i);
			values[1] = list1.getQuick(++i);
			if (values[0] == previous) {
				// Duplicate the last add
				if (sizeLastAdd == 0) {
					continue;
				}
				final int last = sizeLastAdd / 2;


				output.duplicateInsertionWithNewObject(values[1], last);
				newTriples += last;
			} else {
				final LongPairArrayList list2 = usableTriples
						.getbyPredicate((int) values[0]);
				if (list2 == null) {
					continue;
				}
				sizeLastAdd = output.size();
				// get the triples from the second list

				for (int j = 0; j < list2.size(); j += 2) {
					output.add(list2.get(j + subjectPadding));
					output.add(values[1]);

					newTriples++;

				}
				sizeLastAdd = output.size() - sizeLastAdd;

			}
			previous = values[0];
		}

		return newTriples;
	}
}
