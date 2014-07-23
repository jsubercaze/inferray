package fr.ujm.tse.lt2c.satin.inferray.rules.profile;

import fr.ujm.tse.lt2c.satin.inferray.configuration.MyConfiguration;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;

/**
 * Factory providing rules profile instances
 *
 * @author Julien Subercaze
 *
 */
public class RulesProfileFactory {
	/**
	 *
	 * @param profile
	 *            rules profile
	 * @param dictionary
	 *            encoding dictionary
	 * @param mainTripleStore
	 *            the main triple store
	 * @param usableTriples
	 *            triple store for new triples required for inferring
	 * @param outputTriples
	 *            triple store containing all the inferred triples
	 * @return a {@link RulesProfile}
	 */
	public static RulesProfile getProfileInstance(
			final SupportedProfile profile, final NodeDictionary dictionary,
			final CacheTripleStore mainTripleStore,
			final CacheTripleStore usableTriples,
			final CacheTripleStore outputTriples,final MyConfiguration config) {
		RulesProfile ruleprofile = null;
		switch (profile) {
		case RHODF:
			ruleprofile = new RhoDFRuleProfile(dictionary, mainTripleStore,
					usableTriples, outputTriples);
			break;
		case RDFS:
			ruleprofile = new RDFSRuleProfile(dictionary, mainTripleStore,
					usableTriples, outputTriples,config);
			break;
		case RDFSPLUS:
			ruleprofile = new RDFSPlusRuleProfile(dictionary, mainTripleStore,
					usableTriples, outputTriples);
			break;
		case RDFSDEFAULT:
			ruleprofile = new RDFSDefaultRuleProfile(dictionary,
					mainTripleStore, usableTriples, outputTriples);
			break;
		default:
			break;
		}
		return ruleprofile;
	}
}
