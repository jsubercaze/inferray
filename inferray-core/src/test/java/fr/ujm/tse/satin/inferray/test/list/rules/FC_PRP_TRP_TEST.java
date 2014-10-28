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
 * Test PRP-TRP rule implementation
 *
 *
 * @author Julien Subercaze
 *
 */
public class FC_PRP_TRP_TEST {

	@Test
	public void test() throws IOException {
		final InferrayConfiguration config = new DefaultConfiguration();
		// Enable the PRP-TRP rule
		config.setRulesProfile(SupportedProfile.RDFSPLUS);
		final Inferray infere = new Inferray(config);
		infere.parse("src/test/resources/ontologies/prp-trp.rdf");
		infere.process();
		final NodeDictionary dictionary = infere.getDictionary();
		final long s1 = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/prptrp#Humain");

		final long s2 = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/prptrp#Animal");
		final long s3 = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/prptrp#LivingThing");
		final long s4 = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/prptrp#Thing");

		final int p = (int) dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/prptrp#SousClasse");
		// Check transitive closure
		assertTrue(infere.getMainTripleStore().contains(s1, p, s2));
		assertTrue(infere.getMainTripleStore().contains(s2, p, s3));
		assertTrue(infere.getMainTripleStore().contains(s1, p, s3));
		assertTrue(infere.getMainTripleStore().contains(s3, p, s4));
		assertTrue(infere.getMainTripleStore().contains(s2, p, s4));
		assertTrue(infere.getMainTripleStore().contains(s1, p, s4));
	}
}
