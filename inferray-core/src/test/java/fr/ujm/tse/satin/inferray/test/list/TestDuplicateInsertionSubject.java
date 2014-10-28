package fr.ujm.tse.satin.inferray.test.list;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.utils.SortingAlgorithm;
import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;

public class TestDuplicateInsertionSubject {

	@Test
	public void test() {
		final long[] expected = {1, 2, 1, 3, 1, 4, 0, 2, 0, 3, 0, 4};
		final LongPairArrayList list = new LongPairArrayList(SortingAlgorithm.MSD);
		list.add(1);
		list.add(2);
		list.add(1);
		list.add(3);
		list.add(1);
		list.add(4);
		list.duplicateInsertionWithNewSubject(0, 3);
		//System.out.println(list);
		list.trimToSize();
		assertArrayEquals(expected, list.elements());
	}

}
