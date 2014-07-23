package fr.ujm.tse.satin.inferray.test.list.rules;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import fr.ujm.tse.lt2c.satin.inferray.configuration.DefaultConfiguration;
import fr.ujm.tse.lt2c.satin.inferray.configuration.MyConfiguration;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;

/**
 * Test SCM-RNG2 rule implementations
 *
 *
 * @author Julien Subercaze
 *
 */
public class FCB_SCM_SCO_EQC2_Test {

	@Test
	public void test() throws IOException {
		final MyConfiguration config = new DefaultConfiguration();
		config.setRulesProfile(SupportedProfile.RHODF);

		final Inferray infere = new Inferray(config);
		infere.parse("src/test/resources/ontologies/scm-sco-eqc2.rdf");
		infere.process();
		final NodeDictionary dictionary = infere.getDictionary();
		final long o = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/scm-eqc2-sco#Animal");
		final long o2 = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/scm-eqc2-sco#LivingThing");

		final long s = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/scm-eqc2-sco#Humain");
		final int p = (int) dictionary
				.get("http://www.w3.org/2000/01/rdf-schema#subClassOf");
		final int p2 = (int) dictionary
				.get("http://www.w3.org/2002/07/owl#equivalentClass");

		assertTrue(infere.getMainTripleStore().contains(s, p, o2));
		assertTrue(infere.getMainTripleStore().contains(s, p2, o));
	}
}
