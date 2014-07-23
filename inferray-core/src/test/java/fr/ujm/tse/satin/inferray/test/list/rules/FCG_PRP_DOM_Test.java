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
public class FCG_PRP_DOM_Test {

	@Test
	public void test() throws IOException {
		final Inferray infere = new Inferray();
		infere.parse("src/test/resources/ontologies/prp-dom.rdf");
		infere.process();
		final NodeDictionary dictionary = infere.getDictionary();
		final long s = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/prpdom#Michel");

		final int p = (int) dictionary
				.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		final long o = dictionary
				.get("http://www.semanticweb.org/jules/ontologies/2013/6/prpdom#Human");

		assertTrue(infere.getMainTripleStore().contains(s, p, o));
	}
}
