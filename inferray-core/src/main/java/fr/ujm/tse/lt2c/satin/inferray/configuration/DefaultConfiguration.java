package fr.ujm.tse.lt2c.satin.inferray.configuration;

import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.utils.SortingAlgorithm;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;

/**
 * Default values when no configuration is given
 * 
 * @author Julien Subercaze
 * 
 *         Jan. 2014
 * 
 */
public class DefaultConfiguration implements InferrayConfiguration {

	private SupportedProfile supportedProfile = SupportedProfile.RDFSPLUS;

	@Override
	public boolean isMultithread() {
		return true;
	}

	@Override
	public int getThreadpoolSize() {
		return Runtime.getRuntime().availableProcessors();
	}

	@Override
	public boolean isForceQuickSort() {
		return false;
	}

	@Override
	public boolean isDumpFileOnExit() {
		return false;
	}

	@Override
	public SupportedProfile getRulesProfile() {
		return this.supportedProfile;
	}

	@Override
	public int getMinimalPropertyNumber() {
		return 100;
	}

	@Override
	public void setRulesProfile(final SupportedProfile profile) {
		this.supportedProfile = profile;

	}

	@Override
	public String getAxiomaticTriplesDirectory() {
		return "./";
	}

	@Override
	public boolean isFastClosure() {
		return true;
	}

	@Override
	public boolean exportSupport() {
		return false;
	}

	@Override
	public SortingAlgorithm getSortingAlgorithm() {
		return SortingAlgorithm.HYBRID_IMD;
	}

}
