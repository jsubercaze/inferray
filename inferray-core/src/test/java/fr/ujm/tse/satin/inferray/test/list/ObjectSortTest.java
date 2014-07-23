package fr.ujm.tse.satin.inferray.test.list;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;

public class ObjectSortTest {
	/**
	 * Regular case
	 */
	public static final long[] arrayToSort = { 1, 2, 5, 8, 4, 3, 1, 0 };
	public static final long[] arraySorted = { 1, 0, 1, 2, 4, 3, 5, 8 };

	/**
	 * Edge case where all the objects are identical
	 */
	public static final long[] arrayToSort2 = { 8, 1, 2, 1, 5, 1, 0, 1 };
	public static final long[] arraySorted2 = { 0, 1, 2, 1, 5, 1, 8, 1 };

	/**
	 * CAX-SCO test example
	 */
	public static final long[] arrayToSort3 = { 2147483648L, 2147483633L,
		2147483650L, 2147483648L };
	public static final long[] arraySorted3 = { 2147483648L, 2147483633L,
		2147483650L, 2147483648L };

	@Test
	public void test() {
		LongPairArrayList l = new LongPairArrayList(arrayToSort);
		l=l.objectSortedCopy();

		assertArrayEquals(l.elements(), arraySorted);
		l = new LongPairArrayList(arrayToSort2);
		l=l.objectSortedCopy();

		assertArrayEquals(l.elements(), arraySorted2);
		l = new LongPairArrayList(arrayToSort3);
		l=l.objectSortedCopy();

		assertArrayEquals(l.elements(), arraySorted3);
	}

}
