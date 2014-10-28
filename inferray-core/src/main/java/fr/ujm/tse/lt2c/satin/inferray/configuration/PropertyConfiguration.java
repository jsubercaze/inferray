package fr.ujm.tse.lt2c.satin.inferray.configuration;

import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.utils.SortingAlgorithm;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;

public class PropertyConfiguration implements InferrayConfiguration {
	/**
	 * Multi thread sorting operations
	 */
	boolean multithread = true;
	/**
	 * Size of the threadpool in this case
	 */
	int threadpoolSize = Runtime.getRuntime().availableProcessors();;
	/**
	 * Should it force quicksort instead of countsort ?
	 * 
	 * Not advised, for testing only
	 */
	boolean forceQuickSort = false;
	/**
	 * Dump model on exit
	 */
	boolean dumpFileOnExit = false;
	/**
	 * Logical fragment supported
	 */
	SupportedProfile rulesProfile = SupportedProfile.RDFSDEFAULT;
	/**
	 * Number of properties
	 * 
	 * Should be removed in next version
	 */
	int minimalPropertyNumber = 20000;
	/**
	 * Location of axiomatic triples files
	 */
	String axiomaticDirectory = "./";
	/**
	 * Use fast transitivity closure
	 */
	boolean fastClosure = true;
	/**
	 * Don't export triples
	 */
	boolean exportTriples = false;

	SortingAlgorithm algorithm;

	public PropertyConfiguration() {
		super();
	}

	@Override
	public boolean isMultithread() {
		return multithread;
	}

	public void setMultithread(final boolean multithread) {
		this.multithread = multithread;
	}

	@Override
	public int getThreadpoolSize() {
		return threadpoolSize;
	}

	public void setThreadpoolSize(final int threadpoolSize) {
		this.threadpoolSize = threadpoolSize;
	}

	@Override
	public boolean isForceQuickSort() {
		return forceQuickSort;
	}

	public void setForceQuickSort(final boolean forceQuickSort) {
		this.forceQuickSort = forceQuickSort;
	}

	@Override
	public boolean isDumpFileOnExit() {
		return dumpFileOnExit;
	}

	public void setDumpFileOnExit(final boolean dumpFileOnExit) {
		this.dumpFileOnExit = dumpFileOnExit;
	}

	@Override
	public SupportedProfile getRulesProfile() {
		return rulesProfile;
	}

	@Override
	public void setRulesProfile(final SupportedProfile rulesProfile) {
		this.rulesProfile = rulesProfile;
	}

	@Override
	public int getMinimalPropertyNumber() {
		return minimalPropertyNumber;
	}

	public void setMinimalPropertyNumber(final int minimalPropertyNumber) {
		this.minimalPropertyNumber = minimalPropertyNumber;
	}

	@Override
	public String getAxiomaticTriplesDirectory() {
		return axiomaticDirectory;
	}

	public void setAxiomaticDirectory(final String axiomaticDirectory) {
		this.axiomaticDirectory = axiomaticDirectory;
	}

	@Override
	public boolean isFastClosure() {
		return fastClosure;
	}

	public void setFastClosure(final boolean fastClosure) {
		this.fastClosure = fastClosure;
	}

	public void setExportTriples(final boolean exportTriples) {
		this.exportTriples = exportTriples;
	}

	@Override
	public boolean exportSupport() {
		return this.exportTriples;
	}

	public void setSortingAlgorithm(final SortingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public SortingAlgorithm getSortingAlgorithm() {

		return algorithm;
	}

}
