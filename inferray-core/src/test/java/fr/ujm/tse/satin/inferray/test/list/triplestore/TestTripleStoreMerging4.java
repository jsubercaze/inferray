package fr.ujm.tse.satin.inferray.test.list.triplestore;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;
import fr.ujm.tse.lt2c.satin.inferray.triplestore.SortedCacheObliviousTripleStore;

public class TestTripleStoreMerging4 {

	@Test
	public void test() {

		final int p = 10;
		final long[] mainTriples = { 2, 1, 3, 5, 4, 7, 6, 8 };
		final long[] outputTriples = { 1, 0, 2, 1, 8, 9 };
		final long[] expectedNew = { 1, 0, 8, 9 };
		final long[] expectedmain = { 1, 0, 2, 1, 3, 5, 4, 7, 6, 8, 8, 9 };
		final CacheTripleStore main = new SortedCacheObliviousTripleStore(50);
		final CacheTripleStore output = new SortedCacheObliviousTripleStore(50);
		final CacheTripleStore newtriples = new SortedCacheObliviousTripleStore(50);
		main.setPropertyTriples(p, new LongPairArrayList(mainTriples));
		output.setPropertyTriples(p, new LongPairArrayList(outputTriples));
		final Inferray infer = new Inferray();
		infer.setMainTripleStore(main);
		infer.setOutputTriples(output);
		infer.setNewTriples(newtriples);
		infer.updateTripleStores();
		// Get the new triples
		infer.getNewTriples().getbyPredicateRawIndex(p).trimToSize();
		infer.getMainTripleStore().getbyPredicateRawIndex(p).trimToSize();
		assertArrayEquals(expectedmain, infer.getMainTripleStore()
				.getbyPredicateRawIndex(p).elements());
		assertArrayEquals(expectedNew, infer.getNewTriples()
				.getbyPredicateRawIndex(p).elements());

	}
}
