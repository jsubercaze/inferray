package fr.ujm.tse.satin.inferray.test.list.sorting;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;

public class ObjectSortTest {
	public static final long[] arrayToSort = { 1, 2, 5, 8, 4, 3, 1, 0 };
	public static final long[] arraySorted = { 1, 0, 1, 2, 4, 3, 5, 8 };

	@Test
	public void test() {
		LongPairArrayList l = new LongPairArrayList(arrayToSort);
		l=l.objectSortedCopy();

		assertArrayEquals(l.elements(), arraySorted);
	}

}
