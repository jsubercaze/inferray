package fr.ujm.tse.satin.inferray.test.list.rules;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;

/**
 * Test CAX-SCO rule implementations
 *
 *
 * @author Julien Subercaze
 *
 */
public class FCG_PRP_SPO1_Test {

	@Test
	public void test() throws IOException {
		final Inferray infere = new Inferray();
		infere.parse("src/test/resources/ontologies/prp-spo1.rdf");
		infere.process();
		final NodeDictionary dictionary = infere.getDictionary();
		final long s1 = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/prpspo1#Michel");
		final long s2 = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/prpspo1#Alice");
		final long s3 = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/prpspo1#Roger");
		final int p = (int) dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/prpspo1#know");
		assertTrue(infere.getMainTripleStore().contains(s1, p, s2));
		assertTrue(infere.getMainTripleStore().contains(s2, p, s3));
	}
}
