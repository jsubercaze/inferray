package fr.ujm.tse.lt2c.satin.inferray.rules;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;

/**
 * Adaptation of the previous {@link AbstractRule}, here working with cache
 * oriented data structure.
 * 
 * Here rule execution is voluntarily monothreaded
 * 
 * @author Julien Subercaze, Jules Chevalier
 * 
 */
public abstract class AbstractFastRule {

	/**
	 * Dictionary that maps URL to {@code long}
	 */
	protected final NodeDictionary dictionary;
	/**
	 * Global triplestore
	 */
	protected final CacheTripleStore maintripleStore;
	/**
	 * New triples generated on previous iteration
	 */
	protected CacheTripleStore usableTriples;
	/**
	 * New generated triples in s p o form, each as a long
	 */
	protected final CacheTripleStore outputTriples;
	/**
	 * Name of the rule
	 */
	protected String ruleName = "";
	/**
	 * Rule shall be called by inverting triple stores
	 */
	protected final boolean invertible;

	public AbstractFastRule(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore outputTriples, final String ruleName) {
		this.dictionary = dictionary;
		this.maintripleStore = tripleStore;
		this.usableTriples = usableTriples;
		this.outputTriples = outputTriples;
		this.invertible = true;
		this.ruleName = ruleName;

	}

	public AbstractFastRule(final NodeDictionary dictionary,
			final CacheTripleStore tripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore outputTriples, final boolean invertible,
			final String ruleName) {
		this.dictionary = dictionary;
		this.maintripleStore = tripleStore;
		this.usableTriples = usableTriples;
		this.outputTriples = outputTriples;
		this.invertible = invertible;
		this.ruleName = ruleName;

	}

	abstract protected Logger getLogger();

	public String getRuleName() {
		return ruleName;
	}

	/**
	 * Start the execution of the rule, switching the triples if required
	 * 
	 * @return the number of inferred triples
	 */
	public int fire() {
		if (this.getLogger().isTraceEnabled()) {
			this.getLogger().trace("Firing " + this.ruleName);
		}
		if (!maintripleStore.equals(usableTriples)) {
			if (this.getLogger().isTraceEnabled()) {
				this.getLogger().trace(
						"Firing rule " + ruleName + " for steady state");
			}
			if (this.invertible) {
				return this.process(maintripleStore, usableTriples,
						outputTriples)
						+ this.process(usableTriples, maintripleStore,
								outputTriples);
			} else {
				return this.process(maintripleStore, usableTriples,
						outputTriples);
			}
		} else {
			if (this.getLogger().isTraceEnabled()) {
				this.getLogger().trace(
						"Firing rule " + ruleName + " first time");
			}
			return this
					.process(maintripleStore, maintripleStore, outputTriples);
		}
	}

	/**
	 * Simple execution that does not switch the triple stores. To use for
	 * symmetric rules like SCM-SCO and so on
	 * 
	 * @return the number of inferred triples
	 */
	public int simpleFire() {
		return this.process(maintripleStore, usableTriples, outputTriples);
	}

	public void setNewTripleStore(final CacheTripleStore newTriples2) {
		this.usableTriples = newTriples2;

	}

	protected abstract int process(CacheTripleStore ts1, CacheTripleStore ts2,
			CacheTripleStore outputTriples);

	/**
	 * Convert an array of values to a String. For debugging purpose
	 * 
	 * @param values
	 * @return
	 */
	protected String convertValuesToString(final long[] values) {
		String rs = "[";
		for (int i = 0; i < values.length; i++) {
			if (values[i] == 0) {
				rs += " . ";
			} else {
				rs += dictionary.get(values[i]) + ",";
			}
		}
		rs += "]";
		return rs;
	}

	@Override
	public String toString() {
		return "AbstractFastRule [ruleName=" + ruleName + "]";
	}

}
