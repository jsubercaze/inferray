package fr.ujm.tse.lt2c.satin.inferray.configuration;

import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;

/**
 * Utility to build configurations usind builder pattern
 *
 * @author Julien
 *
 *         Feb 14
 */
public class ConfigurationBuilder {

	private PropertyConfiguration configurationBean = new PropertyConfiguration();

	public ConfigurationBuilder setMultithread(final boolean multithread) {
		checkNotBuilt();
		configurationBean.setMultithread(multithread);
		return this;
	}

	public ConfigurationBuilder setThreadpoolSize(final int threadpoolSize) {
		checkNotBuilt();
		configurationBean.setThreadpoolSize(threadpoolSize);
		return this;
	}

	public ConfigurationBuilder setForceQuickSort(final boolean forceQuickSort) {
		checkNotBuilt();
		configurationBean.setForceQuickSort(forceQuickSort);
		return this;
	}

	public ConfigurationBuilder setDumpFileOnExit(final boolean dumpFileOnExit) {
		checkNotBuilt();
		configurationBean.setDumpFileOnExit(dumpFileOnExit);
		return this;
	}

	public ConfigurationBuilder setRulesProfile(
			final SupportedProfile rulesProfile) {
		checkNotBuilt();
		configurationBean.setRulesProfile(rulesProfile);
		return this;
	}

	public ConfigurationBuilder setMinimalPropertyNumber(
			final int minimalPropertyNumber) {
		checkNotBuilt();
		configurationBean.setMinimalPropertyNumber(minimalPropertyNumber);
		return this;
	}

	public ConfigurationBuilder setAxiomaticTriplesDirectory(
			final String directory) {
		checkNotBuilt();
		configurationBean.setAxiomaticDirectory(directory);
		return this;
	}

	public ConfigurationBuilder setExportTriples(final boolean export) {
		checkNotBuilt();
		configurationBean.setExportTriples(export);
		return this;
	}

	public PropertyConfiguration build() {
		try {
			return this.configurationBean;
		} finally {
			configurationBean = null;
		}
	}

	public ConfigurationBuilder setFastClosure(final boolean bool) {
		checkNotBuilt();
		configurationBean.setFastClosure(bool);
		return this;
	}

	private void checkNotBuilt() {
		if (configurationBean == null) {
			throw new IllegalStateException();
		}
	}

}
