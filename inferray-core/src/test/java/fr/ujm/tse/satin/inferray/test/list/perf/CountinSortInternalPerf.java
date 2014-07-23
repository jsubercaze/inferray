package fr.ujm.tse.satin.inferray.test.list.perf;
import java.util.Random;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;

public class CountinSortInternalPerf {
	public static final int SIZE = 10_000_000;

	public static final int MAX_VALUE = 5_000;

	public static void main(final String[] args) {
		final LongPairArrayList l1 = new LongPairArrayList(SIZE);
		final LongPairArrayList l2 = new LongPairArrayList(SIZE);
		final Random r = new Random();
		for (int i = 0; i < SIZE; i++) {
			final int s = r.nextInt(MAX_VALUE);
			final int o = r.nextInt(MAX_VALUE);
			l1.add(s);
			l1.add(o);
			l2.add(s);
			l2.add(o);
		}
		long time1 = System.currentTimeMillis();
		// l1.quickSortFull();
		System.out.println("QuickSort " + (System.currentTimeMillis() - time1)
				+ "ms");
		time1 = System.currentTimeMillis();
		l2.subjectSort();// Calls quicksort
		System.out.println("CountingSort "
				+ (System.currentTimeMillis() - time1) + "ms");
	}
}
