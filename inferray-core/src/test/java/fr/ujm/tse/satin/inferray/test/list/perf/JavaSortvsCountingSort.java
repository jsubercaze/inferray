package fr.ujm.tse.satin.inferray.test.list.perf;
import java.util.Random;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;

public class JavaSortvsCountingSort {
	public static final int MAX_SIZE = 100_000;
	public static final int MIN_SIZE = 10;
	public static final int STEP = 500;
	public static final int MAX_VALUE = 1_000_000;

	public static void main(final String[] args) {
		final long tjava,tcount;
		for (int size = MIN_SIZE; size < MAX_SIZE; size += STEP) {
			System.out.println("For "+size+" elements");
			final LongPairArrayList l1 = new LongPairArrayList(size);
			final LongPairArrayList l2 = new LongPairArrayList(size);
			final Random r = new Random();
			for (int i = 0; i < size; i++) {
				final int s = r.nextInt(MAX_VALUE);
				l1.add(s);
				l2.add(s);
			}
			final long time1 = System.currentTimeMillis();
			//			l2.countingSort();// Calls quicksort
			//			System.out.println("CountingSort "
			//					+ (System.currentTimeMillis() - time1) + "ms");
			//			time1 = System.currentTimeMillis();
			//			l1.javaSort();
			//			System.out.println("QuickSort "
			//					+ (System.currentTimeMillis() - time1) + "ms");


		}
	}
}
