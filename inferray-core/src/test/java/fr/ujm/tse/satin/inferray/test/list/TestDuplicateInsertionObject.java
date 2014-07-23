package fr.ujm.tse.satin.inferray.test.list;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;

public class TestDuplicateInsertionObject {

	@Test
	public void test() {
		final long[] expected = {1, 1, 2, 1, 3, 1, 1, 0, 2, 0, 3, 0};
		final LongPairArrayList list = new LongPairArrayList();
		list.add(1);
		list.add(1);
		list.add(2);
		list.add(1);
		list.add(3);
		list.add(1);
		list.duplicateInsertionWithNewObject(0, 3);
		//System.out.println(list);
		list.trimToSize();
		assertArrayEquals(expected, list.elements());
	}

}
