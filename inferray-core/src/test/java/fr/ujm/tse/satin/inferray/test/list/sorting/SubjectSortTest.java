package fr.ujm.tse.satin.inferray.test.list.sorting;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;

public class SubjectSortTest {

	public static final long[] arrayToSort = { 6, 5, 2, 1, 3, 2, 5, 4, 1, 0, 7,
		6, 1, 1 };
	public static final long[] arraySorted = { 1, 1, 1, 0, 2, 1, 3, 2, 5, 4, 6,
		5, 7, 6 };

	@Test
	public void test() {
		final LongPairArrayList sorting = new LongPairArrayList(arrayToSort);
		// System.out.println(Arrays.toString(sorting.elements()));
		sorting.subjectSort();
		// System.out.println(Arrays.toString(sorting.elements()));
		assertArrayEquals(arraySorted, sorting.elements());
	}
}
