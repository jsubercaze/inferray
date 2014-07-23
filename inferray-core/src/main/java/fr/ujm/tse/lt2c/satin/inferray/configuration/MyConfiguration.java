package fr.ujm.tse.lt2c.satin.inferray.configuration;

import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;

/**
 * Interface for configuration
 *
 * @author Julien Subercaze
 *
 *         Jan. 2014
 *
 */
public interface MyConfiguration {

	/**
	 *
	 * @return true is the application should be multithreaded
	 */
	public boolean isMultithread();

	/**
	 *
	 * @return the number of threads is the thread pools
	 */
	public int getThreadpoolSize();

	/**
	 *
	 * @return true is quicksort should be forced over counting sort
	 */
	public boolean isForceQuickSort();

	/**
	 *
	 * @return true if the inferred model should be dumped in a file after the
	 *         execution
	 */
	public boolean isDumpFileOnExit();

	/**
	 *
	 * @return the set of rules that will be used by the reasoner
	 */
	public SupportedProfile getRulesProfile();

	/**
	 * Set the rule used by the reasoner
	 *
	 * @param profile
	 */
	public void setRulesProfile(SupportedProfile profile);

	/**
	 *
	 * @return minimum number of properties to start with
	 */
	public int getMinimalPropertyNumber();

	/**
	 *
	 * @return the directory containing axiomatic triples
	 */
	public String getAxiomaticTriplesDirectory();

	/**
	 *
	 * @return <code>true</code> if the fast transitivity closure is executed
	 */
	public boolean isFastClosure();

	/**
	 *
	 * @return <code>true</code> to support triples export for external bindings
	 */
	public boolean exportSupport();

}
