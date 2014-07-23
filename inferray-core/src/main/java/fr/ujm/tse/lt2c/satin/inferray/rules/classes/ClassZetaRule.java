package fr.ujm.tse.lt2c.satin.inferray.rules.classes;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.AbstractDictionary;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.AbstractFastRule;

/**
 * Class for stupid rdfs entailment without assertional part. Written for the
 * sake of compatibility and benchmarking . Real usage close to nil.
 * <ul>
 * <li>rdfs6</li>
 * <li>rdfs8</li>
 * <li>rdfs10</li>
 * <li>rdfs12</li>
 * <li>rdfs13</li>
 * <li></li>
 * </ul>
 *
 * @author Julien Subercaze
 *
 *         Jan. 2014
 *
 */
public class ClassZetaRule extends AbstractFastRule {
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(ClassZetaRule.class);
	/**
	 * numerical value of the terminological object from the rule body
	 */
	private final long objectTerminological;
	/**
	 * numerical value of the property for the inferred triples
	 */
	private final long propertyInferred;
	/**
	 * numerical value of the object for the inferred triples. If equals to -1,
	 * use the subject of the terminological triple
	 */
	private final long objectInferred;

	public ClassZetaRule(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore outputTriples,
			final long objectTerminological, final long propertyInferred,
			final long objectInferred, final String ruleName) {
		super(dictionary, tripleStore, usableTriples, outputTriples, ruleName);
		this.objectTerminological = objectTerminological;
		this.propertyInferred = propertyInferred;
		this.objectInferred = objectInferred;
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
		int newTriples = 0;
		// All these rules share a rdf:type in the terminological part
		final LongPairArrayList list1 = ts1
				.getbyPredicate((int) AbstractDictionary.rdftype);
		if (list1 == null || list1.isEmpty()) {
			return 0;
		}
		long currentObject = -1;
		long currentSubject = -1;
		int i = -1;
		final boolean useSubject = (objectInferred == -1);
		final LongPairArrayList output = new LongPairArrayList();
		while (currentObject < objectTerminological && i < (list1.size() - 2)) {
			currentSubject = list1.getQuick(++i);
			currentObject = list1.getQuick(++i);
			if (currentObject == objectTerminological) {
				// Infer
				output.add(currentSubject);
				if (useSubject) {
					output.add(currentSubject);
				} else {
					output.add(objectInferred);
				}
				newTriples++;
			}
		}
		outputTriples.batchInsertion((int) this.propertyInferred, output);
		if (logger.isTraceEnabled()) {
			logger.trace("Main triple after execution " + ts1);
			logger.trace("Output after execution " + outputTriples);

		}
		return newTriples;
	}
}
