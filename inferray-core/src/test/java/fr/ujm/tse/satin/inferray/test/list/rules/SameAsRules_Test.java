package fr.ujm.tse.satin.inferray.test.list.rules;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import fr.ujm.tse.lt2c.satin.inferray.configuration.DefaultConfiguration;
import fr.ujm.tse.lt2c.satin.inferray.configuration.InferrayConfiguration;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;

/**
 * Test CAX-SCO rule implementations
 *
 *
 * @author Julien Subercaze
 *
 */
public class SameAsRules_Test {

	@Test
	public void test() throws IOException {
		final InferrayConfiguration config = new DefaultConfiguration();
		// Enable the same-as rules
		config.setRulesProfile(SupportedProfile.RDFSPLUS);
		final Inferray infere = new Inferray(config);
		infere.parse("src/test/resources/ontologies/sameas.rdf");
		infere.process();
		final NodeDictionary dictionary = infere.getDictionary();
		final long s1 = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/sameastest#Humain");
		final long s2 = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/sameastest#Human");
		final long s3 = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/sameastest#Mensch");
		final int p = (int) dictionary
				.get("http://www.w3.org/2002/07/owl#sameAs");
		final int p2 = (int) dictionary
				.get("http://www.w3.org/2000/01/rdf-schema#subClassOf");
		final long s4 = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/sameastest#Animal");

		// Transitivity and symmetry
		assertTrue(infere.getMainTripleStore().contains(s1, p, s2));
		assertTrue(infere.getMainTripleStore().contains(s2, p, s1));
		assertTrue(infere.getMainTripleStore().contains(s2, p, s3));
		assertTrue(infere.getMainTripleStore().contains(s3, p, s2));
		assertTrue(infere.getMainTripleStore().contains(s1, p, s3));
		assertTrue(infere.getMainTripleStore().contains(s3, p, s1));
		// Eq-req-s eq-rep-o
		assertTrue(infere.getMainTripleStore().contains(s1, p2, s4));
		assertTrue(infere.getMainTripleStore().contains(s2, p2, s4));

	}
}
