package fr.ujm.tse.lt2c.satin.inferray.rules.profile;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.AbstractFastRule;
import fr.ujm.tse.lt2c.satin.inferray.rules.utils.InferrayTriple;

/**
 * Interface for rules profile.
 *
 * @author Julien Subercaze
 *
 */
public abstract class AbstractRulesProfile implements RulesProfile {

	protected static Logger logger = Logger
			.getLogger(AbstractRulesProfile.class);
	/**
	 * Dictionary that maps URL to {@code long}
	 */
	protected NodeDictionary dictionary;
	/**
	 * Global triplestore
	 */
	protected CacheTripleStore mainTripleStore;
	/**
	 * New triples generated on previous iteration
	 */
	protected CacheTripleStore usableTriples;
	/**
	 * New generated triples in s p o form, each as a long
	 */
	protected CacheTripleStore outputTriples;
	/**
	 * The array containing the rules
	 */
	protected AbstractFastRule[] rules;
	/**
	 * Support SCM-EQP SCM-EQC
	 */
	protected boolean hasSCMEQPSCMEQC = false;

	/**
	 * The abstract constructor
	 *
	 * @param dictionary
	 * @param maintripleStore
	 * @param usableTriples
	 * @param outputTriples
	 */
	public AbstractRulesProfile(final NodeDictionary dictionary,
			final CacheTripleStore maintripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore outputTriples) {
		super();
		this.dictionary = dictionary;
		this.mainTripleStore = maintripleStore;
		this.usableTriples = usableTriples;
		this.outputTriples = outputTriples;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.ujm.tse.lt2c.satin.inferray.rules.profile.RProfile#finalization()
	 */
	@Override
	public abstract void finalization();

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.ujm.tse.lt2c.satin.inferray.rules.profile.RProfile#getRules()
	 */
	@Override
	public AbstractFastRule[] getRules() {
		return rules;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.ujm.tse.lt2c.satin.inferray.rules.profile.RProfile#getName()
	 */
	@Override
	public abstract String getName();

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * fr.ujm.tse.lt2c.satin.inferray.rules.profile.RProfile#addAxiomaticTriple
	 * ()
	 */
	@Override
	public void addAxiomaticTriple(final InferrayTriple axiomaticTriple) {
		if (logger.isTraceEnabled()) {
			logger.trace("Adding axiomaticTriple " + axiomaticTriple);
		}
		if (dictionary.get(axiomaticTriple.getObject()) == -1) {
			if (logger.isTraceEnabled()) {
				logger.trace("Not found Axiomatic triple "
						+ axiomaticTriple.getObject());
			}

		}

		mainTripleStore.add(dictionary.get(axiomaticTriple.getSubject()),
				(int) dictionary.get(axiomaticTriple.getProperty()),
				dictionary.get(axiomaticTriple.getObject()));
	}

	@Override
	public boolean hasSCMEQPSCMEQC() {
		return this.hasSCMEQPSCMEQC;
	}

	/**
	 *
	 * @return wether this rule set has a subclass closure
	 */
	@Override
	public boolean hasSubClassClosure() {
		return true;
	}

	/**
	 *
	 * @return wether this rule set has a subproperty closure
	 */
	@Override
	public boolean hasSubPropertyClosure() {
		return true;
	}
}
