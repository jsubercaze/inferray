package fr.ujm.tse.satin.inferray.test.list.triplestore;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.utils.SortingAlgorithm;
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

		final int p = 10;
		final long[] mainTriples = { 2, 1, 3, 5, 4, 7, 6, 8 };
		final long[] outputTriples = {};

		final CacheTripleStore main = new SortedCacheObliviousTripleStore(50,SortingAlgorithm.MSD);
		final CacheTripleStore output = new SortedCacheObliviousTripleStore(50,SortingAlgorithm.MSD);
		final CacheTripleStore newtriples = new SortedCacheObliviousTripleStore(50,SortingAlgorithm.MSD);
		main.setPropertyTriples(p, new LongPairArrayList(mainTriples,SortingAlgorithm.MSD));
		output.setPropertyTriples(p, new LongPairArrayList(outputTriples,SortingAlgorithm.MSD));
		final Inferray infer = new Inferray();
		infer.setMainTripleStore(main);
		infer.setOutputTriples(output);
		infer.setNewTriples(newtriples);
		infer.updateTripleStores();
		// Get the new triples
		assertNull(infer.getNewTriples().getbyPredicateRawIndex(p));

	}
}
