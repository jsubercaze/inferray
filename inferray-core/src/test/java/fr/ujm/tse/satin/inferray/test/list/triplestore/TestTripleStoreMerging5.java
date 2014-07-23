package fr.ujm.tse.satin.inferray.test.list.triplestore;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;
import fr.ujm.tse.lt2c.satin.inferray.triplestore.SortedCacheObliviousTripleStore;
/** Add nothings test
 * 
 * @author Julien
 *
 */
public class TestTripleStoreMerging5 {

	@Test
	public void test() {

		int p = 10;
		long[] mainTriples = { 2, 1, 3, 5, 4, 7, 6, 8 };
		long[] outputTriples = {};

		CacheTripleStore main = new SortedCacheObliviousTripleStore(50);
		CacheTripleStore output = new SortedCacheObliviousTripleStore(50);
		CacheTripleStore newtriples = new SortedCacheObliviousTripleStore(50);
		main.setPropertyTriples(p, new LongPairArrayList(mainTriples));
		output.setPropertyTriples(p, new LongPairArrayList(outputTriples));
		Inferray infer = new Inferray();
		infer.setMainTripleStore(main);
		infer.setOutputTriples(output);
		infer.setNewTriples(newtriples);
		infer.updateTripleStores();
		// Get the new triples
		assertNull(infer.getNewTriples().getbyPredicateRawIndex(p));

	}
}
